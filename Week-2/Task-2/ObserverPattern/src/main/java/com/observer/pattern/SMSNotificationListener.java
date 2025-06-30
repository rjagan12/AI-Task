package com.observer.pattern;

/**
 * SMSNotificationListener is a concrete implementation of Observer
 * that handles SMS notifications when events occur.
 */
public class SMSNotificationListener implements Observer {
    
    private final String observerId;
    private final String phoneNumber;
    
    /**
     * Constructor for SMSNotificationListener.
     * 
     * @param phoneNumber the phone number to send SMS notifications to
     */
    public SMSNotificationListener(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.observerId = "SMSNotificationListener-" + phoneNumber;
    }
    
    @Override
    public void update(Event event) {
        System.out.println("📱 SMS notification sent to " + phoneNumber + 
                          " for event: " + event.getType() + 
                          " - " + event.getMessage());
        
        // In a real implementation, this would send an actual SMS
        // using an SMS service like Twilio or similar
        sendSMS(event);
    }
    
    @Override
    public String getObserverId() {
        return observerId;
    }
    
    /**
     * Simulates sending an SMS notification.
     * 
     * @param event the event to send notification about
     */
    private void sendSMS(Event event) {
        // Simulate SMS sending process
        String message = String.format(
            "Event: %s\n%s\nID: %s",
            event.getType(),
            event.getMessage(),
            event.getId()
        );
        
        // In a real implementation, this would use an SMS service
        System.out.println("📱 SMS Details:");
        System.out.println("  To: " + phoneNumber);
        System.out.println("  Message: " + message.replace("\n", "\n  "));
    }
    
    /**
     * Gets the phone number for this listener.
     * 
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
} 