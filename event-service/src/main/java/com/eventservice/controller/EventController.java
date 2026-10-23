package com.eventservice.controller;

import com.eventservice.dto.event.CreateEventRequest;
import com.eventservice.dto.event.EventResponse;
import com.eventservice.dto.event.UpdateEventRequest;
import com.eventservice.dto.event.search.EventSearchRequest;
import com.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Event management", description = "Endpoints for event management")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> createEvent(
            Authentication authentication,
            @RequestBody @Valid CreateEventRequest request
    ) {
        return ResponseEntity.ok(eventService.createEvent(authentication, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by id")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for events")
    public ResponseEntity<Page<EventResponse>> search(
            @RequestBody @Valid EventSearchRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.search(request, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event's data by id")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody @Valid UpdateEventRequest request
    ) {
        return ResponseEntity.ok(eventService.updateEvent(authentication, request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event by id")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            Authentication authentication,
            @PathVariable Long id
    ) {
        eventService.deleteEvent(authentication, id);

        return ResponseEntity.noContent().build();
    }
}
