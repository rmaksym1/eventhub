package com.reservationservice.service.impl;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventhub.common.security.AuthenticatedUser;
import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.dto.ReservationResponse;
import com.reservationservice.dto.UpdateReservationStatusRequest;
import com.reservationservice.entity.Reservation;
import com.reservationservice.entity.ReservationStatus;
import com.reservationservice.mapper.ReservationMapper;
import com.reservationservice.repository.ReservationRepository;
import com.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository repository;
    private final ReservationMapper reservationMapper;

    @Value("${reservation.expiration-time}")
    private long expirationTime;

    @Override
    @Transactional
    public ReservationResponse createReservation(Authentication authentication, CreateReservationRequest request) {
        Reservation reservation = reservationMapper.toEntity(request);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        reservation.setUserId(user.userId());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(Instant.now().plusSeconds(expirationTime));

        return reservationMapper.toDto(repository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Reservation reservation;

        if (hasElevatedRole(authentication)) {
            reservation = findReservation(id);
        } else {
            reservation = repository.findByUserIdAndId(authenticatedUser.userId(), id).orElseThrow(
                    () -> new EntityNotFoundException(
                            "Reservation by id: "
                                    + id + " and user id: "
                                    + authenticatedUser.userId() + " not found!"
                    )
            );
        }

        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getAllReservations(Pageable pageable, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Page<Reservation> reservations;

        if (hasElevatedRole(authentication)) {
            reservations = repository.findAll(pageable);
        } else {
            reservations = repository.findAllByUserId(pageable, authenticatedUser.userId());
        }

        return reservations.map(reservationMapper::toDto);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(Long id, UpdateReservationStatusRequest request) {
        Reservation reservation = findReservation(id);
        reservationMapper.updateStatus(request, reservation);
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        findReservation(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void expireReservations() {
        List<Reservation> reservations = repository.findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, Instant.now());

        for (Reservation reservation : reservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);
        }
    }

    private Reservation findReservation(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation by id: " + id + " not found!")
        );
    }

    private boolean hasElevatedRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_ORGANIZER") ||
                                role.equals("ROLE_ADMIN"));
    }
}
