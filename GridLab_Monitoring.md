

While, performance benchmark usually producing some measures by itself. Monitoring system under test is very important to understand reasons behind result being measured.

Combining test specif measures with system monitoring together is one of key features for GridLab.

# Stock monitoring capabilities #

Out of box we have bundles for monitoring:
  * System monitoring
    * Process CPU usage (using Sigar library)
    * Host network usage (using Sigar library)
  * Coherence monitoring
    * Thread monitoring (services thread, workers, network threads)
    * Extend bandwidth monitoring (using Coherence JMX)
  * [BTrace](http://kenai.com/projects/btrace)
    * Custom BTrace script cloud be loaded, BTrace extension is provided to collect data and report BTrace script.

# Monitoring targets #

Common way practice is to start monitoring agent on each host being monitored (all out-of-box monitoring work that way).

Process and JVM monitoring is collection data per individual process. There is 2 out-of-box way for selecting process to be monitored:
  * By process execution line using Sigar's query syntax
  * For JVM processes, by combination of system properties (JVM 1.6 and above).

Groups of processes could be monitored and reported independently.

# JMX based monitoring #

Usually, agents access JMX of monitored processes using Java 1.6 attach API which is very handy with combination of system property based filtering monitoring targets.
RMI based JMX monitoring is also supported out of box (could be handy is agent cannot be started on target host for any reason).