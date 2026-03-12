package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcExamplesTest {

    @Test
    void canCreateInsertAndQueryUsingJdbc() throws Exception {
        try (Connection connection = JdbcExamples.openConnection("jdbc:h2:mem:basic-jdbc-test;DB_CLOSE_DELAY=-1")) {
            JdbcExamples.createUserTable(connection);
            JdbcExamples.insertUser(connection, new JdbcUserRecord("u-1", "alice@example.com"));
            JdbcExamples.insertUser(connection, new JdbcUserRecord("u-2", "bob@example.com"));

            List<JdbcUserRecord> users = JdbcExamples.findAllUsers(connection);
            assertEquals(2, users.size());
            assertEquals("u-1", users.get(0).userId());
            assertTrue(JdbcExamples.summarizeUsers(connection).contains("2 users"));
        }
    }
}
