package com.eventservice.service;

import com.eventservice.dto.event.CreateEventRequest;
import com.eventservice.dto.event.EventResponse;
import com.eventservice.dto.event.UpdateEventRequest;
import com.eventservice.dto.event.search.EventSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface EventService {
    EventResponse createEvent(Authentication authentication, CreateEventRequest request);

    EventResponse getEvent(Long id);

    Page<EventResponse> getAllEvents(Pageable pageable);

    Page<EventResponse> search(EventSearchRequest request, Pageable pageable);

    EventResponse updateEvent(Authentication authentication, UpdateEventRequest request, Long id);

    void deleteEvent(Authentication authentication, Long id);
}
