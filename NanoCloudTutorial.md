# API preview warning #

---

> _All `GridLab` components at API preview status. While we are actively using these stuff across projects, a lot of things is going to change as we are sorting out ergonomics etc. In particular a lot of package rebranding and module restructuring are pending._

---




# Introduction #

NanoCloud is an API and library allowing you seamlessly manipulate distributed execution of code.

Originally this API has started as a test library emulating distributed cluster in single JVM (for sake of test automation).
As API matured, idea to use exactly same API to manipulate real distributed clusters have become very attractive.

In particular, this library is positioned as backbone for distributed performance/stress testing.

# Getting started with NanoCloud #

## Test project ##
Please checkout test project at https://gridkit.googlecode.com/svn/grid-lab/trunk/examples/nanocloud-getting-started ([browse](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started)).
This project contains code referenced in this tutorial.

All tutorial examples are implemented as runnable !JUnit test and organized into few files:
  * [StartingWithLocalCloud.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started/src/test/java/org/gridkit/lab/examples/nanocloud/StartingWithLocalCloud.java) - executing code in other java processes on same box.
  * [StartingWithDistributedCloud.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started/src/test/java/org/gridkit/lab/examples/nanocloud/StartingWithDistributedCloud.java) - executing code remotely using SSH ([SSH access should be setup](NanoCloud_Configuring_SSH_credentials.md)).
  * [BasicViNodeUsage.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started/src/test/java/org/gridkit/lab/examples/nanocloud/BasicViNodeUsage.java) - essentials of NanoCloud API.
  * [TransaprentRmi.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started/src/test/java/org/gridkit/lab/examples/nanocloud/TransaprentRmi.java) - examples of "transparent" RMI between nodes.

Examples in Java sources above may be slightly ahead of tutorial text.

## Quick introduction of ViNode ##
NanoCloud API is build around concept of ViNode. With a little over simplification, ViNode is a remote process which allow us to execute code. ViNode offers a set of method to executed code. ViNode could also be a group of real nodes (thus providing us simple way for parallel execution).

There are 3 standard type of nodes backing ViNode instances:
  * separate JVM, running locally
  * separate JVM, started remotely started via SSH
  * thread group inside of current JVM with option ClassLoader isolation (Isolate node)

Last option is extremely useful for debugging.

## Local cloud ##

Let's start with local nodes.

Open [StartingWithLocalCloud.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started-1/src/test/java/org/gridkit/lab/examples/nanocloud/StartingWithLocalCloud.java), I'm separate test methods to illustrate step of tutorial.

### Step 1 ###
```
	@Test
	public void test_hello_world__version1() {
		// Let's create simple local cloud first
		cloud = CloudFactory.createLocalCloud();
		
		// This line says that 'node1' should exists
		// all initialization are lazy and asynchronous
		// so this line will not trigger any process creation
		cloud.node("node1");
		
		sayHelloWorld(cloud);		
	}
```

In console you would see something like this:
```
[node1] Exec command: []
[node1] 16:48:31.349 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
16:48:31.537 [Control hub accepter [127.0.0.1:42122]] INFO  o.g.v.t.LocalJvmProcessFactory - Conntected: Socket[addr=/127.0.0.1,port=2384,localport=42122]
16:48:31.552 [Control hub accepter [127.0.0.1:42122]] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 478c40e92e912c524ce3937aad5f541d - Socket[addr=/127.0.0.1,port=2384,localport=42122]
[node1] My name is '1456@ws4199'. Hello!
16:48:31.709 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] terminated.
16:48:31.709 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2384,localport=42122]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] disconneted.
16:48:31.709 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2384,localport=42122]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[node1]
```

Important part is
```
[node1] My name is '1456@ws4199'. Hello!
```
This is a result of `System.out.println()` from our runnable.
All outputs from nodes are redirected to parent process console and prefixed with node name for convenience.

### Step 2 ###

```
	@Test
	public void test_hello_world__version2() {
		cloud = CloudFactory.createLocalCloud();

		// let's create a few more nodes this time
		cloud.nodes("node1", "node2", "node3", "node4");
		
		sayHelloWorld(cloud);		
	}
```

Now we have created 4 different slave processes and got responses from all of them.

```
[node1] Exec command: []
[node1] 16:53:11.894 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[node2] Exec command: []
[node2] 16:53:11.863 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
16:53:12.035 [Control hub accepter [127.0.0.1:45152]] INFO  o.g.v.t.LocalJvmProcessFactory - Conntected: Socket[addr=/127.0.0.1,port=2671,localport=45152]
16:53:12.035 [Control hub accepter [127.0.0.1:44134]] INFO  o.g.v.t.LocalJvmProcessFactory - Conntected: Socket[addr=/127.0.0.1,port=2672,localport=44134]
16:53:12.050 [Control hub accepter [127.0.0.1:45152]] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 71e800a25a8f919ebd91cd98bce36a24 - Socket[addr=/127.0.0.1,port=2671,localport=45152]
16:53:12.050 [Control hub accepter [127.0.0.1:44134]] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 4251931d9abdcb6a7e1ac78757349d9c - Socket[addr=/127.0.0.1,port=2672,localport=44134]
16:53:12.082 [Control hub accepter [127.0.0.1:48486]] INFO  o.g.v.t.LocalJvmProcessFactory - Conntected: Socket[addr=/127.0.0.1,port=2674,localport=48486]
16:53:12.082 [Control hub accepter [127.0.0.1:48486]] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point ec32abfbe8edf8f4438e8cf7050c4885 - Socket[addr=/127.0.0.1,port=2674,localport=48486]
16:53:12.082 [Control hub accepter [127.0.0.1:47857]] INFO  o.g.v.t.LocalJvmProcessFactory - Conntected: Socket[addr=/127.0.0.1,port=2673,localport=47857]
16:53:12.082 [Control hub accepter [127.0.0.1:47857]] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 6c3c0022c372c16528e952405efda9c6 - Socket[addr=/127.0.0.1,port=2673,localport=47857]
[node1] My name is '4364@ws4199'. Hello!
[node2] My name is '4352@ws4199'. Hello!
[node3] Exec command: []
[node3] 16:53:11.925 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[node3] My name is '1528@ws4199'. Hello!
[node4] Exec command: []
[node4] 16:53:11.925 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[node4] My name is '5256@ws4199'. Hello!
16:53:12.238 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] terminated.
16:53:12.253 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2672,localport=44134]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] disconneted.
16:53:12.253 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2672,localport=44134]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[node1]
16:53:12.347 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node2] terminated.
16:53:12.347 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2671,localport=45152]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node2] disconneted.
16:53:12.347 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2671,localport=45152]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[node2]
16:53:12.456 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node3] terminated.
16:53:12.456 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2674,localport=48486]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node3] disconneted.
16:53:12.456 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2674,localport=48486]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[node3]
16:53:12.550 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node4] terminated.
16:53:12.550 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2673,localport=47857]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node4] disconneted.
16:53:12.550 [RMI-Receiver: Socket[addr=/127.0.0.1,port=2673,localport=47857]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[node4]
```

Output is a little messed though.

### Step 3 ###
```
	@Test
	public void test_hello_world__version3() throws InterruptedException {
		// Let's create simple local cloud first
		cloud = CloudFactory.createLocalCloud();
		
		cloud.nodes("node1", "node2", "node3", "node4");
		
		// let's make sure that all nodes are initialized
		// before saying 'hello' this time
		warmUp(cloud);
		
		// Console output is pulled asynchronously so we have to give it
		// few milliseconds to catch up.
		Thread.sleep(300);
		
		// Now we should see quite good chorus
		sayHelloWorld(cloud);			
	}
```

Now with "warm up" we could see something like this in console.
```
...
[node4] Exec command: []
[node4] 16:55:43.078 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[node2] My name is '4624@ws4199'. Hello!
[node3] My name is '4696@ws4199'. Hello!
[node1] My name is '5596@ws4199'. Hello!
[node4] My name is '4316@ws4199'. Hello!
16:55:43.718 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] terminated.
...
```

Lazy initialization is used very aggressively in core of NanoCloud, because it allows palatalization of time consuming task such as process stating or classpath replication via SSH.

Warm up like in example below allows you to benefit for parallel initialization and yet have you runnable executed almost synchronously (but given nature of distributed system 'almost' could be very lax).

### Step 4, customizing JVM command line ###

One reason to spawn execution to separate JVM could be configuring that JVM in special way.

Here is how you can do it via NanoCloud API.

```
	@Test
	public void test_jvm_args__version1() throws InterruptedException {
		cloud = CloudFactory.createLocalCloud();
		
		// let's create a couple of node1
		cloud.node("node1");
		cloud.node("node2");
		
		// now let's adjust JVM command line options used to start slave process
		JvmProps.at(cloud.node("node1")).addJvmArg("-Xms256m").addJvmArg("-Xmx256m");
		JvmProps.at(cloud.node("node2")).addJvmArg("-Xms512m").addJvmArg("-Xmx512m");
		
		warmUp(cloud);
		Thread.sleep(300);
		
		// Let's see how much memory is available to our childs
		reportMemory(cloud);			
	}	
```

You should see something like this in console:
```
...
[node2] Exec command: [-Xms512m, -Xmx512m]
[node2] 17:13:48.828 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[node1] My name is '3756@ws4199'. Memory limit is 247MiB
[node2] My name is '5668@ws4199'. Memory limit is 494MiB
17:13:49.468 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [node1] terminated.
...
```

As you can see two slave JVMs have different memory configuration.

## Going distributed ##

### Configuring remote access ###
Let's try running remote processes now.
Open [StartingWithDistributedCloud.java](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started-1/src/test/java/org/gridkit/lab/examples/nanocloud/StartingWithDistributedCloud.java) to follow this chapter of tutorial.

Running remote processes via SSH requires a little configuration.
Provided configuration is for my VirtualBox based cluster.

To run examples from this tutorial you need:
  * server with SSH access (either password or private key will do)
  * Java 6 installed on that server (no GCJ please, it is not a java)
  * that's it

First go to [cbox-cluster.viconf](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started-1/src/test/resources/cbox-cluster.viconf) file.

```
# Use remote node type by default
**: node:type=remote

# Read SSH credentials for ssh-credetials.prop at user's home by default
**: remote:ssh-credential-file=~/ssh-credentials.prop

# Map node names to hosts
# in this example mapping is hard coded
cbox1.**: remote:host=cbox1
cbox2.**: remote:host=cbox2
cbox3.**: remote:host=cbox3
cbox4.**: remote:host=cbox4

# We should specify how to start java
cbox?.**: remote:java-exec=/usr/bin/java

# We also need to store cached jars somewhere
cbox?.**: remote:jar-cache-path=/tmp/nanocloud
```

cbox[1-4] are names for my VMs. You could edit this file setting some valid hostnames for following lines:
```
cbox1.**: remote:host=cbox1
cbox2.**: remote:host=cbox2
cbox3.**: remote:host=cbox3
cbox4.**: remote:host=cbox4
```
You could use single hostname for all of them

You may also need to amend following line to configure path to you java executable.
{{
cbox?.: remote:java-exec=/usr/bin/java
}}

You should also prepare `ssh-credentials.prop` file and drop it to your user's home. See [ssh-credentials.sample](http://code.google.com/p/gridkit/source/browse/grid-lab/trunk/examples/nanocloud-getting-started-1/src/test/resources/ssh-credentials.sample) as example.

### Step 1. Running remote nodes ###
Once configuration is finished with could run some code.
```
	@Test
	public void test_distributed_hello_world__version1() throws InterruptedException {
		
		// Using SSH for remote execution requires some configuration
		// it could be done programaticaly but we will use config file in this example
		cloud = CloudFactory.createSshCloud("resource:cbox-cluster.viconf");
		
		// In config we have used first segment of node name is used to map
		// node to host.
		cloud.node("cbox1.node1");
		cloud.node("cbox2.node1");
		
		// Alternatively we could override configuration for particular node
		// host, java command and jar cache path should be configured for node to start
		RemoteNodeProps.at(cloud.node("extranode"))
			.setRemoteHost("cbox1")
			.setRemoteJavaExec("java")
			.setRemoteJarCachePath("/tmp/extra");
				
		
		// now we have 3 nodes configured to run across two servers
		// let say them hello
		
		// warm up is optional, but it makes console out put less messy
		warmUp(cloud);
		sayHelloWorld(cloud);

		// give console output a chance to reach us from remote node
		Thread.sleep(300);
	}
```

For my virtual cluster I could see following output:
```
17:28:12.285 [ViNode[cbox2.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox2:/tmp/nanocloud/.cache/669cfdc74402a49e91c74a27c0c7035542745383/nanocloud-getting-started-1.jar 1752 bytes
17:28:12.363 [ViNode[cbox1.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/nanocloud/.cache/669cfdc74402a49e91c74a27c0c7035542745383/nanocloud-getting-started-1.jar 1752 bytes
17:28:12.379 [ViNode[cbox1.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/nanocloud/.cache/f3f684c590cef35073d1d33578dafb7d5e03745d/test-classes.jar 7119 bytes
17:28:12.535 [ViNode[cbox2.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox2:/tmp/nanocloud/.cache/f3f684c590cef35073d1d33578dafb7d5e03745d/test-classes.jar 7119 bytes
17:28:12.566 [ViNode[cbox1.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/nanocloud/.cache/58494e3dad3c225b43bf9ac2bd08b3db7874b047/booter.jar 813 bytes
17:28:12.597 [ViNode[cbox2.node1] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox2:/tmp/nanocloud/.cache/efdb11b47081727136d4c31f66d52d02b464fdbb/booter.jar 814 bytes
17:28:13.457 [Thread-11] INFO  o.g.v.t.ssh.SimpleSshJvmReplicator - Conntected: [SSH Tunnel: cbox2:50207]
17:28:13.457 [Thread-11] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 62483bad7f270cd3fe4f4ec4bdd063e4 - [SSH Tunnel: cbox2:50207]
[cbox2.node1] Exec command: []
[cbox2.node1] 12:15:41.616 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
17:28:13.457 [Thread-13] INFO  o.g.v.t.ssh.SimpleSshJvmReplicator - Conntected: [SSH Tunnel: cbox1:50994]
17:28:13.457 [Thread-13] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 0cfb910bf86e2caad9209e81322ba188 - [SSH Tunnel: cbox1:50994]
[cbox1.node1] Exec command: []
[cbox1.node1] 16:20:50.421 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
17:28:15.066 [ViNode[extranode] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/extra/.cache/f3f684c590cef35073d1d33578dafb7d5e03745d/test-classes.jar 7119 bytes
17:28:15.206 [ViNode[extranode] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/extra/.cache/669cfdc74402a49e91c74a27c0c7035542745383/nanocloud-getting-started-1.jar 1752 bytes
17:28:15.222 [ViNode[extranode] init] INFO  o.g.v.t.ssh.RemoteFileCache - Uploading: cbox1:/tmp/extra/.cache/3b4b79699514cb7210f10e1fb8c8c7f4ef56798e/booter.jar 810 bytes
17:28:16.112 [Thread-25] INFO  o.g.v.t.ssh.SimpleSshJvmReplicator - Conntected: [SSH Tunnel: cbox1:50101]
17:28:16.112 [Thread-25] INFO  org.gridkit.zerormi.hub.RemotingHub - Stream connected at end point 815372c231d9ae3a7e557ce062b04a92 - [SSH Tunnel: cbox1:50101]
[extranode] Exec command: []
[extranode] 16:20:53.075 [main] INFO  o.g.zerormi.hub.RemotingEndPoint - Connecting to master socket
[cbox2.node1] My name is '6669@cbox2'. Hello!
[cbox1.node1] My name is '6572@cbox1'. Hello!
[extranode] My name is '6619@cbox1'. Hello!
17:28:16.753 [main] INFO  o.g.v.t.s.ConfigurableSshReplicator - Session org.gridkit.vicluster.telecontrol.ssh.ConfigurableSshReplicator$SessionInfo@1c0f2e5 is not used
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox1:50994]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [cbox1.node1] disconneted.
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox1:50994]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[cbox1.node1]
17:28:16.753 [RMI[cbox1.node1]-worker-5] INFO  o.g.v.t.ssh.SimpleSshJvmReplicator - Interrupted: [SSH Tunnel: cbox1:50994]
17:28:16.753 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [cbox1.node1] terminated.
17:28:16.753 [main] INFO  o.g.v.t.s.ConfigurableSshReplicator - Session org.gridkit.vicluster.telecontrol.ssh.ConfigurableSshReplicator$SessionInfo@15e0873 is not used
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox2:50207]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [cbox2.node1] disconneted.
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox2:50207]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[cbox2.node1]
17:28:16.753 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [cbox2.node1] terminated.
17:28:16.753 [main] INFO  o.g.v.t.s.ConfigurableSshReplicator - Session org.gridkit.vicluster.telecontrol.ssh.ConfigurableSshReplicator$SessionInfo@2e1f1f is not used
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox1:50101]] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [extranode] disconneted.
17:28:16.753 [RMI-Receiver: [SSH Tunnel: cbox1:50101]] INFO  org.gridkit.zerormi.hub.RemotingHub - Closed: RmiObjectInputStream[extranode]
17:28:16.753 [main] INFO  org.gridkit.zerormi.RmiGateway - RMI gateway [extranode] terminated.
```

API call for remote execution is same as for local but behind of scene few more things are happening.

In particular:
  * Classpath of master (parent) JVM is replicated to remote host via SSH (and cached there)
  * RPC comunitation between master and slave JVM is tunneled via SSH TCP tunnel (to avoid possible firewall issues)

### Step 2. Mixing remote and embedded node ###
Now, we can run our code remotely. But imagine you want to debug your code. One option is to use remote debugging. But in many cases you could just run node you want to debug in master process thus avoiding these hurdles.

Let's modify previous example to run just one node in master JVM process.
```
@Test
public void test_distributed_hello_world__with_debug() throws InterruptedException {
	
	// Using SSH for remote execution requires some configuration
	// it could be done programmatically, but we will use config file in this example
	cloud = CloudFactory.createSshCloud("resource:cbox-cluster.viconf");
	
	// Our cloud config (box-cluster.viconf) is using first segment of node name 
	// for mapping of node node to hostname
	cloud.node("cbox1.node1");
	cloud.node("cbox2.node1");
	
			
	// Now imagine, that you want to debug one of slave processes.
	// If it could be run on your dev. box (no OS dependencies, etc),
	// you could easy redirect on of slave node to run inside of master JVM.
	// You can achieve this but using either in-process or isolate node type
	ViProps.at(cloud.node("cbox1.node1")).setInProcessType();		
	
	ViNode allNodes = cloud.node("**");

	allNodes.touch(); // warm up, equivalent to sending empty runnable
	
	System.out.println("Master JVM name is '" + ManagementFactory.getRuntimeMXBean().getName() + "'");
	// you can set break point in runnable and catch cbox1.node1 executing it
	// other vinodes are running as separate processes, so they out of reach
	allNodes.exec(new Callable<Void>() {

		@Override
		public Void call() throws Exception {
			String jvmName = ManagementFactory.getRuntimeMXBean().getName();
			System.out.println("My name is '" + jvmName + "'. Hello!");
			return null;
		}
	});
	
	// Notice that output of "in-process" vinode are still prefixed for your convenience.
	// Same isolation applies to system properties too. 
	
	// give console output a chance to reach us from remote node
	Thread.sleep(300);
}	
```

Output would contain something like this
```
...
Master JVM name is '6392@ws4199'
[cbox1.node1] My name is '6392@ws4199'. Hello!
[cbox2.node1] My name is '2118@cbox2'. Hello!
[cbox1.node1] Stopping ...
[cbox1.node1] Stopped
...
```
You can see that "cbox1.node1" actually were running locally in a JVM of master process. So you cloud debug it freely.
Output of "cbox1.node1" is still prefixed. All threads of "cbox1.node1" are also running in dedicated thread group, so it could be forcefully killed (it is impossible to reliably kill thread in JVM, but we are doing our best).