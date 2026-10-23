package com.eventservice.dto.event;

import com.eventservice.entity.event.EventStatus;
import com.eventservice.validation.ValidEventDates;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@ValidEventDates
public record UpdateEventRequest(
        @NotBlank(message = "Title cannot be blank!")
        @Size(max = 255, message = "Title cannot exceed 255 characters!")
        String title,
        @NotBlank(message = "Description cannot be blank!")
        @Size(max = 255, message = "Description cannot exceed 255 characters!")
        String description,
        @NotNull(message = "Start date cannot be null!")
        @Future(message = "Event cannot start in the past!")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startsAt,
        @NotNull(message = "End date cannot be null!")
        @Future(message = "Event cannot end in the past!")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endsAt,
        @NotBlank(message = "City name cannot be blank!")
        @Size(max = 127, message = "City name cannot be more than 127 characters long")
        String city,
        @NotNull(message = "Venue id cannot be null!")
        @Positive(message = "Venue id must be positive!")
        Long venueId,
        @NotNull(message = "Status cannot be null!")
        EventStatus status
) {}
