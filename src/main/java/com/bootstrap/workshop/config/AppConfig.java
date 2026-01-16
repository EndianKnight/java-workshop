package com.bootstrap.workshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application configuration properties.
 * Binds to properties prefixed with "app" in application.properties/yaml.
 * 
 * Example:
 * app.name=workshop
 * app.description=Workshop API Server
 * app.server.port=8080
 */
@ConfigurationProperties(prefix = "app")
public record AppConfig(
        String name,
        String description,
        String version,
        Server server) {
    public record Server(
            int port,
            String contextPath,
            int connectionTimeout,
            int maxConnections) {
        public Server {
            // Default values
            port = port > 0 ? port : 8080;
            contextPath = contextPath != null ? contextPath : "/";
            connectionTimeout = connectionTimeout > 0 ? connectionTimeout : 60000;
            maxConnections = maxConnections > 0 ? maxConnections : 10000;
        }
    }

    public AppConfig {
        // Default values
        name = name != null ? name : "workshop";
        description = description != null ? description : "Workshop Application";
        version = version != null ? version : "1.0.0";
        server = server != null ? server : new Server(8080, "/", 60000, 10000);
    }
}
