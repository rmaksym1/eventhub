package com.reservationservice.repository;

import com.reservationservice.entity.Reservation;
import com.reservationservice.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserIdAndId(Long userId, Long id);

    Page<Reservation> findAllByUserId(Pageable pageable, Long userId);

    List<Reservation> findAllByStatusAndExpiresAtBefore(
            ReservationStatus status,
            Instant time
    );
}
