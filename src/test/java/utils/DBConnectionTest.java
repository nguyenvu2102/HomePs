package utils;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class is used to verify the database connection.
 * It helps diagnose issues with connection credentials (URL, user, password)
 * and the database server's accessibility.
 */
public class DBConnectionTest {

    @Test
    void testDatabaseConnection() {
        System.out.println("Attempting to connect to the database...");
        try (Connection connection = DBConnection.getConnection()) {
            // If DBConnection.getConnection() throws an exception, the test will fail before this point.
            // If it returns a connection, we proceed to check its validity.

            assertNotNull(connection, "The connection object should not be null if no exception was thrown.");

            // Check if the connection is actually alive and usable with a 2-second timeout.
            assertTrue(connection.isValid(2), "Connection is not valid. The database might be down or unreachable.");

            System.out.println("✅ --- Database Connection Successful! --- ✅");
            System.out.println("URL: " + connection.getMetaData().getURL());
            System.out.println("Username: " + connection.getMetaData().getUserName());

        } catch (Exception e) {
            // This catch block will execute if DBConnection.getConnection() fails.
            System.err.println("❌ --- Database Connection Failed! --- ❌");
            System.err.println("Please check the following:");
            System.err.println("1. Is the 'db' service running in Docker? (Check 'docker ps')");
            System.err.println("2. Are the environment variables in 'docker-compose.yml' correct?");
            System.err.println("   - DB_URL: jdbc:postgresql://db:5432/homeps");
            System.err.println("   - DB_USER: postgres");
            System.err.println("   - DB_PASSWORD: postgres");
            System.err.println("3. Is there a network issue preventing the application from reaching the 'db' container?");
            
            // Print the underlying exception and explicitly fail the test.
            e.printStackTrace();
            fail("Database connection test failed due to an exception: " + e.getMessage());
        }
    }
}
