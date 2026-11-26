package com.eventservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SeatControllerTest extends AbstractEventServiceControllerTest {

    @Test
    @DisplayName("Should get seat by ID")
    void shouldGetSeatById() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.rowNumber", equalTo(1)))
                .andExpect(jsonPath("$.seatNumber", equalTo(1)))
                .andExpect(jsonPath("$.sectionId", equalTo(1)))
                .andExpect(jsonPath("$.sectionName", equalTo("Parter")));
    }

    @Test
    @DisplayName("Should return 404 when seat does not exist")
    void shouldReturnNotFoundForMissingSeat() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/seats/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all seats")
    void shouldGetAllSeats() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("Should search seats by section ID")
    void shouldSearchSeatsBySection() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/seats/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sectionId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].sectionId", everyItem(equalTo(1))));
    }

    @Test
    @DisplayName("Should return 401 when unauthenticated client requests seats")
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/seats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should create seat as admin")
    void shouldCreateSeat() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": 2,
                                    "seatNumber": 5,
                                    "sectionId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.rowNumber", equalTo(2)))
                .andExpect(jsonPath("$.seatNumber", equalTo(5)))
                .andExpect(jsonPath("$.sectionName", equalTo("Parter")));
    }

    @Test
    @DisplayName("Should return 409 when creating a duplicate seat")
    void shouldReturnConflictForDuplicateSeat() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": 1,
                                    "seatNumber": 1,
                                    "sectionId": 1
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 when seat request is invalid")
    void shouldReturnBadRequestForInvalidSeat() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": -1,
                                    "sectionId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when creating seat in non-existent section")
    void shouldReturnNotFoundForMissingSection() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": 1,
                                    "seatNumber": 1,
                                    "sectionId": 999
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when regular user tries to create a seat")
    void shouldForbidSeatCreationForUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": 9,
                                    "seatNumber": 9,
                                    "sectionId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update seat as admin")
    void shouldUpdateSeat() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/seats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "rowNumber": 7,
                                    "seatNumber": 8,
                                    "sectionId": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowNumber", equalTo(7)))
                .andExpect(jsonPath("$.seatNumber", equalTo(8)))
                .andExpect(jsonPath("$.sectionId", equalTo(2)))
                .andExpect(jsonPath("$.sectionName", equalTo("Balcony")));
    }

    @Test
    @DisplayName("Should delete seat as admin")
    void shouldDeleteSeat() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/seats/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/seats/3"))
                .andExpect(status().isNotFound());
    }
}
