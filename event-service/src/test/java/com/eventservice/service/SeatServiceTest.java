package com.eventservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.dto.seat.search.SeatSearchRequest;
import com.eventservice.entity.Seat;
import com.eventservice.entity.Section;
import com.eventservice.mapper.SeatMapper;
import com.eventservice.repository.SeatRepository;
import com.eventservice.repository.SectionRepository;
import com.eventservice.service.impl.SeatServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository repository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Section section;
    private Seat seat;
    private SeatResponse seatResponse;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(1L);
        section.setName("Parter");

        seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);
        seat.setSection(section);

        seatResponse = new SeatResponse(1L, 1, 1, 1L, "Parter");
    }

    @Test
    @DisplayName("Should create seat and assign it to section")
    void shouldCreateSeat() {
        SeatRequest request = new SeatRequest(1, 1, 1L);
        Seat newSeat = new Seat();

        when(seatMapper.toEntity(request)).thenReturn(newSeat);
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(repository.save(newSeat)).thenReturn(newSeat);
        when(seatMapper.toDto(newSeat)).thenReturn(seatResponse);

        SeatResponse response = seatService.createSeat(request);

        assertThat(response).isEqualTo(seatResponse);
        assertThat(newSeat.getSection()).isSameAs(section);
        verify(repository).save(newSeat);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating seat in non-existent section")
    void shouldThrowWhenCreatingSeatInMissingSection() {
        SeatRequest request = new SeatRequest(1, 1, 99L);

        when(seatMapper.toEntity(request)).thenReturn(new Seat());
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.createSeat(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Section by id: 99");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get seat by ID")
    void shouldGetSeat() {
        when(repository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(seatResponse);

        assertThat(seatService.getSeat(1L)).isEqualTo(seatResponse);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when seat does not exist")
    void shouldThrowWhenSeatNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getSeat(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Seat by id: 99");
    }

    @Test
    @DisplayName("Should return paged seats")
    void shouldGetAllSeats() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(seat)));
        when(seatMapper.toDto(seat)).thenReturn(seatResponse);

        Page<SeatResponse> result = seatService.getAllSeats(pageable);

        assertThat(result.getContent()).containsExactly(seatResponse);
    }

    @Test
    @DisplayName("Should fall back to findAll(pageable) when search request has no filters")
    void shouldSearchWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(seat)));
        when(seatMapper.toDto(seat)).thenReturn(seatResponse);

        Page<SeatResponse> result = seatService.search(new SeatSearchRequest(null, null, null), pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository, never()).findAll(any(Predicate.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search with QueryDSL predicate when filters are provided")
    void shouldSearchWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(any(Predicate.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(seat)));
        when(seatMapper.toDto(seat)).thenReturn(seatResponse);

        Page<SeatResponse> result = seatService.search(new SeatSearchRequest(1, 1, 1L), pageable);

        assertThat(result.getContent()).containsExactly(seatResponse);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update seat data and section")
    void shouldUpdateSeat() {
        SeatRequest request = new SeatRequest(3, 4, 2L);
        Section newSection = new Section();
        newSection.setId(2L);
        SeatResponse expected = new SeatResponse(1L, 3, 4, 2L, "Balcony");

        when(repository.findById(1L)).thenReturn(Optional.of(seat));
        when(sectionRepository.findById(2L)).thenReturn(Optional.of(newSection));
        when(seatMapper.toDto(seat)).thenReturn(expected);

        SeatResponse response = seatService.updateSeat(request, 1L);

        assertThat(response).isEqualTo(expected);
        assertThat(seat.getSection()).isSameAs(newSection);
        verify(seatMapper).updateSeat(request, seat);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent seat")
    void shouldThrowWhenUpdatingMissingSeat() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.updateSeat(new SeatRequest(1, 1, 1L), 99L))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(seatMapper, sectionRepository);
    }

    @Test
    @DisplayName("Should delete seat")
    void shouldDeleteSeat() {
        when(repository.findById(1L)).thenReturn(Optional.of(seat));

        seatService.deleteSeat(1L);

        verify(repository).delete(seat);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent seat")
    void shouldThrowWhenDeletingMissingSeat() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.deleteSeat(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
