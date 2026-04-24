package com.smartcampus.resource;

import com.smartcampus.application.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sub-resource handler for instrument measurement data.
 * Manages operations at /api/v1/sensors/{sensorId}/readings.
 * Object creation is triggered by the parent SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final Sensor targetInstrument;
    private final DataStore dataPool = DataStore.getInstance();

    public SensorReadingResource(Sensor instrument) {
        this.targetInstrument = instrument;
    }

    /**
     * Retrieves the complete history of measurements for this instrument.
     * @return 200 OK with the list of readings.
     */
    @GET
    public Response fetchMeasurementHistory() {
        List<SensorReading> dataHistory = dataPool.getSensorReadings()
                .getOrDefault(targetInstrument.getId(), List.of());
        return Response.ok(dataHistory).build();
    }

    /**
     * Submits a new measurement for the instrument.
     * Rejects updates if the instrument is currently under maintenance.
     * @param incomingData The reading to record.
     * @return 201 Created or error status.
     */
    @POST
    public Response recordNewMeasurement(SensorReading incomingData) {
        // Validation: Block data ingestion if the instrument status is MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(targetInstrument.getStatus())) {
            throw new SensorUnavailableException(targetInstrument.getId());
        }

        SensorReading entry = new SensorReading(incomingData.getValue());
        dataPool.getSensorReadings()
                .computeIfAbsent(targetInstrument.getId(), key -> new java.util.ArrayList<>())
                .add(entry);

        // Update the sensor's current state to reflect the latest reading
        targetInstrument.setCurrentValue(entry.getValue());

        return Response.status(Response.Status.CREATED).entity(entry).build();
    }

    /**
     * Standardized JSON error response generator.
     */
    private Map<String, String> generateErrorMap(String msg) {
        Map<String, String> failureObj = new HashMap<>();
        failureObj.put("error", msg);
        return failureObj;
    }
}
