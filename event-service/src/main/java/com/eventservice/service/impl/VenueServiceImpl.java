package com.eventservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.venue.VenueRequest;
import com.eventservice.dto.venue.VenueResponse;
import com.eventservice.entity.Venue;
import com.eventservice.mapper.VenueMapper;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {
    private final VenueRepository repository;
    private final VenueMapper venueMapper;

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);

        return venueMapper.toDto(repository.save(venue));
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse getVenue(Long id) {
        return venueMapper.toDto(findVenue(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VenueResponse> getAllVenues(Pageable pageable) {
        return repository.findAll(pageable).map(venueMapper::toDto);
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(VenueRequest request, Long id) {
        Venue savedVenue = findVenue(id);

        venueMapper.updateVenue(request, savedVenue);

        return venueMapper.toDto(savedVenue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        repository.delete(findVenue(id));
    }

    private Venue findVenue(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Venue by id: " + id + " not found!")
        );
    }
}
