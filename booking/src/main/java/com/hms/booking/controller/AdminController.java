package com.hms.booking.controller;

import com.hms.booking.controller.dto.UserDto;
import com.hms.booking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return adminService.createUser(userDto);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return adminService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

}
