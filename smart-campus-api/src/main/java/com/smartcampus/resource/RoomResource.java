package com.smartcampus.resource;

import com.smartcampus.application.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing Room entities.
 * Processes all requests under the /rooms path.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore persistence = DataStore.getInstance();

    /**
     * Retrieves a list of all registered rooms.
     * @return 200 OK with the collection of rooms.
     */
    @GET
    public Response fetchAllRooms() {
        Collection<Room> allRooms = persistence.getRooms().values();
        return Response.ok(allRooms).build();
    }

    /**
     * Registers a new room in the system.
     * @param roomObject The room data to save.
     * @return 201 Created or error response.
     */
    @POST
    public Response createNewRoom(Room roomObject) {
        if (roomObject.getId() == null || roomObject.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(formatErrorResponse("A valid Room ID is mandatory")).build();
        }
        
        if (persistence.getRooms().containsKey(roomObject.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(formatErrorResponse("A room with ID '" + roomObject.getId() + "' already exists")).build();
        }
        
        persistence.getRooms().put(roomObject.getId(), roomObject);
        return Response.status(Response.Status.CREATED).entity(roomObject).build();
    }

    /**
     * Finds a specific room by its unique identifier.
     * @param roomId The ID of the room to locate.
     * @return 200 OK or 404 Not Found.
     */
    @GET
    @Path("/{roomId}")
    public Response findRoomById(@PathParam("roomId") String roomId) {
        Room foundRoom = persistence.getRooms().get(roomId);
        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(formatErrorResponse("Could not locate room with ID: " + roomId)).build();
        }
        return Response.ok(foundRoom).build();
    }

    /**
     * Removes a room from the registry. 
     * Blocked if the room still has associated sensors.
     * @param roomId The ID of the room to delete.
     * @return 204 No Content or error response.
     */
    @DELETE
    @Path("/{roomId}")
    public Response removeRoom(@PathParam("roomId") String roomId) {
        Room targetRoom = persistence.getRooms().get(roomId);
        
        if (targetRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(formatErrorResponse("Room ID not found for deletion: " + roomId)).build();
        }
        
        if (!targetRoom.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, targetRoom.getSensorIds().size());
        }
        
        persistence.getRooms().remove(roomId);
        return Response.noContent().build();
    }

    /**
     * Helper to wrap error messages in a JSON-compatible map.
     */
    private Map<String, String> formatErrorResponse(String detail) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", detail);
        return errorMap;
    }
}
