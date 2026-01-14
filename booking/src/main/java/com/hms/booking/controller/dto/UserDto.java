package com.hms.booking.controller.dto;

import com.hms.booking.domain.User;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String password,
        String role
) {
    public UserDto(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

}