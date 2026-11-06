package com.webdev.webdevelopment.auth;

import com.webdev.webdevelopment.User.User;
import com.webdev.webdevelopment.auth.LoginRequest.LoginRequest;
import com.webdev.webdevelopment.auth.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserAuthController(UserAuthService userAuthService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userAuthService = userAuthService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createAccount(
            @RequestBody RegisterRequest request) {
        // Create account and send verification code
        userAuthService.createAccount(request);
        return ResponseEntity.ok(
                "Verification code sent to your email."
        );
    }
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(
            Authentication authentication
    ) {
        return userAuthService.getMyProfile(authentication.getName());
    }
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(
                userAuthService.updateMyProfile(authentication.getName(), request)
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyAccount(
            @RequestBody VerifyEmailRequest request) {

        return userAuthService.verifyAccount(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

         User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow();

            if (!user.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Please verify your email first");
            }

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed");
        }
    }

}
