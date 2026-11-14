package com.reservationservice.controller;

import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.dto.ReservationResponse;
import com.reservationservice.dto.UpdateReservationStatusRequest;
import com.reservationservice.dto.search.ReservationSearchRequest;
import com.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation management", description = "Endpoints for reservation management")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new reservation")
    @PreAuthorize("hasRole('USER')")
    public ReservationResponse createReservation(
            Authentication authentication,
            @RequestBody @Valid CreateReservationRequest request
    ) {
        return reservationService.createReservation(authentication, request);
    }

    @GetMapping
    @Operation(summary = "Get all reservations")
    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reservationService.getAllReservations(pageable, authentication));
    }

    @GetMapping()
    @Operation(summary = "Search reservations")
    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> search(
            Authentication authentication,
            Pageable pageable,
            ReservationSearchRequest searchRequest
    ) {
        return ResponseEntity.ok(reservationService.search(authentication, pageable, searchRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by id")
    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER', 'ADMIN')")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reservationService.getReservation(id, authentication));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update reservation status by id")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateReservationStatusRequest request
    ) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation by id")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id
    ) {
        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

    @Scheduled(fixedDelay = 600000)
    public void expireReservations() {
        reservationService.expireReservations();
    }
}
