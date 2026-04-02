# HomePS Web App (MVP)

Simple Jakarta Servlet + JSP web app for managing PC stations in a HomePS shop.

## Current MVP features

- Machine dashboard (`/home`)
- Open machine session
- Close machine session and calculate hourly charge
- Service catalog management (`/dichvu`) with create/update/delete
- Flash messages for user actions

## Tech stack

- Java 17
- Maven (WAR project)
- Jakarta Servlet 6 + JSP + JSTL
- PostgreSQL

## Database setup

1. Create database `HomePS` in PostgreSQL.
2. Run SQL script (or rerun after schema updates):

```sql
-- psql -U postgres -d HomePS -f src/main/resources/db/init.sql
```

## Environment variables

- `HOMEPS_DB_URL` (default: `jdbc:postgresql://localhost:5432/HomePS`)
- `HOMEPS_DB_USER` (default: `postgres`)
- `HOMEPS_DB_PASS` (default: `postgres`)

## Build and test

```powershell
mvn clean test
mvn clean package
```

WAR output:

- `target/HomePS.war`

## Main pages

- `http://localhost:8080/HomePS/home`
- `http://localhost:8080/HomePS/dichvu`

Deploy WAR to a Jakarta-compatible servlet container (for example Tomcat 10.1+).
