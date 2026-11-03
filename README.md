# Clyvo VET

Plataforma web e API para a jornada contínua de saúde preventiva e rotina clínica veterinária.

## Integrantes

| Nome | RM |
| :--- | :--- |
| Gabriel Costa Solano | 562325 |
| Kaiky Pereira Rodrigues Da Silva | 564578 |
| Leandro Guarido de Oliveira | 561760 |

## Stack

- Java 17 (LTS)
- Spring Boot 3.2.5
- Spring MVC e Spring Data JPA
- Spring Security 6 (form login, RBAC, BCrypt)
- Thymeleaf com Thymeleaf Extras Spring Security 6
- Flyway Migration
- Banco H2
- Bean Validation (Jakarta Validation)
- Springdoc OpenAPI (Swagger UI)
- Lombok

## Execução local

Pré-requisitos: JDK 17 configurado no `JAVA_HOME`. O Maven Wrapper já acompanha o repositório.

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

## Endereços da aplicação

| Recurso | URL |
| :--- | :--- |
| Portal web | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| Console H2 | http://localhost:8080/h2-console |

Conexão do console H2: JDBC URL `jdbc:h2:mem:clyvovet`, usuário `sa`, senha em branco.
