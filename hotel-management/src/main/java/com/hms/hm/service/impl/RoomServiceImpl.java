package com.hms.hm.service.impl;

import com.hms.hm.controller.api.AvailabilityDto;
import com.hms.hm.controller.api.RoomDto;
import com.hms.hm.dao.HotelRepository;
import com.hms.hm.dao.RoomRepository;
import com.hms.hm.domain.Hotel;
import com.hms.hm.domain.Room;
import com.hms.hm.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    // в реальном сервисе был бы Caffeiine
    private final Map<UUID, Boolean> processedRequests = new ConcurrentHashMap<>();

    @Transactional
    @Override
    public RoomDto createRoom(RoomDto dto) {
        Hotel hotel = hotelRepository.findById(dto.hotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Room room = Room.fromDto(hotel, dto, 0);
        Room saved = roomRepository.save(room);

        log.info("Created room: {} in hotel: {}", saved.getNumber(), hotel.getName());
        return new RoomDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoomDto> getRooms() {
        return roomRepository.findByAvailableTrue().stream()
                .map(RoomDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoomDto> getRecommendedRooms() {
        return roomRepository.findAvailableRoomsRecommended().stream()
                .map(RoomDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void confirm(UUID roomId, AvailabilityDto request) {
        if (processedRequests.containsKey(request.rqUid())) {
            log.info("Request {} already processed (idempotency)", request.rqUid());
            return;
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getAvailable()) {
            throw new RuntimeException("Room is not available");
        }

        room.setTimesBooked(room.getTimesBooked() + 1);
        roomRepository.save(room);

        processedRequests.put(request.rqUid(), true);
        log.info("Confirmed availability for room {} (requestId: {})", roomId, request.rqUid());
    }

    @Transactional
    @Override
    public void release(UUID roomId, UUID rqUid) {
        if (!processedRequests.containsKey(rqUid)) {
            log.info("Request {} not found, nothing to release", rqUid);
            return;
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getTimesBooked() > 0) {
            room.setTimesBooked(room.getTimesBooked() - 1);
            roomRepository.save(room);
        }

        processedRequests.remove(rqUid);
        log.info("Released room {} (requestId: {})", roomId, rqUid);
    }
}