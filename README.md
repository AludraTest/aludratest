aludratest
==========

## Build Status
[![Build Status](https://travis-ci.org/AludraTest/aludratest.svg?branch=master)](https://travis-ci.org/AludraTest/aludratest)

## Release
Version 3.0.0 is the most recent release. Please see below on how to include it e.g. in your Maven build.

## Documentation
The documentation for the most recent release is published [here](http://aludratest.github.io/aludratest/user-guide.html).

## Maven Dependency

To use Aludratest in your Maven project, add this dependency to your pom.xml:

```
<dependency>
  <groupId>org.aludratest</groupId>
  <artifactId>aludratest</artifactId>
  <version>3.0.0</version>
</dependency>
```

Please see documentation link above on how to run your first test.

## Build

To build your own copy of Aludratest, locally clone the repository.

Perform:

* `mvn compile` to build your own version
* `mvn install` to install Aludratest to your local repository and use with your tests
* `mvn site` to render documentation
