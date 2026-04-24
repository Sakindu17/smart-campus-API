package com.smartcampus.exception;

/**
 * Triggered when a sensor is marked as MAINTENANCE and a user tries 
 * to post a new measurement reading.
 */
public class SensorUnavailableException extends RuntimeException {
    private final String instrumentId;

    /**
     * Initializes the exception for the unavailable sensor.
     * @param sensorId The ID of the sensor currently under maintenance.
     */
    public SensorUnavailableException(String sensorId) {
        super("Instrument " + sensorId + " is currently under MAINTENANCE; data ingestion is disabled.");
        this.instrumentId = sensorId;
    }

    public String getSensorId() { return instrumentId; }
}
