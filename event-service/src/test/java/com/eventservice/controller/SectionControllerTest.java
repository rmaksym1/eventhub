package com.eventservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SectionControllerTest extends AbstractEventServiceControllerTest {

    @Test
    @DisplayName("Should get section by ID")
    void shouldGetSectionById() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/sections/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Parter")))
                .andExpect(jsonPath("$.venueId", equalTo(1)))
                .andExpect(jsonPath("$.venueName", equalTo("Main Hall")));
    }

    @Test
    @DisplayName("Should return 404 when section does not exist")
    void shouldReturnNotFoundForMissingSection() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/sections/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all sections")
    void shouldGetAllSections() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("Should create section as admin")
    void shouldCreateSection() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Lodge",
                                    "venueId": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo("Lodge")))
                .andExpect(jsonPath("$.venueName", equalTo("Small Room")));
    }

    @Test
    @DisplayName("Should return 404 when creating section in non-existent venue")
    void shouldReturnNotFoundForMissingVenue() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Lodge",
                                    "venueId": 999
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 when section name is blank")
    void shouldReturnBadRequestForBlankName() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(post("/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "venueId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when organizer tries to create a section")
    void shouldForbidSectionCreationForOrganizer() throws Exception {
        authenticateAs(10L, "organizer@gmail.com", "ORGANIZER");

        mockMvc.perform(post("/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Lodge",
                                    "venueId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update section as admin")
    void shouldUpdateSection() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/sections/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Upper Balcony",
                                    "venueId": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Upper Balcony")))
                .andExpect(jsonPath("$.venueId", equalTo(2)));
    }

    @Test
    @DisplayName("Should delete section without seats as admin")
    void shouldDeleteSection() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/sections/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 409 when deleting a section that still has seats")
    void shouldReturnConflictWhenDeletingSectionWithSeats() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/sections/1"))
                .andExpect(status().isConflict());
    }
}
