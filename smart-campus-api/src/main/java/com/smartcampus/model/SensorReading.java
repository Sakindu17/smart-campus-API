package com.smartcampus.model;

import java.util.UUID;

/**
 * Represents a single measurement captured by a sensor.
 * Automatically generates a unique ID and timestamp upon creation.
 */
public class SensorReading {

    private String id;
    private long timestamp;
    private double value;

    /**
     * Empty constructor for framework use.
     */
    public SensorReading() {}

    /**
     * Creates a new measurement entry.
     * @param readingValue The numerical data point recorded by the device.
     */
    public SensorReading(double readingValue) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = readingValue;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
