package com.eventservice.service;

import com.eventhub.common.exception.ConflictException;
import com.eventhub.common.exception.EntityNotFoundException;
import com.eventhub.common.security.AuthenticatedUser;
import com.eventservice.dto.event.CreateEventRequest;
import com.eventservice.dto.event.EventResponse;
import com.eventservice.dto.event.UpdateEventRequest;
import com.eventservice.dto.event.search.EventSearchRequest;
import com.eventservice.entity.Venue;
import com.eventservice.entity.event.Event;
import com.eventservice.entity.event.EventStatus;
import com.eventservice.mapper.EventMapper;
import com.eventservice.repository.EventRepository;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.impl.EventServiceImpl;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final LocalDateTime STARTS_AT = LocalDateTime.of(2030, 6, 1, 10, 0);
    private static final LocalDateTime ENDS_AT = LocalDateTime.of(2030, 6, 1, 12, 0);

    @Mock
    private EventRepository repository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private AuthenticatedUser organizer;
    private Authentication organizerAuth;
    private Authentication otherOrganizerAuth;
    private Venue venue;

    @BeforeEach
    void setUp() {
        organizer = new AuthenticatedUser(10L, "organizer@test.com");

        organizerAuth = new UsernamePasswordAuthenticationToken(
                organizer,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        otherOrganizerAuth = new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(99L, "other@test.com"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        venue = new Venue();
        venue.setId(1L);
        venue.setName("Main Hall");
    }

    private Event buildEvent(Long id, Long organizerId) {
        Event event = new Event();
        event.setId(id);
        event.setTitle("Conference");
        event.setOrganizerId(organizerId);
        event.setVenue(venue);
        event.setStatus(EventStatus.DRAFT);
        return event;
    }

    private EventResponse buildResponse(Long id, EventStatus status) {
        return new EventResponse(id, "Conference", "description", STARTS_AT, ENDS_AT,
                1L, "Main Hall", "Kosice", BigDecimal.TEN, status, organizer.userId(),
                LocalDateTime.now(), LocalDateTime.now());
    }

    private CreateEventRequest createRequest() {
        return new CreateEventRequest("Conference", "description", STARTS_AT, ENDS_AT,
                "Kosice", BigDecimal.TEN, 1L);
    }

    private UpdateEventRequest updateRequest() {
        return new UpdateEventRequest("Updated", "description", STARTS_AT, ENDS_AT,
                "Kosice", 1L, EventStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should create event as DRAFT and assign current user as organizer")
    void shouldCreateEvent() {
        CreateEventRequest request = createRequest();
        Event event = new Event();
        EventResponse expected = buildResponse(1L, EventStatus.DRAFT);

        when(repository.existsOverlapping(1L, STARTS_AT, ENDS_AT)).thenReturn(false);
        when(eventMapper.toEntity(request)).thenReturn(event);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(repository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(expected);

        EventResponse response = eventService.createEvent(organizerAuth, request);

        assertThat(response).isEqualTo(expected);
        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(event.getOrganizerId()).isEqualTo(organizer.userId());
        assertThat(event.getVenue()).isSameAs(venue);
        verify(repository).save(event);
    }

    @Test
    @DisplayName("Should throw ConflictException when creating an event overlapping another one in the same venue")
    void shouldThrowConflictWhenCreatingOverlappingEvent() {
        CreateEventRequest request = createRequest();

        when(repository.existsOverlapping(1L, STARTS_AT, ENDS_AT)).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(organizerAuth, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any());
        verifyNoInteractions(eventMapper, venueRepository);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating an event in a non-existent venue")
    void shouldThrowWhenCreatingEventInMissingVenue() {
        CreateEventRequest request = createRequest();

        when(repository.existsOverlapping(1L, STARTS_AT, ENDS_AT)).thenReturn(false);
        when(eventMapper.toEntity(request)).thenReturn(new Event());
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(organizerAuth, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Venue by id: 1");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get event by ID")
    void shouldGetEvent() {
        Event event = buildEvent(1L, organizer.userId());
        EventResponse expected = buildResponse(1L, EventStatus.DRAFT);

        when(repository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(expected);

        assertThat(eventService.getEvent(1L)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when event does not exist")
    void shouldThrowWhenEventNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEvent(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Event by id: 99");
    }

    @Test
    @DisplayName("Should return paged events")
    void shouldGetAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        Event event = buildEvent(1L, organizer.userId());

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(event)));
        when(eventMapper.toDto(event)).thenReturn(buildResponse(1L, EventStatus.DRAFT));

        Page<EventResponse> result = eventService.getAllEvents(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should fall back to findAll(pageable) when search request has no filters")
    void shouldSearchWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        EventSearchRequest request = new EventSearchRequest(" ", null, null, null, null, null, null);

        when(repository.findAll(pageable)).thenReturn(Page.empty());

        Page<EventResponse> result = eventService.search(request, pageable);

        assertThat(result).isEmpty();
        verify(repository).findAll(pageable);
        verify(repository, never()).findAll(any(Predicate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search with QueryDSL predicate when filters are provided")
    void shouldSearchWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        EventSearchRequest request = new EventSearchRequest(
                "conf", "Kosice", EventStatus.PUBLISHED,
                BigDecimal.ONE, BigDecimal.valueOf(100), STARTS_AT.minusDays(1), STARTS_AT.plusDays(1)
        );
        Event event = buildEvent(1L, organizer.userId());

        when(repository.findAll(any(Predicate.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(event)));
        when(eventMapper.toDto(event)).thenReturn(buildResponse(1L, EventStatus.PUBLISHED));

        Page<EventResponse> result = eventService.search(request, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(any(Predicate.class), eq(pageable));
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update event when current user is the organizer")
    void shouldUpdateEvent() {
        UpdateEventRequest request = updateRequest();
        Event event = buildEvent(1L, organizer.userId());
        EventResponse expected = buildResponse(1L, EventStatus.PUBLISHED);

        when(repository.findById(1L)).thenReturn(Optional.of(event));
        when(repository.existsOverlappingExcluding(1L, 1L, STARTS_AT, ENDS_AT)).thenReturn(false);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toDto(event)).thenReturn(expected);

        EventResponse response = eventService.updateEvent(organizerAuth, request, 1L);

        assertThat(response).isEqualTo(expected);
        verify(eventMapper).updateEvent(request, event);
        assertThat(event.getVenue()).isSameAs(venue);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when updating an event of another organizer")
    void shouldNotUpdateForeignEvent() {
        Event event = buildEvent(1L, organizer.userId());

        when(repository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(otherOrganizerAuth, updateRequest(), 1L))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(eventMapper);
    }

    @Test
    @DisplayName("Should throw ConflictException when updated event overlaps another event")
    void shouldThrowConflictWhenUpdatingToOverlappingTime() {
        Event event = buildEvent(1L, organizer.userId());

        when(repository.findById(1L)).thenReturn(Optional.of(event));
        when(repository.existsOverlappingExcluding(1L, 1L, STARTS_AT, ENDS_AT)).thenReturn(true);

        assertThatThrownBy(() -> eventService.updateEvent(organizerAuth, updateRequest(), 1L))
                .isInstanceOf(ConflictException.class);

        verifyNoInteractions(eventMapper);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent event")
    void shouldThrowWhenUpdatingMissingEvent() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(organizerAuth, updateRequest(), 99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete event when current user is the organizer")
    void shouldDeleteEvent() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildEvent(1L, organizer.userId())));

        eventService.deleteEvent(organizerAuth, 1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when deleting an event of another organizer")
    void shouldNotDeleteForeignEvent() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildEvent(1L, organizer.userId())));

        assertThatThrownBy(() -> eventService.deleteEvent(otherOrganizerAuth, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delegate ownership check to repository")
    void shouldCheckOwnership() {
        when(repository.existsByIdAndOrganizerId(1L, 10L)).thenReturn(true);
        when(repository.existsByIdAndOrganizerId(1L, 99L)).thenReturn(false);

        assertThat(eventService.isOwner(1L, 10L)).isTrue();
        assertThat(eventService.isOwner(1L, 99L)).isFalse();
    }
}
