package com.hms.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.booking.controller.dto.UserDto;
import com.hms.booking.domain.User;
import com.hms.booking.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AdminService adminService;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRole(String role) {
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return jwt().authorities(List.of(new SimpleGrantedAuthority(authority)));
    }

    @Test
    void createUser_shouldReturnForbidden_whenNoToken() throws Exception {
        UserDto req = new UserDto(null, "newuser", "pass", "USER");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(adminService);
    }

    @Test
    void createUser_shouldReturn403_whenNotAdmin() throws Exception {
        UserDto req = new UserDto(null, "newuser", "pass", "USER");

        mockMvc.perform(post("/user")
                        .with(jwtRole("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminService);
    }

    @Test
    void createUser_shouldReturn200_whenAdmin() throws Exception {
        UserDto req = new UserDto(null, "newuser", "pass", "USER");
        UUID id = UUID.randomUUID();
        UserDto resp = new UserDto(new User(id, "newuser", "encoded", "USER"));

        when(adminService.createUser(any())).thenReturn(resp);

        mockMvc.perform(post("/user")
                        .with(jwtRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // --------------------
    // PATCH /user
    // --------------------

    @Test
    void updateUser_shouldReturn403_whenNoToken() throws Exception {
        UserDto request = new UserDto(
                UUID.randomUUID(), "user1", "newpass", "ADMIN"
        );

        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(adminService);
    }

    @Test
    void updateUser_shouldReturn403_whenNotAdmin() throws Exception {
        UserDto request = new UserDto(
                UUID.randomUUID(), "user1", "newpass", "ADMIN"
        );

        mockMvc.perform(patch("/user")
                        .with(jwtRole("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminService);
    }

    @Test
    void updateUser_shouldReturn200_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        UserDto request = new UserDto(id, "user1", "newpass", "ADMIN");
        UserDto response = new UserDto(new User(id, "user1", "encoded", "ADMIN"));

        when(adminService.updateUser(any())).thenReturn(response);

        mockMvc.perform(patch("/user")
                        .with(jwtRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(adminService).updateUser(any());
    }

    // --------------------
    // DELETE /user/{id}
    // --------------------

    @Test
    void deleteUser_shouldReturn403_whenNoToken() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/user/{id}", id))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(adminService);
    }

    @Test
    void deleteUser_shouldReturn403_whenNotAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/user/{id}", id)
                        .with(jwtRole("USER")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminService);
    }

    @Test
    void deleteUser_shouldReturn200_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(adminService).deleteUser(id);

        mockMvc.perform(delete("/user/{id}", id)
                        .with(jwtRole("ADMIN")))
                .andExpect(status().isOk());

        verify(adminService).deleteUser(id);
    }
}