package com.testcommon.test;

import com.eventhub.common.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerTest extends AbstractIntegrationTest {

    protected static final String INTERNAL_HEADER = "X-Internal-Header";
    protected static final String INTERNAL_SECRET = "TEST";

    @Autowired
    protected MockMvc mockMvc;

    protected void authenticateAs(Long userId, String username, String role) {
        var principal = new AuthenticatedUser(userId, username);

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void authenticateAsAdmin() {
        authenticateAs(1L, "admin@gmail.com", "ADMIN");
    }

    protected void authenticateAsUser() {
        authenticateAs(3L, "user@gmail.com", "USER");
    }

    protected void authenticateAsOrganizer(Long organizerId) {
        authenticateAs(organizerId, "organizer" + organizerId + "@gmail.com", "ORGANIZER");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
