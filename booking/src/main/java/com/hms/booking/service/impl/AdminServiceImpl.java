package com.hms.booking.service.impl;

import com.hms.booking.dao.UserRepository;
import com.hms.booking.domain.User;
import com.hms.booking.controller.dto.UserDto;
import com.hms.booking.exception.InvalidRequestException;
import com.hms.booking.exception.ResourceAlreadyExistsException;
import com.hms.booking.exception.ResourceNotFoundException;
import com.hms.booking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.username())) {
            throw new ResourceAlreadyExistsException(
                    "User already exists with username: " + userDto.username()
            );
        }

        User userToBeCreated = User.fromDto(userDto);
        userToBeCreated.setPassword(passwordEncoder.encode(userDto.password()));

        User user = userRepository.save(userToBeCreated);
        return new UserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto.id() == null) {
            throw new InvalidRequestException("User ID must not be null");
        }

        User existingUser = userRepository.findById(userDto.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userDto.id()
                ));

        // бизнес-правила
        existingUser.setUsername(userDto.username());

        if (userDto.password() != null && !userDto.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.password()));
        }

        existingUser.setRole(userDto.role());

        User updatedUser = userRepository.save(existingUser);
        return new UserDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new InvalidRequestException("User ID must not be null");
        }

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}