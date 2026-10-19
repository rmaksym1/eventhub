package com.eventservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventservice.dto.section.SectionRequest;
import com.eventservice.dto.section.SectionResponse;
import com.eventservice.entity.Section;
import com.eventservice.entity.Venue;
import com.eventservice.mapper.SectionMapper;
import com.eventservice.repository.SectionRepository;
import com.eventservice.repository.VenueRepository;
import com.eventservice.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {
    private final SectionRepository repository;
    private final VenueRepository venueRepository;
    private final SectionMapper sectionMapper;

    @Override
    @Transactional
    public SectionResponse createSection(SectionRequest request) {
        Section section = sectionMapper.toEntity(request);

        section.setVenue(findVenue(request.venueId()));

        return sectionMapper.toDto(repository.save(section));
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponse getSection(Long id) {
        return sectionMapper.toDto(findSection(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SectionResponse> getAllSections(Pageable pageable) {
        return repository.findAll(pageable).map(sectionMapper::toDto);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(SectionRequest request, Long id) {
        Section savedSection = findSection(id);

        sectionMapper.updateSection(request, savedSection);
        savedSection.setVenue(findVenue(request.venueId()));

        return sectionMapper.toDto(savedSection);
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        repository.delete(findSection(id));
    }

    private Section findSection(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Section by id: " + id + " not found!")
        );
    }

    private Venue findVenue(Long venueId) {
        return venueRepository.findById(venueId).orElseThrow(
                () -> new EntityNotFoundException("Venue by id: " + venueId + " not found!")
        );
    }
}
