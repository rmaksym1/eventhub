package com.eventservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.dto.seat.search.SeatSearchRequest;
import com.eventservice.entity.QSeat;
import com.eventservice.entity.Seat;
import com.eventservice.entity.Section;
import com.eventservice.mapper.SeatMapper;
import com.eventservice.repository.SeatRepository;
import com.eventservice.repository.SectionRepository;
import com.eventservice.service.SeatService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository repository;
    private final SectionRepository sectionRepository;
    private final SeatMapper seatMapper;

    @Override
    @Transactional
    public SeatResponse createSeat(SeatRequest request) {
        Seat seat = seatMapper.toEntity(request);

        seat.setSection(findSection(request.sectionId()));

        return seatMapper.toDto(repository.save(seat));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponse getSeat(Long id) {
        return seatMapper.toDto(findSeat(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SeatResponse> getAllSeats(Pageable pageable) {
        return repository.findAll(pageable).map(seatMapper::toDto);
    }

    @Override
    public Page<SeatResponse> search(SeatSearchRequest request, Pageable pageable) {
        QSeat seat = QSeat.seat;
        BooleanBuilder where = new BooleanBuilder();

        if (request.rowNumber() != null) {
            where.and(seat.rowNumber.eq(request.rowNumber()));
        }

        if (request.seatNumber() != null) {
            where.and(seat.seatNumber.eq(request.seatNumber()));
        }

        if (request.sectionId() != null) {
            where.and(seat.section.id.eq(request.sectionId()));
        }

        Predicate predicate = where.getValue();

        if (predicate == null) {
            return repository.findAll(pageable)
                    .map(seatMapper::toDto);
        }

        return repository.findAll(predicate, pageable)
                .map(seatMapper::toDto);
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(SeatRequest request, Long id) {
        Seat savedSeat = findSeat(id);

        seatMapper.updateSeat(request, savedSeat);
        savedSeat.setSection(findSection(request.sectionId()));

        return seatMapper.toDto(savedSeat);
    }

    @Override
    @Transactional
    public void deleteSeat(Long id) {
        repository.delete(findSeat(id));
    }

    private Seat findSeat(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Seat by id: " + id + " not found!")
        );
    }

    private Section findSection(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(
                () -> new EntityNotFoundException("Section by id: " + sectionId + " not found!")
        );
    }
}
