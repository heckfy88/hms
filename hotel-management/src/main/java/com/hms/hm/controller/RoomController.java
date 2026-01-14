package com.hms.hm.controller;

import com.hms.hm.controller.api.AvailabilityDto;
import com.hms.hm.controller.api.RoomDto;
import com.hms.hm.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto createRoom(@RequestBody RoomDto dto) {
        return roomService.createRoom(dto);
    }

    @GetMapping
    public List<RoomDto> getRooms() {
        return roomService.getRooms();
    }

    @GetMapping("/recommend")
    public List<RoomDto> getRecommendedRooms() {
        return roomService.getRecommendedRooms();
    }

    @PostMapping("/{id}/confirm-availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable UUID id, @RequestBody AvailabilityDto request) {
        roomService.confirm(id, request);
    }

    @PostMapping("/{id}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(@PathVariable UUID id, @RequestParam UUID rqUid) {
        roomService.release(id, rqUid);
    }
}
