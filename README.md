# AutoSolutions

AutoSolutions - Sistema de gestión de taller automotriz desarrollado con Spring Boot, Oracle Database y Flyway.

## Requisitos

- Java 21+
- Oracle Database 11g+
- Maven 3.8+

## Configuración

### Variables de Entorno

**⚠️ IMPORTANTE: Todas las variables de entorno son OBLIGATORIAS**

El proyecto utiliza variables de entorno para las credenciales sensibles. Crea un archivo `.env` basado en `.env.example`:

```bash
cp .env.example .env
```

Luego configura las siguientes variables en tu archivo `.env`:

- `DB_URL`: URL de conexión a Oracle (ej: `jdbc:oracle:thin:@localhost:1521/XEPDB1`)
- `DB_USERNAME`: Usuario de la base de datos
- `DB_PASSWORD`: Contraseña de la base de datos
- `ADMIN_USERNAME`: Usuario administrador de la aplicación
- `ADMIN_PASSWORD`: Contraseña del administrador

**⚠️ IMPORTANTE**: 
- Todas estas variables son OBLIGATORIAS. La aplicación no arrancará sin ellas.
- Nunca subas el archivo `.env` al repositorio. Este está incluido en `.gitignore`.
- Usa contraseñas seguras en producción.

### Migración de Base de Datos

El proyecto utiliza Flyway para gestionar las migraciones de base de datos. Para ejecutar las migraciones:

```bash
# Usando Maven
mvn flyway:migrate -Dflyway.url=${DB_URL} -Dflyway.user=${DB_USERNAME} -Dflyway.password=${DB_PASSWORD}

# O configurando las variables de entorno y ejecutando la aplicación
export DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
export DB_USERNAME=GERSON
export DB_PASSWORD=tu_password
mvn spring-boot:run
```

## Compilación y Ejecución

### Compilar el proyecto

```bash
mvn clean compile
```

### Ejecutar tests

```bash
mvn test
```

### Ejecutar la aplicación

```bash
# Con variables de entorno configuradas
mvn spring-boot:run

# O después de compilar
java -jar target/autosolutions-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: http://localhost:8080

## Seguridad

- **Credenciales**: Todas las credenciales sensibles se gestionan mediante variables de entorno.
- **CSRF**: La aplicación tiene protección CSRF habilitada.
- **Autenticación**: Se requiere autenticación para todas las rutas excepto `/login` y recursos estáticos.

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/autosolutions/
│   │       ├── api/           # REST Controllers
│   │       ├── config/        # Configuración Spring
│   │       ├── domain/        # Entidades JPA
│   │       ├── jpa/           # Convertidores JPA
│   │       ├── repo/          # Repositorios
│   │       ├── service/       # Servicios
│   │       └── web/           # Controladores Web
│   └── resources/
│       ├── db/migration/      # Scripts Flyway
│       ├── static/            # CSS, imágenes, JS
│       └── templates/         # Plantillas Thymeleaf
└── test/
    └── java/
        └── com/autosolutions/
```

## Tecnologías

- **Spring Boot 3.5.7**
- **Java 21**
- **Oracle Database** con JDBC
- **Flyway** para migraciones
- **Thymeleaf** para vistas
- **Spring Security** para autenticación
- **Lombok** para reducir código boilerplate
- **Bootstrap 5** para UI

## Licencia

[Especificar licencia]
