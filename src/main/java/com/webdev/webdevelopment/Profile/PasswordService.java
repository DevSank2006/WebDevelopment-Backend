package com.webdev.webdevelopment.Profile;

import com.webdev.webdevelopment.User.User;
import com.webdev.webdevelopment.auth.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        // Find user using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Check current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (request.getNewPassword() == null ||
                request.getNewPassword().length() < 8) {

            throw new RuntimeException(
                    "New password must contain at least 8 characters");
        }

        // Encode new password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        // Save updated user
        userRepository.save(user);
    }
}
