package com.hms.hm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.hm.controller.api.HotelDto;
import com.hms.hm.service.HotelService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HotelControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean
    HotelService hotelService;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRole(String role) {
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return jwt().authorities(List.of(new SimpleGrantedAuthority(authority)));
    }

    // -------------------------
    // POST /api/hotels (ADMIN)
    // -------------------------

    @Test
    void createHotel_shouldReturn201_whenAdmin() throws Exception {
        HotelDto request = new HotelDto(null, "Hilton", "London");
        HotelDto response = new HotelDto(UUID.randomUUID(), "Hilton", "London");

        when(hotelService.createHotel(any(HotelDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/hotels")
                        .with(jwtRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Hilton"));

        verify(hotelService).createHotel(any(HotelDto.class));
    }

    @Test
    void createHotel_shouldReturn401_whenNoToken() throws Exception {
        HotelDto request = new HotelDto(null, "Hilton", "London");

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(hotelService);
    }

    @Test
    void createHotel_shouldReturn403_whenNotAdmin() throws Exception {
        HotelDto request = new HotelDto(null, "Hilton", "London");

        mockMvc.perform(post("/api/hotels")
                        .with(jwtRole("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(hotelService);
    }

    // -------------------------
    // GET /api/hotels (auth only)
    // -------------------------

    @Test
    void getAllHotels_shouldReturn200_whenAuthenticated() throws Exception {
        List<HotelDto> response = List.of(
                new HotelDto(UUID.randomUUID(), "Hilton", "London"),
                new HotelDto(UUID.randomUUID(), "Marriott", "Paris")
        );

        when(hotelService.getAllHotels()).thenReturn(response);

        mockMvc.perform(get("/api/hotels")
                        .with(jwtRole("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Hilton"));

        verify(hotelService).getAllHotels();
    }

    @Test
    void getAllHotels_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(hotelService);
    }
}