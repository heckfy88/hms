package com.hms.booking.service.impl;

import com.hms.booking.controller.dto.BookingDto;
import com.hms.booking.controller.dto.CreateBookingDto;
import com.hms.booking.dao.BookingRepository;
import com.hms.booking.dao.UserRepository;
import com.hms.booking.domain.Booking;
import com.hms.booking.domain.BookingStatus;
import com.hms.booking.domain.User;
import com.hms.booking.exception.InvalidRequestException;
import com.hms.booking.exception.ResourceNotFoundException;
import com.hms.booking.service.BookingService;
import com.hms.booking.service.HotelService;
import com.hms.booking.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final HotelService hotelService;

    @Transactional
    @Override
    public BookingDto create(CreateBookingDto dto) {
        if (dto == null) {
            throw new InvalidRequestException("CreateBookingDto is null");
        }
        if (dto.startDate() == null || dto.endDate() == null) {
            throw new InvalidRequestException("Start date or end date is null");
        }
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new InvalidRequestException("Start date must be before end date");
        }

        User user = getUserFromToken()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID roomId = dto.roomId();
        if (dto.autoSelect()) {
            roomId = hotelService.autoSelect();
        }
        if (roomId == null) {
            throw new ResourceNotFoundException("No available rooms");
        }

        Booking createdBooking = bookingRepository.save(
                Booking.newBooking(user, dto.startDate(), dto.endDate(), roomId)
        );

        log.info("Booking created with status PENDING: bookingId={}, requestId={}",
                createdBooking.getId(), createdBooking.getRqUid());

        try {
            hotelService.confirm(createdBooking);

            createdBooking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(createdBooking);

            log.info("Booking confirmed: bookingId={}", createdBooking.getId());
        } catch (Exception e) {
            log.error("Failed to confirm booking: bookingId={}, error={}",
                    createdBooking.getId(), e.getMessage());

            hotelService.release(createdBooking);

            createdBooking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(createdBooking);
        }

        return new BookingDto(createdBooking);
    }

    @Override
    public List<BookingDto> findAll() {
        User user = getUserFromToken()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(BookingDto::new)
                .toList();
    }

    @Override
    public BookingDto findById(UUID id) {
        if (id == null) {
            throw new InvalidRequestException("Booking id is null");
        }

        User user = getUserFromToken()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        ensureOwner(booking, user);

        return new BookingDto(booking);
    }

    @Override
    public void cancel(UUID id) {
        if (id == null) {
            throw new InvalidRequestException("Booking id is null");
        }

        User user = getUserFromToken()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        ensureOwner(booking, user);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidRequestException("Only confirmed bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        hotelService.release(booking);
        log.info("Booking cancelled: bookingId={}", id);
    }

    private void ensureOwner(Booking booking, User user) {
        if (booking.getUser() == null || booking.getUser().getId() == null) {
            // на всякий случай, если данные неконсистентны
            throw new ResourceNotFoundException("Booking has no owner");
        }
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Optional<User> getUserFromToken() {
        Jwt jwt = tokenUtil.getToken();
        if (jwt == null) {
            return Optional.empty();
        }

        Object subObj = jwt.getClaim("sub");
        if (subObj == null) {
            return Optional.empty();
        }

        String username = String.valueOf(subObj).trim();
        if (username.isEmpty()) {
            return Optional.empty();
        }

        return userRepository.findByUsername(username);
    }
}
