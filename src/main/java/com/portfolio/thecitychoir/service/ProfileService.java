package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.dto.AuthDTO;
import com.portfolio.thecitychoir.dto.RegistrationRequestDto;
import com.portfolio.thecitychoir.dto.RegistrationResponseDto;
import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.exceptions.EmailAlreadyRegisteredException;
import com.portfolio.thecitychoir.repository.ProfileRepository;
import com.portfolio.thecitychoir.role.Role;
import com.portfolio.thecitychoir.util.JWTUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor@Slf4j

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
    public RegistrationResponseDto register(RegistrationRequestDto requestDto)  {

        if (profileRepository.existsByEmail(requestDto.email())) {
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }

        ProfileEntity profile = toEntity(requestDto);

        String regNumber = generateRegistrationNumber(profile.getPart());
        profile.setRegistrationNumber(regNumber);

        profile.setActivationToken(UUID.randomUUID().toString());
        profile.setIsActive(false);

        profileRepository.save(profile);

        try {
            emailService.sendWelcomeEmail(profile);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Welcome email failed for {}", profile.getEmail(), e);
        }

        return toResponseDTO(profile);
    }
    public Role getRoleByEmail(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getRole)
                .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
    }
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            String email = authDTO.getEmail();
            String token = jwtUtil.generateToken(email);

            Role userRoleEnum = getRoleByEmail(email);
            String userRole = userRoleEnum.name();


            return Map.of("token", token,
                    "user", getPublicProfile(email),
                    "role", userRole
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

    @Transactional
    public ProfileEntity updateRole(String email, String newRole) {
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }

        return profileRepository.findByEmail(email)
                .map(profile -> {
                    profile.setRole(roleEnum);
                    return profileRepository.save(profile);
                })
                .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
    }


}
