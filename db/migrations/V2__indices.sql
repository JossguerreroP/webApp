BEGIN;

-- ==============================
-- Incidents: listing & filters
-- ==============================

-- Fecha (ordenamiento y rangos)
CREATE INDEX IF NOT EXISTS idx_incidents_created_at
    ON incidents(created_at);

-- Estado
CREATE INDEX IF NOT EXISTS idx_incidents_status
    ON incidents(status);

-- Nivel
CREATE INDEX IF NOT EXISTS idx_incidents_level
    ON incidents(level);

-- Área
CREATE INDEX IF NOT EXISTS idx_incidents_area_id
    ON incidents(area_id);

-- Responsable
CREATE INDEX IF NOT EXISTS idx_incidents_responsible_id
    ON incidents(responsible_id);



-- Área + fecha (dashboard principal)
CREATE INDEX IF NOT EXISTS idx_incidents_area_created_at
    ON incidents(area_id, created_at);

-- Estado + fecha (filtro común)
CREATE INDEX IF NOT EXISTS idx_incidents_status_created_at
    ON incidents(status, created_at);

-- Partial index (reportes críticos)


CREATE INDEX IF NOT EXISTS idx_incidents_critical_created_at
    ON incidents(created_at)
    WHERE level = 'alto';

-- ==============================
-- Audit history
-- ==============================

CREATE INDEX IF NOT EXISTS idx_incident_history_incident_changed_at
    ON incident_history(incident_id, changed_at);

-- ==============================
-- Attachments
-- ==============================

CREATE INDEX IF NOT EXISTS idx_attachments_incident_id
    ON incident_attachments(incident_id);

CREATE INDEX IF NOT EXISTS idx_attachments_uploaded_at
    ON incident_attachments(uploaded_at);

COMMIT;
