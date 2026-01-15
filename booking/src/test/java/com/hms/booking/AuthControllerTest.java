package com.hms.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.booking.controller.dto.AuthDto;
import com.hms.booking.controller.dto.TokenDto;
import com.hms.booking.exception.InvalidRequestException;
import com.hms.booking.exception.ResourceAlreadyExistsException;
import com.hms.booking.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void register_shouldReturn201_whenOk() throws Exception {
        AuthDto request = new AuthDto("user1", "pass1");
        TokenDto response = new TokenDto("token-123");

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-123"));

        verify(authService).register(any());
    }

    @Test
    void register_shouldReturn400_whenUsernameBlank() throws Exception {
        // userDto.username() blank -> InvalidRequestException
        AuthDto request = new AuthDto("   ", "pass1");

        when(authService.register(any()))
                .thenThrow(new InvalidRequestException("Username is null or blank"));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).register(any());
    }

    @Test
    void register_shouldReturn400_whenPasswordBlank() throws Exception {
        AuthDto request = new AuthDto("user1", "   ");

        when(authService.register(any()))
                .thenThrow(new InvalidRequestException("Password is null or blank"));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).register(any());
    }

    @Test
    void register_shouldReturn409_whenUserAlreadyExists() throws Exception {
        AuthDto request = new AuthDto("user1", "pass1");

        when(authService.register(any()))
                .thenThrow(new ResourceAlreadyExistsException("User already exists with username: user1"));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(authService).register(any());
    }

    // --------------------
    // POST /user/auth
    // --------------------

    @Test
    void auth_shouldReturn200_whenOk() throws Exception {
        AuthDto request = new AuthDto("user1", "pass1");
        TokenDto response = new TokenDto("token-xyz");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-xyz"));

        verify(authService).login(any());
    }

    @Test
    void auth_shouldReturn400_whenUsernameBlank() throws Exception {
        AuthDto request = new AuthDto(" ", "pass1");

        when(authService.login(any()))
                .thenThrow(new InvalidRequestException("Username is null or blank"));

        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).login(any());
    }

    @Test
    void auth_shouldReturn400_whenPasswordBlank() throws Exception {
        AuthDto request = new AuthDto("user1", " ");

        when(authService.login(any()))
                .thenThrow(new InvalidRequestException("Password is null or blank"));

        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService).login(any());
    }

    @Test
    void auth_shouldReturn401_whenBadCredentials() throws Exception {
        AuthDto request = new AuthDto("user1", "wrong");

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any());
    }
}