BEGIN;

-- Roles
INSERT INTO roles (name)
VALUES ('ANALISTA'), ('SUPERVISOR')
ON CONFLICT (name) DO NOTHING;

-- Users (plain text passwords ONLY for demo)
INSERT INTO users (username, password, full_name, role_id)
SELECT 'analista', 'analista123', 'Analista SIGESS', id
FROM roles WHERE name = 'ANALISTA'
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, full_name, role_id)
SELECT 'supervisor', 'supervisor123', 'Supervisor SIGESS', id
FROM roles WHERE name = 'SUPERVISOR'
ON CONFLICT (username) DO NOTHING;

-- Areas
INSERT INTO areas (name)
VALUES ('Operaciones'), ('Mantenimiento'), ('Logística')
ON CONFLICT (name) DO NOTHING;

-- Sample incident
INSERT INTO incidents (
    title, description, type, level, status,
    date, responsible, area_id, created_by
)
SELECT
    'Tool fall in assembly area',
    'A tool fell from an upper shelf. No injuries.',
    'near miss',
    'alto',
    'abierto',
    CURRENT_DATE - INTERVAL '10 days',
    'Juan Pérez',
    a.id,
    u.id
FROM areas a, users u
WHERE a.name = 'Operaciones'
  AND u.username = 'analista'
ON CONFLICT DO NOTHING;

COMMIT;
