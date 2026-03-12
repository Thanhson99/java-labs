package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiDatabaseUserProfileRepositoryTest {

    @Test
    void routesWritesByRegionAndReadsAcrossAllDatabases() {
        InMemoryUserProfileRepository apac = new InMemoryUserProfileRepository("users-apac");
        InMemoryUserProfileRepository eu = new InMemoryUserProfileRepository("users-eu");
        InMemoryUserProfileRepository us = new InMemoryUserProfileRepository("users-us");

        MultiDatabaseUserProfileRepository repository = new MultiDatabaseUserProfileRepository(Map.of(
                Region.APAC, apac,
                Region.EU, eu,
                Region.US, us
        ));

        repository.save(new UserProfile("u-1", "apac@example.com", Region.APAC));
        repository.save(new UserProfile("u-2", "eu@example.com", Region.EU));

        assertEquals(1, apac.size());
        assertEquals(1, eu.size());
        assertEquals(0, us.size());
        assertEquals("users-eu", repository.databaseNameFor(Region.EU));
        assertTrue(repository.findById("u-1").isPresent());
    }
}
