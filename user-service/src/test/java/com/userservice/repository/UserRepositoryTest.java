package com.userservice.repository;

import com.testcommon.test.AbstractIntegrationTest;
import com.userservice.entity.user.Role;
import com.userservice.entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/user/users-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email and fetch roles")
    void shouldFindByEmailWithRoles() {
        Optional<User> user = userRepository.findByEmail("user@gmail.com");

        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(3L);
        assertThat(user.get().getFirstName()).isEqualTo("User");
        assertThat(user.get().getRoles())
                .extracting(Role::getName)
                .containsExactly(Role.RoleName.ROLE_USER);
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> user = userRepository.findByEmail("missing@gmail.com");

        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when email case does not match")
    void shouldReturnEmptyWhenEmailCaseDoesNotMatch() {
        Optional<User> user = userRepository.findByEmail("User@gmail.com");

        assertThat(user).isEmpty();
    }
}
