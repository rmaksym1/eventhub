package com.eventservice.controller.internal;

import com.eventservice.dto.event.EventResponse;
import com.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
@Tag(name = "Internal events endpoints", description = "Endpoints for internal service-service management")
public class InternalEventController {
    private final EventService eventService;

    @GetMapping("/{eventId}/ownership")
    public Boolean checkOrganizerOwnership(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {
        return eventService.isOwner(eventId, organizerId);
    }

    @GetMapping("/{id}")
    public EventResponse eventExists(
            @PathVariable Long id
    ) {
        return eventService.getEvent(id);
    }
}
