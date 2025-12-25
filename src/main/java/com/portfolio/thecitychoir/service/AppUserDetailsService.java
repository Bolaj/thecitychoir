package com.portfolio.thecitychoir.service;


import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity profileEntity = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile Not Found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(profileEntity.getEmail())
                .password(profileEntity.getPassword()) // MUST be encoded
                .authorities(
                        List.of(new SimpleGrantedAuthority("ROLE_" + profileEntity.getRole().name()))
                )
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!profileEntity.getIsActive()) // respect profile activation
                .build();
    }


}
