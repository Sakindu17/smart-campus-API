package com.smartcampus.application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * Entry point for the Smart Campus Backend.
 * Responsible for configuring and launching the Grizzly HTTP server.
 */
public class Main {

    private static final Logger sysLogger = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8080/api/v1";

    /**
     * Initializes and configures the HTTP server instance.
     * @return A running HttpServer instance.
     */
    public static HttpServer launchServer() {
        final ResourceConfig config = new ResourceConfig().packages("com.smartcampus");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer activeServer = launchServer();
        
        sysLogger.info("Backend service is now operational at: " + BASE_URI);
        sysLogger.info("To shut down the server, press the ENTER key...");
        
        System.in.read();
        activeServer.stop();
    }
}
