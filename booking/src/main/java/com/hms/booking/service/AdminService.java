package com.hms.booking.service;

import com.hms.booking.controller.dto.UserDto;

import java.util.UUID;

public interface AdminService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUser(UUID id);
}
