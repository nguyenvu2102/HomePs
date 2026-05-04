# 🚀 HomePS Quick Reference Card

## ⚡ QUICKEST START (5 minutes)

### Windows
```bash
# Option 1: Double-click file
.\quickstart.bat

# Option 2: Run command
docker-compose up -d
```

### Linux/Mac
```bash
chmod +x quickstart.sh
./quickstart.sh

# Or
docker-compose up -d
```

### Then Open Browser
```
http://localhost:8080/HomePS
```

---

## 📋 What Changed

| Old | New |
|-----|-----|
| JSP pages | Single HTML5 page |
| Server-side rendering | Client-side SPA |
| Form submissions | API calls |
| Multiple JSP files | One index.html file |
| Manual deployment | Docker containers |

---

## 🎯 Key Files

### Frontend
- `src/main/webapp/index.html` - Main application (replaces all JSP files)

### Backend
- `src/main/java/controller/AppController.java` - NEW
- `src/main/java/controller/ApiMachineController.java` - NEW
- `src/main/java/controller/LoginController.java` - UPDATED
- `src/main/java/controller/HomeController.java` - UPDATED
- `src/main/java/filter/AuthFilter.java` - UPDATED

### Docker
- `docker-compose.yml` - Container orchestration
- `Dockerfile` - Image build configuration
- `.dockerignore` - Optimize build

### Documentation
- `COMPLETION_SUMMARY.md` - What was done
- `INSTALLATION_GUIDE.md` - Step-by-step setup
- `DOCKER_SETUP.md` - Docker details
- `MIGRATION_SUMMARY.md` - Technical details

---

## ✅ Verification

After starting, check:

```bash
# Services running?
docker-compose ps
# Expected: 2 services (db, backend) - Status: Up

# Logs clean?
docker-compose logs backend
# Should see: "Tomcat started in ... ms"

# App loads?
Open http://localhost:8080/HomePS
# Should see login page

# Can login?
Username: (any)
Password: (any)
# Should redirect to machine management page
```

---

## 🔧 Essential Commands

```bash
# START
docker-compose up -d

# STOP
docker-compose down

# VIEW LOGS
docker-compose logs -f backend

# RESTART
docker-compose restart

# REBUILD (after code changes)
mvn clean package
docker-compose up -d --build

# DATABASE ACCESS
docker-compose exec db psql -U postgres -d homeps

# CLEAN UP
docker-compose down -v
docker system prune -a
```

---

## 🌐 Access Points

| Service | URL |
|---------|-----|
| Application | http://localhost:8080/HomePS |
| Tomcat Manager | http://localhost:8080/manager |
| Database | localhost:5432 |
| Database Admin | psql (command line) |

---

## 👥 Default Credentials

| Service | User | Password |
|---------|------|----------|
| Application | Any | Any (demo) |
| Database | postgres | postgres |
| Tomcat | - | - (no auth) |

⚠️ **Change for production!**

---

## 📊 Build Information

```
Status: ✅ BUILD SUCCESSFUL
Last Built: May 4, 2026
Java Version: 17
Container Engine: Docker/Compose
Database: PostgreSQL 15
App Server: Tomcat 10.1
```

---

## 🐛 Quick Troubleshooting

| Issue | Fix |
|-------|-----|
| Port in use | Kill process: `taskkill /PID <id> /F` |
| Won't start | Check logs: `docker-compose logs backend` |
| Page 404 | Restart: `docker-compose restart backend` |
| DB error | Check DB: `docker-compose logs db` |
| Can't login | Clear cache: Ctrl+Shift+Delete in browser |

---

## 📚 Need More Help?

1. **Installation** → Read `INSTALLATION_GUIDE.md`
2. **Docker** → Read `DOCKER_SETUP.md`
3. **What Changed** → Read `MIGRATION_SUMMARY.md`
4. **Full Details** → Read `COMPLETION_SUMMARY.md`
5. **Project Info** → Read `README.md`

---

## 🎯 Next Steps

1. Run quickstart script (Windows/Linux/Mac)
2. Wait 2-3 minutes for build
3. Open http://localhost:8080/HomePS
4. Login (any credentials)
5. Explore the application
6. Read documentation if needed

---

## 💡 Pro Tips

✅ **Working with Docker:**
- Always check logs first when something fails
- Use `docker-compose ps` to verify services running
- `docker-compose exec <service> <command>` to run commands in containers

✅ **Development:**
- Edit `.env` file to override environment variables (create it)
- Code changes need rebuild: `mvn clean package`
- Hot reload not available with Docker WAR deployment

✅ **Performance:**
- Database caches queries automatically
- Frontend is single HTML file, loads once
- JavaScript caches API responses in browser

---

## 📞 Getting Help

```bash
# Get all information you might need:
docker-compose ps
docker-compose logs backend
docker-compose logs db
docker exec homeps_app curl http://localhost:8080/HomePS/login
```

Copy output and share if asking for help.

---

## 🚀 You're Ready!

✅ Everything is set up and ready to go  
✅ One command: `docker-compose up -d`  
✅ Application: http://localhost:8080/HomePS  
✅ Full documentation included  

**Enjoy! 🎮**

---

**Reference Card Version:** 1.0  
**Last Updated:** May 4, 2026  
**For:** HomePS v2.0

