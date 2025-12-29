# Supuestos del Proyecto - SIGESS

Este documento detalla los supuestos y premisas bajo los cuales se ha desarrollado el sistema SIGESS.

### 1. Entorno de Ejecución
- **Supuesto:** El servidor de aplicaciones es Apache Tomcat 7.
- **Implicación:** Se asume que no todas las características de Servlet 3.1 están disponibles, por lo que el código se mantiene compatible con Servlet 3.0.

### 2. Estructura de Seguridad
- **Supuesto:** Los roles `SUPERVISOR` y `ANALISTA` son suficientes para la lógica de negocio actual.
- **Implicación:** La interfaz y los endpoints están diseñados específicamente para estos dos niveles de privilegios.

### 3. Gestión de Archivos
- **Supuesto:** Los archivos adjuntos se almacenan en el sistema de archivos local del servidor (carpeta `sigess_uploads`).
- **Implicación:** No se requiere de un almacenamiento en la nube (como S3) o una base de datos de objetos para la fase actual.

### 4. Persistencia de Sesión
- **Supuesto:** El cliente es responsable de almacenar y enviar el token JWT en cada petición.
- **Implicación:** El backend es stateless y no mantiene sesiones en memoria (JSESSIONID).

### 5. Formatos de Datos
- **Supuesto:** Todas las fechas se manejan en formato ISO_LOCAL_DATE_TIME (`yyyy-MM-ddTHH:mm:ss`).
- **Implicación:** Los serializadores de GSON y Angular están configurados para este estándar estricto para evitar errores de parseo.

### 6. Conectividad
- **Supuesto:** El frontend y el backend se ejecutan en el mismo dominio o están configurados para permitir CORS durante el desarrollo.
- **Implicación:** Se han incluido cabeceras de CORS permisivas en `AuthFilter` para facilitar la integración.

### 7. Integridad de Datos
- **Supuesto:** El borrado de un incidente es en realidad un "cierre" lógico, pero el sistema permite la eliminación física en base de datos si es necesario (según la implementación del DAO).
- **Implicación:** La trazabilidad en el historial de auditoría es prioritaria sobre la persistencia eterna de registros eliminados.
