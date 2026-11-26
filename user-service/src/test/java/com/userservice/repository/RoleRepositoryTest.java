package com.userservice.repository;

import com.testcommon.test.AbstractIntegrationTest;
import com.userservice.entity.user.Role;
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
class RoleRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should find role by name")
    void shouldFindByName() {
        Optional<Role> role = roleRepository.findByName(Role.RoleName.ROLE_ORGANIZER);

        assertThat(role).isPresent();
        assertThat(role.get().getId()).isEqualTo(3L);
        assertThat(role.get().getName()).isEqualTo(Role.RoleName.ROLE_ORGANIZER);
    }

    @Test
    @DisplayName("Should return empty when role name does not exist")
    void shouldReturnEmptyWhenRoleDoesNotExist() {
        Optional<Role> role = roleRepository.findByName(null);

        assertThat(role).isEmpty();
    }
}
