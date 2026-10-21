package com.eventservice.service;

import com.eventservice.dto.section.SectionRequest;
import com.eventservice.dto.section.SectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SectionService {
    SectionResponse createSection(SectionRequest request);

    SectionResponse getSection(Long id);

    Page<SectionResponse> getAllSections(Pageable pageable);

    SectionResponse updateSection(SectionRequest request, Long id);

    void deleteSection(Long id);
}
