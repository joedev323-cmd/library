CREATE TABLE notifications (
                               id BIGINT IDENTITY(1,1) PRIMARY KEY,
                               recipient VARCHAR(255) NOT NULL,
                               subject VARCHAR(200) NOT NULL,
                               message VARCHAR(MAX) NOT NULL,

    deadline_at DATETIMEOFFSET NOT NULL,
    reminder_at DATETIMEOFFSET NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    sent_at DATETIMEOFFSET,

    attempts INT NOT NULL DEFAULT 0,
    last_error VARCHAR(2000),

    processing_at DATETIMEOFFSET,
    next_attempt_at DATETIMEOFFSET
);

CREATE INDEX idx_notifications_due
    ON notifications(status, reminder_at);