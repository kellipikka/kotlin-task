PRAGMA foreign_keys= OFF;
BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "flyway_schema_history"
(
    "installed_rank" INT           NOT NULL PRIMARY KEY,
    "version"        VARCHAR(50),
    "description"    VARCHAR(200)  NOT NULL,
    "type"           VARCHAR(20)   NOT NULL,
    "script"         VARCHAR(1000) NOT NULL,
    "checksum"       INT,
    "installed_by"   VARCHAR(100)  NOT NULL,
    "installed_on"   TEXT          NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')),
    "execution_time" INT           NOT NULL,
    "success"        BOOLEAN       NOT NULL
);
INSERT INTO flyway_schema_history
VALUES (1, '1', 'create schema', 'SQL', 'V1__create_schema.sql', -1374386858, '', '2026-09-16 19:36:02.491', 5, 1);
INSERT INTO flyway_schema_history
VALUES (2, '2', 'insert sectors', 'SQL', 'V2__insert_sectors.sql', -1230325418, '', '2026-09-16 19:36:02.514', 2, 1);
INSERT INTO flyway_schema_history
VALUES (3, '3', 'create sessions table', 'SQL', 'V3__create_sessions_table.sql', -1043232100, '',
        '2026-09-16 19:36:02.521', 2, 1);
INSERT INTO flyway_schema_history
VALUES (4, '4', 'create session sector table', 'SQL', 'V4__create_session_sector_table.sql', 1035063911, '',
        '2026-09-16 19:36:02.530', 1, 1);
INSERT INTO flyway_schema_history
VALUES (5, '5', 'rename sessions', 'SQL', 'V5__rename_sessions.sql', 147313670, '', '2026-09-16 19:36:02.537', 3, 1);
CREATE TABLE sectors
(
    id        INTEGER PRIMARY KEY,
    name      TEXT NOT NULL,
    parent_id INTEGER,

    FOREIGN KEY (parent_id)
        REFERENCES sectors (id)
        ON DELETE RESTRICT
);
INSERT INTO sectors
VALUES (1, 'Manufacturing', NULL);
INSERT INTO sectors
VALUES (2, 'Service', NULL);
INSERT INTO sectors
VALUES (3, 'Other', NULL);
INSERT INTO sectors
VALUES (5, 'Printing', 1);
INSERT INTO sectors
VALUES (6, 'Food and Beverage', 1);
INSERT INTO sectors
VALUES (7, 'Textile and Clothing', 1);
INSERT INTO sectors
VALUES (8, 'Wood', 1);
INSERT INTO sectors
VALUES (9, 'Plastic and Rubber', 1);
INSERT INTO sectors
VALUES (11, 'Metalworking', 1);
INSERT INTO sectors
VALUES (12, 'Machinery', 1);
INSERT INTO sectors
VALUES (13, 'Furniture', 1);
INSERT INTO sectors
VALUES (18, 'Electronics and Optics', 1);
INSERT INTO sectors
VALUES (19, 'Construction materials', 1);
INSERT INTO sectors
VALUES (21, 'Transport and Logistics', 2);
INSERT INTO sectors
VALUES (22, 'Tourism', 2);
INSERT INTO sectors
VALUES (25, 'Business services', 2);
INSERT INTO sectors
VALUES (28, 'Information Technology and Telecommunications', 2);
INSERT INTO sectors
VALUES (29, 'Energy technology', 3);
INSERT INTO sectors
VALUES (33, 'Environment', 3);
INSERT INTO sectors
VALUES (35, 'Engineering', 2);
INSERT INTO sectors
VALUES (37, 'Creative industries', 3);
INSERT INTO sectors
VALUES (39, 'Milk & dairy products', 6);
INSERT INTO sectors
VALUES (40, 'Meat & meat products', 6);
INSERT INTO sectors
VALUES (42, 'Fish & fish products', 6);
INSERT INTO sectors
VALUES (43, 'Beverages', 6);
INSERT INTO sectors
VALUES (44, 'Clothing', 7);
INSERT INTO sectors
VALUES (45, 'Textile', 7);
INSERT INTO sectors
VALUES (47, 'Wooden houses', 8);
INSERT INTO sectors
VALUES (51, 'Wooden building materials', 8);
INSERT INTO sectors
VALUES (53, 'Plastics welding and processing', 559);
INSERT INTO sectors
VALUES (54, 'Packaging', 9);
INSERT INTO sectors
VALUES (55, 'Blowing', 559);
INSERT INTO sectors
VALUES (57, 'Moulding', 559);
INSERT INTO sectors
VALUES (62, 'Forgings, Fasteners', 542);
INSERT INTO sectors
VALUES (66, 'MIG, TIG, Aluminum welding', 542);
INSERT INTO sectors
VALUES (67, 'Construction of metal structures', 11);
INSERT INTO sectors
VALUES (69, 'Gas, Plasma, Laser cutting', 542);
INSERT INTO sectors
VALUES (75, 'CNC-machining', 542);
INSERT INTO sectors
VALUES (91, 'Machinery equipment/tools', 12);
INSERT INTO sectors
VALUES (93, 'Metal structures', 12);
INSERT INTO sectors
VALUES (94, 'Machinery components', 12);
INSERT INTO sectors
VALUES (97, 'Maritime', 12);
INSERT INTO sectors
VALUES (98, 'Kitchen', 13);
INSERT INTO sectors
VALUES (99, 'Project furniture', 13);
INSERT INTO sectors
VALUES (101, 'Living room', 13);
INSERT INTO sectors
VALUES (111, 'Air', 21);
INSERT INTO sectors
VALUES (112, 'Road', 21);
INSERT INTO sectors
VALUES (113, 'Water', 21);
INSERT INTO sectors
VALUES (114, 'Rail', 21);
INSERT INTO sectors
VALUES (121, 'Software, Hardware', 28);
INSERT INTO sectors
VALUES (122, 'Telecommunications', 28);
INSERT INTO sectors
VALUES (141, 'Translation services', 2);
INSERT INTO sectors
VALUES (145, 'Labelling and packaging printing', 5);
INSERT INTO sectors
VALUES (148, 'Advertising', 5);
INSERT INTO sectors
VALUES (150, 'Book/Periodicals printing', 5);
INSERT INTO sectors
VALUES (224, 'Manufacture of machinery', 12);
INSERT INTO sectors
VALUES (227, 'Repair and maintenance service', 12);
INSERT INTO sectors
VALUES (230, 'Ship repair and conversion', 97);
INSERT INTO sectors
VALUES (263, 'Houses and buildings', 11);
INSERT INTO sectors
VALUES (267, 'Metal products', 11);
INSERT INTO sectors
VALUES (269, 'Boat/Yacht building', 97);
INSERT INTO sectors
VALUES (271, 'Aluminium and steel workboats', 97);
INSERT INTO sectors
VALUES (337, 'Other (Wood)', 8);
INSERT INTO sectors
VALUES (341, 'Outdoor', 13);
INSERT INTO sectors
VALUES (342, 'Bakery & confectionery products', 6);
INSERT INTO sectors
VALUES (378, 'Sweets & snack food', 6);
INSERT INTO sectors
VALUES (385, 'Bedroom', 13);
INSERT INTO sectors
VALUES (389, 'Bathroom/sauna', 13);
INSERT INTO sectors
VALUES (390, 'Children’s room', 13);
INSERT INTO sectors
VALUES (392, 'Office', 13);
INSERT INTO sectors
VALUES (394, 'Other (Furniture)', 13);
INSERT INTO sectors
VALUES (437, 'Other', 6);
INSERT INTO sectors
VALUES (508, 'Other', 12);
INSERT INTO sectors
VALUES (542, 'Metal works', 11);
INSERT INTO sectors
VALUES (556, 'Plastic goods', 9);
INSERT INTO sectors
VALUES (559, 'Plastic processing technology', 9);
INSERT INTO sectors
VALUES (560, 'Plastic profiles', 9);
INSERT INTO sectors
VALUES (576, 'Programming, Consultancy', 28);
INSERT INTO sectors
VALUES (581, 'Data processing, Web portals, E-marketing', 28);
CREATE TABLE IF NOT EXISTS "form_responses"
(
    id              INTEGER PRIMARY KEY,
    name            TEXT NOT NULL,
    agreed_to_terms INTEGER
);
CREATE TABLE IF NOT EXISTS "form_response_sector"
(
    form_response_id INTEGER NOT NULL,
    sector_id        INTEGER NOT NULL,

    PRIMARY KEY (form_response_id, sector_id),

    FOREIGN KEY (form_response_id)
        REFERENCES "form_responses" (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (sector_id)
        REFERENCES sectors (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
CREATE INDEX "flyway_schema_history_s_idx" ON "flyway_schema_history" ("success");
COMMIT;
