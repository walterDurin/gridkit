# Coherence `cache-config.xml` configuration options support #

## `<local-scheme>` ##
  * `<local-scheme><service-name>` - TODO
  * `<local-scheme><listener>` - TODO
  * `<local-scheme><cachestore-scheme` - TODO
  * `<local-scheme><eviction-policy>`
    * HYBRID - **Done**
    * LRU - **Done**
    * LFU - **Done**
    * custom policy - **Done**, Spring bean suport
  * `<local-scheme><high-units>` - **Done**
  * `<local-scheme><low-units>` - **Done**
  * `<local-scheme><unit-calculator>` - **Done**, Spring bean support
  * `<local-scheme><unit-factor>` - TODO
  * `<local-scheme><expiry-delay>` - **Done**, improvement pending
  * `<local-scheme><flush-delay>` - **Done**, improvement pending

## `<external-scheme>` ##
TODO

## `<page-external-scheme>` ##
TODO

## `<distributed-scheme>` ##
  * `<distributed-scheme><service-name>` - **Done**
  * `<distributed-scheme><serializer>` - **Done**, Spring bean support
  * `<distributed-scheme><listener>` - **Done**
  * `<distributed-scheme><backing-map-scheme>` - **Done**, Spring bean support
    * `<distributed-scheme><backing-map-scheme><partitioned>` - TODO
  * `<distributed-scheme><partition-count>` - **Done**
  * `<distributed-scheme><key-associator>` - **Done**, Spring bean support
  * `<distributed-scheme><key-partitioning>` - **Done**, Spring bean support
  * `<distributed-scheme><partition-listener>` - **Done**, Spring bean support, improvement pending
  * `<distributed-scheme><backup-count>` - **Done**
  * `<distributed-scheme><backup-count-after-writebehind>` - **Done**
  * `<distributed-scheme><backup-storage>` - TODO, broken on Coherence side
  * `<distributed-scheme><thread-count>` - **Done**
  * `<distributed-scheme><lease-granularity>` - **Done**
  * `<distributed-scheme><transfer-threshold>` - **Done**
  * `<distributed-scheme><local-storage>` - **Done**
  * `<distributed-scheme><autostart>` - **Done**
  * `<distributed-scheme><task-hung-threshold>` - **Done**
  * `<distributed-scheme><task-timeout>` - **Done**
  * `<distributed-scheme><request-timeout>` - **Done**
  * `<distributed-scheme><guardian-timeout>` - TODO
  * `<distributed-scheme><service-failure-policy>` - TODO
  * `<distributed-scheme><member-listener>` - TODO
  * {{{

&lt;distributed-scheme&gt;



&lt;partitioned-quorum-policy-scheme&gt;

}} - TODO
  * `<distributed-scheme><operation-bundling>` - TODO

## `<transactional-scheme>` ##
TODO

## `<replicated-scheme>` ##
  * `<replicated-scheme><service-name>` - **Done**
  * `<replicated-scheme><serializer>` - **Done**, Spring bean support
  * `<replicated-scheme><listener>` - **Done**
  * `<replicated-scheme><backing-map-scheme>` - **Done**, Spring bean support
  * `<replicated-scheme><standard-lease-milliseconds>` - **Done**
  * `<replicated-scheme><lease-granularity>` - **Done**
  * `<replicated-scheme><mobile-issues>` - **Done**
  * `<replicated-scheme><guardian-timeout>` - TODO
  * `<replicated-scheme><service-failure-policy>` - TODO
  * `<replicated-scheme><autostart>` - **Done**
  * `<replicated-scheme><member-listener>` - **Done**

## `<optimistic-scheme>` ##
  * `<optimistic-scheme><service-name>` - **Done**
  * `<optimistic-scheme><serializer>` - **Done**, Spring bean support
  * `<optimistic-scheme><listener>` - TODO
  * `<optimistic-scheme><backing-map-scheme>` - **Done**, Spring bean support
  * `<optimistic-scheme><guardian-timeout>` - TODO
  * `<optimistic-scheme><service-failure-policy>` - TODO
  * `<optimistic-scheme><autostart>` - **Done**
  * `<optimistic-scheme><member-listener>` - **Done**

## `<near-scheme>` ##
  * `<near-scheme><class-name>` - **Done**, Spring bean support
  * `<near-scheme><listener>` - TODO
  * `<near-scheme><front-scheme>` - **Done**, Spring bean support
  * `<near-scheme><back-scheme>` - **Done**, Spring bean support
  * `<near-scheme><invalidation-strategy>` - **Done**
  * `<near-scheme><autostart>` - doesn't make sense, not a service

## `<versioned-near-scheme>` ##
Will not support

## `<overflow-scheme>` ##
TODO

## `<invocation-scheme>` ##
  * `<invocation-scheme><serializer>` - **Done**, Spring bean support
  * `<invocation-scheme><thread-count>` - **Done**
  * `<invocation-scheme><autostart>` - **Done**
  * `<invocation-scheme><task-hung-threshold>` - **Done**
  * `<invocation-scheme><task-timeout>` - **Done**
  * `<invocation-scheme><request-timeout>` - **Done**
  * `<invocation-scheme><guardian-timeout>` - TODO
  * `<invocation-scheme><service-failure-policy>`
  * `<invocation-scheme><member-listener>`

## `<proxy-scheme>` ##
  * `<proxy-scheme><service-name>` -
  * `<proxy-scheme><task-hung-threshold>` -
  * `<proxy-scheme><task-timeout>` -
  * `<proxy-scheme><request-timeout>` -
  * `<proxy-scheme><thread-count>` -
  * `<proxy-scheme><acceptor-config>`
    * `<proxy-scheme><acceptor-config><connection-limit>`
    * `<proxy-scheme><acceptor-config><outgoing-message-handler>`
      * `<proxy-scheme><acceptor-config><outgoing-message-handler><heartbeat-interval>`
      * `<proxy-scheme><acceptor-config><outgoing-message-handler><heartbeat-timeout>`
      * `<proxy-scheme><acceptor-config><outgoing-message-handler><request-timeout>`
    * `<proxy-scheme><acceptor-config><serializer>`
    * `<proxy-scheme><acceptor-config><tcp-acceptor>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><local-address>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><address-provider>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><socket-provider>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><reuse-address>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><keep-alive-enabled>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><tcp-delay-enabled>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><receive-buffer-size>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><send-buffer-size>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><listen-backlog>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><linger-timeout>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><authorized-hosts>`
        * `<proxy-scheme><acceptor-config><tcp-acceptor><authorized-hosts><host-address>`
        * `<proxy-scheme><acceptor-config><tcp-acceptor><authorized-hosts><host-range>`
        * `<proxy-scheme><acceptor-config><tcp-acceptor><authorized-hosts><host-filter>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><suspect-protocol-enabled>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><suspect-buffer-size>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><suspect-buffer-length>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><nominal-buffer-size>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><nominal-buffer-length>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><limit-buffer-size>`
      * `<proxy-scheme><acceptor-config><tcp-acceptor><limit-buffer-length>`
    * `<proxy-scheme><acceptor-config><jms-acceptor>` - has been depricated
    * `<proxy-scheme><acceptor-config><use-filters>`
      * `<proxy-scheme><acceptor-config><use-filters><filter-name>`
  * `<proxy-scheme><proxy-config>` -
    * `<proxy-scheme><proxy-config><cache-service-proxy>` -
      * `<proxy-scheme><proxy-config><cache-service-proxy><enabled>` -
      * `<proxy-scheme><proxy-config><cache-service-proxy><lock-enabled>` -
      * `<proxy-scheme><proxy-config><cache-service-proxy><read-only>` -
      * `<proxy-scheme><proxy-config><cache-service-proxy><class-name>` -
      * `<proxy-scheme><proxy-config><cache-service-proxy><init-params>` -
    * `<proxy-scheme><proxy-config><invocation-service-proxy>` -
      * `<proxy-scheme><proxy-config><invocation-service-proxy><enabled>` -
      * `<proxy-scheme><proxy-config><invocation-service-proxy><class-name>` -
      * `<proxy-scheme><proxy-config><invocation-service-proxy><init-params>` -
  * `<proxy-scheme><autostart>` -
  * `<proxy-scheme><guardian-timeout>` -
  * `<proxy-scheme><service-failure-policy>` -
  * `<proxy-scheme><member-listener>` -
  * `<proxy-scheme><proxy-quorum-policy-scheme>` -


## `<read-write-backing-map-scheme>` ##
  * `<read-write-backing-map-scheme><listener>` - TODO
  * `<read-write-backing-map-scheme><cachestore-scheme>` - **Done**, Spring bean support
  * `<read-write-backing-map-scheme><cachestore-timeout>` - **Done**
  * `<read-write-backing-map-scheme><internal-cache-scheme>` - **Done**, Spring bean support
  * `<read-write-backing-map-scheme><miss-cache-scheme>` - **Done**, Spring bean support
  * `<read-write-backing-map-scheme><read-only>` - **Done**
  * `<read-write-backing-map-scheme><write-delay>` - **Done**, improvement pending
  * `<read-write-backing-map-scheme><write-batch-factor>` - **Done**
  * `<read-write-backing-map-scheme><write-requeue-threshold>` - **Done**
  * `<read-write-backing-map-scheme><refresh-ahead-factor>` - **Done**
  * `<read-write-backing-map-scheme><rollback-cachestore-failures>` - TODO


## `<versioned-backing-map-scheme>` ##
Will not support

## `<remote-cache-scheme>` ##
TODO

## `<class-scheme>` :) ##
Spring IoC power unleashed

## `<disk-scheme>` ##
Will not support


## Enhanced options ##
  * Configure map indexes - TODO
  * Continious query front tier - TODO

# Non configuration features #
  * JMX registration - TODO
  * Class loader dispatch semantic - TODO