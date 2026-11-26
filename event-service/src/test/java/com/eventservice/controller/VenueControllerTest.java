package com.eventservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VenueControllerTest extends AbstractEventServiceControllerTest {

    @Test
    @DisplayName("Should get venue by ID")
    void shouldGetVenueById() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/venues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Main Hall")));
    }

    @Test
    @DisplayName("Should return 404 when venue does not exist")
    void shouldReturnNotFoundForMissingVenue() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/venues/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Venue by id: 999")));
    }

    @Test
    @DisplayName("Should get all venues")
    void shouldGetAllVenues() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("Should return 401 when unauthenticated client requests venues")
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/venues/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should create venue as admin")
    void shouldCreateVenue() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New Arena"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo("New Arena")));
    }

    @Test
    @DisplayName("Should return 400 when venue name is blank")
    void shouldReturnBadRequestForBlankName() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "  "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when regular user tries to create a venue")
    void shouldForbidVenueCreationForUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(post("/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New Arena"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update venue as admin")
    void shouldUpdateVenue() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Renamed Hall"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Renamed Hall")));
    }

    @Test
    @DisplayName("Should delete venue without sections and events as admin")
    void shouldDeleteVenue() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/venues/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/venues/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 409 when deleting a venue that still has sections or events")
    void shouldReturnConflictWhenDeletingVenueInUse() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/venues/1"))
                .andExpect(status().isConflict());
    }
}
