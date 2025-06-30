package com.factorypattern.test;

import com.factorypattern.db.*;

/**
 * Simple test runner to demonstrate the Factory Pattern functionality
 * without requiring JUnit dependencies.
 */
public class SimpleTestRunner {
    
    private static int testCount = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Factory Pattern Test Runner ===\n");
        
        // Test Factory Creation
        testFactoryCreation();
        
        // Test Configuration String Creation
        testConfigurationStringCreation();
        
        // Test Connection Lifecycle
        testConnectionLifecycle();
        
        // Test Error Handling
        testErrorHandling();
        
        // Test Connection String Formats
        testConnectionStringFormats();
        
        // Print Summary
        printTestSummary();
    }
    
    private static void testFactoryCreation() {
        System.out.println("Testing Factory Creation Methods:");
        
        try {
            // Test MySQL creation
            DBConnection mysqlConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.MYSQL, "localhost", 3306, "testdb", "user", "pass"
            );
            assertTest(mysqlConn instanceof MySQLConnection, "MySQL connection creation");
            assertTest("MySQL".equals(mysqlConn.getDatabaseType()), "MySQL database type");
            
            // Test PostgreSQL creation
            DBConnection postgresConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.POSTGRESQL, "localhost", 5432, "testdb", "user", "pass"
            );
            assertTest(postgresConn instanceof PostgreSQLConnection, "PostgreSQL connection creation");
            assertTest("PostgreSQL".equals(postgresConn.getDatabaseType()), "PostgreSQL database type");
            
            // Test Oracle creation
            DBConnection oracleConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.ORACLE, "localhost", 1521, "ORCL", "user", "pass"
            );
            assertTest(oracleConn instanceof OracleConnection, "Oracle connection creation");
            assertTest("Oracle".equals(oracleConn.getDatabaseType()), "Oracle database type");
            
        } catch (Exception e) {
            failTest("Factory creation", e.getMessage());
        }
    }
    
    private static void testConfigurationStringCreation() {
        System.out.println("\nTesting Configuration String Creation:");
        
        try {
            // Test MySQL config string
            String mysqlConfig = "mysql:localhost:3306:testdb:user:pass";
            DBConnection mysqlConn = DBConnectionFactory.createConnectionFromConfig(mysqlConfig);
            assertTest(mysqlConn instanceof MySQLConnection, "MySQL config string creation");
            
            // Test PostgreSQL config string
            String postgresConfig = "postgresql:localhost:5432:testdb:user:pass";
            DBConnection postgresConn = DBConnectionFactory.createConnectionFromConfig(postgresConfig);
            assertTest(postgresConn instanceof PostgreSQLConnection, "PostgreSQL config string creation");
            
            // Test Oracle config string
            String oracleConfig = "oracle:localhost:1521:ORCL:user:pass";
            DBConnection oracleConn = DBConnectionFactory.createConnectionFromConfig(oracleConfig);
            assertTest(oracleConn instanceof OracleConnection, "Oracle config string creation");
            
        } catch (Exception e) {
            failTest("Configuration string creation", e.getMessage());
        }
    }
    
    private static void testConnectionLifecycle() {
        System.out.println("\nTesting Connection Lifecycle:");
        
        try {
            DBConnection conn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.MYSQL, "localhost", 3306, "testdb", "user", "pass"
            );
            
            // Test initial state
            assertTest(!conn.isConnected(), "Initial disconnected state");
            
            // Test connection
            boolean connected = conn.connect();
            assertTest(connected, "Connection success");
            assertTest(conn.isConnected(), "Connected state after connect");
            
            // Test query execution
            String result = conn.executeQuery("SELECT * FROM users");
            assertTest(result != null && result.contains("MySQL Query Result"), "Query execution");
            
            // Test disconnection
            conn.disconnect();
            assertTest(!conn.isConnected(), "Disconnected state after disconnect");
            
        } catch (Exception e) {
            failTest("Connection lifecycle", e.getMessage());
        }
    }
    
    private static void testErrorHandling() {
        System.out.println("\nTesting Error Handling:");
        
        try {
            // Test null database type
            try {
                DBConnectionFactory.createConnection(null, "localhost", 3306, "testdb", "user", "pass");
                failTest("Null database type handling", "Should have thrown exception");
            } catch (IllegalArgumentException e) {
                passTest("Null database type handling");
            }
            
            // Test invalid config string
            try {
                DBConnectionFactory.createConnectionFromConfig("invalid:config");
                failTest("Invalid config string handling", "Should have thrown exception");
            } catch (IllegalArgumentException e) {
                passTest("Invalid config string handling");
            }
            
            // Test query without connection
            DBConnection conn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.MYSQL, "localhost", 3306, "testdb", "user", "pass"
            );
            String result = conn.executeQuery("SELECT * FROM users");
            assertTest("Error: Not connected to database".equals(result), "Query without connection");
            
        } catch (Exception e) {
            failTest("Error handling", e.getMessage());
        }
    }
    
    private static void testConnectionStringFormats() {
        System.out.println("\nTesting Connection String Formats:");
        
        try {
            // Test MySQL connection string
            DBConnection mysqlConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.MYSQL, "localhost", 3306, "testdb", "user", "pass"
            );
            String mysqlString = mysqlConn.getConnectionString();
            assertTest(mysqlString.contains("jdbc:mysql://"), "MySQL connection string format");
            
            // Test PostgreSQL connection string
            DBConnection postgresConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.POSTGRESQL, "localhost", 5432, "testdb", "user", "pass"
            );
            String postgresString = postgresConn.getConnectionString();
            assertTest(postgresString.contains("jdbc:postgresql://"), "PostgreSQL connection string format");
            
            // Test Oracle connection string
            DBConnection oracleConn = DBConnectionFactory.createConnection(
                DBConnectionFactory.DatabaseType.ORACLE, "localhost", 1521, "ORCL", "user", "pass"
            );
            String oracleString = oracleConn.getConnectionString();
            assertTest(oracleString.contains("jdbc:oracle:thin:@"), "Oracle connection string format");
            
        } catch (Exception e) {
            failTest("Connection string formats", e.getMessage());
        }
    }
    
    private static void assertTest(boolean condition, String testName) {
        testCount++;
        if (condition) {
            passTest(testName);
        } else {
            failTest(testName, "Condition failed");
        }
    }
    
    private static void passTest(String testName) {
        testCount++;
        passedTests++;
        System.out.println("  ✅ PASS: " + testName);
    }
    
    private static void failTest(String testName, String reason) {
        testCount++;
        failedTests++;
        System.out.println("  ❌ FAIL: " + testName + " - " + reason);
    }
    
    private static void printTestSummary() {
        System.out.println("\n=== Test Summary ===");
        System.out.println("Total Tests: " + testCount);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " + (testCount > 0 ? (passedTests * 100 / testCount) : 0) + "%");
        
        if (failedTests == 0) {
            System.out.println("\n🎉 All tests passed! Factory Pattern implementation is working correctly.");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please review the implementation.");
        }
    }
} 