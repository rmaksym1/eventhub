package com.eventservice.dto.seat;

public record SeatResponse(
        Long id,
        Integer rowNumber,
        Integer seatNumber,
        Long sectionId,
        String sectionName
) {}
