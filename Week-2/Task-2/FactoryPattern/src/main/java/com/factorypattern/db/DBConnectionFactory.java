package com.factorypattern.db;

/**
 * Factory class for creating database connections.
 * Encapsulates the instantiation logic and provides a clean interface for clients.
 */
public class DBConnectionFactory {
    
    /**
     * Database types supported by the factory
     */
    public enum DatabaseType {
        MYSQL,
        POSTGRESQL,
        ORACLE
    }
    
    /**
     * Creates a database connection based on the specified type and parameters.
     * 
     * @param type the type of database connection to create
     * @param host the database host
     * @param port the database port
     * @param database the database name
     * @param username the username for authentication
     * @param password the password for authentication
     * @return a DBConnection instance of the specified type
     * @throws IllegalArgumentException if an unsupported database type is specified
     */
    public static DBConnection createConnection(DatabaseType type, 
                                              String host, 
                                              int port, 
                                              String database, 
                                              String username, 
                                              String password) {
        
        if (type == null) {
            throw new IllegalArgumentException("Database type cannot be null");
        }
        
        switch (type) {
            case MYSQL:
                return new MySQLConnection(host, port, database, username, password);
            case POSTGRESQL:
                return new PostgreSQLConnection(host, port, database, username, password);
            case ORACLE:
                return new OracleConnection(host, port, database, username, password);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
    
    /**
     * Creates a database connection using a configuration string.
     * Format: "type:host:port:database:username:password"
     * 
     * @param configString the configuration string
     * @return a DBConnection instance
     * @throws IllegalArgumentException if the configuration string is invalid
     */
    public static DBConnection createConnectionFromConfig(String configString) {
        if (configString == null || configString.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration string cannot be null or empty");
        }
        
        String[] parts = configString.split(":");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid configuration string format. Expected: type:host:port:database:username:password");
        }
        
        try {
            DatabaseType type = DatabaseType.valueOf(parts[0].toUpperCase());
            String host = parts[1];
            int port = Integer.parseInt(parts[2]);
            String database = parts[3];
            String username = parts[4];
            String password = parts[5];
            
            return createConnection(type, host, port, database, username, password);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number in configuration string");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid database type in configuration string");
        }
    }
    
    /**
     * Gets a list of supported database types.
     * 
     * @return array of supported database types
     */
    public static DatabaseType[] getSupportedTypes() {
        return DatabaseType.values();
    }
    
    /**
     * Checks if a database type is supported.
     * 
     * @param type the database type to check
     * @return true if supported, false otherwise
     */
    public static boolean isSupported(DatabaseType type) {
        if (type == null) {
            return false;
        }
        
        for (DatabaseType supportedType : DatabaseType.values()) {
            if (supportedType == type) {
                return true;
            }
        }
        return false;
    }
} 