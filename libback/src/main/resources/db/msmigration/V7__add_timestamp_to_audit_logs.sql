
-- Add the timestamp column if it does not already exist.
IF COL_LENGTH('dbo.audit_logs', 'timestamp') IS NULL
BEGIN
ALTER TABLE dbo.audit_logs
    ADD [timestamp] DATETIME2 NULL;
END;
GO

-- Populate timestamps for existing audit records.
UPDATE dbo.audit_logs
SET [timestamp] = SYSDATETIME()
WHERE [timestamp] IS NULL;
GO

-- Require a timestamp for all audit records.
ALTER TABLE dbo.audit_logs
ALTER COLUMN [timestamp] DATETIME2 NOT NULL;
GO
