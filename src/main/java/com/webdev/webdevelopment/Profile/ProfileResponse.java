package com.webdev.webdevelopment.Profile;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String address;
}