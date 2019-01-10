hmrctest
========
[![Build Status](https://travis-ci.org/hmrc/hmrctest.svg)](https://travis-ci.org/hmrc/hmrctest) [ ![Download](https://api.bintray.com/packages/hmrc/releases/hmrctest/images/download.svg) ](https://bintray.com/hmrc/releases/hmrctest/_latestVersion)

hmrctest is a Scala library providing some useful functionality for unit and integration tests.

In order to use the classes from the `uk.gov.hmrc.play.it` package, you need to run [smserver](https://github.com/hmrc/service-manager).

## Adding to your service

Include the one of the following dependencies in your SBT build based on whether
you are using Play 2.5 or Play 2.6

```scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "hmrctest" % "x.x.x-play-25" % "test"
or
libraryDependencies += "uk.gov.hmrc" %% "hmrctest" % "x.x.x-play-26" % "test"
```

Optionally, to make hmrctest-provided components available for dependency injection, add the following to your reference.conf:

```
play.modules.enabled += "uk.gov.hmrc.play.it.HmrcTestModule"
```

## License ##
 
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

