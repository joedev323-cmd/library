
-- V8: Ensure audit_logs.timestamp exists and is NOT NULL
-- SQL Server

IF OBJECT_ID('dbo.audit_logs', 'U') IS NOT NULL
BEGIN
    -- Add the column only if it does not exist.
    IF COL_LENGTH('dbo.audit_logs', 'timestamp') IS NULL
BEGIN
ALTER TABLE dbo.audit_logs
    ADD [timestamp] DATETIME2 NULL;
END;

    -- Populate any missing timestamps.
UPDATE dbo.audit_logs
SET [timestamp] = SYSDATETIME()
WHERE [timestamp] IS NULL;

-- Match Auditlog.java: @Column(nullable = false)
ALTER TABLE dbo.audit_logs
ALTER COLUMN [timestamp] DATETIME2 NOT NULL;
END;
