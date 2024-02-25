--liquibase formatted sql

--changeset vladimir_marzuev:4_insert_into_all-data

INSERT INTO genre (genre_id, name)
    VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

INSERT INTO mpa (mpa_id, title)
    VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

--rollback delete from tables all data;