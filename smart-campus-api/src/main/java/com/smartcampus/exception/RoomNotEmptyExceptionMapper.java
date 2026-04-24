package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps RoomNotEmptyException to a 409 Conflict HTTP response.
 * Alerts the user that data constraints prevent the deletion of the room.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("status", 409);
        errorPayload.put("error", "Conflict");
        errorPayload.put("message", "The room '" + exception.getRoomId() + 
                "' cannot be removed while it contains " + exception.getSensorCount() + 
                " assigned sensors. Clear the sensors before deleting the room.");
        
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorPayload)
                .build();
    }
}
