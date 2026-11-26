INSERT INTO events.venues (id, name)
VALUES
    (1, 'Main Hall'),
    (2, 'Small Room'),
    (3, 'Empty Venue');

INSERT INTO events.sections (id, name, venue_id)
VALUES
    (1, 'Parter', 1),
    (2, 'Balcony', 1),
    (3, 'Empty Section', 2);

INSERT INTO events.seats (id, row_number, seat_number, section_id)
VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 1, 1, 2);

INSERT INTO events.events (
    id, title, description, starts_at, ends_at, venue_id, city,
    ticket_price, status, organizer_id, created_at, updated_at
)
VALUES
    (1, 'Future Conference', 'description', '2030-06-01 10:00:00', '2030-06-01 12:00:00', 1, 'Kosice',     25.00, 'PUBLISHED', 10, '2026-05-01 12:00:00', '2026-05-01 12:00:00'),
    (2, 'Future Concert',    'description', '2030-07-01 18:00:00', '2030-07-01 21:00:00', 2, 'Bratislava', 50.00, 'DRAFT',     20, '2026-05-01 12:00:00', '2026-05-01 12:00:00');

SELECT setval(pg_get_serial_sequence('events.venues', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('events.sections', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('events.seats', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('events.events', 'id'), 2, true);
