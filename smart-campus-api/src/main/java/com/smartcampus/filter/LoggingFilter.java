package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Observability middleware for traffic monitoring.
 * Intercepts all inbound requests and outbound responses to log 
 * metadata like HTTP methods, URIs, and status codes.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger trafficLogger = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext inbound) throws IOException {
        trafficLogger.info(String.format("<<< INBOUND:  %s %s",
                inbound.getMethod(),
                inbound.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext inbound,
                       ContainerResponseContext outbound) throws IOException {
        trafficLogger.info(String.format(">>> OUTBOUND: %s %s | STATUS: %d",
                inbound.getMethod(),
                inbound.getUriInfo().getRequestUri(),
                outbound.getStatus()));
    }
}
