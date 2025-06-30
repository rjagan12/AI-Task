package com.factorypattern.db;

/**
 * Concrete implementation of DBConnection for PostgreSQL database.
 */
public class PostgreSQLConnection extends DBConnection {
    
    private boolean connected = false;
    
    public PostgreSQLConnection(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password);
    }
    
    @Override
    public boolean connect() {
        try {
            // Simulate connection process
            System.out.println("Connecting to PostgreSQL database at " + host + ":" + port);
            System.out.println("Database: " + database);
            System.out.println("Username: " + username);
            
            // In a real implementation, this would use JDBC or similar
            // For demonstration purposes, we'll simulate a successful connection
            Thread.sleep(150); // Simulate connection time (PostgreSQL might be slightly slower)
            
            connected = true;
            System.out.println("Successfully connected to PostgreSQL database");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect to PostgreSQL database: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from PostgreSQL database");
            connected = false;
            System.out.println("Successfully disconnected from PostgreSQL database");
        }
    }
    
    @Override
    public String executeQuery(String query) {
        if (!connected) {
            return "Error: Not connected to database";
        }
        
        System.out.println("Executing PostgreSQL query: " + query);
        // In a real implementation, this would execute the actual query
        return "PostgreSQL Query Result: " + query + " executed successfully";
    }
    
    @Override
    public String getConnectionString() {
        return String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", 
                           host, port, database, username, "***");
    }
    
    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
} 