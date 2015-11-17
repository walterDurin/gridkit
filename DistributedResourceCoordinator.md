# Introduction #

Distributed Resource Coordinator (DRC) is a framework for reliable execution of batch or maintenance jobs in Coherence cluster. Distributed Resource Coordinator leverages Coherence to provide fail over, load balancing for such type of jobs.

# Problem to be solved #
In complex grid based applications, there is often a need to have one or more constantly running maintenance jobs. E.g. application may need to subscribe to JMS topic to listen to data change notifications in external source. Jobs themselves may be quite simple, but once you consider that your application is actually multiple processes distributed over a cluster of servers, thing may become more complicated.
Usually you do not want to have dedicated maintenance server or have copy of maintenance job running on each and every server. Instead you want:
  * execute your maintenance job only on one JVM in cluster at time,
  * provide fault tolerance for a job by automatically restarting it on another cluster member in case if its previous host has been crashed or stopped,
  * for multiple independent jobs balance then evenly across members (and redistribute if number of members are changing).
Distributed locks provided by Orace Coherence are very useful for coordination between members of cluster, but still a lot of  work is require to achieve all three goals mentioned above.
Goal distributed resource coordinator (DRC) framework is to hide all this complexity and offer reliable instrument for maintenance jobs in Coherence based distributed application.

# Framework #
Distributed resource coordinator is implementing controller pattern. Your application has to register a set of resources.  Distributed resource coordinator (DRC) daemon is main component of framework. DRC daemon takes a set of resources, during initialization, and after start it will be responsible for distributing “ownership” of resources between other instances of DRC daemons in the same group (which typically run inside normal application nodes). Each resource can be owned by at most one instance of DRC daemon at any given time. DRC daemons we also ensure fair distribution of resources between nodes and fast fail over in case if node owning some resources is down.
Application is notified about acquisition/withdrawing of resource ownership via callback interface ResourceHandler. Resources are indentified by IDs. Any java object can be used as ID for resource if it is supports Coherence serialization protocol (either POF or java serialization dependent on your grid configuration). java.lang.String is a good candidate to be used as resource ID.

## How it works? ##
Ownership of resources is controlled with Oracle Coherence distributed locks. Each resource has two associated locks (primary and stand by). Primary lock is acquired by owner DRC instance. Stand by lock is used for rebalancing protocol; DRC instance interested in acquiring resource captures this lock indicating that it is safe for current owner of resource to give up ownership.
Each instance of DRC calculates how much resource it should own (usually by checking number of DRC instances in cluster though this strategy is pluggable) and try either acquire more resources or give up some resources to other instances.

## Using distributed resource coordinator ##
Distributed resource coordinator is designed to be used with IoC framework. It is configured by injecting dependencies via setter methods. Once instance is configured, it should be started by start() method.
Three mandatory dependencies for DistributedResourceCoordinator object are
  * `controlMap` – instance of distributed or replicated Coherence cache, which is used to coordinated resources.
  * `resources` – collection of resource id to be managed.
  * `resourceHandler` – an application specific of interface, which is used to enumerate resources and notify ownership changes.
Resource handler (`org.gridkit.drc.coherence.ResourceHandler`) is an application specific call back, which is used by DRC to notify application about ownership of particular resource.
  * `connect(Object resourceId)` – notifies that permit for resource is accrued and application may start working with it.
  * `disconnect(Object resourceId)` – notifies that DRC want to transfer permit to another node and application should stop working with it (permit will not be transferred until return of this method)
  * {{{terminate(Object resourceId)}} – notifies that DRC was abnormally disconnected from cluster and cannot participate in resource distribution protocol. This is an emergency situation.
Framework also includes an implementation of resource handler which will be suitable to situation when maintenance task should be run periodically on resource (e.g. polling of database). `ScheduledResourceJobHandler` is using `ScheduledThreadPoolExecutor` to schedule execution of jobs owned by local DRC daemon. `ScheduledResourceJob` is encapsulating actual job.
```
class SampleResourceJob implements ScheduledResourceJob {

	private String id;	
	//...
        // other stuff required for job to run, e.g. keep persistent JDBCConnection is a good idea
	
	@Override
	public Object getResourceId() {
		// an ID of resource in cluster (e.g. database URI)
		// ID should be immutable, implement hash()/equals() and be in compliance with Coherence serialization protocol
		// java.lang.String is good choice for ID
		return id;
	}

	@Override
	public SchedulingPolicy getSchedulingPolicy() {
		// SchedulingPolicy interface is very flexible
		// but you can use few out of box simple scheduling policies
		
		// this will make this job run every 15 seconds
		return SchedulingPolicies.newPeriodicTimeTablePolicy(15, TimeUnit.SECONDS);
	}

	@Override
	public void connect() {
		// you may do some initialization here
		// or you can defer it to execute call
		
	}

	@Override
	public void execute(boolean bySchedule) {
		// execute is called from thread pool, so we can take our time hear
		if (!bySchedule) {
			// we have been called just after connection to resource
			// it is a good place to initialize some long term resources such as database connection 
		}
		
		// do our job
	}

	@Override
	public void disconnect() {
		// resource is going to be disconnected
		// it is time to release long term resources (e.g. database connections) if we have allocated any
	}
}
```

Initialization of DRC with `ScheduledResourceJobHandler` in code will have following look.
```
		// all Distributed Resource Coordination in same resource group
		// should use same Coherence cache (either distribute or replicated scheme)
		// but you may create multiple DRC groups using different caches
		ConcurrentMap cache = CacheFactory.getCache("some-cache-name");
		
		// All DRC instances in same group are expected to have same resource list
		Collection<ScheduledResourceJob> jobs; // you should get list of job somehow 
		jobs = null;
		
		// This thread pool will be used to run jobs
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
		
		// creating a handler
		ScheduledResourceJobHandler handler = new ScheduledResourceJobHandler();
		handler.setExecutionService(executor);
		handler.setJobs(jobs);
		
		// creating DRC (minimal working setup)
		DistributedResourceCoordinator coordinator = new DistributedResourceCoordinator();
		coordinator.setLockMap(cache);
		coordinator.setResources(handler.getResources());
		coordinator.setResourceHandler(handler);
		
		// This is default share calculator which is using process role to count all DRC in cluster
		//coordinator.setShareCalculator(new RoleBasedShareCalculator());
		
		// Now minimal configuration is done and we can start DRC node
		coordinator.start();
		
		// ...
		
		// Eventually we may want to bring local DRC node down.
		// stop() method will block until, all jobs will be gracefully disconnected
		coordinator.stop();
```
As you may see from style of code, components are meant to be initialized with IoC container e.g. Spring IoC.

# Source code and documentation #

This module is in inqubation stage.
You can you source code from trunk.