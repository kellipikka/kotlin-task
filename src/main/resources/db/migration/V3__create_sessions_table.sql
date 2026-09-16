CREATE TABLE sessions
(
    id              INTEGER PRIMARY KEY,
    name            TEXT NOT NULL,
    agreed_to_terms INTEGER NOT NULL CHECK (agreed_to_terms = 1)
);
