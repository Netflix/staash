package com.netflix.staash.embedded;

import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Embedded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatServer {
    private Embedded tomcat;
    private int port;
    private boolean isRunning;

    private static final Logger LOG = LoggerFactory.getLogger(TomcatServer.class);
    private static final boolean isInfo = LOG.isInfoEnabled();

    public TomcatServer(String contextPath, int port, String appBase, boolean shutdownHook) {
        if(contextPath == null || appBase == null || appBase.length() == 0) {
            throw new IllegalArgumentException("Context path or appbase should not be null");
        }
        if(!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }

        this.port = port;

        tomcat  = new Embedded();
        tomcat.setName("TomcatEmbeddedtomcat");

        Host localHost = tomcat.createHost("localhost", appBase);
        localHost.setAutoDeploy(false);

        StandardContext rootContext = (StandardContext) tomcat.createContext(contextPath, "webapps");
        rootContext.setDefaultWebXml("web.xml");
        localHost.addChild(rootContext);

        Engine engine = tomcat.createEngine();
        engine.setDefaultHost(localHost.getName());
        engine.setName("TomcatEngine");
        engine.addChild(localHost);

        tomcat.addEngine(engine);

        Connector connector = tomcat.createConnector(localHost.getName(), port, false);
        tomcat.addConnector(connector);

        // register shutdown hook
        if(shutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if(isRunning) {
                        if(isInfo) LOG.info("Stopping the Tomcat tomcat, through shutdown hook");
                        try {
                            if (tomcat != null) {
                                tomcat.stop();
                            }
                        } catch (LifecycleException e) {
                            LOG.error("Error while stopping the Tomcat tomcat, through shutdown hook", e);
                        }
                    }
                }
            });
        }

    }

    /**
     * Start the tomcat embedded tomcat
     */
    public void start() throws LifecycleException {
        if(isRunning) {
            LOG.warn("Tomcat tomcat is already running @ port={}; ignoring the start", port);
            return;
        }

        if(isInfo) LOG.info("Starting the Tomcat tomcat @ port={}", port);

        tomcat.setAwait(true);
        tomcat.start();
        isRunning = true;
    }

    /**
     * Stop the tomcat embedded tomcat
     */
    public void stop() throws LifecycleException {
        if(!isRunning) {
            LOG.warn("Tomcat tomcat is not running @ port={}", port);
            return;
        }

        if(isInfo) LOG.info("Stopping the Tomcat tomcat");

        tomcat.stop();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
    public static void main(String[] args) {
    	TomcatServer tomcat = new TomcatServer("staash4", 8080, "src/main", true);
    	try {
			tomcat.start();
			Thread.sleep(1000000);
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}