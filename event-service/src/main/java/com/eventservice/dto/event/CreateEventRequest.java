package com.eventservice.dto.event;

import com.eventservice.validation.ValidEventDates;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@ValidEventDates
public record CreateEventRequest(
        @NotBlank(message = "Title cannot be blank!")
        @Size(max = 255, message = "Title cannot exceed 255 characters!")
        String title,
        @NotBlank(message = "Description cannot be blank!")
        @Size(max = 255, message = "Description cannot exceed 255 characters!")
        String description,
        @NotNull(message = "Start date cannot be null!")
        @Future(message = "Event cannot start in the past!")
        LocalDateTime startsAt,
        @NotNull(message = "End date cannot be null!")
        @Future(message = "Event cannot end in the past!")
        LocalDateTime endsAt,
        @NotNull(message = "Venue id cannot be null!")
        @Positive(message = "Venue id must be positive!")
        Long venueId
) {}
