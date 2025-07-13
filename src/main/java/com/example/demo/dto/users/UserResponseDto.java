package com.example.demo.dto.users;

import com.example.demo.model.Address;
import com.example.demo.model.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String name,
        Address address,
        String phoneNumber,
        String email,
        LocalDateTime createdTimestamp,
        LocalDateTime updatedTimestamp
) {

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getCreatedTimestamp(),
                user.getUpdatedTimestamp()
        );
    }
}

