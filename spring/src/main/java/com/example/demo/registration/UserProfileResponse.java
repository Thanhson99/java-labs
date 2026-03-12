package com.example.demo.registration;

import com.example.demo.profile.Region;
import com.example.demo.profile.UserProfileEntity;

/**
 * Response model returned by the registration endpoints.
 */
public record UserProfileResponse(String userId, String email, Region region) {

    public static UserProfileResponse fromEntity(UserProfileEntity entity) {
        return new UserProfileResponse(entity.getUserId(), entity.getEmail(), entity.getRegion());
    }
}
