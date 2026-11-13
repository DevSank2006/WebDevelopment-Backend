package com.webdev.webdevelopment.Profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@CrossOrigin(origins =
        {"http://localhost:5173",
                "https://web-development-two-plum.vercel.app"})
@RequestMapping("/profile/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PutMapping("/change")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,Authentication authentication) {

        String email = authentication.getName();

        passwordService.changePassword(email, request);

        return ResponseEntity.ok("Password changed successfully");
    }
}