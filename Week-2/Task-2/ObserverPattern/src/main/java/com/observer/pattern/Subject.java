package com.observer.pattern;

/**
 * Subject interface defines the contract for managing observers.
 * This is the core of the Observer Pattern that allows objects to subscribe
 * and unsubscribe from notifications.
 */
public interface Subject {
    
    /**
     * Registers an observer to receive notifications.
     * 
     * @param observer the observer to register
     */
    void registerObserver(Observer observer);
    
    /**
     * Removes an observer from the notification list.
     * 
     * @param observer the observer to remove
     */
    void removeObserver(Observer observer);
    
    /**
     * Notifies all registered observers about an event.
     * 
     * @param event the event to notify observers about
     */
    void notifyObservers(Event event);
} 