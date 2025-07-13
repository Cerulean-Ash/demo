package com.example.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {
    @NotBlank(message = "Address line 1 cannot be empty")
    @Size(max = 100, message = "Address line 1 cannot exceed 100 characters")
    private String line1;

    @Nullable
    @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
    private String line2;

    @Nullable
    @Size(max = 100, message = "Address line 3 cannot exceed 100 characters")
    private String line3;

    @NotBlank(message = "Town cannot be empty")
    @Size(max = 50, message = "Town cannot exceed 50 characters")
    private String town;

    @NotBlank(message = "Country cannot be empty")
    @Size(max = 50, message = "County cannot exceed 50 characters")
    private String county;

    @NotBlank(message = "Postcode cannot be empty")
    @Size(max = 20, message = "Postcode cannot exceed 20 characters")
    private String postcode;
}
