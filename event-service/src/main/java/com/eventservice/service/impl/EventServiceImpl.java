package com.eventservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventhub.common.security.AuthenticatedUser;
import com.eventservice.dto.event.CreateEventRequest;
import com.eventservice.dto.event.EventResponse;
import com.eventservice.dto.event.UpdateEventRequest;
import com.eventservice.entity.Venue;
import com.eventservice.entity.event.Event;
import com.eventservice.entity.event.EventStatus;
import com.eventservice.mapper.EventMapper;
import com.eventservice.repository.EventRepository;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.EventService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final VenueRepository venueRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse createEvent(Authentication authentication, CreateEventRequest request) {
        Event event = eventMapper.toEntity(request);

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        event.setVenue(findVenue(request.venueId()));
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizerId(user.userId());

        return eventMapper.toDto(repository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long id) {
        Event event = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Event by id: " + id + " not found!")
        );

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return repository.findAll(pageable).map(eventMapper::toDto);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Authentication authentication, UpdateEventRequest request, Long id) {
        Event event = findEventAndCheckOrganizer(id, authentication);

        eventMapper.updateEvent(request, event);
        event.setVenue(findVenue(request.venueId()));

        return eventMapper.toDto(event);
    }

    @Override
    public void deleteEvent(Authentication authentication, Long id) {
        findEventAndCheckOrganizer(id, authentication);

        repository.deleteById(id);
    }

    private Venue findVenue(Long venueId) {
        return venueRepository.findById(venueId).orElseThrow(
                () -> new EntityNotFoundException("Venue by id: " + venueId + " not found!")
        );
    }

    private Event findEventAndCheckOrganizer(Long eventId, Authentication authentication) {
        Event event = repository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event by id: " + eventId + " not found!")
        );

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        if (!event.getOrganizerId().equals(user.userId())) {
            throw new AccessDeniedException("You are not the organizer of this event");
        }

        return event;
    }
}
