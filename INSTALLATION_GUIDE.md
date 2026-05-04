# HomePS Setup & Installation Guide

## Quick Start (5 minutes)

### For Windows Users
1. **Open PowerShell/Command Prompt** in the project directory
2. **Double-click** `quickstart.bat` or run:
   ```bash
   ./quickstart.bat
   ```
3. **Wait** for the build and Docker startup (2-3 minutes)
4. **Open browser** and go to: `http://localhost:8080/HomePS`
5. **Login** with any username/password (demo mode)

### For Linux/Mac Users
1. **Open Terminal** in the project directory
2. **Run** the quickstart script:
   ```bash
   chmod +x quickstart.sh
   ./quickstart.sh
   ```
3. **Wait** for the build and Docker startup (2-3 minutes)
4. **Open browser** and go to: `http://localhost:8080/HomePS`
5. **Login** with any username/password (demo mode)

## Prerequisites

### Required
- **Docker** - [Download & Install](https://www.docker.com/products/docker-desktop)
- **Docker Compose** - Included with Docker Desktop
- **Git** (optional, for version control)

### Optional (for local development without Docker)
- **Java 17+** - [Download & Install](https://adoptopenjdk.net/)
- **Maven 3.9+** - [Download & Install](https://maven.apache.org/download.cgi)
- **PostgreSQL 15** - [Download & Install](https://www.postgresql.org/download/)

## Installation Methods

### Method 1: Docker Compose (Recommended)

**Step 1: Clone/Download the project**
```bash
cd E:\HUST\HomePS
# or your project location
```

**Step 2: Make sure Docker is running**
- Open Docker Desktop
- Wait for it to fully start

**Step 3: Build and start**
```bash
docker-compose up -d
```

**Step 4: Wait for services**
```bash
# Check status
docker-compose ps

# View logs
docker-compose logs -f backend
```

**Step 5: Access the application**
- Open browser: http://localhost:8080/HomePS
- Login: any username/password

### Method 2: Quickstart Script

**Windows:**
```bash
.\quickstart.bat
```

**Linux/Mac:**
```bash
bash quickstart.sh
```

### Method 3: Manual Build (Local Development)

**Requirements:** Java 17+, Maven 3.9+, PostgreSQL 15

**Step 1: Build the WAR file**
```bash
mvn clean package
```

**Step 2: Start PostgreSQL**
```bash
# Option A: Using Docker for DB only
docker run -d \
  --name homeps_db \
  -e POSTGRES_DB=homeps \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# Option B: Using local PostgreSQL installation
# Make sure PostgreSQL is running on localhost:5432
```

**Step 3: Initialize database**
```bash
# Run the init.sql script with your PostgreSQL client
psql -U postgres -f src/main/resources/db/init.sql
```

**Step 4: Setup Tomcat**
- Download Apache Tomcat 10.1.x
- Copy `target/HomePS.war` to `tomcat/webapps/ROOT.war`
- Start Tomcat

**Step 5: Access application**
- Open browser: http://localhost:8080

## Verifying Installation

### Check Docker Services
```bash
docker-compose ps
# You should see:
# - homeps_db   (postgres:15) - Up
# - homeps_app  (tomcat)       - Up
```

### Check Application Logs
```bash
# View all logs
docker-compose logs

# View specific service
docker-compose logs backend
docker-compose logs db

# Follow logs in real-time
docker-compose logs -f backend
```

### Test Database Connection
```bash
# Connect to database from command line
docker-compose exec db psql -U postgres -d homeps -c "SELECT version();"

# Should return PostgreSQL version
```

## Configuration

### Environment Variables

**Default values in docker-compose.yml:**
```yaml
DB_URL: jdbc:postgresql://db:5432/homeps
DB_USER: postgres
DB_PASSWORD: postgres
DB_HOST: db
DB_PORT: 5432
```

**To change values:**
1. Edit `docker-compose.yml`
2. Modify the `environment` section under `backend` service
3. Restart services: `docker-compose restart`

### Port Configuration

**Default ports:**
- Application: **8080** (Tomcat)
- Database: **5432** (PostgreSQL)

**To change ports:**
1. Edit `docker-compose.yml`
2. Change the `ports` section:
   ```yaml
   backend:
     ports:
       - "8080:8080"  # Change first port for access port
   
   db:
     ports:
       - "5432:5432"  # Change first port for access port
   ```
3. Restart: `docker-compose restart`

## Common Tasks

### View Application Logs
```bash
# Real-time logs
docker-compose logs -f backend

# Last 100 lines
docker-compose logs backend --tail=100

# With timestamps
docker-compose logs backend --timestamps
```

### Access Database Directly
```bash
# Connect to database shell
docker-compose exec db psql -U postgres -d homeps

# Run SQL query
docker-compose exec db psql -U postgres -d homeps -c "SELECT * FROM nhan_vien;"

# Backup database
docker-compose exec db pg_dump -U postgres homeps > backup.sql

# Restore database
docker-compose exec -T db psql -U postgres homeps < backup.sql
```

### Restart Services
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart backend
docker-compose restart db

# Full restart (with rebuild)
docker-compose down
docker-compose up -d --build
```

### Update Application
```bash
# After code changes
git pull                 # Get latest code
mvn clean package       # Build locally
docker-compose restart backend  # Restart app

# Or full rebuild
docker-compose down -v
docker-compose up -d --build
```

### Monitor Resource Usage
```bash
# View Docker resource usage
docker stats

# View logs with resource spikes
docker-compose logs --tail=50
```

## Troubleshooting

### Issue 1: "Address already in use"
**Error:** `Error response from daemon: driver failed programming external connectivity on endpoint`

**Solution:**
```bash
# Check what's using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Change port in docker-compose.yml
# Or kill process using the port
taskkill /PID <PID> /F        # Windows
kill -9 <PID>                 # Linux/Mac
```

### Issue 2: "Connection refused"
**Error:** `Cannot connect to database` or `Connection refused`

**Solution:**
```bash
# Check if services are running
docker-compose ps

# Check database logs
docker-compose logs db

# Restart database
docker-compose restart db

# Wait 10 seconds for database to start
docker-compose logs db | grep "ready to accept"

# Restart application
docker-compose restart backend
```

### Issue 3: "Failed to build image"
**Error:** `ERROR [build] FAILED`

**Solution:**
```bash
# Clean Docker resources
docker system prune -a

# Rebuild with verbose output
docker-compose up -d --build

# Check Maven build locally
mvn clean package -DskipTests
```

### Issue 4: "Page not found" or "404 error"
**Error:** `HTTP 404: The requested URL was not found`

**Solution:**
```bash
# Check application logs
docker-compose logs backend | grep -i error

# Verify application started
docker-compose logs backend | grep "Tomcat started"

# Check Docker exec command
docker-compose exec backend curl http://localhost:8080/HomePS/login

# If still not working, restart
docker-compose restart backend
```

### Issue 5: "Login page loads but login doesn't work"
**Error:** `Redirect loop` or `No response from server`

**Solution:**
```bash
# Check Java logs in container
docker-compose exec backend bash -c "tail -f /usr/local/tomcat/logs/catalina.out"

# Verify session is being set
# Check browser developer console (F12 -> Console)

# Clear browser cache and cookies
# Try in incognito/private window
```

## Monitoring & Maintenance

### Regular Maintenance
```bash
# Daily - Check logs for errors
docker-compose logs --since 1h

# Weekly - Update Docker images
docker-compose pull

# Weekly - Check disk usage
docker system df

# Monthly - Clean up old images/containers
docker system prune -a --volumes
```

### Performance Tuning
```bash
# Check resource limits
docker inspect homeps_app --format='{{json .HostConfig.Memory}}'

# Update docker-compose.yml for limited resources:
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
```

### Backup & Restore

**Backup database:**
```bash
docker-compose exec db pg_dump -U postgres homeps > backup_$(date +%Y%m%d).sql
```

**Restore database:**
```bash
docker-compose exec -T db psql -U postgres homeps < backup_20260504.sql
```

**Backup application data:**
```bash
# Docker volumes
docker cp homeps_db:/var/lib/postgresql/data ./backup_postgres_data

# Volume backup using tar
docker run --rm -v homeps_postgres_data:/data -v $(pwd):/backup \
  busybox tar czf /backup/postgres_backup.tar.gz /data
```

## Security Best Practices

### For Development
✅ Current setup is fine for development
- Demo login accepts any credentials
- Database exposed on localhost only
- No authentication required

### For Production
⚠️ **Required changes:**
1. **Enable authentication:**
   - Implement proper user authentication
   - Use password hashing (bcrypt)
   - Add database verification

2. **Secure database:**
   - Change default PostgreSQL password
   - Don't expose database port to internet
   - Use network policies

3. **Use HTTPS:**
   - Add SSL/TLS certificates
   - Configure Tomcat with HTTPS
   - Use reverse proxy (Nginx)

4. **Implement API security:**
   - Add CORS configuration
   - Implement rate limiting
   - Enable CSRF protection

5. **Monitor & log:**
   - Collect logs to centralized location
   - Monitor resource usage
   - Set up alerts

## Getting Help

### Check Documentation
- Detailed guide: `DOCKER_SETUP.md`
- Migration details: `MIGRATION_SUMMARY.md`
- Project README: `README.md`

### Debug Information to Provide
When asking for help, include:
```bash
# Get system info
docker --version
docker-compose --version

# Get service status
docker-compose ps

# Get logs
docker-compose logs backend --tail=50

# Get error details
docker-compose logs db --tail=50
```

### Useful Commands Reference
```bash
# Basic operations
docker-compose up -d              # Start services
docker-compose down               # Stop services
docker-compose logs -f             # View logs
docker-compose ps                 # List services

# Database operations
docker-compose exec db psql -U postgres -d homeps

# Rebuild
docker-compose up -d --build      # Rebuild and restart

# Cleanup
docker-compose down -v            # Remove volumes (data loss!)
docker system prune               # Clean unused resources
```

## Next Steps

1. **Verify installation** - Check that app loads at http://localhost:8080/HomePS
2. **Test login** - Try logging in with test credentials
3. **Explore features** - Navigate through different pages
4. **Check logs** - Review logs to ensure no errors
5. **Read documentation** - Explore DOCKER_SETUP.md for advanced usage

## Support Resources

- **Docker Documentation**: https://docs.docker.com/
- **Docker Compose Reference**: https://docs.docker.com/compose/compose-file/
- **PostgreSQL Documentation**: https://www.postgresql.org/docs/
- **Jakarta EE**: https://jakarta.ee/
- **Tomcat Documentation**: https://tomcat.apache.org/

---

**Last Updated:** May 4, 2026
**Version:** 2.0
**Status:** ✅ Ready for deployment

