package com.factorypattern.client;

import com.factorypattern.db.DBConnection;
import com.factorypattern.db.DBConnectionFactory;
import com.factorypattern.db.DBConnectionFactory.DatabaseType;

/**
 * Client class that demonstrates the usage of the DBConnectionFactory.
 * Shows how to create different types of database connections using the factory pattern.
 */
public class DatabaseClient {
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Factory Pattern Demo ===\n");
        
        // Demo 1: Create connections using factory with explicit parameters
        demoExplicitConnectionCreation();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 2: Create connections using configuration strings
        demoConfigurationStringCreation();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 3: Demonstrate connection lifecycle
        demoConnectionLifecycle();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 4: Error handling
        demoErrorHandling();
    }
    
    /**
     * Demonstrates creating connections using explicit factory parameters
     */
    private static void demoExplicitConnectionCreation() {
        System.out.println("Demo 1: Creating connections with explicit parameters");
        
        try {
            // Create MySQL connection
            DBConnection mysqlConn = DBConnectionFactory.createConnection(
                DatabaseType.MYSQL, "localhost", 3306, "testdb", "user1", "password123"
            );
            System.out.println("Created " + mysqlConn.getDatabaseType() + " connection");
            System.out.println("Connection string: " + mysqlConn.getConnectionString());
            
            // Create PostgreSQL connection
            DBConnection postgresConn = DBConnectionFactory.createConnection(
                DatabaseType.POSTGRESQL, "localhost", 5432, "testdb", "user2", "password456"
            );
            System.out.println("Created " + postgresConn.getDatabaseType() + " connection");
            System.out.println("Connection string: " + postgresConn.getConnectionString());
            
            // Create Oracle connection
            DBConnection oracleConn = DBConnectionFactory.createConnection(
                DatabaseType.ORACLE, "localhost", 1521, "ORCL", "user3", "password789"
            );
            System.out.println("Created " + oracleConn.getDatabaseType() + " connection");
            System.out.println("Connection string: " + oracleConn.getConnectionString());
            
        } catch (Exception e) {
            System.err.println("Error creating connections: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates creating connections using configuration strings
     */
    private static void demoConfigurationStringCreation() {
        System.out.println("Demo 2: Creating connections with configuration strings");
        
        String[] configs = {
            "mysql:localhost:3306:testdb:user1:password123",
            "postgresql:localhost:5432:testdb:user2:password456",
            "oracle:localhost:1521:ORCL:user3:password789"
        };
        
        for (String config : configs) {
            try {
                DBConnection conn = DBConnectionFactory.createConnectionFromConfig(config);
                System.out.println("Created " + conn.getDatabaseType() + " connection from config");
                System.out.println("Connection string: " + conn.getConnectionString());
            } catch (Exception e) {
                System.err.println("Error creating connection from config '" + config + "': " + e.getMessage());
            }
        }
    }
    
    /**
     * Demonstrates the complete connection lifecycle
     */
    private static void demoConnectionLifecycle() {
        System.out.println("Demo 3: Connection lifecycle demonstration");
        
        try {
            // Create a MySQL connection
            DBConnection conn = DBConnectionFactory.createConnection(
                DatabaseType.MYSQL, "localhost", 3306, "testdb", "user1", "password123"
            );
            
            System.out.println("Initial connection state: " + (conn.isConnected() ? "Connected" : "Disconnected"));
            
            // Connect to database
            boolean connected = conn.connect();
            System.out.println("Connection attempt result: " + (connected ? "Success" : "Failed"));
            System.out.println("Connection state after connect: " + (conn.isConnected() ? "Connected" : "Disconnected"));
            
            // Execute a query
            if (conn.isConnected()) {
                String result = conn.executeQuery("SELECT * FROM users");
                System.out.println("Query result: " + result);
            }
            
            // Disconnect
            conn.disconnect();
            System.out.println("Connection state after disconnect: " + (conn.isConnected() ? "Connected" : "Disconnected"));
            
        } catch (Exception e) {
            System.err.println("Error in connection lifecycle demo: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates error handling scenarios
     */
    private static void demoErrorHandling() {
        System.out.println("Demo 4: Error handling demonstration");
        
        // Test with null database type
        try {
            DBConnectionFactory.createConnection(null, "localhost", 3306, "testdb", "user", "pass");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error for null type: " + e.getMessage());
        }
        
        // Test with invalid configuration string
        try {
            DBConnectionFactory.createConnectionFromConfig("invalid:config:string");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error for invalid config: " + e.getMessage());
        }
        
        // Test with unsupported database type (if we had one)
        System.out.println("Supported database types:");
        for (DatabaseType type : DBConnectionFactory.getSupportedTypes()) {
            System.out.println("  - " + type);
        }
    }
} 