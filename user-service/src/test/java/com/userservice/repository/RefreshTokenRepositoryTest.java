package com.userservice.repository;

import com.testcommon.test.AbstractIntegrationTest;
import com.userservice.entity.user.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/refresh/refresh-tokens-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RefreshTokenRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Should find refresh token by token value")
    void shouldFindByToken() {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken("valid-refresh-token");

        assertThat(refreshToken).isPresent();
        assertThat(refreshToken.get().getId()).isEqualTo(1L);
        assertThat(refreshToken.get().getUsername()).isEqualTo("user@gmail.com");
        assertThat(refreshToken.get().getExpiryDate()).isAfter(Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("Should return empty when token does not exist")
    void shouldReturnEmptyWhenTokenDoesNotExist() {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken("missing-token");

        assertThat(refreshToken).isEmpty();
    }
}
