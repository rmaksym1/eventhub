package com.reservationservice.dto;

import com.reservationservice.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long userId,
        Long eventId,
        Long seatId,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
