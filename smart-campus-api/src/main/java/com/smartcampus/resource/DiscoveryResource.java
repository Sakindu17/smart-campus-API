package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Root discovery controller.
 * Responds to GET /api/v1 with service information and navigation links.
 */
@Path("/")
public class DiscoveryResource {

    /**
     * Provides a summary of the API and its primary entry points.
     * @return JSON response containing service metadata.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceDiscovery() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("api", "Smart Campus Sensor & Room Management API");
        metadata.put("version", "1.0");
        metadata.put("contact", "admin@smartcampus.ac.uk");

        Map<String, String> endpointMap = new HashMap<>();
        endpointMap.put("rooms", "/api/v1/rooms");
        endpointMap.put("sensors", "/api/v1/sensors");
        metadata.put("resources", endpointMap);

        return Response.ok(metadata).build();
    }
}
