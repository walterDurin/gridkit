# Introduction #

Its a rare but possible situation, when data stored in Coherence cluster is lost (due to hardware fault, network partitioning, or even software bug).
Since Coherence version 3.3, there is a PARTITION\_LOST event type declared in com.tangosol.net.partition.PartitionEvent class, and corresponding PartiotionListener may be implemented. While we do not have any agruments against hooking PARTITION\_LOST event, we just do not have a trust in this. So we decided to implement an alternative solution, version-independent and 100% reliable solution here in GridDynamics Coherence team.

# Solution #

All distributed data within partitioned Coherence cache (`distributed-scheme`) is stored, replicated, and rebalanced among cluster nodes in partitions. Number of partitions is fixed (though it may be configured in cache config).
That's why one cannot lose a separate key in the cluster - minimal unit of loss is a whole partition. Partition spans through all caches create using single _partitioned cache service_, so you will loose exactly same portion of data in all these caches.

Proposed solution consists of a special marker cache, called "Canary cache" (©Alexey Ragozin), and a PartitionListener implementation, listening for partition movement events (as of Coherence 3.6.1, these include PARTITION\_LOST, PARTITION\_TRANSMIT\_ROLLBACK and PARTITION\_TRANSMIT\_COMMIT) and checking "canary cache" size. Cache contains exactly _partitionNumber_ entries, one per partition. Other said, each cache partition contains one and only one "canary cache" entry.

If some cluster nodes crash or other way become inaccessible, tearing down both primary and backup copies of particular cache partitions, these partitions are considered lost. "Canary cache" looses markers for these partitions and its size will get reduced. This solution does not depend on deployed version of Oracle Coherence, does not bring significant overhead due to asynchronous implementation, and enables custom, application-provided recovery or stopping logic.

# Implementation notes #

## Marker cache popuation ##

To get all things working, first task to do is marker cache population. Let me remind you, that it is a very special cache, that contains only one entry in each of available partitions.
One way to populate this cache is a custom KeyPartitioningStrategy, that just takes _partitionNumber_ entries with keys [0..partitionNumber-1] and returns partition number equal to the entry key. It is called CanaryKeyPartitioningStrategy. This is a possible, but rude solution - it forces application developers to change key partitioning strategy on each monitored distributed cache service, while they possibly already specified some and cannot change it just to enable consistency monitoring.
Thats why by default Coherence data loss listener uses another approach - a "brute-force" cache population algorithm. This algorithm takes (partitionNumber x 10000) iterations starting from 0, calculates partition numbers for these keys (using existing key partitioning strategy set for this cache service - either application-provided or Coherence default) and tries to find keys for ALL the partitions.
It has been tested that "brute-force" populator takes less than 30 ms to build marker cache for 257 partitions (cluster size about 100M) with Coherence default key partitioning strategy on commodity PC, and less that 150 ms for 8191 partitions (cluster size about 100G) in the same conditions. Also, it does not take a lots of iterations to accomplish the job. For example, number of iterations for default key partitioning strategy may be estimated as (N x partitionNumber), where N < 10.
This algorithm has been tested and proved its implementation with all key partitioning strategies bundled with Coherence distribution. However, if application specifies "bad" custom strategy (i.e., there are no keys that map to particular partition, or no integer key less than or equal to (partitionNumber x 10000) maps to particular partition), then this populator fails on startup with IllegalStateExceptin. As an alternative, proposal to use CanaryKeyPartitioningStrategy is written to the error log. Yes, this class is bundled with Coherence data loss listener jar as well.
Upon completion, marker cache contains partitionNumber entries in a form

> [guessed integer key -> partitionNo ](.md)

Each cluster node calculates marker cache contents on startup and holds reverse copy of this map (i.e., partition to key mapping) for later marker cache restoration after data loss.

## Asynchronous implementation ##

When PartitionListener detects partition loss event or one of transition events, it submits a check request for DataLossMonitor object, associated with this listener. Monitor encapsulates all marker cache management operations - cache construction, cache consistency checks and cache restoration after partitions loss. Lets look at these in detail.
First of all, monitor objects keeps an instance of !singleThreadExecutor - it fact, a worker thread and associated unbounded ordered queue of executable tasks. First of these tasks is marker cache population. Second is the marker cache consistency check, so check request triggered by PartitionListener just submits a new task to the worker thread.
All this asynchronous infrastructure required to avoid possible deadlocks between Coherence-Coherence and Coherence-Application code. Such deadlocks are possible when we call some Coherence partitioned service synchronously from a listener of another service and vice versa. For more information, refer to corresponding section of Coherence Developer's Guide http://download.oracle.com/docs/cd/E15357_01/coh.360/e15723/api_constraints.htm#sthref213.
As an additional benefit, single worker thread reduces performance impact of data loss checks.

## Consistency checks ##
Consistency check performs the following operations:
  * calculates canaryCache.getAll(canaryCache.keySet()).size() and compares with partitionNumber
  * if calculated number is less then partitionNumber, then lost partitions list is calculated using "canary cache" values and passed to application-provided DataLossListener to perform some application-specific logic
  * recovers "canary cache", using saved reverse map calculated during cache population and lost partitions list (so we push only lost segment, not the whole cache, and no "brute-force" calculation is needed again)

# Source code and documentation #

Both jar-files, javadocs and source code are available via maven:

```
<dependency>
    <groupId>org.gridkit.coherence-tools</groupId>
    <artifactId>coherence-dataloss-listener</artifactId>
    <version>0.9.1</version>
</dependency>
```

Don't forget to add [GridKit Maven Repository](MavenRepo.md) to your parent POM to get this dependency properly resolved.

# Resources #

Follow these links to get additional information on Coherence data loss listener configuration and usage.
  * [Configuration and usage](HowToDataLossListener.md)