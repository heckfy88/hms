package com.hms.hm.dao;


import com.hms.hm.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("SELECT r FROM Room r WHERE r.available = true ORDER BY r.timesBooked ASC, r.id ASC")
    List<Room> findAvailableRoomsRecommended();
    List<Room> findByAvailableTrue();
}


