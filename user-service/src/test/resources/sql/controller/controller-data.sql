INSERT INTO users.roles (id, name)
VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER'),
    (3, 'ROLE_ORGANIZER');

INSERT INTO users.users (id, email, first_name, last_name, password)
VALUES
    (1, 'admin@gmail.com', 'Admin', 'Admin', '$2a$08$cIC8ZKgVE/.ip83tZ5sapes46NnmrQ7CM0u0.IXwL4.PbC4niTyFy'),
    (2, 'organizer@gmail.com', 'Organizer', 'Organizer', '$2a$08$cIC8ZKgVE/.ip83tZ5sapes46NnmrQ7CM0u0.IXwL4.PbC4niTyFy'),
    (3, 'user@gmail.com', 'User', 'User', '$2a$08$cIC8ZKgVE/.ip83tZ5sapes46NnmrQ7CM0u0.IXwL4.PbC4niTyFy');

INSERT INTO users.users_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 3),
    (3, 2);

INSERT INTO users.refresh_tokens (id, token, username, expiry_date)
VALUES
    (1, 'valid-refresh-token', 'user@gmail.com', '2030-01-01 00:00:00'),
    (2, 'expired-refresh-token', 'user@gmail.com', '2020-01-01 00:00:00');

SELECT setval(pg_get_serial_sequence('users.users', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('users.roles', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('users.refresh_tokens', 'id'), 2, true);
