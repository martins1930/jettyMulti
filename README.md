jettyMulti
==========

This project provides the same functionality as the Jetty Plugin: http://www.gradle.org/docs/current/userguide/jetty_plugin.html,
But JettyMulti is based on Jetty 8 and allows you to deploy multiple war in a single Jetty instance.


Features added to the jetty plugin:
* Jetty 8 embedded
* Run multiple war in a single Jetty instance
* You can utilize Servlets (in your project) with @WebServlet annotation
* Easy SSL configuration. 

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
_Note: If SSL is not needed coment jMPortSecure, jMKeyStore and jMKeyStorePassword_

3) In your project folder execute the gradle task jettyMRun: 
```shell
gradle jettyMRun
```

4) Go to browser and open the project URL: 
<code>http://localhost:8096/ProjectName/</code>
or
<code>https://localhost:8443/ProjectName/</code>

## Usage with two apps in a single Jetty instance:

Add jMDeployDeps with path to other wars to be deployed in jetty:
```groovy
jettyMulti {
    jMPort = 8096 ;
    jMScanInterval = 1 ;
    jMDeployDeps = ['/path/to/archive/App1.war', '/path/to/other/archive/App3.war']
}
```
When run the project with ```shell gradle jettyMRun ``` , 
three projects will be deployed in Jetty in the following order:
    1) App1 - http://localhost:8096/App1
    2) App2 - http://localhost:8096/App2
    3) The project itself, if the project is called Foo then you can access http://localhost:8096/Foo

## Sample Project that uses the plugin:
https://github.com/martins1930/samples/tree/master/gradle/Foop

### Search latest and greatest version deployed in Maven Central:
http://search.maven.org/#search|ga|1|a%3A%22JettyMulti%22

