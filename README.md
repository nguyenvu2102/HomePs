# HomePS

HomePS la he thong quan ly cua hang may choi game PlayStation: quan ly may, luot choi, dich vu, hoa don, su kien giam gia, thong ke doanh thu, lich su su dung va quan tri he thong.

## Yeu Cau

- Docker Desktop
- PowerShell hoac terminal tuong duong

Du an chay bang Docker. Khong can cai Maven hay Tomcat local trong thu muc du an. Dockerfile se tu dung image Maven de build WAR va image Tomcat de chay ung dung.

## Chay Nhanh

Tai thu muc goc du an:

```powershell
docker compose up -d --build
```

Mo trinh duyet:

```text
http://localhost:8080/
```

Tai khoan mac dinh:

```text
Admin: admin / admin
Nhan vien: staff / staff
```

Database Docker:

```text
DB: homeps
User: postgres
Password: postgres
```

## Deploy Lai Sau Khi Sua Code

Sau khi sua Java, HTML, CSS, JS hoac SQL init:

```powershell
docker compose up -d --build
```

Kiem tra container:

```powershell
docker compose ps
```

Xem log neu can:

```powershell
docker compose logs -f backend
docker compose logs -f db
```

Dung ung dung:

```powershell
docker compose down
```

## Cau Truc Chinh

```text
src/main/java/          Backend Servlet/DAO/model
src/main/webapp/        Giao dien web
src/main/resources/db/  SQL khoi tao database
Dockerfile              Build WAR va chay Tomcat trong container
docker-compose.yml      PostgreSQL + app container
pom.xml                 Cau hinh Maven cho Docker build
```

## API Chinh

```text
GET/POST /api/machines
GET/POST /api/services
GET      /api/invoices
GET/POST /api/events
GET      /api/stats
GET      /api/history
GET/POST /api/admin
POST     /login
GET      /logout
POST     /hoadon
```

## Luu Y

- Khong can commit `target/`, `maven/`, `tomcat/` hoac `.mvn/`.
- Neu port `8080` hoac `5432` bi chiem, doi mapping port trong `docker-compose.yml`.
- Neu can tao database moi tu dau, chay `docker compose down -v` roi `docker compose up -d --build`.
