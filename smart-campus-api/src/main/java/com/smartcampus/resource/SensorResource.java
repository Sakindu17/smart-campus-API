package com.smartcampus.resource;

import com.smartcampus.application.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Endpoint controller for Sensor management.
 * Handles primary /sensors paths and routes sub-resource calls for readings.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataPool = DataStore.getInstance();

    /**
     * Lists all instruments, with optional category filtering.
     * @param category Type of sensor to filter by (optional).
     * @return List of instruments.
     */
    @GET
    public Response listAllInstruments(@QueryParam("type") String category) {
        Collection<Sensor> instrumentCollection = dataPool.getSensors().values();
        
        if (category != null && !category.isBlank()) {
            instrumentCollection = instrumentCollection.stream()
                    .filter(i -> i.getType().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        return Response.ok(instrumentCollection).build();
    }

    /**
     * Enrolls a new sensor into the database.
     * Verifies that the associated room exists before saving.
     * @param instrument The sensor entity to create.
     * @return 201 Created or error status.
     */
    @POST
    public Response registerInstrument(Sensor instrument) {
        if (instrument.getId() == null || instrument.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorReport("Instrument ID is required")).build();
        }
        
        if (dataPool.getSensors().containsKey(instrument.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(buildErrorReport("Instrument '" + instrument.getId() + "' is already registered")).build();
        }
        
        // Ensure the room ID is valid
        if (instrument.getRoomId() == null || !dataPool.getRooms().containsKey(instrument.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", instrument.getRoomId());
        }
        
        dataPool.getSensors().put(instrument.getId(), instrument);
        
        // Establish the link between room and sensor
        dataPool.getRooms().get(instrument.getRoomId()).getSensorIds().add(instrument.getId());
        
        // Prepare storage for future readings
        dataPool.getSensorReadings().put(instrument.getId(), new ArrayList<>());
        
        return Response.status(Response.Status.CREATED).entity(instrument).build();
    }

    /**
     * Fetches details for a specific sensor.
     * @param instrumentId Unique ID of the sensor.
     * @return Instrument details or 404.
     */
    @GET
    @Path("/{sensorId}")
    public Response fetchInstrumentDetails(@PathParam("sensorId") String instrumentId) {
        Sensor instrument = dataPool.getSensors().get(instrumentId);
        if (instrument == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(buildErrorReport("Instrument not found: " + instrumentId)).build();
        }
        return Response.ok(instrument).build();
    }

    /**
     * Sub-resource routing for sensor-specific readings.
     * Delegates handling to SensorReadingResource.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource delegateToReadingHandler(@PathParam("sensorId") String instrumentId) {
        Sensor instrument = dataPool.getSensors().get(instrumentId);
        if (instrument == null) {
            throw new NotFoundException("Instrument not found: " + instrumentId);
        }
        return new SensorReadingResource(instrument);
    }

    /**
     * Internal utility for generating consistent error JSON.
     */
    private Map<String, String> buildErrorReport(String info) {
        Map<String, String> errorData = new HashMap<>();
        errorData.put("error", info);
        return errorData;
    }
}
