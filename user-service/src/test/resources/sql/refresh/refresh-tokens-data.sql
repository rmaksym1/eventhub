INSERT INTO users.refresh_tokens (id, token, username, expiry_date)
VALUES
    (1, 'valid-refresh-token', 'user@gmail.com', '2030-01-01 00:00:00'),
    (2, 'expired-refresh-token', 'user@gmail.com', '2020-01-01 00:00:00');
