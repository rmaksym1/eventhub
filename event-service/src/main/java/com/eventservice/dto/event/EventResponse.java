package com.eventservice.dto.event;

import com.eventservice.entity.event.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Long venueId,
        String venueName,
        String city,
        BigDecimal ticketPrice,
        EventStatus status,
        Long organizerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
