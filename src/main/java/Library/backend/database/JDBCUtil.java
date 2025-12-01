package Library.backend.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

public class JDBCUtil {
    private static final String CONNECTION_ERROR_MESSAGE = "KhA'ng th ¯Ÿ k §¨t n ¯`i t ¯>i c’­ s ¯Y d ¯_ li ¯Øu.";
    private static volatile Supplier<Connection> connectionSupplier = JDBCUtil::createConnection;

    public static Connection getConnection() {
        try {
            return connectionSupplier.get();
        } catch (DatabaseConnectionException e) {
            throw e;
        } catch (RuntimeException e) {
            System.err.println(CONNECTION_ERROR_MESSAGE);
            throw new DatabaseConnectionException(CONNECTION_ERROR_MESSAGE, e);
        }
    }

    /**
     * Allow tests to override how connections are created (e.g., in-memory H2).
     */
    public static void setConnectionSupplier(Supplier<Connection> supplier) {
        connectionSupplier = Objects.requireNonNull(supplier);
    }

    /**
     * Reset connection supplier back to default MySQL provider.
     */
    public static void resetConnectionSupplier() {
        connectionSupplier = JDBCUtil::createConnection;
    }

    private static Connection createConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            String url = "jdbc:mySQL://localhost:3306/library";
            String username = "root";
            String password = "root";
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println(CONNECTION_ERROR_MESSAGE);
            throw new DatabaseConnectionException(CONNECTION_ERROR_MESSAGE, e);
        }
    }

    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printInfo(Connection c) {
        try {
            if (c != null) {
                DatabaseMetaData mtdt = c.getMetaData();
                System.out.println(mtdt.getDatabaseProductName());
                System.out.println(mtdt.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

