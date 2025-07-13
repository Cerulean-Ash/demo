package com.example.demo.dto.users;

import com.example.demo.model.Address;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        @Nullable
        String email,

        @Size(max = 100, message = "Name cannot exceed 100 characters")
        @Nullable
        String name,

        @Valid
        @Nullable
        Address address,

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number is invalid")
        @Nullable
        String phoneNumber
) {}
