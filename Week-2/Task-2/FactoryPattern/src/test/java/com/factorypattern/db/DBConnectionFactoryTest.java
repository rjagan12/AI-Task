package com.factorypattern.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DBConnectionFactory class.
 * Tests the creation of different database connection types and error handling.
 */
public class DBConnectionFactoryTest {
    
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 3306;
    private static final String TEST_DATABASE = "testdb";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    
    @Test
    public void testCreateMySQLConnection() {
        DBConnection connection = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.MYSQL,
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        assertNotNull(connection);
        assertTrue(connection instanceof MySQLConnection);
        assertEquals("MySQL", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
    }
    
    @Test
    public void testCreatePostgreSQLConnection() {
        DBConnection connection = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.POSTGRESQL,
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        assertNotNull(connection);
        assertTrue(connection instanceof PostgreSQLConnection);
        assertEquals("PostgreSQL", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
    }
    
    @Test
    public void testCreateOracleConnection() {
        DBConnection connection = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.ORACLE,
            TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
        );
        
        assertNotNull(connection);
        assertTrue(connection instanceof OracleConnection);
        assertEquals("Oracle", connection.getDatabaseType());
        assertEquals(TEST_HOST, connection.getHost());
        assertEquals(TEST_PORT, connection.getPort());
        assertEquals(TEST_DATABASE, connection.getDatabase());
        assertEquals(TEST_USERNAME, connection.getUsername());
    }
    
    @Test
    public void testCreateConnectionWithNullType() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnection(
                null, TEST_HOST, TEST_PORT, TEST_DATABASE, TEST_USERNAME, TEST_PASSWORD
            );
        });
    }
    
    @Test
    public void testCreateConnectionFromConfigMySQL() {
        String config = "mysql:localhost:3306:testdb:user:pass";
        DBConnection connection = DBConnectionFactory.createConnectionFromConfig(config);
        
        assertNotNull(connection);
        assertTrue(connection instanceof MySQLConnection);
        assertEquals("MySQL", connection.getDatabaseType());
    }
    
    @Test
    public void testCreateConnectionFromConfigPostgreSQL() {
        String config = "postgresql:localhost:5432:testdb:user:pass";
        DBConnection connection = DBConnectionFactory.createConnectionFromConfig(config);
        
        assertNotNull(connection);
        assertTrue(connection instanceof PostgreSQLConnection);
        assertEquals("PostgreSQL", connection.getDatabaseType());
    }
    
    @Test
    public void testCreateConnectionFromConfigOracle() {
        String config = "oracle:localhost:1521:ORCL:user:pass";
        DBConnection connection = DBConnectionFactory.createConnectionFromConfig(config);
        
        assertNotNull(connection);
        assertTrue(connection instanceof OracleConnection);
        assertEquals("Oracle", connection.getDatabaseType());
    }
    
    @Test
    public void testCreateConnectionFromConfigWithNullString() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnectionFromConfig(null);
        });
    }
    
    @Test
    public void testCreateConnectionFromConfigWithEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnectionFromConfig("");
        });
    }
    
    @Test
    public void testCreateConnectionFromConfigWithInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnectionFromConfig("invalid:format");
        });
    }
    
    @Test
    public void testCreateConnectionFromConfigWithInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnectionFromConfig("mysql:localhost:invalid:testdb:user:pass");
        });
    }
    
    @Test
    public void testCreateConnectionFromConfigWithInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            DBConnectionFactory.createConnectionFromConfig("invalid:localhost:3306:testdb:user:pass");
        });
    }
    
    @Test
    public void testGetSupportedTypes() {
        DBConnectionFactory.DatabaseType[] types = DBConnectionFactory.getSupportedTypes();
        
        assertNotNull(types);
        assertEquals(3, types.length);
        
        boolean hasMySQL = false;
        boolean hasPostgreSQL = false;
        boolean hasOracle = false;
        
        for (DBConnectionFactory.DatabaseType type : types) {
            switch (type) {
                case MYSQL:
                    hasMySQL = true;
                    break;
                case POSTGRESQL:
                    hasPostgreSQL = true;
                    break;
                case ORACLE:
                    hasOracle = true;
                    break;
            }
        }
        
        assertTrue(hasMySQL, "MySQL should be in supported types");
        assertTrue(hasPostgreSQL, "PostgreSQL should be in supported types");
        assertTrue(hasOracle, "Oracle should be in supported types");
    }
    
    @Test
    public void testIsSupported() {
        assertTrue(DBConnectionFactory.isSupported(DBConnectionFactory.DatabaseType.MYSQL));
        assertTrue(DBConnectionFactory.isSupported(DBConnectionFactory.DatabaseType.POSTGRESQL));
        assertTrue(DBConnectionFactory.isSupported(DBConnectionFactory.DatabaseType.ORACLE));
        assertFalse(DBConnectionFactory.isSupported(null));
    }
    
    @Test
    public void testConnectionStringFormats() {
        // Test MySQL connection string
        DBConnection mysqlConn = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.MYSQL, "localhost", 3306, "testdb", "user", "pass"
        );
        assertTrue(mysqlConn.getConnectionString().contains("jdbc:mysql://"));
        
        // Test PostgreSQL connection string
        DBConnection postgresConn = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.POSTGRESQL, "localhost", 5432, "testdb", "user", "pass"
        );
        assertTrue(postgresConn.getConnectionString().contains("jdbc:postgresql://"));
        
        // Test Oracle connection string
        DBConnection oracleConn = DBConnectionFactory.createConnection(
            DBConnectionFactory.DatabaseType.ORACLE, "localhost", 1521, "ORCL", "user", "pass"
        );
        assertTrue(oracleConn.getConnectionString().contains("jdbc:oracle:thin:@"));
    }
} 