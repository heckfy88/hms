package com.hms.booking.client;

import com.hms.booking.client.dto.AvailabilityDto;
import com.hms.booking.controller.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hotel-service")
public interface HotelClient {

    @GetMapping("/api/rooms/recommend")
    List<RoomDto> getRooms(@RequestHeader("Authorization") String token);

    @PostMapping("/api/rooms/{id}/confirm-availability")
    void confirm(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID roomId,
            @RequestBody AvailabilityDto request
    );

    @PostMapping("/api/rooms/{id}/release")
    void release(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID roomId,
            @RequestParam("rqUid") UUID requestId
    );
}
