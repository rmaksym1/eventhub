package com.eventservice.service;

import com.eventservice.dto.venue.VenueRequest;
import com.eventservice.dto.venue.VenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VenueService {
    VenueResponse createVenue(VenueRequest request);

    VenueResponse getVenue(Long id);

    Page<VenueResponse> getAllVenues(Pageable pageable);

    VenueResponse updateVenue(VenueRequest request, Long id);

    void deleteVenue(Long id);
}
