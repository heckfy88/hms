package com.hms.booking.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private UUID roomId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "rq_uid", unique = true)
    private UUID rqUid;


    public static Booking newBooking(User user, LocalDate startDate, LocalDate endDate, UUID roomId) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setRoomId(roomId);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setRqUid(UUID.randomUUID());
        return booking;
    }
}
