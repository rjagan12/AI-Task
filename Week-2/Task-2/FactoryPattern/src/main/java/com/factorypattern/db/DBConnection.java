package com.factorypattern.db;

/**
 * Abstract base class for database connections.
 * Defines the common interface that all database connections must implement.
 */
public abstract class DBConnection {
    
    protected String host;
    protected int port;
    protected String database;
    protected String username;
    protected String password;
    
    public DBConnection(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Connect to the database
     * @return true if connection is successful, false otherwise
     */
    public abstract boolean connect();
    
    /**
     * Disconnect from the database
     */
    public abstract void disconnect();
    
    /**
     * Execute a query on the database
     * @param query the SQL query to execute
     * @return the result of the query execution
     */
    public abstract String executeQuery(String query);
    
    /**
     * Get the connection string for this database type
     * @return the connection string
     */
    public abstract String getConnectionString();
    
    /**
     * Get the database type name
     * @return the database type
     */
    public abstract String getDatabaseType();
    
    /**
     * Check if the connection is currently active
     * @return true if connected, false otherwise
     */
    public abstract boolean isConnected();
    
    // Getters for connection properties
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
} 