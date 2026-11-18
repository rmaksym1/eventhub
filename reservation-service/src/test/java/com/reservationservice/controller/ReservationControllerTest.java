
package com.reservationservice.controller;

import com.eventhub.common.security.AuthenticatedUser;
import com.reservationservice.client.EventClient;
import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
        scripts = "/sql/reservations-controller-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class ReservationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventClient eventClient;

    private void authenticateAs(Long userId, String username, String role) {
        var principal = new AuthenticatedUser(userId, username);

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should get reservation by ID successfully")
    void shouldGetReservationById() throws Exception {
        authenticateAs(3L, "user@gmail.com", "USER");

        mockMvc.perform(get("/reservations/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(10)))
                .andExpect(jsonPath("$.userId", equalTo(3)))
                .andExpect(jsonPath("$.status", equalTo("PENDING")));
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservation() throws Exception {
        authenticateAs(3L, "user@gmail.com", "USER");

        doNothing().when(eventClient).seatExists(anyLong());
        doNothing().when(eventClient).eventExists(anyLong());

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "eventId": 1,
                                    "seatId": 100
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", equalTo("PENDING")))
                .andExpect(jsonPath("$.seatId", equalTo(100)));
    }

    @Test
    @DisplayName("Should update reservation status successfully (Admin / Organizer)")
    void shouldUpdateReservationStatus() throws Exception {
        authenticateAs(1L, "admin@gmail.com", "ADMIN");

        mockMvc.perform(patch("/reservations/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "CONFIRMED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("CONFIRMED")));
    }

    @Test
    @DisplayName("Should delete reservation successfully")
    void shouldDeleteReservation() throws Exception {
        authenticateAs(1L, "admin@gmail.com", "ADMIN");

        mockMvc.perform(delete("/reservations/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
