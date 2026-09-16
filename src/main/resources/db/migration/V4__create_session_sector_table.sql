CREATE TABLE session_sector
(
    session_id INTEGER NOT NULL,
    sector_id  INTEGER NOT NULL,

    PRIMARY KEY (session_id, sector_id),

    FOREIGN KEY (session_id)
        REFERENCES sessions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (sector_id)
        REFERENCES sectors (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);