# Data loss listener usage example #

In order to get data loss listener up and running, the following steps should be performed

## Maven dependency ##

First, add a coherence-dataloss-listener maven dependency as described [here](DataLossListener#Source_code_and_documentation.md).

## Cache configuration ##

Add "canary" caches declaration to your Coherence cache configuration file, mapping them to cache services you want to monitor

```
<?xml version="1.0"?>
<!DOCTYPE cache-config SYSTEM "cache-config.dtd">

<cache-config>
	
	<caching-scheme-mapping>
	
		<cache-mapping>
			<cache-name>canary-cache-1</cache-name>
			<scheme-name>distributed-1</scheme-name>
		</cache-mapping>
		
		<cache-mapping>
			<cache-name>DataCacheA</cache-name>
			<scheme-name>distributed-1</scheme-name>
		</cache-mapping>
		
		<cache-mapping>
			<cache-name>canary-cache-2</cache-name>
			<scheme-name>distributed-2</scheme-name>
		</cache-mapping>
		
		<cache-mapping>
			<cache-name>DataCacheB</cache-name>
			<scheme-name>distributed-2</scheme-name>
		</cache-mapping>
		
	</caching-scheme-mapping>
	
	<caching-schemes>
	
		<distributed-scheme>
			<scheme-name>distributed-1</scheme-name>
			<service-name>DistributedCache-1</service-name> 
			<backing-map-scheme>
				<local-scheme/>
			</backing-map-scheme>
			<partition-listener>
				<class-name>org.gridkit.coherence.util.dataloss.PartitionLossListener</class-name>
				<init-params>
					<init-param>
						<param-type>java.lang.String</param-type>
						<param-value>canary-cache-1</param-value>
					</init-param>
					<init-param>
						<param-type>java.lang.String</param-type>
						<param-value>org.gridkit.coherence.util.dataloss.DataLossListenerImpl</param-value>
					</init-param>
				</init-params>
			</partition-listener>
			<backup-count>0</backup-count>
			<autostart>true</autostart>
		</distributed-scheme>
		
		<distributed-scheme>
			<scheme-name>distributed-2</scheme-name>
			<service-name>DistributedCache-2</service-name> 
			<backing-map-scheme>
				<local-scheme/>
			</backing-map-scheme>
			<partition-listener>
				<class-name>org.gridkit.coherence.util.dataloss.PartitionLossListener</class-name>
				<init-params>
					<init-param>
						<param-type>java.lang.String</param-type>
						<param-value>canary-cache-2</param-value>
					</init-param>
					<init-param>
						<param-type>java.lang.String</param-type>
						<param-value>org.gridkit.coherence.util.dataloss.DataLossListenerImpl</param-value>
					</init-param>
				</init-params>
			</partition-listener>
			<backup-count>0</backup-count>
			<partition-count>1017</partition-count>
			<autostart>true</autostart>
		</distributed-scheme>
		
	</caching-schemes>
	
</cache-config>
```

Note that monitored distributed scheme should get a `<partition-listener>` implementation as shown above. This listener takes two mandatory `<init-param>`s - "canary" cache name and application-provided implementation of **org.gridkit.coherence.util.dataloss.DataLossListener** interface.

These 3 changes are all you need to enable partition loss monitoring in your Coherence configuration: cache declaration, gridkit PartitionLossListener binding and custom DataLossListener implementation.

## Listener implementation ##

Application provides its own implementation of DataLossListener, and can perform node shutdown, audit and/or log message creation, or recovery attempt. onPartitionLost() method accepts Coherence Partitioned service reference and array of lost partition numbers.
Example implementation below just prints a list of lost partition numbers to standard output.

```
package my.application;

import com.tangosol.net.PartitionedService;
import org.gridkit.coherence.util.dataloss.DataLossListener;

/**
 * DataLossListener demonstration implementation
 * 
 * @author malekseev
 * 06.04.2011
 */
public class DataLossListenerImpl implements DataLossListener {
	
	@Override
	public void onPartitionLost(PartitionedService partitionedService, int[] lostPartitions) {
		
		System.out.print("Test listener got lost partitions list for service '");
		System.out.print(partitionedService.getInfo().getServiceName());
		System.out.print("':");
		
		for (int p : lostPartitions) System.out.print(" " + lostPartitions[p]);
		System.out.println();

	}

}
```