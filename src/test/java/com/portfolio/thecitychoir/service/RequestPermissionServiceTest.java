package com.portfolio.thecitychoir.service;

import com.portfolio.thecitychoir.repository.ProfileRepository;
import com.portfolio.thecitychoir.repository.RequestPermissionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RequestPermissionServiceTest {

    @Mock
    private RequestPermissionRepository repository;
    @Mock
    private ProfileService profileService;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RequestPermissionService service;

    private final String MOCK_EMAIL = "member@church.com";
    private final String ADMIN_EMAIL = "admin@church.com";


}