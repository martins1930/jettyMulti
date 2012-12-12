jettyMulti
==========

Gradle Jetty plugin for multi (or single) projects

```groovy
buildscript {
    repositories {
        maven {
            url uri('../repo')
        }
        mavenCentral()
    }
    dependencies {
        classpath group: 'uy.org.gradle', name: 'JettyMulti', version: '1.0-SNAPSHOT'
    }
}
apply plugin: 'jettymulti'
```
