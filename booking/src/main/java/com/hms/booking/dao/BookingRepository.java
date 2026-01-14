package com.hms.booking.dao;

import com.hms.booking.domain.Booking;
import com.hms.booking.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
}
