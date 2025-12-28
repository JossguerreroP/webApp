BEGIN;

-- =====================
-- ROLES
-- =====================
INSERT INTO roles (name)
VALUES 
    ('ANALISTA'),
    ('SUPERVISOR')
ON CONFLICT (name) DO NOTHING;

-- =====================
-- USERS (demo)
-- =====================
INSERT INTO users (username, password, full_name, role_id)
SELECT 
    'analista',
    'analista123',
    'Analista SIGESS',
    r.id
FROM roles r
WHERE r.name = 'ANALISTA'
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, full_name, role_id)
SELECT 
    'supervisor',
    'supervisor123',
    'Supervisor SIGESS',
    r.id
FROM roles r
WHERE r.name = 'SUPERVISOR'
ON CONFLICT (username) DO NOTHING;

-- =====================
-- AREAS
-- =====================
INSERT INTO areas (name)
VALUES 
    ('Operaciones'),
    ('Mantenimiento'),
    ('Logística')
ON CONFLICT (name) DO NOTHING;

-- =====================
-- INCIDENTE 1
-- =====================
INSERT INTO incidents (
    title,
    description,
    type,
    level,
    status,
    created_at,
    responsible_id,
    area_id,
    created_by
)
SELECT
    'Caída de herramienta en zona de ensamblaje',
    'Se reporta caída de herramienta desde estantería superior. No hubo heridos.',
    'casi incidente',
    'alto',
    'abierto',
    NOW() - INTERVAL '10 days',
    resp.id,
    a.id,
    creator.id
FROM areas a
JOIN users creator ON creator.username = 'analista'
JOIN users resp ON resp.username = 'supervisor'
WHERE a.name = 'Operaciones';

-- =====================
-- INCIDENTE 2
-- =====================
INSERT INTO incidents (
    title,
    description,
    type,
    level,
    status,
    created_at,
    responsible_id,
    area_id,
    created_by
)
SELECT
    'Fuga menor en línea hidráulica',
    'Se detecta fuga pequeña, se programó intervención correctiva.',
    'condición insegura',
    'medio',
    'en análisis',
    NOW() - INTERVAL '5 days',
    resp.id,
    a.id,
    creator.id
FROM areas a
JOIN users creator ON creator.username = 'analista'
JOIN users resp ON resp.username = 'supervisor'
WHERE a.name = 'Mantenimiento';

-- =====================
-- HISTORIAL INCIDENTE 2
-- =====================
INSERT INTO incident_history (
    incident_id,
    changed_by,
    field_name,
    old_value,
    new_value
)
SELECT
    i.id,
    u.id,
    'status',
    'abierto',
    'en análisis'
FROM incidents i
JOIN users u ON u.username = 'supervisor'
WHERE i.title = 'Fuga menor en línea hidráulica';

COMMIT;

