package com.eventservice.dto.event.search;

import com.eventservice.entity.event.EventStatus;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSearchRequest(
        String title,
        String city,
        EventStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startsAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startsAtTo
) {}
