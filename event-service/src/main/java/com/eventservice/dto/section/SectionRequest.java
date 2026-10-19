package com.eventservice.dto.section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SectionRequest(
        @NotBlank(message = "Name cannot be blank!")
        @Size(max = 255, message = "Name cannot exceed 255 characters!")
        String name,
        @NotNull(message = "Venue id cannot be null!")
        @Positive(message = "Venue id must be positive!")
        Long venueId
) {}
