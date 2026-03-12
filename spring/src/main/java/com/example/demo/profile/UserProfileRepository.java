package com.example.demo.profile;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Primary database repository backed by Spring Data JPA.
 */
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, String> {
}
