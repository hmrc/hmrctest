hmrctest-25
========
[![Build Status](https://travis-ci.org/hmrc/hmrctest-25.svg)](https://travis-ci.org/hmrc/hmrctest-25) [ ![Download](https://api.bintray.com/packages/hmrc/releases/hmrctest-25/images/download.svg) ](https://bintray.com/hmrc/releases/hmrctest-25/_latestVersion)

hmrctest-25 is a Scala library providing some useful functionality for unit and integration tests.

In order to use the classes from the `uk.gov.hmrc.play.it` package, you need to run [smserver](https://github.com/hmrc/service-manager).

## Adding to your service

Include the following dependency in your SBT build

```scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "hmrctest-25" % "x.x.x" % "test"
```

Optionally, to make hmrctest-25-provided components available for dependency injection, add the following to your reference.conf:

```
play.modules.enabled += "uk.gov.hmrc.play.it.HmrcTestModule"
```

## License ##
 
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
