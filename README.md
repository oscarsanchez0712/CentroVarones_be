# Backend de Centro de Varones

API de matrículas, cursos y notas con Spring Boot, Maven y SQL Server. Docker Compose está separado en `docker-compose-db.yml` (SQL Server) y `docker-compose-be.yml` (API), siguiendo la estructura del proyecto de referencia. Ambos servicios se ejecutan en el grupo `centro-varones` y comparten una red; SQL Server conserva sus datos en un volumen persistente.

## Preparación inicial

Ejecuta PowerShell desde la raíz `Centro_de_varones_be` y abre Docker Desktop.

### 1. Configurar SQL Server

```powershell
Copy-Item .\docker-compose-Fabrizio\.env.example .\docker-compose-Fabrizio\.env
notepad .\docker-compose-Fabrizio\.env
docker volume create centro_varones_sql_data
```

Define `CENTRO_DB_PASSWORD` en `.env`. El volumen conserva los datos aunque detengas o recrees los contenedores.

### 2. Iniciar la base de datos

Los dos archivos se pasan a Compose para que reconozca todos los servicios del mismo grupo; `sqlserver` inicia solamente la base:

```powershell
docker compose --env-file .\docker-compose-Fabrizio\.env -p centro-varones -f .\docker-compose-Fabrizio\docker-compose-db.yml -f .\docker-compose-Fabrizio\docker-compose-be.yml up -d sqlserver
```

Conecta SSMS a `localhost,1433` con usuario `sa` y la contraseña de `.env`. Ejecuta los scripts en este orden:

1. `src/main/resources/sql/create-database.sql`
2. `src/main/resources/sql/schema.sql`
3. `src/main/resources/sql/inserts.sql`

El último script agrega 11 matrículas ficticias y 22 notas. No vuelvas a ejecutar `create-database.sql` si ya tienes información real que deseas conservar.

### 3. Iniciar la API

Después de iniciar SQL Server y crear la base/tablas:

```powershell
docker compose --env-file .\docker-compose-Fabrizio\.env -p centro-varones -f .\docker-compose-Fabrizio\docker-compose-db.yml -f .\docker-compose-Fabrizio\docker-compose-be.yml up -d --build backend
```

La API usa `http://localhost:8085`. Puedes validar los datos con:

```powershell
(Invoke-RestMethod http://localhost:8085/api/matriculas).Count
(Invoke-RestMethod http://localhost:8085/api/notas).Count
```

Con los datos de demostración, los resultados son `11` y `22`.

### 4. Swagger y Postman

- Swagger UI: `http://localhost:8085/swagger-ui.html`
- OpenAPI: `http://localhost:8085/api-docs`
- Importa `postman/Centro-de-Varones.postman_collection.json` en Postman.

## Actualizar la API

```powershell
docker compose --env-file .\docker-compose-Fabrizio\.env -p centro-varones -f .\docker-compose-Fabrizio\docker-compose-db.yml -f .\docker-compose-Fabrizio\docker-compose-be.yml up -d --build --force-recreate backend
```

## Detener los servicios

```powershell
docker compose --env-file .\docker-compose-Fabrizio\.env -p centro-varones -f .\docker-compose-Fabrizio\docker-compose-db.yml -f .\docker-compose-Fabrizio\docker-compose-be.yml down
```

Este comando conserva el volumen `centro_varones_sql_data`. No uses `docker volume rm` ni `down --volumes` si quieres conservar la base.