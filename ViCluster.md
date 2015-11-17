This project has been transformed to ChTest

# Introduction #
`ViCluster` is evolution of TestUtils. It provides an alternative to direct use of Isolate class and few additional enhancements.

`ViCluster` was originally developed for writing automated test of Coherence based application. Key challenge for such testing is internal use of singletons in Coherence which make it impossible to have two independent nodes in same JVM. This limitation is worked around using multiple classloaders, each pseudo node (isolate) use own class loader (thus own singletons).

# Key features #
  * Starting all kind of nodes inside one JVM (member and extend client, storage enabled and disabled), supporting any type of Coherence topology.
  * Have independent **system properties** for each isolate.
  * **Classpath** of each isolate could be tweaked independently.
  * Output of isolates could be optionally prefixed by isolate name.
  * Isolate could be forcibly terminated.
  * Isolate can be suspended/resumed (server crash and long GC pauses could be simulated this way).
  * Test code can be executed in context of any JVM.

# Getting started #
Simple test cluster example
```
ViCluster cluster;

@After
public void dropCluster() {
	// It is not recommended to shutdown cluster after each test because
	// cluster startup takes few seconds.
	// Normally you would setup your application topology once and resuse it
	// in multiple tests
	if (cluster != null) {
		cluster.shutdown();
	}
}

@Test
public void test_simple_cluster() {
	// you should add to isolate package list
	// * Coherence (com.tangosol)
	// * GridKit (org.gridkit)
	// * Your application package
	// * Some libraries should also be included to work properly (e.g. mockito)
	cluster = new ViCluster("simple_cluster", "org.gridkit", "com.tangosol");
	
	// present for in-JVM cluster
	CohHelper.enableFastLocalCluster(cluster);
	// using default config in this case
	CohHelper.cacheConfig(cluster, "/coherence-cache-config.xml");

	// Creating server node
	ViNode storage = cluster.node("storage");
	CohHelper.localstorage(storage, true);
	
	// simulating DefaultCacheServer startup
	storage.start(DefaultCacheServer.class);
	
	ViNode client = cluster.node("client");
	CohHelper.localstorage(client, false);

	final String cacheName = "distr-a";
	
	// pure magic at this point
	// instance of callable will be serialized and deserialized with classloader of "client" node
	// and executed in context of client JVM
	// you can think of it a of remote call
	// return values or raised exceptions are converted to application classloader on way back
	client.exec(new Callable<Void>(){
		@Override
		public Void call() throws Exception {
			
			NamedCache cache = CacheFactory.getCache(cacheName);
			
			cache.put(0, 0);
			Assert.assertEquals(0, cache.get(0));
			
			return null;
		}
	});
}
```

While running, output of each isolate is prefixed:
```
[simple_cluster.storage] 2012-03-07 12:53:57.558/0.656 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <Info> (thread=simple_cluster.storage-Main, member=n/a): Loaded operational configuration from "jar:file:/C:/WarZone/tools/maven-repos/default/com/oracle/coherence/coherence/3.7.1.3.rc1/coherence-3.7.1.3.rc1.jar!/tangosol-coherence.xml"
[simple_cluster.storage] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <Info> (thread=simple_cluster.storage-Main, member=n/a): Loaded operational overrides from "jar:file:/C:/WarZone/tools/maven-repos/default/com/oracle/coherence/coherence/3.7.1.3.rc1/coherence-3.7.1.3.rc1.jar!/tangosol-coherence-override-dev.xml"
[simple_cluster.storage] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <D5> (thread=simple_cluster.storage-Main, member=n/a): Optional configuration override "/tangosol-coherence-override.xml" is not specified
[simple_cluster.storage] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <D5> (thread=simple_cluster.storage-Main, member=n/a): Optional configuration override "/custom-mbeans.xml" is not specified
[simple_cluster.storage] 
[simple_cluster.storage] Oracle Coherence Version 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 Build 0
[simple_cluster.storage]  Grid Edition: Development mode
[simple_cluster.storage] Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
[simple_cluster.storage] 
[simple_cluster.client] 2012-03-07 12:53:57.558/0.656 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <Info> (thread=Isolate-simple_cluster.client, member=n/a): Loaded operational configuration from "jar:file:/C:/WarZone/tools/maven-repos/default/com/oracle/coherence/coherence/3.7.1.3.rc1/coherence-3.7.1.3.rc1.jar!/tangosol-coherence.xml"
[simple_cluster.client] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <Info> (thread=Isolate-simple_cluster.client, member=n/a): Loaded operational overrides from "jar:file:/C:/WarZone/tools/maven-repos/default/com/oracle/coherence/coherence/3.7.1.3.rc1/coherence-3.7.1.3.rc1.jar!/tangosol-coherence-override-dev.xml"
[simple_cluster.client] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <D5> (thread=Isolate-simple_cluster.client, member=n/a): Optional configuration override "/tangosol-coherence-override.xml" is not specified
[simple_cluster.client] 2012-03-07 12:53:57.605/0.703 Oracle Coherence 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 <D5> (thread=Isolate-simple_cluster.client, member=n/a): Optional configuration override "/custom-mbeans.xml" is not specified
[simple_cluster.client] 
[simple_cluster.client] Oracle Coherence Version 3.7.1.3 (Release Candidate) Internal-TANGOSOL-BUILD-0213.2012.0357 Build 0
[simple_cluster.client]  Grid Edition: Development mode
[simple_cluster.client] Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
[simple_cluster.client] 
```

Nice, isn't it.

# Understanding limitations #
`ViNode` interface allows you to exec any code in scope of isolate. Execution code in Isolate requires conversion to its classloader. This puts some limitation on that can be passed to `exec()` method.
`Runnable`/`Callable` object should be
  * either `Serializable` static( or top level) class
  * or anonymous inner class.

In case of anonymous inner class it cannot access to fields of otter class (though final local variables are accessible as usual).

All objects referenced by instance passed to `exec()` should be serializable (serailization/deserialization is way to convert object to other classloader).

```
@Test
public void test_parameter_passing() {

	cluster = new ViCluster("test_parameter_passing", "org.gridkit");
	
	final double doubleV = 1.1d;
	
	cluster.node("node").exec(new Callable<Void>(){
		@Override
		public Void call() throws Exception {

			// final local variable from outer scope can be accessed as usual
			Assert.assertEquals(1.1d, doubleV, 0d);
			return null;
		}
	});

	final double[] doubleA = new double[]{1.1d};
	
	cluster.node("node").exec(new Callable<Void>(){
		@Override
		public Void call() throws Exception {
			
			Assert.assertEquals(1.1d, doubleA[0], 0d);
			
			// this will not be visible to caller, 
			// code inside of isolate is working with copy of array				
			doubleA[0] = 2.2d;
			return null;
		}
	});

	// array is outer scope there not changed
	Assert.assertEquals(1.1d, doubleA[0], 0d);
}	

void doSomething() {		
}

@Test(expected=NullPointerException.class)
public void test_outter_methods_unaccessible() {

	cluster = new ViCluster("test_parameter_passing", "org.gridkit");
	
	cluster.node("node").exec(new Callable<Void>(){
		@Override
		public Void call() throws Exception {
			
			// this will cause NPE
			// instance to outer class were not passed to isolate
			// this limitation is intentional
			doSomething();

			return null;
		}
	});
}	
```

# Permanent space usage #
To simulate several JVM, we are loading miltiple copies of same classes in JVM. More classes more permanent space is required (`-XX:MaxPermSize` JVM option).
More isolates you create - more permanent space you need.

Shutting down an isoalte (or whole `ViCluster`) will make classes eligible for garbage collection (`ViCluster` is tested for memory leaks and shouldn't have any). But GC may happen too late, so it is recommended:
  * reuse cluster where possible,
  * reserve enough perm space,
  * sometimes create new JVM for each test class is useable (though it will make tests much slower)

# Source code and documentation #

Both jar-files, javadocs and source code are available via maven:

```
<dependency>
	<groupId>org.gridkit.coherence-tools</groupId>
	<artifactId>test-utils</artifactId>
	<version>0.1.13</version>
	<scope>test</scope>
</dependency>
```

Don't forget to add [GridKit Maven Repository](MavenRepo.md) to your parent POM to get this dependency properly resolved.