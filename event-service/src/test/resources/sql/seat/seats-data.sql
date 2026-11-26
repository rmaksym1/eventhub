INSERT INTO events.venues (id, name)
VALUES
    (1, 'Main Hall');

INSERT INTO events.sections (id, name, venue_id)
VALUES
    (1, 'Parter', 1),
    (2, 'Balcony', 1);

INSERT INTO events.seats (id, row_number, seat_number, section_id)
VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 2, 1, 1),
    (4, 1, 1, 2);

SELECT setval(pg_get_serial_sequence('events.venues', 'id'), 1, true);
SELECT setval(pg_get_serial_sequence('events.sections', 'id'), 2, true);
SELECT setval(pg_get_serial_sequence('events.seats', 'id'), 4, true);
