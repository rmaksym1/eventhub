package com.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractUserServiceControllerTest {

    private static final String REGISTER_BODY = """
            {
                "email": "new.user@gmail.com",
                "firstName": "New",
                "lastName": "User",
                "password": "1234567890",
                "repeatPassword": "1234567890"
            }
            """;

    @Test
    @DisplayName("Should register a new user and return tokens")
    void shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("Should return 409 when email is already taken")
    void shouldRejectDuplicateEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@gmail.com",
                                    "firstName": "New",
                                    "lastName": "User",
                                    "password": "1234567890",
                                    "repeatPassword": "1234567890"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email is taken!")));
    }

    @Test
    @DisplayName("Should return 400 when password confirmation does not match")
    void shouldRejectMismatchedPasswords() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "another@gmail.com",
                                    "firstName": "New",
                                    "lastName": "User",
                                    "password": "1234567890",
                                    "repeatPassword": "different"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login an existing user and return tokens")
    void shouldLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@gmail.com",
                                    "password": "1234567890"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    @DisplayName("Should return 404 when login email does not exist")
    void shouldReturnNotFoundForUnknownLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "missing@gmail.com",
                                    "password": "1234567890"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User by email: missing@gmail.com")));
    }

    @Test
    @DisplayName("Should return 400 when password is wrong")
    void shouldRejectWrongPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "user@gmail.com",
                                    "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should refresh an access token")
    void shouldRefreshToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .param("token", "valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    @DisplayName("Should return 409 when refresh token does not exist")
    void shouldRejectUnknownRefreshToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .param("token", "missing-token"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Refresh token by token: missing-token")));
    }

    @Test
    @DisplayName("Should return 409 when refresh token is expired")
    void shouldRejectExpiredRefreshToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .param("token", "expired-refresh-token"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Refresh token is expired!")));
    }
}
