package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.RequestResponseDTO;
import com.portfolio.thecitychoir.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<RequestResponseDTO> signUp(@RequestBody RequestResponseDTO signUpRequest){
        return ResponseEntity.ok(userAuthService.register(signUpRequest));
    }
    @PostMapping("/signin")
    public ResponseEntity<RequestResponseDTO> signIn(@RequestBody RequestResponseDTO signInRequest){
        return ResponseEntity.ok(userAuthService.login(signInRequest));
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<RequestResponseDTO> refreshToken(@RequestBody RequestResponseDTO refreshTokenRequest){
        return ResponseEntity.ok(userAuthService.refreshToken(refreshTokenRequest));
    }
}
