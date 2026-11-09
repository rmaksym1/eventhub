package com.reservationservice.dto;

import com.reservationservice.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReservationStatusRequest(
        @NotNull(message = "Status cannot be null!")
        ReservationStatus status
) {}
