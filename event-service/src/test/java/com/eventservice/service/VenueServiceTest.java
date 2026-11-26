package com.eventservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.venue.VenueRequest;
import com.eventservice.dto.venue.VenueResponse;
import com.eventservice.entity.Venue;
import com.eventservice.mapper.VenueMapper;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.impl.VenueServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository repository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueServiceImpl venueService;

    private Venue venue;
    private VenueResponse venueResponse;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Main Hall");

        venueResponse = new VenueResponse(1L, "Main Hall");
    }

    @Test
    @DisplayName("Should create venue")
    void shouldCreateVenue() {
        VenueRequest request = new VenueRequest("Main Hall");
        Venue newVenue = new Venue();

        when(venueMapper.toEntity(request)).thenReturn(newVenue);
        when(repository.save(newVenue)).thenReturn(venue);
        when(venueMapper.toDto(venue)).thenReturn(venueResponse);

        VenueResponse response = venueService.createVenue(request);

        assertThat(response).isEqualTo(venueResponse);
        verify(repository).save(newVenue);
    }

    @Test
    @DisplayName("Should get venue by ID")
    void shouldGetVenue() {
        when(repository.findById(1L)).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(venueResponse);

        assertThat(venueService.getVenue(1L)).isEqualTo(venueResponse);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when venue does not exist")
    void shouldThrowWhenVenueNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.getVenue(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Venue by id: 99");
    }

    @Test
    @DisplayName("Should return paged venues")
    void shouldGetAllVenues() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(venue)));
        when(venueMapper.toDto(venue)).thenReturn(venueResponse);

        Page<VenueResponse> result = venueService.getAllVenues(pageable);

        assertThat(result.getContent()).containsExactly(venueResponse);
    }

    @Test
    @DisplayName("Should update venue")
    void shouldUpdateVenue() {
        VenueRequest request = new VenueRequest("Renamed Hall");
        VenueResponse expected = new VenueResponse(1L, "Renamed Hall");

        when(repository.findById(1L)).thenReturn(Optional.of(venue));
        when(venueMapper.toDto(venue)).thenReturn(expected);

        VenueResponse response = venueService.updateVenue(request, 1L);

        assertThat(response).isEqualTo(expected);
        verify(venueMapper).updateVenue(request, venue);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent venue")
    void shouldThrowWhenUpdatingMissingVenue() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.updateVenue(new VenueRequest("X"), 99L))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(venueMapper);
    }

    @Test
    @DisplayName("Should delete venue")
    void shouldDeleteVenue() {
        when(repository.findById(1L)).thenReturn(Optional.of(venue));

        venueService.deleteVenue(1L);

        verify(repository).delete(venue);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent venue")
    void shouldThrowWhenDeletingMissingVenue() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.deleteVenue(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
