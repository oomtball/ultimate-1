# Usage

To use the latest release of the ADD-Lib in your Maven project, simply include the following dependency in your `pom.xml`.

```xml
<dependencies>
    ...
    <dependency>
        <groupId>info.scce</groupId>
        <artifactId>addlib</artifactId>
        <version>2.0.0-BETA</version>
        <packaging>pom</packaging>
    </dependency>
</dependencies>
```

# Build Instructions

The build process of ADD-Lib requires a running docker daemon, as platform specific images will be pulled to build the native backends used in ADD-Lib.

Furthermore, in order execute all tests, the following programs need to be installed:

* Graphviz (DOT specifically)
* Mono
* NPM
* GCC
