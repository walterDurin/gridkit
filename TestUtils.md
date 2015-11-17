This project has been transformed to ChTest library.

# Test utils package #

This package contains useful utility classes for unit- and integration-testing of data grid applications. These currently include:
  * Isolate - enabling clusters creation within single JVM,
  * IsolateTestRunner - JUnit test runner forcing test suite to be executed in isolated ClassLoader (helpful if you want different global Coherence setting in different test suites).

## Isolate ##

Isolate class enables multiple applications running within single JVM. This is achieved by a single executor thread created by Isolate instance and separate ClassLoader, used to load submitted tasks. Client code submits Runnable classes, providing their name and optional constructor arguments, to be executed by Isolate.
Isolate is not tied to Coherence in any way, so it is possible to run any application instances in semi-isolated environments.

But it is especially useful for integration testing of Coherence libs, since it is possible to run multiple cluster nodes in integration test, or even run multiple clusters. There is a separate NodeActions utility class, containing reusable actions for Coherence cluster nodes - Start (takes cache configuration file name as constructor parameter), Stop and Crash (simulates node crash).

Here is the usage example of Isolate, running two cluster nodes within single integration test:

```
package org.gridkit.coherence.util.classloader;

import org.junit.Test;

/**
 * Some unit tests for Isolate
 * 
 * @author malekseev
 * @see Isolate
 */
public class IsolateTest {
	
	@Test
	public void twoNodes() throws InterruptedException {
		
		// Initialize and start isolates
		Isolate is1 = new Isolate("node-1", "com.tangosol", "org.gridkit");
		Isolate is2 = new Isolate("node-2", "com.tangosol", "org.gridkit");
		is1.start();
		is2.start();
		
		// Start Coherence nodes within isolates
		is1.submit(NodeActions.Start.class, "test-cache-config.xml");
		is2.submit(NodeActions.Start.class, "test-cache-config.xml");
		
		// Simulate second node crash after 3 seconds
		Thread.sleep(3 * 1000L);
		is2.submit(NodeActions.Crash.class, 1);
		
		// Stop first node after 3 seconds
		Thread.sleep(3 * 1000L);
		is1.submit(NodeActions.Stop.class);
	}
	
}
```

Note that Isolate takes a list of packages to be isolated from each other. It is also possible to exclude particular classes from isolation.

## IsolateTestRunner ##

IsolateTestRunner is a JUnit4 custom test runner, that runs test classes in a isolated class loader. It may be used for emulating remote Coherence clients in a single JVM during unit and integration tests.

```
/**
 * Need to run in isolated class loader, otherwise Coherence defaults 
 * are not going to be overridden by system properties
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
@RunWith(IsolateTestRunner.class)
public class RemoteStorageSchemeContextTest extends BaseSimpleContextTest {
        
        static Isolate node1;
        
        @BeforeClass
        public static void init() {

                node1 = new Isolate("node1", "com.tangosol", "org.gridkit");
                node1.start();
                node1.submit(StartCmd.class.getName());

                // TODO default property reset for tests
                System.setProperty("tangosol.coherence.wka", "localhost");
                System.setProperty("tangosol.coherence.distributed.localstorage", "false");
                context = new ClassPathXmlApplicationContext("schema/simple-coherence-context.xml");
        }

        // tests skipped

}
```

You can get complete example code [here](http://code.google.com/p/gridkit/source/browse/spring-cache-config/branches/preview/spring-integration/src/test/java/org/gridkit/coherence/integration/RemoteStorageSchemeContextTest.java?spec=svn340&r=340).

# Source code and documentation #

Both jar-files, javadocs and source code are available via maven:

```
<dependency>
	<groupId>org.gridkit.coherence-tools</groupId>
	<artifactId>test-utils</artifactId>
	<version>0.0.2</version>
	<scope>test</scope>
</dependency>
```

Don't forget to add [GridKit Maven Repository](MavenRepo.md) to your parent POM to get this dependency properly resolved.