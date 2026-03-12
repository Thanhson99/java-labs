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

    public FakeDatabaseConnection(int id, String databaseName) {
        this.id = id;
        this.databaseName = databaseName;
    }

    public int id() {
        return id;
    }

    public String databaseName() {
        return databaseName;
    }

    public int queryCount() {
        return queryCount;
    }

    /**
     * Simulates a query execution.
     *
     * @param sql a pretend SQL statement
     * @return a debug string that shows which connection handled the work
     */
    public String query(String sql) {
        queryCount++;
        return "[%s#%d] %s".formatted(databaseName, id, sql);
    }
}
