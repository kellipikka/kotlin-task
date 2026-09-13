CREATE TABLE sectors
(
    id        INTEGER PRIMARY KEY,
    name      TEXT NOT NULL,
    parent_id INTEGER,

    FOREIGN KEY (parent_id)
        REFERENCES sectors (id)
        ON DELETE RESTRICT
);
