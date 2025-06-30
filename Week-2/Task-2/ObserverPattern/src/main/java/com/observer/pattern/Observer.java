package com.observer.pattern;

/**
 * Observer interface defines the contract for objects that want to be notified
 * when events occur. This follows the Observer Pattern's observer side.
 */
public interface Observer {
    
    /**
     * Called by the subject when an event occurs.
     * 
     * @param event the event that occurred
     */
    void update(Event event);
    
    /**
     * Returns the unique identifier for this observer.
     * 
     * @return the observer's identifier
     */
    String getObserverId();
} 