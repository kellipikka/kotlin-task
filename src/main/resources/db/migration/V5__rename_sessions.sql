ALTER TABLE sessions
    RENAME TO form_responses;

ALTER TABLE session_sector
    RENAME TO form_response_sector;

ALTER TABLE form_response_sector
    RENAME session_id TO form_response_id;