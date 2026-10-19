package com.eventservice.dto.section;

public record SectionResponse(
        Long id,
        String name,
        Long venueId,
        String venueName
) {}
