aludratest
==========

## Release
We will release aludratest to repo1.maven.org in calendar week 47 2014. Until then, **you have to build the code and documentation on your own**.

##Documentation
Latest documentation is available wihtin the repository at `src/site`.
Please `git clone` the repository and perform `mvn site` to render the documentation.

After first release of aludratest, documentation of release version will be provided at [github pages](http://pages.github.io/AludraTest/aludratest).

##Build
Perform:
* `mvn compile` to build your own version
* `mvn install` to your local repository and use with your tests
* `mvn site` to render documentation
 
##Maven Dependency
Please referre to `build.pom` in root directory of repository.
* Group ID: org.aludratest
*	Artifact ID: aludratest

mvn with local build:
```
<dependency>
  <groupId>org.aludratest</groupId>
  <artifactId>aludratest</artifactId>
  <version>0.0.0-SNAPSHOT</version>
</dependency>
```
