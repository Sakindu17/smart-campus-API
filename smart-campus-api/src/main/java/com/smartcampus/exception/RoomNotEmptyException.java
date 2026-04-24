package com.smartcampus.exception;

/**
 * Exception raised when attempting to delete a room that still has sensors linked to it.
 * Enforces data integrity by preventing orphan sensors.
 */
public class RoomNotEmptyException extends RuntimeException {
    private final String identifier;
    private final int count;

    /**
     * Constructs the exception with room details.
     * @param roomId The ID of the room being deleted.
     * @param totalSensors The number of sensors still in that room.
     */
    public RoomNotEmptyException(String roomId, int totalSensors) {
        super("Cannot delete room " + roomId + " because it still contains " + totalSensors + " sensor(s).");
        this.identifier = roomId;
        this.count = totalSensors;
    }

    public String getRoomId() { return identifier; }
    public int getSensorCount() { return count; }
}
