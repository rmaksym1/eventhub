INSERT INTO events.venues (id, name)
VALUES
    (1, 'Main Hall'),
    (2, 'Small Room'),
    (3, 'Open Air Stage');

SELECT setval(pg_get_serial_sequence('events.venues', 'id'), 3, true);
