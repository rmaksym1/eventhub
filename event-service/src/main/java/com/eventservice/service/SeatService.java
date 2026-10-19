package com.eventservice.service;

import com.eventservice.dto.seat.SeatRequest;
import com.eventservice.dto.seat.SeatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatService {
    SeatResponse createSeat(SeatRequest request);

    SeatResponse getSeat(Long id);

    Page<SeatResponse> getAllSeats(Pageable pageable);

    SeatResponse updateSeat(SeatRequest request, Long id);

    void deleteSeat(Long id);
}
