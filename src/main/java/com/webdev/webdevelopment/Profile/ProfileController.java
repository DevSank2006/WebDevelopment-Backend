package com.webdev.webdevelopment.Profile;


import com.webdev.webdevelopment.User.User;
import com.webdev.webdevelopment.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    // GET USER PROFILE
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        ProfileResponse response = new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCompanyName(),
                user.getAddress()
        );

        return ResponseEntity.ok(response);
    }


    // UPDATE USER PROFILE
    @PutMapping("/update")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setCompanyName(request.getCompanyName());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);

        ProfileResponse response = new ProfileResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getCompanyName(),
                updatedUser.getAddress()
        );

        return ResponseEntity.ok(response);
    }
}