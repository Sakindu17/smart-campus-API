package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical space within the campus.
 * Contains metadata about the room and a list of associated sensor IDs.
 */
public class Room {

    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();

    /**
     * Default constructor for JSON deserialization.
     */
    public Room() {}

    /**
     * Full constructor for manual Room instantiation.
     * @param roomId Unique identifier for the room.
     * @param roomName Descriptive name of the room.
     * @param maxCapacity Maximum occupancy of the room.
     */
    public Room(String roomId, String roomName, int maxCapacity) {
        this.id = roomId;
        this.name = roomName;
        this.capacity = maxCapacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}
