package com.reservationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReservationRequest(
        @NotNull(message = "Event id cannot be null!")
        @Positive(message = "Event id must be positive!")
        Long eventId,
        @NotNull(message = "Seat id cannot be null!")
        @Positive(message = "Seat id must be positive!")
        Long seatId
) {}
