package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Translator for SensorUnavailableException.
 * Produces a 403 Forbidden HTTP response when sensors are locked for maintenance.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("status", 403);
        errorInfo.put("error", "Forbidden");
        errorInfo.put("message", exception.getMessage());
        errorInfo.put("sensorId", exception.getSensorId());
        
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorInfo)
                .build();
    }
}
