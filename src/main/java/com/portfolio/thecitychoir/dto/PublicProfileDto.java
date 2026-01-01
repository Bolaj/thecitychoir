package com.portfolio.thecitychoir.dto;

import com.portfolio.thecitychoir.role.Role;

public record PublicProfileDto(
        String fullName,
        String email,
        String gender,
        String part,
        String phone,
        Role role,
        String regNumber
) {}

