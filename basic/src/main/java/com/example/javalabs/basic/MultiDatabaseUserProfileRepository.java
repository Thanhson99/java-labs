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

    /**
     * Creates a region router from one repository per region.
     *
     * @param repositoriesByRegion repositories keyed by region
     * @throws IllegalArgumentException when the map is missing a region or contains invalid values
     */
    public MultiDatabaseUserProfileRepository(Map<Region, InMemoryUserProfileRepository> repositoriesByRegion) {
        if (repositoriesByRegion == null) {
            throw new IllegalArgumentException("repositoriesByRegion must not be null");
        }
        this.repositoriesByRegion = new EnumMap<>(repositoriesByRegion);
        for (Region region : Region.values()) {
            if (!this.repositoriesByRegion.containsKey(region) || this.repositoriesByRegion.get(region) == null) {
                throw new IllegalArgumentException("missing repository for region " + region);
            }
        }
    }

    /**
     * Saves a profile to the repository that owns its region.
     *
     * @param userProfile profile to save
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    @Override
    public void save(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        repositoryFor(userProfile.region()).save(userProfile);
    }

    /**
     * Searches all regional repositories for a profile id.
     *
     * @param userId user identifier
     * @return profile when found
     * @throws IllegalArgumentException when {@code userId} is blank
     */
    @Override
    public Optional<UserProfile> findById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
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
     * @throws IllegalArgumentException when {@code region} is {@code null}
     */
    public String databaseNameFor(Region region) {
        return repositoryFor(region).databaseName();
    }

    /**
     * Selects the repository for a region.
     *
     * @param region target region
     * @return repository assigned to that region
     * @throws IllegalArgumentException when {@code region} is {@code null}
     */
    private InMemoryUserProfileRepository repositoryFor(Region region) {
        if (region == null) {
            throw new IllegalArgumentException("region must not be null");
        }
        return repositoriesByRegion.get(region);
    }
}
