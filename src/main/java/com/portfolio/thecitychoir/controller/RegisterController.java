package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.AuthDTO;
import com.portfolio.thecitychoir.dto.RegistrationRequestDto;
import com.portfolio.thecitychoir.dto.RegistrationResponseDto;
import com.portfolio.thecitychoir.service.ProfileService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(
            @Valid @RequestBody RegistrationRequestDto dto
    ) {
        RegistrationResponseDto response = profileService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used.");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try{
            if(!profileService.isProfileActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( Map.of("message", "Profile is not active. Please activate your account."));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid email or password."));
        }
    }

}
