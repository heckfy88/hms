package com.hms.hm.service;

import com.hms.hm.controller.api.AvailabilityDto;
import com.hms.hm.controller.api.RoomDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomDto createRoom(RoomDto roomDto);
    List<RoomDto> getRooms();
    List<RoomDto> getRecommendedRooms();
    void confirm(UUID id, AvailabilityDto availabilityDto);
    void release(UUID id, UUID rqUid);
}
