package com.example.javalabs.basic;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Demonstrates region-based routing across multiple logical databases.
 *
 * <p>In real systems, this type of router might be based on geography, tenant ID, compliance
 * rules, or workload separation between read and write databases.</p>
 */
public final class MultiDatabaseUserProfileRepository implements UserProfileRepository {

    private final Map<Region, InMemoryUserProfileRepository> repositoriesByRegion;

    public MultiDatabaseUserProfileRepository(Map<Region, InMemoryUserProfileRepository> repositoriesByRegion) {
        this.repositoriesByRegion = new EnumMap<>(repositoriesByRegion);
        for (Region region : Region.values()) {
            if (!this.repositoriesByRegion.containsKey(region)) {
                throw new IllegalArgumentException("missing repository for region " + region);
            }
        }
    }

    @Override
    public void save(UserProfile userProfile) {
        repositoryFor(userProfile.region()).save(userProfile);
    }

    @Override
    public Optional<UserProfile> findById(String userId) {
        for (InMemoryUserProfileRepository repository : repositoriesByRegion.values()) {
            Optional<UserProfile> result = repository.findById(userId);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the database name currently responsible for a region.
     *
     * @param region the target region
     * @return the database name handling that region
     */
    public String databaseNameFor(Region region) {
        return repositoryFor(region).databaseName();
    }

    private InMemoryUserProfileRepository repositoryFor(Region region) {
        return repositoriesByRegion.get(region);
    }
}
