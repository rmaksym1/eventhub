package com.eventservice.controller.internal;

import com.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {
    private final EventService eventService;

    @GetMapping("/{id}/ownership")
    @PreAuthorize("hasAuthority('SCOPE_INTERNAL')")
    public boolean checkOrganizerOwnership(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {
        return eventService.isOwner(eventId, organizerId);
    }
}
