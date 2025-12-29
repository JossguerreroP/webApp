# Decisiones Técnicas - SIGESS

Este documento resume las decisiones técnicas clave tomadas durante el desarrollo del sistema de gestión de incidentes SIGESS.

### 1. Arquitectura de Monorepositorio
- **Decisión:** Organizar el proyecto con carpetas separadas para `fronted-angular`, `backend` y `db` en un único repositorio.
- **Razón:** Facilita la sincronización entre el cliente y el servidor, simplificando la gestión de contratos de API y scripts de base de datos.

### 2. Compatibilidad con Servlet 3.0 (Tomcat 7)
- **Decisión:** Implementar un extractor manual de nombres de archivo para partes multipart en lugar de usar `getSubmittedFileName()`.
- **Razón:** El entorno de ejecución (Tomcat 7) no soporta Servlet 3.1. Esta decisión evita errores de `NoSuchMethodError` y asegura la portabilidad en servidores legacy.

### 3. Interceptor de Autenticación Resiliente
- **Decisión:** Refinar el interceptor de Angular para manejar errores 401 de forma menos agresiva durante subidas de archivos.
- **Razón:** Evita cierres de sesión accidentales ("pantalla blanca") causados por errores técnicos del servidor que anteriormente se reportaban erróneamente como problemas de token.

### 4. Validación Proactiva de JWT
- **Decisión:** Implementar `isTokenExpired` con un margen de seguridad de 60 segundos en el frontend.
- **Razón:** Mitiga problemas de desincronización de reloj entre cliente y servidor, reduciendo peticiones fallidas por tokens a punto de expirar.

### 5. Control de Acceso basado en Roles (RBAC)
- **Decisión:** Seguridad multinivel (Frontend y Backend) para roles `SUPERVISOR` y `ANALISTA`.
- **Razón:** Garantiza que operaciones críticas como el cierre de incidentes estén restringidas jerárquicamente.

### 6. Soporte Extendido de Documentos
- **Decisión:** Inclusión de tipos MIME para Microsoft Office (DOCX, XLSX) y formatos de imagen adicionales en la validación del backend.
- **Razón:** Soporta la necesidad operativa de adjuntar diversos tipos de evidencia técnica a los incidentes.

### 7. Gestión Unificada de Errores
- **Decisión:** Diferenciación clara en `AuthFilter` entre errores de validación de token (401) y errores internos del servidor (500).
- **Razón:** Mejora drásticamente la capacidad de diagnóstico al no enmascarar fallos de lógica de negocio como problemas de sesión.

### 8. Stack Tecnológico
- **Frontend:** Angular 18 (Standalone Components) para un desarrollo modular y moderno.
- **Backend:** Java Servlets (J2EE) para máxima compatibilidad y bajo consumo de recursos.
- **Seguridad:** JWT (Stateless) para una comunicación segura y escalable entre desacoplada.
- **DB:** PostgreSQL para persistencia relacional robusta y trazabilidad de auditoría.
