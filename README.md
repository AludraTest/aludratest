aludratest
==========

## Release
Until now, there is no final release of AludraTest available in Maven Central. Only a Release Candidate, 2.7.0-RC1, is released, which has known issues. We are currently finalizing the Selenium 2 support of AludraTest and will then release version 2.7.0.

##Documentation
Latest documentation is available wihtin the repository at `src/site`.
Please `git clone` the repository and perform `mvn site` to render the documentation.

Snapshots of the documentation are stored [here](http://aludratest.github.io/aludratest/user-guide.html).

##Build
Perform:
* `mvn compile` to build your own version
* `mvn install` to your local repository and use with your tests
* `mvn site` to render documentation
 
##Maven Dependency
Please refer to `pom.xml` in root directory of repository.
* Group ID: org.aludratest
*	Artifact ID: aludratest

mvn with local build:
```
<dependency>
  <groupId>org.aludratest</groupId>
  <artifactId>aludratest</artifactId>
  <version>2.7.0-SNAPSHOT</version>
</dependency>
```
