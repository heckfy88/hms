package com.hms.hm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.hm.controller.api.AvailabilityDto;
import com.hms.hm.controller.api.RoomDto;
import com.hms.hm.service.RoomService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean
    RoomService roomService;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRole(String role) {
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return jwt().authorities(List.of(new SimpleGrantedAuthority(authority)));
    }

    // -------------------------
    // POST /api/rooms  (ADMIN)
    // -------------------------

    @Test
    void createRoom_shouldReturn201_whenAdmin() throws Exception {
        RoomDto req = mock(RoomDto.class);
        RoomDto res = mock(RoomDto.class);

        when(roomService.createRoom(any(RoomDto.class))).thenReturn(res);

        mockMvc.perform(post("/api/rooms")
                        .with(jwtRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(roomService).createRoom(any(RoomDto.class));
    }

    @Test
    void createRoom_shouldReturn401_whenNoToken() throws Exception {
        RoomDto req = mock(RoomDto.class);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roomService);
    }

    @Test
    void createRoom_shouldReturn403_whenNotAdmin() throws Exception {
        RoomDto req = mock(RoomDto.class);

        mockMvc.perform(post("/api/rooms")
                        .with(jwtRole("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roomService);
    }

    // -------------------------
    // GET /api/rooms
    // -------------------------

    @Test
    void getRooms_shouldReturn200_whenAuthenticated() throws Exception {
        when(roomService.getRooms()).thenReturn(List.of(mock(RoomDto.class), mock(RoomDto.class)));

        mockMvc.perform(get("/api/rooms")
                        .with(jwtRole("USER")))
                .andExpect(status().isOk());

        verify(roomService).getRooms();
    }

    @Test
    void getRooms_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roomService);
    }

    // -------------------------
    // GET /api/rooms/recommend
    // -------------------------

    @Test
    void getRecommendedRooms_shouldReturn200_whenAuthenticated() throws Exception {
        when(roomService.getRecommendedRooms()).thenReturn(List.of(mock(RoomDto.class)));

        mockMvc.perform(get("/api/rooms/recommend")
                        .with(jwtRole("USER")))
                .andExpect(status().isOk());

        verify(roomService).getRecommendedRooms();
    }

    @Test
    void getRecommendedRooms_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/rooms/recommend"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roomService);
    }

    // -------------------------
    // POST /api/rooms/{id}/confirm-availability
    // -------------------------

    @Test
    void confirm_shouldReturn204_whenAuthenticated() throws Exception {
        UUID roomId = UUID.randomUUID();
        AvailabilityDto req = mock(AvailabilityDto.class);

        doNothing().when(roomService).confirm(eq(roomId), any(AvailabilityDto.class));

        mockMvc.perform(post("/api/rooms/{id}/confirm-availability", roomId)
                        .with(jwtRole("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(roomService).confirm(eq(roomId), any(AvailabilityDto.class));
    }

    @Test
    void confirm_shouldReturn204_whenNoToken() throws Exception {
        UUID roomId = UUID.randomUUID();
        AvailabilityDto req = mock(AvailabilityDto.class);

        mockMvc.perform(post("/api/rooms/{id}/confirm-availability", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verifyNoInteractions(roomService);
    }

    // -------------------------
    // POST /api/rooms/{id}/release?rqUid=...
    // -------------------------

    @Test
    void release_shouldReturn204_whenAuthenticated() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID rqUid = UUID.randomUUID();

        doNothing().when(roomService).release(eq(roomId), eq(rqUid));

        mockMvc.perform(post("/api/rooms/{id}/release", roomId)
                        .with(jwtRole("USER"))
                        .param("rqUid", rqUid.toString()))
                .andExpect(status().isNoContent());

        verify(roomService).release(eq(roomId), eq(rqUid));
    }

    @Test
    void release_shouldReturn204() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID rqUid = UUID.randomUUID();

        mockMvc.perform(post("/api/rooms/{id}/release", roomId)
                        .param("rqUid", rqUid.toString()))
                .andExpect(status().isNoContent());

        verifyNoInteractions(roomService);
    }
}