package com.reservationservice.dto.search;

import com.reservationservice.entity.ReservationStatus;

public record ReservationSearchRequest(
        Long userId,
        Long eventId,
        ReservationStatus status
) {}
