hmrctest
========
[![Build Status](https://travis-ci.org/hmrc/hmrctest.svg)](https://travis-ci.org/hmrc/hmrctest) [ ![Download](https://api.bintray.com/packages/hmrc/releases/hmrctest/images/download.svg) ](https://bintray.com/hmrc/releases/hmrctest/_latestVersion)

hmrctest is a Scala library providing some useful functionality for unit and integration tests.

In order to use the classes from the `uk.gov.hmrc.play.it` package, you need to run [smserver](https://github.com/hmrc/service-manager).

## Adding to your service

Include the following dependency in your SBT build

```scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "hmrctest" % "x.x.x" % "test"
```
