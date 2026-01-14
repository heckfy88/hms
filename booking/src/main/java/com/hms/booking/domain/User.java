package com.hms.booking.domain;

import com.hms.booking.controller.dto.AuthDto;
import com.hms.booking.controller.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public static User fromDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(userDto.password());
        user.setRole(userDto.role());
        return user;
    }

    public static User fromDto(AuthDto authDto, String role) {
        User user = new User();
        user.setUsername(authDto.username());
        user.setPassword(authDto.password());
        user.setRole(role);
        return user;
    }
}
