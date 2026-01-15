package com.hms.hm.config;

import com.hms.hm.dao.HotelRepository;
import com.hms.hm.dao.RoomRepository;
import com.hms.hm.domain.Hotel;
import com.hms.hm.domain.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        log.info("Заполнение данных ");

        // ---- Hotels ----
        Hotel hilton = new Hotel();
        hilton.setName("Hilton London Bankside");
        hilton.setAddress("2-8 Great Suffolk St, London SE1 0UG");

        Hotel marriott = new Hotel();
        marriott.setName("London Marriott Hotel County Hall");
        marriott.setAddress("Westminster Bridge Rd, London SE1 7PB");

        Hotel ibis = new Hotel();
        ibis.setName("ibis London Blackfriars");
        ibis.setAddress("49 Blackfriars Rd, London SE1 8NZ");

        hilton = hotelRepository.save(hilton);
        marriott = hotelRepository.save(marriott);
        ibis = hotelRepository.save(ibis);

        // ---- Rooms ----
        List<Room> rooms = List.of(
                room(hilton, "101", true, 3),
                room(hilton, "102", true, 1),
                room(hilton, "103", false, 7),

                room(marriott, "201", true, 0),
                room(marriott, "202", true, 2),
                room(marriott, "203", false, 9),

                room(ibis, "301", true, 5),
                room(ibis, "302", true, 0),
                room(ibis, "303", true, 1)
        );

        roomRepository.saveAll(rooms);
    }

    private static Room room(Hotel hotel, String number, boolean available, int timesBooked) {
        Room r = new Room();
        r.setHotel(hotel);
        r.setNumber(number);
        r.setAvailable(available);
        r.setTimesBooked(timesBooked);
        return r;
    }
}