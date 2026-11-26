package com.eventservice.controller.internal;

import com.eventservice.controller.AbstractEventServiceControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InternalEventControllerTest extends AbstractEventServiceControllerTest {

    @Test
    @DisplayName("Should return event for internal call with valid secret header")
    void shouldReturnEventForInternalCall() throws Exception {
        mockMvc.perform(get("/internal/events/1")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo("Future Conference")));
    }

    @Test
    @DisplayName("Should return 404 for internal call when event does not exist")
    void shouldReturnNotFoundForMissingEvent() throws Exception {
        mockMvc.perform(get("/internal/events/999")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return true when organizer owns the event")
    void shouldConfirmOwnership() throws Exception {
        mockMvc.perform(get("/internal/events/1/ownership")
                        .param("organizerId", "10")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Should return false when organizer does not own the event")
    void shouldDenyOwnership() throws Exception {
        mockMvc.perform(get("/internal/events/1/ownership")
                        .param("organizerId", "20")
                        .header(INTERNAL_HEADER, INTERNAL_SECRET))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Should return 403 when internal secret header is missing")
    void shouldRejectMissingHeader() throws Exception {
        mockMvc.perform(get("/internal/events/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when internal secret header is wrong")
    void shouldRejectWrongHeader() throws Exception {
        mockMvc.perform(get("/internal/events/1")
                        .header(INTERNAL_HEADER, "WRONG"))
                .andExpect(status().isForbidden());
    }
}
