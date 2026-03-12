package com.example.javalabs.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates direct JDBC usage without Spring.
 *
 * <p>This class is intentionally verbose because JDBC teaches foundational backend ideas:
 * connections, SQL statements, parameter binding, result-set mapping, and resource cleanup.</p>
 */
public final class JdbcExamples {

    private JdbcExamples() {
    }

    /**
     * Opens a JDBC connection using the provided URL.
     *
     * @param jdbcUrl database URL, for example an in-memory H2 URL
     * @return an open JDBC connection
     * @throws SQLException when the database driver cannot open the connection
     */
    public static Connection openConnection(String jdbcUrl) throws SQLException {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("jdbcUrl must not be blank");
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    /**
     * Creates the demo table when it does not exist yet.
     *
     * @param connection the open JDBC connection
     * @throws SQLException when SQL execution fails
     */
    public static void createUserTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    create table if not exists jdbc_users (
                        user_id varchar(64) primary key,
                        email varchar(255) not null
                    )
                    """);
        }
    }

    /**
     * Inserts one user record using a prepared statement.
     *
     * @param connection open JDBC connection
     * @param userRecord record to persist
     * @throws SQLException when insertion fails
     */
    public static void insertUser(Connection connection, JdbcUserRecord userRecord) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into jdbc_users(user_id, email) values (?, ?)")) {
            statement.setString(1, userRecord.userId());
            statement.setString(2, userRecord.email());
            statement.executeUpdate();
        }
    }

    /**
     * Reads all users in deterministic order.
     *
     * @param connection open JDBC connection
     * @return mapped user records
     * @throws SQLException when the query fails
     */
    public static List<JdbcUserRecord> findAllUsers(Connection connection) throws SQLException {
        List<JdbcUserRecord> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "select user_id, email from jdbc_users order by user_id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new JdbcUserRecord(
                        resultSet.getString("user_id"),
                        resultSet.getString("email")
                ));
            }
        }
        return users;
    }

    /**
     * Builds a small report that explains what the database currently contains.
     *
     * @param connection open JDBC connection
     * @return a human-readable summary
     * @throws SQLException when the query fails
     */
    public static String summarizeUsers(Connection connection) throws SQLException {
        List<JdbcUserRecord> users = findAllUsers(connection);
        return "JDBC demo contains %d users: %s".formatted(users.size(), users);
    }
}
