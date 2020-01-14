/*
 *  2016 Charles Syperski <csyperski@cwssoft.com> - CWS Software LLC
 */
package com.cwssoft.tinytomcat;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * This is the class to start the embedded tomcat instance
 *
 * @author csyperski
 */
public class Driver {

    private final static Logger log = Logger.getLogger(Driver.class.getName());

    private static final String ROOT = ".";
    private static final int PORT = 8080;
    private static final String[] WELCOME_FILES = new String[] {"index.html", "index.htm", "index"};

    public static void main(String[] args) throws Exception {

        log.info("Starting Tiny Tomcat...");

        if ( isHelp(args) ) {
            printHelp();
            System.exit(1);
        }

        final int port = getPort(args);
        final String webRoot = getWebRoot(args);

        log.info("Using web root: " + webRoot + " on port: " + port);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setConnector(tomcat.getConnector());
        tomcat.setAddDefaultWebXmlToWebapp(false);

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(webRoot).getAbsolutePath());


        Wrapper defaultServlet = ctx.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);

        ctx.setAddWebinfClassesResources(false);
        ctx.addChild(defaultServlet);
        ctx.addServletMappingDecoded("/", "default");

        Arrays.asList(WELCOME_FILES).stream().forEachOrdered(ctx::addWelcomeFile);

        log.info("Starting server...");
        tomcat.start();

        log.info("server started.");
        tomcat.getServer().await();
    }

    private static int getPort(String[] args) {
        if (args != null && args.length >= 2) {
            try {
                int port = Integer.parseInt(args[1]);
                if (port > 0 && port < 65535) {
                    log.info("Using port: " + port);
                    return port;
                }
            } catch (NumberFormatException nfe) {
                log.warning("NumberFormatException: " + nfe.getMessage());
            }
        }
        log.info("Using default port: " + PORT);
        return PORT;
    }

    public static String getWebRoot(String[] args) {
        if ( args != null && args.length > 0 && args[0].trim().length() > 0 ) {
            return args[0].trim();
        }
        return ROOT;
    }

    public static boolean isHelp(String[] args) {
        if ( args != null && args.length > 0 && args[0].trim().length() > 0 ) {
            String firstArg = args[0].trim().toLowerCase(Locale.US);
            return ( firstArg.equalsIgnoreCase("-h") || firstArg.equalsIgnoreCase("--help") );
        }
        return false;
    }

    public static void printHelp() {

        System.err.println("");
        System.err.println("Usage:");
        System.err.println("     java -jar tinytomcat.jar [web-root-path(default=.)] [port(default=8080)]");
        System.err.println("");

    }
}   
