package com.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractUserServiceControllerTest {

    private static final String UPDATE_BODY = """
            {
                "firstName": "Updated",
                "lastName": "Name"
            }
            """;

    @Test
    @DisplayName("Should get the current authenticated user")
    void shouldGetCurrentUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("user@gmail.com")))
                .andExpect(jsonPath("$.firstName", equalTo("User")))
                .andExpect(jsonPath("$.lastName", equalTo("Userov")))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0].name", equalTo("ROLE_USER")));
    }

    @Test
    @DisplayName("Should return 401 when current user is requested without authentication")
    void shouldRejectUnauthenticatedCurrentUser() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should update the current authenticated user")
    void shouldUpdateCurrentUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("user@gmail.com")))
                .andExpect(jsonPath("$.firstName", equalTo("Updated")))
                .andExpect(jsonPath("$.lastName", equalTo("Name")));
    }

    @Test
    @DisplayName("Should return 400 when current user update is invalid")
    void shouldRejectInvalidCurrentUserUpdate() throws Exception {
        authenticateAsUser();

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "",
                                    "lastName": "Name"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should let an admin update a user by id")
    void shouldUpdateUserAsAdmin() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("user@gmail.com")))
                .andExpect(jsonPath("$.firstName", equalTo("Updated")))
                .andExpect(jsonPath("$.lastName", equalTo("Name")));
    }

    @Test
    @DisplayName("Should return 403 when a regular user updates another user")
    void shouldForbidUserUpdateForRegularUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when admin updates a missing user")
    void shouldReturnNotFoundWhenUpdatingMissingUser() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User by id: 999")));
    }

    @Test
    @DisplayName("Should return the full user profile for an admin")
    void shouldGetUserAsAdmin() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(get("/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("user@gmail.com")))
                .andExpect(jsonPath("$.firstName", equalTo("User")))
                .andExpect(jsonPath("$.lastName", equalTo("Userov")))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0].name", equalTo("ROLE_USER")));
    }

    @Test
    @DisplayName("Should return the public user profile for a regular user")
    void shouldGetPublicUserAsRegularUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.email", equalTo("admin@gmail.com")))
                .andExpect(jsonPath("$.lastName", equalTo("Adminov")))
                .andExpect(jsonPath("$.firstName").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    void shouldReturnNotFoundForMissingUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User by id: 999")));
    }

    @Test
    @DisplayName("Should replace roles for a user")
    void shouldEditRoles() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/users/roles/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "roles": ["ROLE_USER", "ROLE_ORGANIZER"]
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(2)));
    }

    @Test
    @DisplayName("Should return 403 when a regular user edits roles")
    void shouldForbidRoleEditForRegularUser() throws Exception {
        authenticateAsUser();

        mockMvc.perform(put("/users/roles/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "roles": ["ROLE_ADMIN"]
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when editing roles of a missing user")
    void shouldReturnNotFoundWhenEditingRolesOfMissingUser() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(put("/users/roles/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "roles": ["ROLE_USER"]
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User by id: 999")));
    }
}
