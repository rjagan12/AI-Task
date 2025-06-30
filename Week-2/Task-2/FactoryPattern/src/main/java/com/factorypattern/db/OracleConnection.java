package com.factorypattern.db;

/**
 * Concrete implementation of DBConnection for Oracle database.
 */
public class OracleConnection extends DBConnection {
    
    private boolean connected = false;
    
    public OracleConnection(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password);
    }
    
    @Override
    public boolean connect() {
        try {
            // Simulate connection process
            System.out.println("Connecting to Oracle database at " + host + ":" + port);
            System.out.println("Database: " + database);
            System.out.println("Username: " + username);
            
            // In a real implementation, this would use JDBC or similar
            // For demonstration purposes, we'll simulate a successful connection
            Thread.sleep(200); // Simulate connection time (Oracle might be slower)
            
            connected = true;
            System.out.println("Successfully connected to Oracle database");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect to Oracle database: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from Oracle database");
            connected = false;
            System.out.println("Successfully disconnected from Oracle database");
        }
    }
    
    @Override
    public String executeQuery(String query) {
        if (!connected) {
            return "Error: Not connected to database";
        }
        
        System.out.println("Executing Oracle query: " + query);
        // In a real implementation, this would execute the actual query
        return "Oracle Query Result: " + query + " executed successfully";
    }
    
    @Override
    public String getConnectionString() {
        return String.format("jdbc:oracle:thin:@%s:%d:%s", 
                           host, port, database);
    }
    
    @Override
    public String getDatabaseType() {
        return "Oracle";
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
} 