# quarkusIVOA

These are a set of libraries that provide a framework for implementing IVOA protocols in Quarkus. The libraries implement the quarkus specific parts of ivoa protocols and build on the framework agnosic [ivoacore](https://github.com/Javastro/ivoacore) libraries. 


[![Java CI with Gradle](https://github.com/Javastro/quarkusIVOA/actions/workflows/gradle.yml/badge.svg)](https://github.com/Javastro/quarkusIVOA/actions/workflows/gradle.yml)

[![Publish To Repository](https://github.com/Javastro/quarkusIVOA/actions/workflows/publish.yml/badge.svg)](https://github.com/Javastro/quarkusIVOA/actions/workflows/publish.yml)

## Versions

```kotlin
implementation("org.javastro.ivoa.core.quarkus:quarkus-tap-lib:0.9.1")
implementation("org.javastro.ivoa.core.quarkus:quarkus-config:0.1.1")
implementation("org.javastro.ivoa.core.quarkus:quarkus-aai:0.1.0")

```

## Notes

### quarkus-tap-lib

when using the tap library you will need to add the following to your `application.properties` so that quarkus can see the annotations in the library at build time, and resolve the library elements properly.
```properties
# quarkus-tap-lib contains base JAX-RS/CDI resource classes with interceptor annotations
# such as @Transactional. Index it so Quarkus can see those annotations at build time.
quarkus.index-dependency.quarkus-tap-lib.group-id=org.javastro.ivoa.core.quarkus
quarkus.index-dependency.quarkus-tap-lib.artifact-id=quarkus-tap-lib

```