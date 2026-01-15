package com.hms.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.booking.controller.dto.BookingDto;
import com.hms.booking.controller.dto.CreateBookingDto;
import com.hms.booking.exception.InvalidRequestException;
import com.hms.booking.exception.ResourceNotFoundException;
import com.hms.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    // helper: JWT с sub=username (как у тебя в BookingServiceImpl)
    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser(String username) {
        return jwt().jwt(j -> j
                .claim("sub", username)
                .claim("scope", "")
                .header("alg", "none")
                .tokenValue("test-token")
        );
    }

// -------------------------
// POST /booking
// -------------------------

    @Test
    void createBooking_shouldReturn201_whenOk() throws Exception {
        CreateBookingDto request = new CreateBookingDto(
                UUID.randomUUID(),
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        BookingDto response = mock(BookingDto.class);
        when(bookingService.create(any())).thenReturn(response);

        mockMvc.perform(post("/booking")
                        .with(jwtUser("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(bookingService).create(any());
    }

    @Test
    void createBooking_shouldReturn401_whenNoToken() throws Exception {
        CreateBookingDto request = new CreateBookingDto(
                UUID.randomUUID(),
                false,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

    @Test
    void createBooking_shouldReturn400_whenInvalidRequest() throws Exception {
        CreateBookingDto request = new CreateBookingDto(
                UUID.randomUUID(),
                false,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1) // start > end
        );

        when(bookingService.create(any()))
                .thenThrow(new InvalidRequestException("Start date must be before end date"));

        mockMvc.perform(post("/booking")
                        .with(jwtUser("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bookingService).create(any());
    }

    @Test
    void createBooking_shouldReturn404_whenNoRooms() throws Exception {
        CreateBookingDto request = new CreateBookingDto(
                null,
                true, // autoSelect -> может не найти комнат
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        when(bookingService.create(any()))
                .thenThrow(new ResourceNotFoundException("No available rooms"));

        mockMvc.perform(post("/booking")
                        .with(jwtUser("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(bookingService).create(any());
    }

// -------------------------
// GET /bookings
// -------------------------

    @Test
    void getUserBookings_shouldReturn200_whenOk() throws Exception {
        when(bookingService.findAll()).thenReturn(List.of(mock(BookingDto.class), mock(BookingDto.class)));

        mockMvc.perform(get("/bookings")
                        .with(jwtUser("user1")))
                .andExpect(status().isOk());

        verify(bookingService).findAll();
    }

    @Test
    void getUserBookings_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

// -------------------------
// GET /booking/{id}
// -------------------------

    @Test
    void getBooking_shouldReturn200_whenOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.findById(eq(id))).thenReturn(mock(BookingDto.class));

        mockMvc.perform(get("/booking/{id}", id)
                        .with(jwtUser("user1")))
                .andExpect(status().isOk());

        verify(bookingService).findById(eq(id));
    }

    @Test
    void getBooking_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/booking/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

    @Test
    void getBooking_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.findById(eq(id)))
                .thenThrow(new ResourceNotFoundException("Booking not found: " + id));

        mockMvc.perform(get("/booking/{id}", id)
                        .with(jwtUser("user1")))
                .andExpect(status().isNotFound());

        verify(bookingService).findById(eq(id));
    }

    @Test
    void getBooking_shouldReturn403_whenNotOwner() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookingService.findById(eq(id)))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/booking/{id}", id)
                        .with(jwtUser("user2")))
                .andExpect(status().isForbidden());

        verify(bookingService).findById(eq(id));
    }

// -------------------------
// DELETE /booking/{id}
// -------------------------

    @Test
    void cancelBooking_shouldReturn204_whenOk() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(bookingService).cancel(eq(id));

        mockMvc.perform(delete("/booking/{id}", id)
                        .with(jwtUser("user1")))
                .andExpect(status().isNoContent());

        verify(bookingService).cancel(eq(id));
    }

    @Test
    void cancelBooking_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(delete("/booking/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

    @Test
    void cancelBooking_shouldReturn400_whenInvalidRequest() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new InvalidRequestException("Only confirmed bookings can be cancelled"))
                .when(bookingService).cancel(eq(id));

        mockMvc.perform(delete("/booking/{id}", id)
                        .with(jwtUser("user1")))
                .andExpect(status().isBadRequest());

        verify(bookingService).cancel(eq(id));
    }

    @Test
    void cancelBooking_shouldReturn403_whenNotOwner() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new AccessDeniedException("Access denied"))
                .when(bookingService).cancel(eq(id));

        mockMvc.perform(delete("/booking/{id}", id)
                        .with(jwtUser("user2")))
                .andExpect(status().isForbidden());

        verify(bookingService).cancel(eq(id));
    }

    @Test
    void cancelBooking_shouldReturn404_whenBookingNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Booking not found: " + id))
                .when(bookingService).cancel(eq(id));

        mockMvc.perform(delete("/booking/{id}", id)
                        .with(jwtUser("user1")))
                .andExpect(status().isNotFound());

        verify(bookingService).cancel(eq(id));
    }
}