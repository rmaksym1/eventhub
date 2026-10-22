package com.eventservice.controller;

import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.dto.seat.search.SeatSearchRequest;
import com.eventservice.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Seat management", description = "Endpoints for seat management")
@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    @Operation(summary = "Create a new seat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatResponse> createSeat(
            @RequestBody @Valid SeatRequest request
    ) {
        return ResponseEntity.ok(seatService.createSeat(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seat by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<SeatResponse> getSeat(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(seatService.getSeat(id));
    }

    @GetMapping
    @Operation(summary = "Get all seats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<Page<SeatResponse>> getAllSeats(
            Pageable pageable
    ) {
        return ResponseEntity.ok(seatService.getAllSeats(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for seats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<Page<SeatResponse>> search(
            @RequestBody @Valid SeatSearchRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(seatService.search(request, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update seat's data by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long id,
            @RequestBody @Valid SeatRequest request
    ) {
        return ResponseEntity.ok(seatService.updateSeat(request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seat by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long id
    ) {
        seatService.deleteSeat(id);

        return ResponseEntity.noContent().build();
    }
}
