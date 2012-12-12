package uy.com.github.martins1930.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.artifacts.ProjectDependency

public class JettyMultiPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(WarPlugin.class);
        project.extensions.create("jettyMulti", JettyMultiExtension);
        
        
        String proyName = project.name;
        String webappDirName = project.webAppDir;
        String classDirName = project.sourceSets.main.output.classesDir.absolutePath
        String resourceDirName = project.sourceSets.main.output.resourcesDir.absolutePath

        
        project.task('jettyMRun', type: JettyMultiRun){   
            doFirst {
                classPathApp = project.configurations.runtime.collect { it.absolutePath }.join(',')  ;
                scanInterval = project.jettyMulti.jMScanInterval ;
                port         = project.jettyMulti.jMPort;        
                portSecure   = project.jettyMulti.jMPortSecure;        
                keyStorePath = project.jettyMulti.jMKeyStore;        
                keyStorePassword = project.jettyMulti.jMKeyStorePassword;        
                deployDeps       = project.jettyMulti.jMDeployDeps;        
                automaticDeps    = project.jettyMulti.jMAutomaticDeps;    
//                project.parent?.allprojects.each {
//                    itProy -> 
//                        if (itProy.metaClass.hasProperty(itProy, 'war')) {
//                            println "Find War proy: ${itProy.war?.archivePath}" ;
//                        }
//                }
//                project.configurations.runtime.
//                project.configurations.collectMany { it.allDependencies }
//                                        .findAll { it instanceof ProjectDependency }
//                                        .each { println "Dependency Find: ${it.name}" }
            }
            dependsOn = ['build']
            description = "Task to run exploded war with jetty"
            group = "jettyMulti"
            contextApp = proyName;   
            webappDir = webappDirName;
            classDir = classDirName;
            resourceDir = resourceDirName;
        }
        
        
    }
    
}
