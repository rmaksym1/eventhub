package com.eventservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.section.SectionRequest;
import com.eventservice.dto.section.SectionResponse;
import com.eventservice.entity.Section;
import com.eventservice.entity.Venue;
import com.eventservice.mapper.SectionMapper;
import com.eventservice.repository.SectionRepository;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.impl.SectionServiceImpl;
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
class SectionServiceTest {

    @Mock
    private SectionRepository repository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SectionMapper sectionMapper;

    @InjectMocks
    private SectionServiceImpl sectionService;

    private Venue venue;
    private Section section;
    private SectionResponse sectionResponse;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Main Hall");

        section = new Section();
        section.setId(1L);
        section.setName("Parter");
        section.setVenue(venue);

        sectionResponse = new SectionResponse(1L, "Parter", 1L, "Main Hall");
    }

    @Test
    @DisplayName("Should create section and assign it to venue")
    void shouldCreateSection() {
        SectionRequest request = new SectionRequest("Parter", 1L);
        Section newSection = new Section();

        when(sectionMapper.toEntity(request)).thenReturn(newSection);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(repository.save(newSection)).thenReturn(newSection);
        when(sectionMapper.toDto(newSection)).thenReturn(sectionResponse);

        SectionResponse response = sectionService.createSection(request);

        assertThat(response).isEqualTo(sectionResponse);
        assertThat(newSection.getVenue()).isSameAs(venue);
        verify(repository).save(newSection);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating section in non-existent venue")
    void shouldThrowWhenCreatingSectionInMissingVenue() {
        SectionRequest request = new SectionRequest("Parter", 99L);

        when(sectionMapper.toEntity(request)).thenReturn(new Section());
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.createSection(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Venue by id: 99");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get section by ID")
    void shouldGetSection() {
        when(repository.findById(1L)).thenReturn(Optional.of(section));
        when(sectionMapper.toDto(section)).thenReturn(sectionResponse);

        assertThat(sectionService.getSection(1L)).isEqualTo(sectionResponse);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when section does not exist")
    void shouldThrowWhenSectionNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.getSection(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Section by id: 99");
    }

    @Test
    @DisplayName("Should return paged sections")
    void shouldGetAllSections() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(section)));
        when(sectionMapper.toDto(section)).thenReturn(sectionResponse);

        Page<SectionResponse> result = sectionService.getAllSections(pageable);

        assertThat(result.getContent()).containsExactly(sectionResponse);
    }

    @Test
    @DisplayName("Should update section data and venue")
    void shouldUpdateSection() {
        SectionRequest request = new SectionRequest("VIP", 2L);
        Venue newVenue = new Venue();
        newVenue.setId(2L);
        SectionResponse expected = new SectionResponse(1L, "VIP", 2L, "Small Room");

        when(repository.findById(1L)).thenReturn(Optional.of(section));
        when(venueRepository.findById(2L)).thenReturn(Optional.of(newVenue));
        when(sectionMapper.toDto(section)).thenReturn(expected);

        SectionResponse response = sectionService.updateSection(request, 1L);

        assertThat(response).isEqualTo(expected);
        assertThat(section.getVenue()).isSameAs(newVenue);
        verify(sectionMapper).updateSection(request, section);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating section to non-existent venue")
    void shouldThrowWhenUpdatingSectionToMissingVenue() {
        SectionRequest request = new SectionRequest("VIP", 99L);

        when(repository.findById(1L)).thenReturn(Optional.of(section));
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.updateSection(request, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Venue by id: 99");
    }

    @Test
    @DisplayName("Should delete section")
    void shouldDeleteSection() {
        when(repository.findById(1L)).thenReturn(Optional.of(section));

        sectionService.deleteSection(1L);

        verify(repository).delete(section);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent section")
    void shouldThrowWhenDeletingMissingSection() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.deleteSection(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
