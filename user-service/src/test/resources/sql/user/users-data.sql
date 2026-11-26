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
