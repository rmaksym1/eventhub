package com.eventservice.controller;

import com.eventservice.dto.venue.VenueRequest;
import com.eventservice.dto.venue.VenueResponse;
import com.eventservice.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Venue management", description = "Endpoints for venue management")
@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @PostMapping
    @Operation(summary = "Create a new venue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponse> createVenue(
            @RequestBody @Valid VenueRequest request
    ) {
        return ResponseEntity.ok(venueService.createVenue(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get venue by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<VenueResponse> getVenue(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(venueService.getVenue(id));
    }

    @GetMapping
    @Operation(summary = "Get all venues")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<Page<VenueResponse>> getAllVenues(
            Pageable pageable
    ) {
        return ResponseEntity.ok(venueService.getAllVenues(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update venue's data by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable Long id,
            @RequestBody @Valid VenueRequest request
    ) {
        return ResponseEntity.ok(venueService.updateVenue(request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete venue by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVenue(
            @PathVariable Long id
    ) {
        venueService.deleteVenue(id);

        return ResponseEntity.noContent().build();
    }
}
