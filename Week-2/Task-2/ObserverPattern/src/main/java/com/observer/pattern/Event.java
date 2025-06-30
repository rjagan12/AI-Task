package com.observer.pattern;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event class represents an event that can be observed by observers.
 * Contains event metadata and data.
 */
public class Event {
    private final String id;
    private final String type;
    private final String message;
    private final Object data;
    private final LocalDateTime timestamp;
    
    /**
     * Constructor for creating an event.
     * 
     * @param type the type of event
     * @param message the event message
     * @param data the event data
     */
    public Event(String type, String message, Object data) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for creating an event without data.
     * 
     * @param type the type of event
     * @param message the event message
     */
    public Event(String type, String message) {
        this(type, message, null);
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("Event{id='%s', type='%s', message='%s', timestamp=%s}", 
                           id, type, message, timestamp);
    }
} 