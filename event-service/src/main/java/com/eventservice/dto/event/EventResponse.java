package com.eventservice.dto.event;

import com.eventservice.entity.event.EventStatus;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Long venueId,
        String venueName,
        EventStatus status,
        Long organizerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
