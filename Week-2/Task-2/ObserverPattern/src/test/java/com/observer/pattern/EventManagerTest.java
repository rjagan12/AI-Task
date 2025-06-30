package com.observer.pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EventManager class.
 * Tests observer registration, removal, and event notification functionality.
 */
@DisplayName("EventManager Tests")
public class EventManagerTest {
    
    private EventManager eventManager;
    private TestObserver testObserver1;
    private TestObserver testObserver2;
    
    @BeforeEach
    void setUp() {
        eventManager = new EventManager("TestEventManager");
        testObserver1 = new TestObserver("TestObserver1");
        testObserver2 = new TestObserver("TestObserver2");
    }
    
    @Test
    @DisplayName("Should register observer successfully")
    void testRegisterObserver() {
        eventManager.registerObserver(testObserver1);
        
        assertEquals(1, eventManager.getObserverCount());
        assertTrue(eventManager.getObservers().contains(testObserver1));
    }
    
    @Test
    @DisplayName("Should not register duplicate observer")
    void testRegisterDuplicateObserver() {
        eventManager.registerObserver(testObserver1);
        eventManager.registerObserver(testObserver1); // Try to register same observer again
        
        assertEquals(1, eventManager.getObserverCount());
    }
    
    @Test
    @DisplayName("Should register multiple observers")
    void testRegisterMultipleObservers() {
        eventManager.registerObserver(testObserver1);
        eventManager.registerObserver(testObserver2);
        
        assertEquals(2, eventManager.getObserverCount());
        assertTrue(eventManager.getObservers().contains(testObserver1));
        assertTrue(eventManager.getObservers().contains(testObserver2));
    }
    
    @Test
    @DisplayName("Should throw exception when registering null observer")
    void testRegisterNullObserver() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventManager.registerObserver(null);
        });
    }
    
    @Test
    @DisplayName("Should remove observer successfully")
    void testRemoveObserver() {
        eventManager.registerObserver(testObserver1);
        eventManager.registerObserver(testObserver2);
        
        eventManager.removeObserver(testObserver1);
        
        assertEquals(1, eventManager.getObserverCount());
        assertFalse(eventManager.getObservers().contains(testObserver1));
        assertTrue(eventManager.getObservers().contains(testObserver2));
    }
    
    @Test
    @DisplayName("Should handle removing non-existent observer")
    void testRemoveNonExistentObserver() {
        eventManager.registerObserver(testObserver1);
        
        eventManager.removeObserver(testObserver2); // Try to remove unregistered observer
        
        assertEquals(1, eventManager.getObserverCount());
        assertTrue(eventManager.getObservers().contains(testObserver1));
    }
    
    @Test
    @DisplayName("Should throw exception when removing null observer")
    void testRemoveNullObserver() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventManager.removeObserver(null);
        });
    }
    
    @Test
    @DisplayName("Should notify all registered observers")
    void testNotifyObservers() {
        eventManager.registerObserver(testObserver1);
        eventManager.registerObserver(testObserver2);
        
        Event testEvent = new Event("TEST_EVENT", "Test message");
        eventManager.notifyObservers(testEvent);
        
        assertEquals(1, testObserver1.getNotificationCount());
        assertEquals(1, testObserver2.getNotificationCount());
        assertEquals(testEvent, testObserver1.getLastReceivedEvent());
        assertEquals(testEvent, testObserver2.getLastReceivedEvent());
    }
    
    @Test
    @DisplayName("Should not notify removed observers")
    void testNotifyAfterObserverRemoval() {
        eventManager.registerObserver(testObserver1);
        eventManager.registerObserver(testObserver2);
        
        Event firstEvent = new Event("FIRST_EVENT", "First message");
        eventManager.notifyObservers(firstEvent);
        
        eventManager.removeObserver(testObserver1);
        
        Event secondEvent = new Event("SECOND_EVENT", "Second message");
        eventManager.notifyObservers(secondEvent);
        
        assertEquals(1, testObserver1.getNotificationCount()); // Should not receive second event
        assertEquals(2, testObserver2.getNotificationCount()); // Should receive both events
        assertEquals(firstEvent, testObserver1.getLastReceivedEvent());
        assertEquals(secondEvent, testObserver2.getLastReceivedEvent());
    }
    
    @Test
    @DisplayName("Should handle empty observer list")
    void testNotifyWithNoObservers() {
        Event testEvent = new Event("TEST_EVENT", "Test message");
        
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            eventManager.notifyObservers(testEvent);
        });
        
        assertEquals(0, eventManager.getObserverCount());
    }
    
    @Test
    @DisplayName("Should throw exception when notifying with null event")
    void testNotifyWithNullEvent() {
        eventManager.registerObserver(testObserver1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            eventManager.notifyObservers(null);
        });
    }
    
    @Test
    @DisplayName("Should return correct manager ID")
    void testGetManagerId() {
        assertEquals("TestEventManager", eventManager.getManagerId());
    }
    
    @Test
    @DisplayName("Should return copy of observers list")
    void testGetObserversReturnsCopy() {
        eventManager.registerObserver(testObserver1);
        
        var observersList = eventManager.getObservers();
        observersList.clear(); // Modify the returned list
        
        // Original list should remain unchanged
        assertEquals(1, eventManager.getObserverCount());
        assertTrue(eventManager.getObservers().contains(testObserver1));
    }
    
    @Test
    @DisplayName("Should trigger event correctly")
    void testTriggerEvent() {
        eventManager.registerObserver(testObserver1);
        
        Event testEvent = new Event("TEST_EVENT", "Test message");
        eventManager.triggerEvent(testEvent);
        
        assertEquals(1, testObserver1.getNotificationCount());
        assertEquals(testEvent, testObserver1.getLastReceivedEvent());
    }
    
    /**
     * Test observer implementation for unit testing.
     */
    private static class TestObserver implements Observer {
        private final String observerId;
        private int notificationCount = 0;
        private Event lastReceivedEvent = null;
        
        public TestObserver(String observerId) {
            this.observerId = observerId;
        }
        
        @Override
        public void update(Event event) {
            notificationCount++;
            lastReceivedEvent = event;
        }
        
        @Override
        public String getObserverId() {
            return observerId;
        }
        
        public int getNotificationCount() {
            return notificationCount;
        }
        
        public Event getLastReceivedEvent() {
            return lastReceivedEvent;
        }
    }
} 