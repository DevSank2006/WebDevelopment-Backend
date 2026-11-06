package com.webdev.webdevelopment.auth;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String name;
    private String phone;
    private String companyName;
    private String address;
}
