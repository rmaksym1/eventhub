INSERT INTO reservations.reservations (id, user_id, event_id, seat_id, status, created_at, expires_at)
VALUES
    (1, 3, 1, 10, 'PENDING', '2026-06-01T10:00:00Z', '2026-06-01T09:00:00Z'),
    (2, 3, 1, 11, 'CONFIRMED', '2026-06-05T10:00:00Z', '2026-06-01T09:00:00Z'),
    (3, 3, 1, 12, 'PENDING', '2025-01-01T10:00:00Z', '2025-01-01T09:00:00Z');