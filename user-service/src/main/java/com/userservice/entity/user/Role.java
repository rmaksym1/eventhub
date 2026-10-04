package com.userservice.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Override
    public @Nullable String getAuthority() {
        return name.name();
    }

    public enum RoleName {
        ROLE_USER,
        ROLE_ORGANIZER,
        ROLE_ADMIN
    }
}
