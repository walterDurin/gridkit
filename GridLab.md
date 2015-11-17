# API preview warning #

---

> _All GridLab components at API preview status. While we are actively using these stuff across projects, a lot of things is going to change as we are sorting out ergonomics etc. In particular a lot of package rebranding and module restructuring are pending._

---


# Introduction #

Developing of distributed  software (i.e. mass parallel and clustered) is a quite challenging. In particular there is a lack of tools to cover
  * Test automation for distributed applications
  * Distributed performance and stress testing
  * Collecting and process comprehensive runtime metering from for distributed tests (experiment)

Collecting and analyzing metrics from rich suite of performance test is a key for developing performance critical system.

Unfortunately, very often, monitoring means keep console with top open during test and distributed test means fire dozen of shell command across number of consoles.

In my carrier I have write performance test automation countless number of times, just to start this work over again in next project.

But finally, quantity have started to converge into quality and here under 'GridLab' umbrella with have few solution to alienate these long standing pains.

## GridLab components ##
  * NanoCloud - hurdle free, java centric library for distributed execution
  * A simple solution for orchestrating distributed execution scenarios
  * Metering processing library - it allows to aggregate and format metering produced from experiment in meaningful way (replacing typical spreadsheet step)
  * Monitoring components - an easy way to complement your application metrics with various monitoring data (e.g. CPU usage, network usage, JMX based data etc)

Together this stack allows to automate a huge part of testing.

GridLab's main goal - make end-to-end automated test runs possible even for complex distributed applications.

**TODO: work in progress**