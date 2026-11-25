INSERT INTO events.venues (id, name) VALUES (1, 'Main Hall'), (2, 'Small Room');

INSERT INTO events.events (
    id,
    title,
    description,
    starts_at,
    ends_at,
    venue_id,
    city,
    ticket_price,
    status,
    organizer_id,
    created_at,
    updated_at
)
VALUES
    (1, 'Morning Conference', 'description', '2026-06-01 10:00:00', '2026-06-01 12:00:00', 1, 'Kosice', 25.00, 'PUBLISHED', 10, '2026-05-01 12:00:00', '2026-05-01 12:00:00'),
    (2, 'Evening Concert',    'description', '2026-06-01 14:00:00', '2026-06-01 16:00:00', 1, 'Kosice', 50.00, 'PUBLISHED', 10, '2026-05-01 12:00:00', '2026-05-01 12:00:00');