package com.factorypattern.db;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for individual database connection classes.
 * Tests the behavior of MySQL, PostgreSQL, and Oracle connections.
 */
public class DBConnectionTest {
    
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 3306;
    private static final String TEST_DATABASE = "testdb";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    
    @Test
    public void testMySQLConnection() {
        MySQLConnection connection = new MySQLConnection(
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        // Test initial state
        assertFalse(connection.isConnected());
        assertEquals("MySQL", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
        assertTrue(connection.getConnectionString().contains("jdbc:mysql://"));
        
        // Test connection
        assertTrue(connection.connect());
        assertTrue(connection.isConnected());
        
        // Test query execution
        String result = connection.executeQuery("SELECT * FROM users");
        assertNotNull(result);
        assertTrue(result.contains("MySQL Query Result"));
        
        // Test disconnection
        connection.disconnect();
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void testPostgreSQLConnection() {
        PostgreSQLConnection connection = new PostgreSQLConnection(
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        // Test initial state
        assertFalse(connection.isConnected());
        assertEquals("PostgreSQL", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
        assertTrue(connection.getConnectionString().contains("jdbc:postgresql://"));
        
        // Test connection
        assertTrue(connection.connect());
        assertTrue(connection.isConnected());
        
        // Test query execution
        String result = connection.executeQuery("SELECT * FROM users");
        assertNotNull(result);
        assertTrue(result.contains("PostgreSQL Query Result"));
        
        // Test disconnection
        connection.disconnect();
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void testOracleConnection() {
        OracleConnection connection = new OracleConnection(
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        // Test initial state
        assertFalse(connection.isConnected());
        assertEquals("Oracle", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
        assertTrue(connection.getConnectionString().contains("jdbc:oracle:thin:@"));
        
        // Test connection
        assertTrue(connection.connect());
        assertTrue(connection.isConnected());
        
        // Test query execution
        String result = connection.executeQuery("SELECT * FROM users");
        assertNotNull(result);
        assertTrue(result.contains("Oracle Query Result"));
        
        // Test disconnection
        connection.disconnect();
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void testQueryExecutionWithoutConnection() {
        MySQLConnection connection = new MySQLConnection(
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        // Try to execute query without connecting
        String result = connection.executeQuery("SELECT * FROM users");
        assertEquals("Error: Not connected to database", result);
    }
    
    @Test
    public void testMultipleConnectDisconnect() {
        MySQLConnection connection = new MySQLConnection(
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        // First connection
        assertTrue(connection.connect());
        assertTrue(connection.isConnected());
        
        // Disconnect
        connection.disconnect();
        assertFalse(connection.isConnected());
        
        // Second connection
        assertTrue(connection.connect());
        assertTrue(connection.isConnected());
        
        // Disconnect again
        connection.disconnect();
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void testConnectionStringFormats() {
        // Test MySQL connection string format
        MySQLConnection mysqlConn = new MySQLConnection("localhost", 3306, "testdb", "user", "pass");
        String mysqlString = mysqlConn.getConnectionString();
        assertTrue(mysqlString.startsWith("jdbc:mysql://"));
        assertTrue(mysqlString.contains("localhost:3306"));
        assertTrue(mysqlString.contains("testdb"));
        assertTrue(mysqlString.contains("user"));
        assertTrue(mysqlString.contains("***")); // Password should be masked
        
        // Test PostgreSQL connection string format
        PostgreSQLConnection postgresConn = new PostgreSQLConnection("localhost", 5432, "testdb", "user", "pass");
        String postgresString = postgresConn.getConnectionString();
        assertTrue(postgresString.startsWith("jdbc:postgresql://"));
        assertTrue(postgresString.contains("localhost:5432"));
        assertTrue(postgresString.contains("testdb"));
        assertTrue(postgresString.contains("user"));
        assertTrue(postgresString.contains("***")); // Password should be masked
        
        // Test Oracle connection string format
        OracleConnection oracleConn = new OracleConnection("localhost", 1521, "ORCL", "user", "pass");
        String oracleString = oracleConn.getConnectionString();
        assertTrue(oracleString.startsWith("jdbc:oracle:thin:@"));
        assertTrue(oracleString.contains("localhost:1521"));
        assertTrue(oracleString.contains("ORCL"));
    }
} 