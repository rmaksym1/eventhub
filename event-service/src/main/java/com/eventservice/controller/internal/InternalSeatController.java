package com.eventservice.controller.internal;

import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/seats")
@RequiredArgsConstructor
@Tag(name = "Internal seats endpoints", description = "Endpoints for internal service-service seat management")
public class InternalSeatController {
    private final SeatService seatService;

    @GetMapping("/{id}")
    public SeatResponse seatExists(@PathVariable Long id) {
        return seatService.getSeat(id);
    }
}
