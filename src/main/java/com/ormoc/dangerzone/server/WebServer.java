package com.ormoc.dangerzone.server;

import com.ormoc.dangerzone.config.DatabaseConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Embedded Jetty Web Server
 * Serves static files and REST API endpoints
 */
public class WebServer {
    
    private Server server;
    private final DatabaseConfig config;

    public WebServer() {
        this.config = DatabaseConfig.getInstance();
    }

    public void start() throws Exception {
        int port = config.getServerPort();
        server = new Server(port);

        // Create handlers
        HandlerCollection handlers = new HandlerCollection();

        // Static resource handler for webapp files
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        
        // Try to find webapp resources
        String webappPath = getClass().getClassLoader()
            .getResource("webapp")
            .toExternalForm();
        resourceHandler.setResourceBase(webappPath);

        // Servlet context handler for REST API
        ServletContextHandler servletHandler = new ServletContextHandler(
            ServletContextHandler.SESSIONS
        );
        servletHandler.setContextPath("/");
        
        // Register API servlet
        ServletHolder apiHolder = new ServletHolder(new ApiServlet());
        servletHandler.addServlet(apiHolder, "/api/*");

        // Add handlers
        handlers.addHandler(resourceHandler);
        handlers.addHandler(servletHandler);

        server.setHandler(handlers);
        server.start();
    }

    public void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public boolean isRunning() {
        return server != null && server.isRunning();
    }
}