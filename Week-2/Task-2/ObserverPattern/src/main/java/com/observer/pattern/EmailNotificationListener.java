package com.observer.pattern;

/**
 * EmailNotificationListener is a concrete implementation of Observer
 * that handles email notifications when events occur.
 */
public class EmailNotificationListener implements Observer {
    
    private final String observerId;
    private final String emailAddress;
    
    /**
     * Constructor for EmailNotificationListener.
     * 
     * @param emailAddress the email address to send notifications to
     */
    public EmailNotificationListener(String emailAddress) {
        this.emailAddress = emailAddress;
        this.observerId = "EmailNotificationListener-" + emailAddress;
    }
    
    @Override
    public void update(Event event) {
        System.out.println("📧 Email notification sent to " + emailAddress + 
                          " for event: " + event.getType() + 
                          " - " + event.getMessage());
        
        // In a real implementation, this would send an actual email
        // using an email service like JavaMail API or similar
        sendEmail(event);
    }
    
    @Override
    public String getObserverId() {
        return observerId;
    }
    
    /**
     * Simulates sending an email notification.
     * 
     * @param event the event to send notification about
     */
    private void sendEmail(Event event) {
        // Simulate email sending process
        String subject = "Event Notification: " + event.getType();
        String body = String.format(
            "Event Details:\n" +
            "Type: %s\n" +
            "Message: %s\n" +
            "Timestamp: %s\n" +
            "Event ID: %s",
            event.getType(),
            event.getMessage(),
            event.getTimestamp(),
            event.getId()
        );
        
        // In a real implementation, this would use an email service
        System.out.println("📧 Email Details:");
        System.out.println("  To: " + emailAddress);
        System.out.println("  Subject: " + subject);
        System.out.println("  Body: " + body.replace("\n", "\n  "));
    }
    
    /**
     * Gets the email address for this listener.
     * 
     * @return the email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }
} 