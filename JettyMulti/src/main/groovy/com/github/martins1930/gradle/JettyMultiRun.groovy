
package com.github.martins1930.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.bio.SocketConnector;
//import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector
import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.webapp.WebXmlConfiguration
import org.eclipse.jetty.webapp.WebInfConfiguration
import org.eclipse.jetty.plus.webapp.PlusConfiguration
import org.eclipse.jetty.webapp.MetaInfConfiguration
import org.eclipse.jetty.webapp.TagLibConfiguration
import org.eclipse.jetty.webapp.FragmentConfiguration
import org.eclipse.jetty.webapp.Configuration ;
import org.eclipse.jetty.plus.webapp.EnvConfiguration
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppClassLoader ;
import org.eclipse.jetty.util.resource.Resource;
import org.apache.commons.io.FileUtils ;

public class JettyMultiRun extends DefaultTask  {
    
    private static Logger logger = LoggerFactory.getLogger(JettyMultiRun.class);    
    
    String contextApp ; 
    String webappDir ;
    String classDir ;
    String resourceDir;
    String classPathApp;
    Integer scanInterval;
    Integer port;
    Integer portSecure;
    String keyStorePath;
    String keyStorePassword;
    List<String> deployDeps ;
    Boolean automaticDeps ;    
    
    @TaskAction
    public void start(){
//        http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty
// TODO :
//      add server config xml
        
        logger.info("Init jetty...");
        logger.debug("Classpath obt: {}",classPathApp) ;
        
        // SERVER ----------------------------------------------------------------
        Integer port_j = port == null ? 8080 : port ;
        Integer port_j_sec = portSecure == null ? 8443 : portSecure ;
        logger.info("port assigned: {} ",port);
        Server server = new Server();
        
        QueuedThreadPool qthp = new QueuedThreadPool();
        qthp.setMinThreads(10) ;
        qthp.setMaxThreads(200) ;
        qthp.setDetailedDump(false);
        server.setThreadPool(qthp); 
        server.setStopAtShutdown(true);
        server.setSendServerVersion(true);
        server.setSendDateHeader(true);
        server.setGracefulShutdown(1000);
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        
        List<Connector> conectorsServer = [] ;
        SocketConnector connectorHttp = new SocketConnector();
//        SelectChannelConnector connectorHttp = new SelectChannelConnector();
        connectorHttp.setPort(port_j);
        connectorHttp.setMaxIdleTime(300000);
        connectorHttp.setAcceptors(2) ;
        connectorHttp.setStatsOn(false) ;
        connectorHttp.setConfidentialPort(port_j_sec) ; 
//        connectorHttp.setLowResourcesConnections(20000);
//        connectorHttp.setLowResourcesMaxIdleTime(5000);
        conectorsServer.add(connectorHttp);
        
 
        if (!(keyStorePath==null || keyStorePath.equals(""))) {
            SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
            ssl_connector.setPort(port_j_sec);
            ssl_connector.setStatsOn(false);
            ssl_connector.setAcceptors(2) ;
            logger.info("keysotre: {}",keyStorePath);
            SslContextFactory cf = ssl_connector.getSslContextFactory();
            cf.setKeyStore(keyStorePath);
            cf.setKeyStorePassword(keyStorePassword);
            conectorsServer.add(ssl_connector);
        }
            
        server.setConnectors(conectorsServer as Connector[]);        
        
        
        String[] plusConfig = [
                    "org.eclipse.jetty.webapp.WebInfConfiguration",
                    "org.eclipse.jetty.webapp.WebXmlConfiguration",
                    "org.eclipse.jetty.webapp.MetaInfConfiguration",
                    "org.eclipse.jetty.webapp.FragmentConfiguration",
                    "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                    "org.eclipse.jetty.plus.webapp.PlusConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration",
                    "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.webapp.TagLibConfiguration"] as String[] ;
        
        server.setAttribute("org.eclipse.jetty.webapp.configuration", plusConfig);
        
        
        
        // WEB APPS TO DEPLOY ---------------------------------------------------
        
        boolean isDeployDependencies ;
        boolean containsElements = false ;
        if ( deployDeps!=null && !deployDeps.isEmpty() ) {
            isDeployDependencies = false;
            containsElements = true ;
        }
        else {
            isDeployDependencies = (automaticDeps!=null && automaticDeps) ;
        }
        logger.info("Dependencies deployed automatic?: {}", isDeployDependencies);
        
        final WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/" + contextApp);
        webapp.setLogUrlOnStart(true);
        webapp.setResourceBase(webappDir);
        webapp.setParentLoaderPriority(false);
        webapp.setCopyWebInf(true);
//        webapp.setCopyWebDir(true); 
        webapp.setExtraClasspath(classPathApp+","+resourceDir);
        createClassDir(classDir, webappDir);
        
        
//        webapp.setExtraClasspath(classPathApp+","+classDir+","+resourceDir);
        
        
//        1.WebAppClassLoader wacl = new WebAppClassLoader(webapp);
//        2.ver JettyConfiguration
//        3.probar poner los .class en la carpeta WEB-INF como si fuera un war, luego de deployado borrar la carpeta...
//        webapp.addServlet(new ServletHolder(new Hola()),"/*");
        
        
        List<WebAppContext> webDeps = [] ;
        if (containsElements) {
            deployDeps.each { nameWar -> 
                    final WebAppContext webCtxIt = new WebAppContext();
                    String vctxPath = this.extractWarName(nameWar) ;
                    webCtxIt.setContextPath("/" + vctxPath);
                    webCtxIt.setWar(nameWar);
                    webDeps.add(webCtxIt);
                    logger.info("web dep find: {},   ctxPath: {}",nameWar, vctxPath);
                
            }
        }
        
        webDeps.add(webapp) ;
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(webDeps as WebAppContext[]);
        server.setHandler(contexts);
        
        // LISTENER -------------------------------------------------------------
        Scanner scan = new Scanner();
        Integer scanInterval_j = scanInterval == null ? 1 : scanInterval ;
        scan.setScanInterval(scanInterval_j); 
        scan.setReportExistingFilesOnStartup(false);
        scan.setRecursive(true);
        List<File> listenFile = new LinkedList<>();
        listenFile.add(new File(classDir));
        if (containsElements) {
            deployDeps.each { nameWar -> 
                    listenFile.add(new File(nameWar));
                
            }            
        } 
        scan.setScanDirs(listenFile);     
        
        
        //var in listener:
        final String classDirListener = classDir ;
        final String webappDirListener = webappDir ;
        final Logger loggerListener = logger;
        Scanner.Listener listener = new Scanner.BulkListener() {

            @Override
            public void filesChanged(List<String> filenames) throws Exception {
                //part redeplpoy 
                if (filenames.size() > 1) {
                    loggerListener.info("******Restart application by listener...");
                    webapp.stop();
                    createClassDir(classDirListener, webappDirListener);
                    webapp.start();
                    Thread.sleep(1000) ; //sleep for stability of deploy
                    deleteClassDir(webappDirListener) ;
                }
                else if (filenames.size()==1) {
                    String fileChange = filenames.get(0) ;
                    if (fileChange.endsWith(".war")) {
                        loggerListener.info("******Restart dependent application by listener...");
                        WebAppContext ctxToRestart = JettyMultiRun.getContextApp(fileChange, webDeps);
                        ctxToRestart.stop();
                        ctxToRestart.start();
                    }
                    else {
                        loggerListener.warn("******Restart application by listener (filenames.size==1)??...");
//                        webapp.stop();
//                        createClassDir(classDirListener, webappDirListener);
//                        webapp.start();
//                        deleteClassDir(webappDirListener) ;
                    }
                    
                }
            }
        };
        
        scan.addListener(listener);
        scan.start();
        
        server.start();
        deleteClassDir(webappDir);
        server.join();

        scan.stop();
        server.stop();
        
        
    }
    
    public String extractWarName(String warName){
        String ret = "" ;
        
        if(warName.indexOf("/")==-1){
            ret = warName.replaceAll("\\.war", "") ;
        }
        else {
            File f = new File(warName);
            ret = f.name.replaceAll("\\.war", "") ; ;
        }
        
        
        return ret;
    }
    
    public void createClassDir(String classDir, String webappDir) {
        File dirClassesWeb = new File(webappDir+"/WEB-INF/classes") ; 
        if (dirClassesWeb.exists()){
            FileUtils.deleteDirectory(dirClassesWeb) ;
        }
        boolean dirCreated = dirClassesWeb.mkdir();
        if (dirCreated) {
            File fclassDir = new File(classDir) ; 
            FileUtils.copyDirectory(fclassDir, dirClassesWeb);        
        }
    }
    
    public void deleteClassDir(String dirName){
        File dirClassesWeb = new File(dirName+"/WEB-INF/classes") ; 
        if (dirClassesWeb.exists()){
            FileUtils.deleteDirectory(dirClassesWeb) ;
        }
    }
    
    
    public static WebAppContext getContextApp(String fileChange, List<WebAppContext> webDeps) {
        WebAppContext retCtx = null ;
        webDeps.each {
            webIter ->
                if (webIter.war?.equals(fileChange)) {
                    retCtx = webIter ;
                }
        }
        return retCtx ;
    }
    
    
    
    
}
