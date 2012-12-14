jettyMulti
==========

This project provides the same functionality as the Jetty Plugin: http://www.gradle.org/docs/current/userguide/jetty_plugin.html,
But JettyMulti is based on Jetty 8 and allows you to deploy multiple war in a single Jetty instance.


Features added to the jetty plugin:
* Jetty 8 embedded
* Run multiple war in a single Jetty instance

Also support hot deploy of declared dependents war and the project as the Jetty Plugin:
http://www.gradle.org/docs/current/dsl/org.gradle.api.plugins.jetty.JettyRun.html


This plugin is designated for Gradle projects, the project layout that require this plugin is the same of war plugin:
http://www.gradle.org/docs/current/userguide/war_plugin.html


## Basic usage:

1) Modify build.gradle of your project and add the following lines:

```groovy
apply plugin: 'java'
apply plugin: 'war'

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.github.martins1930.gradle', name: 'JettyMulti', version: '1.0.0-RELEASE'
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
<code>http://localhost:8096/ProjectName/</code>
or
<code>https://localhost:8443/ProjectName/</code>


## Sample Project that uses the plugin
https://github.com/martins1930/samples/tree/master/gradle/Foop

## Search latest and greatest version deployed in Maven Central:
http://search.maven.org/#search|ga|1|a%3A%22JettyMulti%22

