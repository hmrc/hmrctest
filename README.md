hmrctest
========

hmrctest is a Scala library providing some useful functionality for unit and integration tests.

In order to use the classes from the `uk.gov.hmrc.play.it` package, you need to run [smserver](https://github.com/hmrc/service-manager).

## Adding to your service

Include the following dependency in your SBT build

```scala
libraryDependencies += "uk.gov.hmrc" %% "hmrctest" % "1.0.0" % "test"
```
