CREATE TABLE IF NOT EXISTS person(
    person_id BIGSERIAL PRIMARY KEY NOT NULL,
    username TEXT,
    email TEXT,
    password TEXT
);