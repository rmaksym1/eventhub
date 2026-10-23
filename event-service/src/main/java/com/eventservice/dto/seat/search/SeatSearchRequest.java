package com.eventservice.dto.seat.search;

public record SeatSearchRequest(
    Integer rowNumber,
    Integer seatNumber,
    Long sectionId
) {}
