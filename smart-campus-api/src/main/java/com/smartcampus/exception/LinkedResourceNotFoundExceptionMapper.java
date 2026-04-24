package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Translator for LinkedResourceNotFoundException.
 * Converts the exception into a 422 Unprocessable Entity HTTP response.
 * This indicates that while the syntax is correct, the reference (like roomId) is invalid.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 422);
        errorDetails.put("error", "Unprocessable Entity");
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("field", exception.getResourceType().toLowerCase() + "Id");
        
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}
