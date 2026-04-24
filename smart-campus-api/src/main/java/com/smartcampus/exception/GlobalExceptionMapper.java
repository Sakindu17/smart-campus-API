package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General-purpose error handler. Captures all otherwise unhandled exceptions.
 * Returns a standardized 500 Internal Server Error response to prevent
 * leaking implementation details or stack traces to the public.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger errLogger = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable caught) {
        // Record the incident on the server console for developers
        errLogger.log(Level.SEVERE, "The global exception handler intercepted an unmanaged error", caught);

        // Send a sanitized notification to the API consumer
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 500);
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", "Something went wrong internally. Support has been notified.");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}
