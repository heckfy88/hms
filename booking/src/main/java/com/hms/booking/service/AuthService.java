package com.hms.booking.service;

import com.hms.booking.controller.dto.AuthDto;
import com.hms.booking.controller.dto.TokenDto;

public interface AuthService {
    TokenDto register(AuthDto userDto);
    TokenDto login(AuthDto userDto);
}
