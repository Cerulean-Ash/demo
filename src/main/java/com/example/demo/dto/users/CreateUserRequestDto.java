package com.example.demo.dto.users;

import com.example.demo.model.Address;
import com.example.demo.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateUserRequestDto(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @Valid
        @NotNull(message = "Address cannot be null")
        Address address,

        @NotBlank(message = "Phone number cannot be empty")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number is invalid")
        String phoneNumber
) {
        public static User toEntity(CreateUserRequestDto request) {
                return new User(
                        request.email(),
                        request.password(),
                        "USER",
                        request.name(),
                        request.address(),
                        request.phoneNumber()
                );
        }
}

