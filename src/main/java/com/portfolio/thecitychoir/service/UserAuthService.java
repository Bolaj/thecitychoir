package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.dto.RequestResponseDTO;
import com.portfolio.thecitychoir.entity.AppUser;
import com.portfolio.thecitychoir.repository.UserRepository;
import com.portfolio.thecitychoir.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RequestResponseDTO register(RequestResponseDTO registrationRequest){
        RequestResponseDTO response = new RequestResponseDTO();
        try{
            AppUser appUser = new AppUser();
            appUser.setEmail(registrationRequest.getEmail());
            appUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            appUser.setRole(registrationRequest.getRole());
            AppUser saveUser = userRepository.save(appUser);
            if(saveUser !=null && saveUser.getId() > 0){
                response.setAppUsers(saveUser);
                response.setMessage("You have successfully signed up");
                response.setStatusCode(200);
            }

        }catch(Exception e){

            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO login(RequestResponseDTO signInRequest){
        RequestResponseDTO response = new RequestResponseDTO();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
            var appUser = userRepository.findByEmail(signInRequest.getEmail()).orElseThrow();
            log.info("USER IS :" + appUser);
            var jwt = jwtUtils.generateToken(appUser);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), appUser);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("5MIN");
            response.setMessage("Successfully Signed In");

        }catch(Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());

        }
        return response;

    }
    public RequestResponseDTO refreshToken(RequestResponseDTO refreshTokenRequest){
        RequestResponseDTO response = new RequestResponseDTO();
        String userEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
        AppUser appUser = userRepository.findByEmail(userEmail).orElseThrow();
        if(jwtUtils.isTokenValid(refreshTokenRequest.getToken(), appUser)){
            var jwt = jwtUtils.generateToken(appUser);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            response.setExpirationTime("5MIN");
            response.setMessage("Token Successfully Refreshed");
        }
        response.setStatusCode(500);
        return response;
    }
}
