jettyMulti
==========

Gradle Jetty plugin for multi (or single) projects.
This plugin utilizes Jetty 8. 

This plugin is designated for Gradle projects, the project layout that require this plugin is the same of war plugin:
http://www.gradle.org/docs/current/userguide/war_plugin.html

### Basic usage:

1) Modify build.gradle of your project and add the following lines:

```groovy
apply plugin: 'java'
apply plugin: 'war'

buildscript {
    repositories {
        maven {
            url uri('../repo')
        }
        mavenCentral()
    }
    dependencies {
        classpath group: 'uy.com.github.martins1930.gradle', name: 'JettyMulti', version: '1.0'
    }
}
apply plugin: 'jettymulti'
```

2) Configure the ports :

```groovy
jettyMulti {
    jMPort = 8096 ;
    jMPortSecure = 8443 ;
    jMKeyStore = "/opt/keystore/jetty.keystore" ;
    jMKeyStorePassword = "secret" ;
    jMScanInterval = 1 ;
}
```

3) In your project folder execute the gradle task jettyMRun: 
```shell
gradle jettyMRun
```

4) Go to browser and open the project URL: 
http://localhost:8096/ProjectName/
or
http://localhost:8443/ProjectName/

