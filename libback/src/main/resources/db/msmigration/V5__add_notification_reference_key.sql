ALTER TABLE notifications
    ADD reference_key VARCHAR(150);

CREATE UNIQUE INDEX uk_notifications_reference_key
    ON notifications(reference_key);