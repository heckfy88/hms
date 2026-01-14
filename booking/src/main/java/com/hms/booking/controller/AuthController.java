package com.hms.booking.controller;

import com.hms.booking.controller.dto.AuthDto;
import com.hms.booking.controller.dto.TokenDto;
import com.hms.booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDto register(@RequestBody AuthDto request) {
        return authService.register(request);
    }

    @PostMapping("/auth")
    public TokenDto authenticate(@RequestBody AuthDto request) {
        return authService.login(request);
    }
}
