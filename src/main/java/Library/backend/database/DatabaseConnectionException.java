package Library.backend.database;

/**
 * Runtime exception thrown when the application cannot create a connection to the database.
 */
public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

