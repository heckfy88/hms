package com.hms.booking.config;


import com.hms.booking.dao.BookingRepository;
import com.hms.booking.dao.UserRepository;
import com.hms.booking.domain.Booking;
import com.hms.booking.domain.BookingStatus;
import com.hms.booking.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Заполнение данных");

        // --- Users ---
        User admin = new User(null, "admin", passwordEncoder.encode("admin123"), "ADMIN");
        User user1 = new User(null, "user1", passwordEncoder.encode("pass1"), "USER");
        User user2 = new User(null, "user2", passwordEncoder.encode("pass2"), "USER");

        admin = userRepository.save(admin);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // --- Bookings ---
        UUID roomA = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID roomB = UUID.fromString("22222222-2222-2222-2222-222222222222");

        Booking b1 = new Booking();
        b1.setUser(user1);
        b1.setRoomId(roomA);
        b1.setStartDate(LocalDate.now().plusDays(1));
        b1.setEndDate(LocalDate.now().plusDays(3));
        b1.setStatus(BookingStatus.CONFIRMED);
        b1.setCreatedAt(LocalDateTime.now().minusHours(3));
        b1.setRqUid(UUID.randomUUID());

        Booking b2 = new Booking();
        b2.setUser(user1);
        b2.setRoomId(roomB);
        b2.setStartDate(LocalDate.now().plusDays(10));
        b2.setEndDate(LocalDate.now().plusDays(12));
        b2.setStatus(BookingStatus.PENDING);
        b2.setCreatedAt(LocalDateTime.now().minusHours(1));
        b2.setRqUid(UUID.randomUUID());

        Booking b3 = new Booking();
        b3.setUser(user2);
        b3.setRoomId(roomA);
        b3.setStartDate(LocalDate.now().plusDays(5));
        b3.setEndDate(LocalDate.now().plusDays(7));
        b3.setStatus(BookingStatus.CANCELLED);
        b3.setCreatedAt(LocalDateTime.now().minusDays(1));
        b3.setRqUid(UUID.randomUUID());

        bookingRepository.save(b1);
        bookingRepository.save(b2);
        bookingRepository.save(b3);
    }
}