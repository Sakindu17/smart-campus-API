package com.smartcampus.application;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized in-memory storage for the application.
 * Utilizes ConcurrentHashMap to ensure thread-safe operations across 
 * simultaneous data access and modification requests.
 */
public class DataStore {

    private static final DataStore sharedStore = new DataStore();

    private final Map<String, Room> roomRegistry = new ConcurrentHashMap<>();
    private final Map<String, Sensor> instrumentMap = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> dataLog = new ConcurrentHashMap<>();

    private DataStore() {
        // Initialize with default demonstration data
        Room libraryRoom = new Room("LIB-301", "Library Quiet Study", 50);
        Room computerLab = new Room("LAB-101", "Computer Lab A", 30);
        
        roomRegistry.put(libraryRoom.getId(), libraryRoom);
        roomRegistry.put(computerLab.getId(), computerLab);

        Sensor tempSensor = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        instrumentMap.put(tempSensor.getId(), tempSensor);
        
        libraryRoom.getSensorIds().add(tempSensor.getId());
        dataLog.put(tempSensor.getId(), new ArrayList<>());
    }

    public static DataStore getInstance() {
        return sharedStore;
    }

    public Map<String, Room> getRooms() { 
        return roomRegistry; 
    }

    public Map<String, Sensor> getSensors() { 
        return instrumentMap; 
    }

    public Map<String, List<SensorReading>> getSensorReadings() { 
        return dataLog; 
    }
}
