package com.reservationservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class EventClient {

    private final RestClient eventRestClient;

    public void seatExists(Long seatId) {
        eventRestClient.get()
                .uri("/api/seats/{id}", seatId)
                .retrieve()
                .toBodilessEntity();
    }

    public void eventExists(Long eventId) {
        eventRestClient.get()
                .uri("/api/events/{id}", eventId)
                .retrieve()
                .toBodilessEntity();
    }

    public boolean isEventOwner(Long eventId, Long organizerId) {
        return eventRestClient.get()
                .uri(
                        "/internal/events/{eventId}/organizer/{organizerId}/ownership",
                        eventId,
                        organizerId
                )
                .retrieve()
                .body(Boolean.class);
    }
}