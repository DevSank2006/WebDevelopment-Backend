package com.webdev.webdevelopment.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {

    private String name;
    private String phone;
    private String companyName;
    private String address;
}
