package com.reservationservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class EventClient {
    private final RestClient eventRestClient;

    @Value("${app.internal.secret}")
    private String internalSecret;

    public void seatExists(Long seatId) {
        eventRestClient.get()
                .uri("/internal/seats/{id}", seatId)
                .header("X-Internal-Header", internalSecret)
                .retrieve()
                .toBodilessEntity();
    }

    public void eventExists(Long eventId) {
        eventRestClient.get()
                .uri("/internal/events/{id}", eventId)
                .header("X-Internal-Header", internalSecret)
                .retrieve()
                .toBodilessEntity();
    }

    public boolean isEventOwner(Long eventId, Long organizerId) {
        return eventRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/events/{eventId}/ownership")
                        .queryParam("organizerId", organizerId)
                        .build(eventId))
                .header("X-Internal-Header", internalSecret)
                .retrieve()
                .body(Boolean.class);
    }
}