package com.eventservice.controller.internal;

import com.eventservice.controller.AbstractEventServiceControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InternalSeatControllerTest extends AbstractEventServiceControllerTest {

    @Test
    @DisplayName("Should return seat for internal call with valid secret header")
    void shouldReturnSeatForInternalCall() throws Exception {
        mockMvc.perform(get("/internal/seats/1")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.sectionName", equalTo("Parter")));
    }

    @Test
    @DisplayName("Should return 404 for internal call when seat does not exist")
    void shouldReturnNotFoundForMissingSeat() throws Exception {
        mockMvc.perform(get("/internal/seats/999")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when internal secret header is missing")
    void shouldRejectMissingHeader() throws Exception {
        mockMvc.perform(get("/internal/seats/1"))
                .andExpect(status().isForbidden());
    }
}
