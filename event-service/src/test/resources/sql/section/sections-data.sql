INSERT INTO events.venues (id, name)
VALUES
    (1, 'Main Hall'),
    (2, 'Small Room');

INSERT INTO events.sections (id, name, venue_id)
VALUES
    (1, 'Parter', 1),
    (2, 'Balcony', 1),
    (3, 'VIP', 2);

SELECT setval(pg_get_serial_sequence('events.venues', 'id'), 2, true);
SELECT setval(pg_get_serial_sequence('events.sections', 'id'), 3, true);
