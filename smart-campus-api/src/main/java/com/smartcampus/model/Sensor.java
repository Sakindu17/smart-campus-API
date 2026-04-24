package com.smartcampus.model;

/**
 * Data model for a Sensor instrument.
 * Tracks the current state, type, and location of the device.
 */
public class Sensor {

    private String id;
    private String type;
    private String status; // Possible values: ACTIVE, MAINTENANCE, OFFLINE
    private double currentValue;
    private String roomId;

    /**
     * No-args constructor for JSON binding.
     */
    public Sensor() {}

    /**
     * Constructs a new Sensor with the specified details.
     * @param instrumentId The unique device ID.
     * @param sensorType The category (e.g., Temperature, CO2).
     * @param operationalStatus Current working state of the device.
     * @param initialValue The most recent reading.
     * @param locationRoomId The ID of the room where it is installed.
     */
    public Sensor(String instrumentId, String sensorType, String operationalStatus, double initialValue, String locationRoomId) {
        this.id = instrumentId;
        this.type = sensorType;
        this.status = operationalStatus;
        this.currentValue = initialValue;
        this.roomId = locationRoomId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
