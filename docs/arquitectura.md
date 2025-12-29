├── backend/
│   ├── src/main/java/com/company/sigess/
│   │   ├── controllers/
│   │   │   ├── HistoryController.java
│   │   │   ├── IncidentController.java
│   │   │   └── UserController.java
│   │   ├── services/
│   │   │   ├── IncidentService.java
│   │   │   └── UserService.java
│   │   ├── repositories/
│   │   │   ├── AttachmentDAO.java
│   │   │   ├── HistoryDAO.java
│   │   │   ├── IncidentDAO.java
│   │   │   └── UserDAO.java
│   │   ├── models/DTO/
│   │   │   ├── AttachmentDTO.java
│   │   │   ├── IncidentDTO.java
│   │   │   └── UserDTO.java
│   │   └── security/
│   │       ├── AuthFilter.java
│   │       ├── JwtUtil.java
│   │       └── SecurityContext.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── fronted-angular/
│   ├── src/app/
│   │   ├── core/
│   │   │   ├── Interceptor/
│   │   │   ├── models/
│   │   │   └── services/
│   │   ├── shared/
│   │   │   └── components/
│   │   ├── incidents/
│   │   │   ├── create-incident/
│   │   │   ├── incident-attachments/
│   │   │   ├── incident-history/
│   │   │   └── report-incident/
│   │   └── auth/
│   ├── src/assets/
│   └── angular.json
├── db/
│   └── migrations/
│       ├── V1__schema.sql
│       ├── V2__indices.sql
│       └── V3__seed_usuarios.sql
├── docs/
│   ├── arquitectura.md
│   ├── decisiones_tecnicas.md
│   └── supuestos.md
├── postman/
└── README.md