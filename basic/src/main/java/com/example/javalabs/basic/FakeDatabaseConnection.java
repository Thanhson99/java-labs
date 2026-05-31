package com.example.javalabs.basic;

/**
 * Tiny stand-in for a real database connection.
 *
 * <p>Real systems would hold sockets, transactions, driver state, and authentication details.
 * This class keeps only the parts needed to teach connection-pool behavior.</p>
 */
public final class FakeDatabaseConnection {

    private final int id;
    private final String databaseName;
    private int queryCount;

    /**
     * Creates a fake connection.
     *
     * @param id connection identifier
     * @param databaseName logical database name
     * @throws IllegalArgumentException when id or database name is invalid
     */
    public FakeDatabaseConnection(int id, String databaseName) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("databaseName must not be blank");
        }
        this.id = id;
        this.databaseName = databaseName;
    }

    /**
     * @return fake connection identifier
     */
    public int id() {
        return id;
    }

    /**
     * @return logical database name
     */
    public String databaseName() {
        return databaseName;
    }

    /**
     * @return number of simulated queries executed by this connection
     */
    public int queryCount() {
        return queryCount;
    }

    /**
     * Simulates a query execution.
     *
     * @param sql a pretend SQL statement
     * @return a debug string that shows which connection handled the work
     * @throws IllegalArgumentException when {@code sql} is blank
     */
    public String query(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("sql must not be blank");
        }
        queryCount++;
        return "[%s#%d] %s".formatted(databaseName, id, sql);
    }
}
