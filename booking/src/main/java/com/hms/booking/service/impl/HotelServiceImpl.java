package com.hms.booking.service.impl;

import com.hms.booking.client.HotelClient;
import com.hms.booking.client.dto.AvailabilityDto;
import com.hms.booking.controller.dto.RoomDto;
import com.hms.booking.domain.Booking;
import com.hms.booking.exception.ResourceNotFoundException;
import com.hms.booking.service.HotelService;
import com.hms.booking.util.TokenUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final TokenUtil tokenUtil;
    private final HotelClient hotelClient;

    @Retryable(
            retryFor = {FeignException.class, Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    @Override
    public void confirm(Booking booking) {
        log.info("Attempting to confirm availability with Hotel Service: bookingId={}, attempt", booking.getId());

        AvailabilityDto request = new AvailabilityDto(
                booking.getRqUid(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        hotelClient.confirm("Bearer " + tokenUtil.getToken(), booking.getRoomId(), request);
    }

    @Override
    public UUID autoSelect() {
        List<RoomDto> rooms = hotelClient.getRooms("Bearer " + tokenUtil.getToken());
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("No rooms");
        }
        return rooms.get(0).id();
    }

    public void release(Booking booking) {
        try {
            log.info("Release: bookingId={}, requestId={}", booking.getId(), booking.getRqUid());
            hotelClient.release("Bearer " + tokenUtil.getToken(), booking.getRoomId(), booking.getRqUid());
            log.info("Release OK: bookingId={}", booking.getId());
        } catch (Exception e) {
            log.error("Release failed: bookingId={}, error={}", booking.getId(), e.getMessage());
        }
    }
}
