INSERT INTO reservations.reservations (id, user_id, event_id, seat_id, status, created_at, expires_at)
VALUES
    (10, 3, 1, 50, 'PENDING', '2026-06-01T10:00:00Z', '2026-06-01T09:00:00Z');

SELECT setval(pg_get_serial_sequence('reservations', 'id'), 10, true);