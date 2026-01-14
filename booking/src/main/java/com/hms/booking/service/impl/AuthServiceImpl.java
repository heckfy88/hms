package com.hms.booking.service.impl;

import com.hms.booking.controller.dto.AuthDto;
import com.hms.booking.controller.dto.TokenDto;
import com.hms.booking.dao.UserRepository;
import com.hms.booking.domain.User;
import com.hms.booking.exception.InvalidRequestException;
import com.hms.booking.exception.ResourceAlreadyExistsException;
import com.hms.booking.service.AuthService;
import com.hms.booking.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    @Override
    public TokenDto register(AuthDto userDto) {
        if (userDto == null) {
            throw new InvalidRequestException("Request body is null");
        }

        if (userDto.username() == null || userDto.username().isBlank()) {
            throw new InvalidRequestException("Username is null or blank");
        }

        if (userDto.password() == null || userDto.password().isBlank()) {
            throw new InvalidRequestException("Password is null or blank");
        }

        if (userRepository.existsByUsername(userDto.username())) {
            throw new ResourceAlreadyExistsException(
                    "User already exists with username: " + userDto.username()
            );
        }

        User userToBeSaved = User.fromDto(userDto, "USER");
        userToBeSaved.setPassword(passwordEncoder.encode(userDto.password()));

        userRepository.save(userToBeSaved);

        String token = tokenUtil.generateToken(userToBeSaved.getUsername());
        return new TokenDto(token);
    }

    @Override
    public TokenDto login(AuthDto userDto) {
        if (userDto == null) {
            throw new InvalidRequestException("Request body is null");
        }
        if (userDto.username() == null || userDto.username().isBlank()) {
            throw new InvalidRequestException("Username is null or blank");
        }
        if (userDto.password() == null || userDto.password().isBlank()) {
            throw new InvalidRequestException("Password is null or blank");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.username(), userDto.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new TokenDto(tokenUtil.generateToken(authentication));
    }
}
