package com.smartcampus.exception;

/**
 * Custom runtime exception thrown when a referenced entity (e.g., a Room for a Sensor) 
 * does not exist in the data store.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    private final String entityType;
    private final String entityId;

    /**
     * Initializes the exception with the type and ID of the missing resource.
     * @param type The category of the resource (e.g., "Room").
     * @param id The unique identifier that was not found.
     */
    public LinkedResourceNotFoundException(String type, String id) {
        super(type + " with ID '" + id + "' could not be located.");
        this.entityType = type;
        this.entityId = id;
    }

    public String getResourceType() { return entityType; }
    public String getResourceId() { return entityId; }
}
