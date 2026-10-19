package com.eventservice.dto.seat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SeatRequest(
        @NotNull(message = "Row number cannot be null!")
        @Positive(message = "Row number must be positive!")
        Integer rowNumber,
        @NotNull(message = "Seat number cannot be null!")
        @Positive(message = "Seat number must be positive!")
        Integer seatNumber,
        @NotNull(message = "Section id cannot be null!")
        @Positive(message = "Section id must be positive!")
        Long sectionId
) {}
