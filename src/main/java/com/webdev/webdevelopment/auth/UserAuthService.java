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

        try {
            System.out.println("========== CREATE ACCOUNT STARTED ==========");

            // STEP 1: Check whether email already exists
            Optional<User> existingUser;

            try {
                existingUser = userRepository.findByEmail(request.getEmail());
                System.out.println("STEP 1 SUCCESS: Email lookup completed");

            } catch (Exception e) {
                System.err.println("STEP 1 FAILED: Database email lookup");
                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to check existing user."
                );
            }

            // STEP 2: Check whether user is already verified
            if (existingUser.isPresent()
                    && existingUser.get().isEmailVerified()) {

                System.out.println("STEP 2: User already verified");

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "User already exists. Please login."
                );
            }

            // STEP 3: Generate verification code
            String code;

            try {
                code = String.format(
                        "%06d",
                        new SecureRandom().nextInt(1_000_000)
                );

                System.out.println("STEP 3 SUCCESS: Verification code generated");

            } catch (Exception e) {
                System.err.println("STEP 3 FAILED: Code generation");
                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to generate verification code."
                );
            }

            // STEP 4: Existing but unverified user
            if (existingUser.isPresent()) {

                User user = existingUser.get();

                try {
                    user.setVerificationCode(code);
                    user.setVerificationCodeExpiry(
                            LocalDateTime.now().plusMinutes(10)
                    );

                    userRepository.save(user);

                    System.out.println("STEP 4 SUCCESS: Existing user updated");

                } catch (Exception e) {
                    System.err.println("STEP 4 FAILED: Updating existing user");
                    e.printStackTrace();

                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Unable to update existing user."
                    );
                }

                // STEP 5: Send verification email
                try {
                    emailService.sendVerificationCode(
                            user.getEmail(),
                            code
                    );

                    System.out.println("STEP 5 SUCCESS: Verification email sent");

                } catch (Exception e) {
                    System.err.println("STEP 5 FAILED: Sending verification email");
                    e.printStackTrace();

                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Unable to send verification email."
                    );
                }

                System.out.println("========== CREATE ACCOUNT COMPLETED ==========");
                return;
            }

            // STEP 6: Create new user
            User user = new User();

            try {
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

                System.out.println("STEP 6 SUCCESS: User object created");

            } catch (Exception e) {
                System.err.println("STEP 6 FAILED: Creating user object");
                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to prepare user account."
                );
            }

            // STEP 7: Save user in database
            try {
                userRepository.save(user);

                System.out.println("STEP 7 SUCCESS: User saved to database");

            } catch (Exception e) {
                System.err.println("STEP 7 FAILED: Saving user to database");
                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to save user."
                );
            }

            // STEP 8: Send verification email
            try {
                emailService.sendVerificationCode(
                        user.getEmail(),
                        code
                );

                System.out.println("STEP 8 SUCCESS: Verification email sent");

            } catch (Exception e) {
                System.err.println("STEP 8 FAILED: Sending verification email");
                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to send verification email."
                );
            }

            System.out.println("========== CREATE ACCOUNT COMPLETED ==========");

        } catch (ResponseStatusException e) {

            // Preserve intentional HTTP errors (409, 500, etc.)
            throw e;

        } catch (Exception e) {

            System.err.println("UNEXPECTED ERROR IN CREATE ACCOUNT");
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error while creating account."
            );
        }
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
