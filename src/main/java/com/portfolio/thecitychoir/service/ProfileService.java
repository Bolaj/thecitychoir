package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.dto.AuthDTO;
import com.portfolio.thecitychoir.dto.RegistrationRequestDto;
import com.portfolio.thecitychoir.dto.RegistrationResponseDto;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.repository.ProfileRepository;
import com.portfolio.thecitychoir.util.JWTUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private RegistrationRequestDto toDTO(ProfileEntity profileEntity) {
        return new RegistrationRequestDto(
                profileEntity.getFullName(),
                profileEntity.getGender(),
                profileEntity.getEmail(),
                profileEntity.getPart(),
                profileEntity.getPhone(),
                null // Do not expose password
        );
    }

    private ProfileEntity toEntity(RegistrationRequestDto dto) {
        return ProfileEntity.builder()
                .fullName(dto.fullName())
                .gender(dto.gender())
                .email(dto.email())
                .phone(dto.phone())
                .part(dto.part())
                .password(passwordEncoder.encode(dto.password()))
                .isActive(false)
                .build();
    }

    private RegistrationResponseDto toResponseDTO(ProfileEntity entity) {
        return RegistrationResponseDto.builder()
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .part(entity.getPart())
                .registrationNumber(entity.getRegistrationNumber())
                .build();
    }

    private String generateRegistrationNumber(String part) {
        String prefix = switch (part.toUpperCase()) {
            case "SOP", "ALT", "TEN", "BAS" -> part.toUpperCase();
            default -> throw new IllegalArgumentException("Invalid choir part");
        };

        int year = LocalDateTime.now().getYear();
        long count = profileRepository.countByPart(prefix) + 1;

        return String.format("%s/%d/%03d", prefix, year, count);
    }

    @Transactional
    public RegistrationResponseDto register(RegistrationRequestDto requestDto)
            throws MessagingException, UnsupportedEncodingException {

        if (profileRepository.existsByEmail(requestDto.email())) {
            throw new RuntimeException("Email is already registered");
        }

        ProfileEntity profile = toEntity(requestDto);

        String regNumber = generateRegistrationNumber(profile.getPart());
        profile.setRegistrationNumber(regNumber);

        profile.setActivationToken(UUID.randomUUID().toString());
        profile.setIsActive(false);

        profileRepository.save(profile);

        emailService.sendWelcomeEmail(profile);

        return toResponseDTO(profile);
    }
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of("token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public boolean activateProfile(String activationToken){

        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profile.setActivationToken(null);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);

    }
    public boolean isProfileActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found for email: " + authentication.getName()));

    }
    public RegistrationRequestDto getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if(email == null){
            currentUser = getCurrentProfile();
        }else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
        }
        return RegistrationRequestDto.builder()
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .part(currentUser.getPart())
                .gender(currentUser.getGender())
                .phone(currentUser.getPhone())
                .build();
    }
}
