package com.eventservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest extends AbstractEventServiceControllerTest {

    private static final String VALID_CREATE_BODY = """
            {
                "title": "New Workshop",
                "description": "Hands-on workshop",
                "startsAt": "2030-08-01T10:00:00",
                "endsAt": "2030-08-01T12:00:00",
                "city": "Kosice",
                "ticketPrice": 15.50,
                "venueId": 1
            }
            """;

    @Test
    @DisplayName("Should get all events without authentication")
    void shouldGetAllEventsPublicly() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Should get event by ID")
    void shouldGetEventById() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo("Future Conference")))
                .andExpect(jsonPath("$.venueId", equalTo(1)))
                .andExpect(jsonPath("$.venueName", equalTo("Main Hall")))
                .andExpect(jsonPath("$.status", equalTo("PUBLISHED")))
                .andExpect(jsonPath("$.organizerId", equalTo(10)));
    }

    @Test
    @DisplayName("Should return 404 when event does not exist")
    void shouldReturnNotFoundForMissingEvent() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Event by id: 999")));
    }

    @Test
    @DisplayName("Should search events by city without authentication")
    void shouldSearchEventsByCity() throws Exception {
        mockMvc.perform(get("/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "city": "brati"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", equalTo("Future Concert")));
    }

    @Test
    @DisplayName("Should search events by price range and status")
    void shouldSearchEventsByPriceAndStatus() throws Exception {
        mockMvc.perform(get("/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "minPrice": 20,
                                    "maxPrice": 30,
                                    "status": "PUBLISHED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", equalTo(1)));
    }

    @Test
    @DisplayName("Should create event as DRAFT owned by the organizer")
    void shouldCreateEvent() throws Exception {
        authenticateAsOrganizer(10L);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", equalTo("New Workshop")))
                .andExpect(jsonPath("$.status", equalTo("DRAFT")))
                .andExpect(jsonPath("$.organizerId", equalTo(10)))
                .andExpect(jsonPath("$.venueName", equalTo("Main Hall")));
    }

    @Test
    @DisplayName("Should return 409 when creating an event overlapping another one in the same venue")
    void shouldReturnConflictForOverlappingEvent() throws Exception {
        authenticateAsOrganizer(10L);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Overlapping",
                                    "description": "description",
                                    "startsAt": "2030-06-01T11:00:00",
                                    "endsAt": "2030-06-01T13:00:00",
                                    "city": "Kosice",
                                    "ticketPrice": 10,
                                    "venueId": 1
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 when event ends before it starts")
    void shouldReturnBadRequestForInvalidDates() throws Exception {
        authenticateAsOrganizer(10L);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Broken",
                                    "description": "description",
                                    "startsAt": "2030-08-01T12:00:00",
                                    "endsAt": "2030-08-01T10:00:00",
                                    "city": "Kosice",
                                    "ticketPrice": 10,
                                    "venueId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when regular user tries to create an event")
    void shouldForbidEventCreationForUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 when unauthenticated client tries to create an event")
    void shouldRejectUnauthenticatedEventCreation() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should update event when current user is its organizer")
    void shouldUpdateEvent() throws Exception {
        authenticateAsOrganizer(10L);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Conference",
                                    "description": "updated",
                                    "startsAt": "2030-06-01T10:00:00",
                                    "endsAt": "2030-06-01T13:00:00",
                                    "city": "Presov",
                                    "venueId": 1,
                                    "status": "CANCELLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Updated Conference")))
                .andExpect(jsonPath("$.city", equalTo("Presov")))
                .andExpect(jsonPath("$.status", equalTo("CANCELLED")));
    }

    @Test
    @DisplayName("Should return 400 when updated event ends before it starts")
    void shouldReturnBadRequestForInvalidUpdateDates() throws Exception {
        authenticateAsOrganizer(10L);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Broken",
                                    "description": "description",
                                    "startsAt": "2030-06-01T12:00:00",
                                    "endsAt": "2030-06-01T10:00:00",
                                    "city": "Kosice",
                                    "venueId": 1,
                                    "status": "PUBLISHED"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when organizer updates an event they do not own")
    void shouldForbidUpdatingForeignEvent() throws Exception {
        authenticateAsOrganizer(20L);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Hijacked",
                                    "description": "description",
                                    "startsAt": "2030-06-01T10:00:00",
                                    "endsAt": "2030-06-01T12:00:00",
                                    "city": "Kosice",
                                    "venueId": 1,
                                    "status": "PUBLISHED"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete event when current user is its organizer")
    void shouldDeleteEvent() throws Exception {
        authenticateAsOrganizer(20L);

        mockMvc.perform(delete("/events/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/events/2"))
                .andExpect(status().isNotFound());
    }
}
