package com.webdev.webdevelopment.auth;

public record UserProfileResponse(
        String name,
        String email,
        String phone,
        String companyName,
        String address
) {}
