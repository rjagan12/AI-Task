package com.factorypattern.db;

/**
 * Concrete implementation of DBConnection for MySQL database.
 */
public class MySQLConnection extends DBConnection {
    
    private boolean connected = false;
    
    public MySQLConnection(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password);
    }
    
    @Override
    public boolean connect() {
        try {
            // Simulate connection process
            System.out.println("Connecting to MySQL database at " + host + ":" + port);
            System.out.println("Database: " + database);
            System.out.println("Username: " + username);
            
            // In a real implementation, this would use JDBC or similar
            // For demonstration purposes, we'll simulate a successful connection
            Thread.sleep(100); // Simulate connection time
            
            connected = true;
            System.out.println("Successfully connected to MySQL database");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect to MySQL database: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from MySQL database");
            connected = false;
            System.out.println("Successfully disconnected from MySQL database");
        }
    }
    
    @Override
    public String executeQuery(String query) {
        if (!connected) {
            return "Error: Not connected to database";
        }
        
        System.out.println("Executing MySQL query: " + query);
        // In a real implementation, this would execute the actual query
        return "MySQL Query Result: " + query + " executed successfully";
    }
    
    @Override
    public String getConnectionString() {
        return String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s", 
                           host, port, database, username, "***");
    }
    
    @Override
    public String getDatabaseType() {
        return "MySQL";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
} 