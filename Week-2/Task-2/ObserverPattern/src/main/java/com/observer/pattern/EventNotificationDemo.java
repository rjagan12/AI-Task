package com.observer.pattern;

/**
 * EventNotificationDemo demonstrates the usage of the Observer Pattern
 * for an event notification system. This class shows how to register
 * observers and trigger events.
 */
public class EventNotificationDemo {
    
    public static void main(String[] args) {
        System.out.println("🚀 Observer Pattern - Event Notification System Demo");
        System.out.println("=" .repeat(60));
        
        // Create the event manager (Subject)
        EventManager eventManager = new EventManager("MainEventManager");
        
        // Create observers
        EmailNotificationListener emailListener = new EmailNotificationListener("admin@company.com");
        SMSNotificationListener smsListener = new SMSNotificationListener("+1234567890");
        LogEventListener logListener = new LogEventListener("INFO", "/var/log/events.log");
        
        // Register observers with the subject
        System.out.println("\n📋 Registering Observers:");
        eventManager.registerObserver(emailListener);
        eventManager.registerObserver(smsListener);
        eventManager.registerObserver(logListener);
        
        // Trigger some events
        System.out.println("\n🎯 Triggering Events:");
        
        // Event 1: New User Registration
        Event userRegistrationEvent = new Event(
            "USER_REGISTRATION",
            "New user John Doe registered successfully",
            new UserData("john.doe@email.com", "John Doe")
        );
        eventManager.triggerEvent(userRegistrationEvent);
        
        // Event 2: Order Placed
        Event orderPlacedEvent = new Event(
            "ORDER_PLACED",
            "Order #12345 placed by user john.doe@email.com",
            new OrderData("12345", 299.99, "john.doe@email.com")
        );
        eventManager.triggerEvent(orderPlacedEvent);
        
        // Event 3: Payment Processed
        Event paymentEvent = new Event(
            "PAYMENT_PROCESSED",
            "Payment processed for order #12345",
            new PaymentData("12345", 299.99, "SUCCESS")
        );
        eventManager.triggerEvent(paymentEvent);
        
        // Demonstrate removing an observer
        System.out.println("\n🗑️ Removing SMS Observer:");
        eventManager.removeObserver(smsListener);
        
        // Trigger another event to show SMS observer is no longer notified
        Event systemEvent = new Event(
            "SYSTEM_MAINTENANCE",
            "Scheduled maintenance starting in 30 minutes"
        );
        eventManager.triggerEvent(systemEvent);
        
        // Show final statistics
        System.out.println("\n📊 Final Statistics:");
        System.out.println("Active observers: " + eventManager.getObserverCount());
        System.out.println("Event manager ID: " + eventManager.getManagerId());
        
        System.out.println("\n✅ Demo completed successfully!");
    }
    
    /**
     * Helper class to represent user data.
     */
    public static class UserData {
        private final String email;
        private final String name;
        
        public UserData(String email, String name) {
            this.email = email;
            this.name = name;
        }
        
        public String getEmail() { return email; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return String.format("User{email='%s', name='%s'}", email, name);
        }
    }
    
    /**
     * Helper class to represent order data.
     */
    public static class OrderData {
        private final String orderId;
        private final double amount;
        private final String customerEmail;
        
        public OrderData(String orderId, double amount, String customerEmail) {
            this.orderId = orderId;
            this.amount = amount;
            this.customerEmail = customerEmail;
        }
        
        public String getOrderId() { return orderId; }
        public double getAmount() { return amount; }
        public String getCustomerEmail() { return customerEmail; }
        
        @Override
        public String toString() {
            return String.format("Order{id='%s', amount=%.2f, customer='%s'}", 
                               orderId, amount, customerEmail);
        }
    }
    
    /**
     * Helper class to represent payment data.
     */
    public static class PaymentData {
        private final String orderId;
        private final double amount;
        private final String status;
        
        public PaymentData(String orderId, double amount, String status) {
            this.orderId = orderId;
            this.amount = amount;
            this.status = status;
        }
        
        public String getOrderId() { return orderId; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
        
        @Override
        public String toString() {
            return String.format("Payment{orderId='%s', amount=%.2f, status='%s'}", 
                               orderId, amount, status);
        }
    }
} 