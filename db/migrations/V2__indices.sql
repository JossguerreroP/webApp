BEGIN;

-- Incidents: main listing & filters
CREATE INDEX IF NOT EXISTS idx_incidents_date
    ON incidents(date);

CREATE INDEX IF NOT EXISTS idx_incidents_status
    ON incidents(status);

CREATE INDEX IF NOT EXISTS idx_incidents_level
    ON incidents(level);

CREATE INDEX IF NOT EXISTS idx_incidents_area
    ON incidents(area_id);

-- Composite index for frequent filters (area + date)
CREATE INDEX IF NOT EXISTS idx_incidents_area_date
    ON incidents(area_id, date);

-- Partial index for critical incidents (reports)
CREATE INDEX IF NOT EXISTS idx_incidents_critical_date
    ON incidents(date)
    WHERE level = 'alto';

-- Audit history access
CREATE INDEX IF NOT EXISTS idx_incident_history_incident_date
    ON incident_history(incident_id, changed_at);

COMMIT;
