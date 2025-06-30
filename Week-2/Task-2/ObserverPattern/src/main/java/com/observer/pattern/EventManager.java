package com.observer.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * EventManager is the concrete implementation of Subject interface.
 * It manages a list of observers and notifies them when events occur.
 * Uses thread-safe collection for concurrent access.
 */
public class EventManager implements Subject {
    
    private final List<Observer> observers;
    private final String managerId;
    
    /**
     * Constructor for EventManager.
     * 
     * @param managerId unique identifier for this event manager
     */
    public EventManager(String managerId) {
        this.managerId = managerId;
        // Using CopyOnWriteArrayList for thread safety
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void registerObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer " + observer.getObserverId() + " registered with " + managerId);
        } else {
            System.out.println("Observer " + observer.getObserverId() + " is already registered with " + managerId);
        }
    }
    
    @Override
    public void removeObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        
        if (observers.remove(observer)) {
            System.out.println("Observer " + observer.getObserverId() + " removed from " + managerId);
        } else {
            System.out.println("Observer " + observer.getObserverId() + " was not registered with " + managerId);
        }
    }
    
    @Override
    public void notifyObservers(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        System.out.println("EventManager " + managerId + " notifying " + observers.size() + " observers about: " + event.getType());
        
        for (Observer observer : observers) {
            try {
                observer.update(event);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getObserverId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Triggers an event and notifies all observers.
     * 
     * @param event the event to trigger
     */
    public void triggerEvent(Event event) {
        notifyObservers(event);
    }
    
    /**
     * Gets the number of registered observers.
     * 
     * @return the number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Gets the manager ID.
     * 
     * @return the manager ID
     */
    public String getManagerId() {
        return managerId;
    }
    
    /**
     * Gets a copy of the current observers list.
     * 
     * @return list of observers
     */
    public List<Observer> getObservers() {
        return new ArrayList<>(observers);
    }
} 