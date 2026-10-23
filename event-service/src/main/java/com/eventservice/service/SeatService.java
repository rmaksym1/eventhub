package com.eventservice.service;

import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import com.eventservice.dto.seat.search.SeatSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatService {
    SeatResponse createSeat(SeatRequest request);

    SeatResponse getSeat(Long id);

    Page<SeatResponse> getAllSeats(Pageable pageable);

    Page<SeatResponse> search(SeatSearchRequest request, Pageable pageable);

    SeatResponse updateSeat(SeatRequest request, Long id);

    void deleteSeat(Long id);
}
