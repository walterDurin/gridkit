# Introduction #

Coherence has a very powerful cluster MBean which allows you to monitor whole cluster via JMX connection to single node. In cluster applications such ability is very desirable. While it is easy to collection statics within single node and expose it via MBean, collecting aggregated statistics from whole cluster is challenging.

That's why [GridDynamics](http://griddynamics.com) developed cluster-stats utility library to accommodate distributes statistics across all cluster nodes.

# Cluster-stats #

Library is focused on collecting precise statistic on measured values. You can collect statistics on anything you can measure (in most cases it will be time intervals). Library is not providing any tools to measure values; instead its sole goal is to aggregate statistics between JVMs in Coherence cluster.
So far we have only one implementation of aggregator which calculates:
  * sample count
  * mean
  * sum
  * std. deviation (using histogram)
  * maximum sample
  * 95, 99 and 99.9 percentiles (by sample count)

These metrics are exposed via cluster-stats JMX bean, and can also be accessed from within application via special cache (see "Implementation notes" below). Finally we have command line tool to query statistics from cluster (or reset collected statistics).

JMX bean supports several methods, such as turning profiling on and off, and resetting metrics values - either all or by mask.

# Implementation notes #

Cluster-stats processing is sampler-based, and raw data is accumulated in a special cache. Raw data is aggregated on metrics data request.
Metrics cache name is "distributed.cluster-info".

# Source code and documentation #

Both jar-files, javadocs and source code are available via maven:

```
<dependency>
    <groupId>org.gridkit.coherence-tools</groupId>
    <artifactId>cluster-stats</artifactId>
    <version>0.1.0</version>
</dependency>
```

Don't forget to add [GridKit Maven Repository](MavenRepo.md) to your parent POM to get this dependency properly resolved.

# Configuration and Usage #

To get cluster statistics working,

1. Add a maven dependency above

2. Configure Coherence statistics cache and its caching scheme.

Typical configuration may look like the following:

```
<?xml version="1.0"?>

<!DOCTYPE cache-config SYSTEM "cache-config.dtd">

<cache-config>
    <caching-scheme-mapping>

        <cache-mapping>
            <cache-name>distributed.cluster-info</cache-name>
            <scheme-name>StatisticsCacheScheme</scheme-name>
        </cache-mapping>

        ... (business caches omitted)

    </caching-scheme-mapping>
    <caching-schemes>

        <distributed-scheme>
            <scheme-name>StatisticsCacheScheme</scheme-name>
            <service-name>StatisticsCacheService</service-name>
            <backing-map-scheme>
                <local-scheme/>
            </backing-map-scheme>
            <backup-count>1</backup-count>
        </distributed-scheme>

        ... (business schemes omitted)

    </caching-schemes>
</cache-config>
```

3. Set system property "org.gridkit.coherence.profile.enabled" to "true"

4. Acquire sampler and populate it with data. This depends on profiling aims.

**Example 1.**
A typical use case - latency sampler defined to measure a particular cache gets by means of StopWatch.

Sampler
```
public class PerformanceMetrics {
	public static final Sampler CACHE_GET_SAMPLER = RuntimeStats.getLatencyTimer("cache.get", 1000);
}
```

Sampler population code
```
	private NamedCache cache;

	public Object lookup(Serializable key) {
		StopWatch sw = new StopWatch();
		Object result = cache.get(key);
		sw.stop(PerformanceMetrics.CACHE_GET_SAMPLER);

		return result;
	}

	// ...
```

After application is started and some raw data is submitted into statistics cache, it is possible to get aggregated statistics via JMX or directly from cache. Entry name is "cache.get", as defined in the above code.

**Example 2.**
More complex use case with custom sampler definition, measuring processing delays of some "records"

Sampler and usage
```
	private Sampler delaySampler = ClusterInfoService.getInstance().getHistogramService().defineSampler(
		"custom", 1L, 0L, 50000, 500);

	public void processRecords(List<Record> records) {
		for (Record record : records) {
			long delay = System.currentTimeMillis() - record.getLastUpdated();
			delaySampler.addSample(delay);
		}
	}
```