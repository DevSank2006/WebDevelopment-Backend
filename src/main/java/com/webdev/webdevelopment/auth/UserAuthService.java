package com.webdev.webdevelopment.auth;


import com.webdev.webdevelopment.User.User;
import com.webdev.webdevelopment.auth.LoginRequest.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserAuthService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void createAccount(RegisterRequest request) {

        Optional<User> existingUser =
                userRepository.findByEmail(request.getEmail());

        // Case 1: User already exists and email is verified
        if (existingUser.isPresent()
                && existingUser.get().isEmailVerified()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists. Please login."
            );
        }

        // Generate a 6-digit verification code
        String code = String.format(
                "%06d",
                new SecureRandom().nextInt(1_000_000)
        );

        // Case 2: User exists but email is not verified
        if (existingUser.isPresent()) {

            User user = existingUser.get();

            user.setVerificationCode(code);
            user.setVerificationCodeExpiry(
                    LocalDateTime.now().plusMinutes(10)
            );

            userRepository.save(user);

            emailService.sendVerificationCode(
                    user.getEmail(),
                    code
            );

            return;
        }

        // Case 3: User does not exist — create a new user
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());
        user.setEmailVerified(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(
                LocalDateTime.now().plusMinutes(10)
        );

        userRepository.save(user);

        // Send the verification email
        emailService.sendVerificationCode(
                user.getEmail(),
                code
        );
    }


    public ResponseEntity<?> verifyAccount(VerifyEmailRequest request) {

        // 1. Check whether user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", "User not found"
                    ));
        }

        // 2. Check whether user is already verified
        if (user.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", HttpStatus.CONFLICT.value(),
                            "message", "Account is already verified"
                    ));
        }

        // 3. Check whether verification code exists
        if (user.getVerificationCode() == null ||
                user.getVerificationCode().isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", "Verification code not found. Please request a new code"
                    ));
        }

        // 4. Check verification code expiry
        if (user.getVerificationCodeExpiry() == null ||
                user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", "Verification code expired. Please request a new code"
                    ));
        }

        // 5. Match verification code
        if (!user.getVerificationCode().equals(request.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", "Invalid verification code"
                    ));
        }

        // 6. Mark account as verified
        user.setEmailVerified(true);

        // 7. Clear verification code and expiry
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Account verified successfully"
                )
        );
    }
    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return new UserProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCompanyName(),
                user.getAddress()
        );
    }

    @Transactional
    public UserProfileResponse updateMyProfile(
            String email,
            UpdateProfileRequest request
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setCompanyName(request.getCompanyName());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return getMyProfile(email);
    }
}
