package com.reservationservice.service;

import com.eventhub.common.exception.EntityNotFoundException;
import com.eventhub.common.security.AuthenticatedUser;
import com.reservationservice.client.EventClient;
import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.dto.ReservationResponse;
import com.reservationservice.dto.UpdateReservationStatusRequest;
import com.reservationservice.dto.search.ReservationSearchRequest;
import com.reservationservice.entity.Reservation;
import com.reservationservice.entity.ReservationStatus;
import com.reservationservice.mapper.ReservationMapper;
import com.reservationservice.repository.ReservationRepository;
import com.reservationservice.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private EventClient eventClient;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private AuthenticatedUser regularUser;
    private AuthenticatedUser adminUser;
    private Authentication regularAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservationService, "expirationTime", 900L);

        regularUser = new AuthenticatedUser(10L, "user@test.com");
        adminUser = new AuthenticatedUser(1L, "admin@test.com");

        regularAuth = new UsernamePasswordAuthenticationToken(
                regularUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        adminAuth = new UsernamePasswordAuthenticationToken(
                adminUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("Should successfully create a reservation")
    void shouldCreateReservation() {
        CreateReservationRequest request = new CreateReservationRequest(1L, 100L);
        Reservation reservation = new Reservation();
        Reservation savedReservation = new Reservation();
        ReservationResponse expectedResponse = new ReservationResponse(1L, 10L, 1L, 100L, ReservationStatus.PENDING, LocalDateTime.now(), Instant.now());

        doNothing().when(eventClient).seatExists(request.seatId());
        doNothing().when(eventClient).eventExists(request.eventId());
        when(reservationMapper.toEntity(request)).thenReturn(reservation);
        when(repository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toDto(savedReservation)).thenReturn(expectedResponse);

        ReservationResponse response = reservationService.createReservation(regularAuth, request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getUserId()).isEqualTo(regularUser.userId());
        verify(repository).save(reservation);
    }

    @Test
    @DisplayName("Should get reservation for regular user when it belongs to them")
    void shouldGetReservationForRegularUser() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setUserId(regularUser.userId());

        ReservationResponse expectedResponse = new ReservationResponse(reservationId, regularUser.userId(), 1L, 100L, ReservationStatus.PENDING, LocalDateTime.now(), Instant.now());

        when(repository.findByUserIdAndId(regularUser.userId(), reservationId)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDto(reservation)).thenReturn(expectedResponse);

        ReservationResponse response = reservationService.getReservation(reservationId, regularAuth);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when regular user requests non-existent or foreign reservation")
    void shouldThrowExceptionWhenRegularUserAccessesForeignReservation() {
        Long reservationId = 99L;

        when(repository.findByUserIdAndId(regularUser.userId(), reservationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservation(reservationId, regularAuth))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Reservation by id: " + reservationId);
    }

    @Test
    @DisplayName("Should allow admin to get any reservation by ID")
    void shouldAllowAdminToGetAnyReservation() {
        Long reservationId = 5L;
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setUserId(999L);

        ReservationResponse expectedResponse = new ReservationResponse(reservationId, 999L, 1L, 100L, ReservationStatus.CONFIRMED, LocalDateTime.now(), Instant.now());

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDto(reservation)).thenReturn(expectedResponse);

        ReservationResponse response = reservationService.getReservation(reservationId, adminAuth);

        assertThat(response).isNotNull();
        verify(repository).findById(reservationId);
    }

    @Test
    @DisplayName("Should update reservation status successfully")
    void shouldUpdateReservationStatus() {
        Long reservationId = 1L;
        UpdateReservationStatusRequest request = new UpdateReservationStatusRequest(ReservationStatus.CONFIRMED);
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        ReservationResponse expectedResponse = new ReservationResponse(reservationId, 10L, 1L, 100L, ReservationStatus.CONFIRMED, LocalDateTime.now(), Instant.now());

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservation));
        doNothing().when(reservationMapper).updateStatus(request, reservation);
        when(reservationMapper.toDto(reservation)).thenReturn(expectedResponse);

        ReservationResponse response = reservationService.updateReservationStatus(reservationId, request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationMapper).updateStatus(request, reservation);
    }

    @Test
    @DisplayName("Should delete reservation successfully")
    void shouldDeleteReservation() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservation));
        doNothing().when(repository).deleteById(reservationId);

        reservationService.deleteReservation(reservationId);

        verify(repository).deleteById(reservationId);
    }

    @Test
    @DisplayName("Should expire pending reservations correctly")
    void shouldExpireReservations() {
        Reservation res1 = new Reservation();
        res1.setStatus(ReservationStatus.PENDING);
        Reservation res2 = new Reservation();
        res2.setStatus(ReservationStatus.PENDING);

        when(repository.findAllByStatusAndExpiresAtBefore(eq(ReservationStatus.PENDING), any(Instant.now().getClass())))
                .thenReturn(List.of(res1, res2));

        reservationService.expireReservations();

        assertThat(res1.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(res2.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    @DisplayName("Should return paged reservations for regular user")
    void shouldGetAllReservationsForUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Reservation reservation = new Reservation();
        Page<Reservation> page = new PageImpl<>(List.of(reservation));

        when(repository.findAllByUserId(pageable, regularUser.userId())).thenReturn(page);
        when(reservationMapper.toDto(reservation)).thenReturn(
                new ReservationResponse(1L, regularUser.userId(), 1L, 100L, ReservationStatus.PENDING, LocalDateTime.now(), Instant.now())
        );

        Page<ReservationResponse> result = reservationService.getAllReservations(pageable, regularAuth);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByUserId(pageable, regularUser.userId());
    }
}