package com.reservationservice.service;

import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.dto.ReservationResponse;
import com.reservationservice.dto.UpdateReservationStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ReservationService {
    ReservationResponse createReservation(Authentication authentication, CreateReservationRequest request);

    ReservationResponse getReservation(Long id, Authentication authentication);

    Page<ReservationResponse> getAllReservations(Pageable pageable, Authentication authentication);

    ReservationResponse updateReservationStatus(Long id, UpdateReservationStatusRequest request);

    void deleteReservation(Long id);

    void expireReservations();
}
