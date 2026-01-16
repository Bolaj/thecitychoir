package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.AuthDTO;
import com.portfolio.thecitychoir.dto.PublicProfileDto;
import com.portfolio.thecitychoir.dto.RegistrationRequestDto;
import com.portfolio.thecitychoir.dto.RegistrationResponseDto;
import com.portfolio.thecitychoir.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & User Management", description = "Endpoints for registration, login, activation, and user roles")
public class RegisterController {

    private final ProfileService profileService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends an activation token")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = RegistrationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(
            @Valid @RequestBody RegistrationRequestDto dto
    ) {
        RegistrationResponseDto response = profileService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Activate user profile", description = "Activates a user account using an activation token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile activated successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found or already used")
    })
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activation token not found or already used.");
        }
    }

    @Operation(summary = "Get all users", description = "Returns all users (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users returned",
                    content = @Content(schema = @Schema(implementation = PublicProfileDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<PublicProfileDto>> getAllUsers() {
        List<PublicProfileDto> users = profileService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update user role", description = "Updates a user's role (SUPER_ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/users/{email}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String email,
            @RequestParam String role
    ) {
        profileService.updateRole(email, role);
        return ResponseEntity.ok(Map.of("message", "Role updated"));
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Profile not activated"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        log.info("REST request to login user: {}", authDTO.getEmail());
        try {
            if (!profileService.isProfileActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Profile is not active. Please activate your account."));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid email or password."));
        }
    }
}
