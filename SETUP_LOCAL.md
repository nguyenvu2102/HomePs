# Hướng dẫn chạy HomePS trên Tomcat Local

## Bước 1: Chuẩn bị Database PostgreSQL

### Nếu chưa cài PostgreSQL:
1. Tải và cài PostgreSQL từ https://www.postgresql.org/download/windows/
2. Chọn phiên bản stable mới nhất
3. Config port: `5432`
4. Nhớ password cho user `postgres` (ghi lại, sẽ dùng sau)

### Nếu đã cài PostgreSQL:
1. Mở Command Prompt hoặc PowerShell
2. Chạy: `psql -U postgres` (nếu lỗi, thêm `-h localhost`)
3. Nhập password PostgreSQL khi được hỏi

### Tạo Database và Schema:

```sql
-- Kết nối superuser postgres
psql -U postgres -h localhost

-- Trong SQL console, chạy:
CREATE DATABASE homeps;
\c homeps

-- Tạo bảng mayps
CREATE TABLE mayps (
    id SERIAL PRIMARY KEY,
    tenmay VARCHAR(50) NOT NULL,
    tinhtrang VARCHAR(50) DEFAULT 'BINH_THUONG',
    ghichu TEXT
);

-- Tạo bảng nhanvien
CREATE TABLE nhanvien (
    id SERIAL PRIMARY KEY,
    ten VARCHAR(100),
    vaitro VARCHAR(50)
);

-- Tạo bảng luotchoi
CREATE TABLE luotchoi (
    id SERIAL PRIMARY KEY,
    mayid INTEGER REFERENCES mayps(id),
    nhanvienid INTEGER REFERENCES nhanvien(id),
    thoigian_bat_dau TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoigian_ket_thuc TIMESTAMP,
    dongiagio NUMERIC(10,2)
);

-- Insert data sample
INSERT INTO nhanvien (ten, vaitro) VALUES ('Nhân viên Test', 'EMPLOYEE');
INSERT INTO mayps (tenmay, tinhtrang) VALUES 
    ('PS 01', 'BINH_THUONG'),
    ('PS 02', 'DANG_CHOI'),
    ('PS 03', 'BINH_THUONG'),
    ('PS 04', 'BINH_THUONG'),
    ('PS 05', 'DANG_CHOI');

-- Kiểm tra
SELECT * FROM mayps;
\q  -- Thoát
```

## Bước 2: Config Tomcat

### Sửa biến môi trường (Windows):

Cách 1: Sửa file `E:\HUST\HomePS\tomcat\bin\setenv.bat`:

```batch
set CATALINA_HOME=E:\HUST\HomePS\tomcat
set CATALINA_BASE=E:\HUST\HomePS\tomcat

:: PostgreSQL Config
set DB_URL=jdbc:postgresql://localhost:5432/homeps
set DB_USER=postgres
set DB_PASSWORD=postgres
```

Cách 2: Hoặc set biến môi trường Windows level:
1. Nhấn `Win + X` → `System`
2. → `Advanced system settings`
3. → `Environment Variables`
4. → `New` (System variables):
   - `DB_URL` = `jdbc:postgresql://localhost:5432/homeps`
   - `DB_USER` = `postgres`
   - `DB_PASSWORD` = `postgres`
5. Click OK, restart Tomcat

## Bước 3: Chạy Tomcat

### Start Tomcat:

```powershell
cd E:\HUST\HomePS\tomcat\bin
.\startup.bat
```

Hoặc chạy ngầm:

```powershell
start .\startup.bat
```

### Kiểm tra Tomcat đã start:

```powershell
# Mở trình duyệt và truy cập:
http://localhost:8080/
# Hoặc trực tiếp vào HomePS app:
http://localhost:8080/HomePS/
```

## Bước 4: Test chức năng

1. Mở app: `http://localhost:8080/HomePS/`
2. Nếu thấy danh sách máy từ database → **✅ OK**
3. Bấm nút **"Thêm máy mới"** → Nhập tên máy → **✅ Thêm máy được**
4. Chọn máy, bấm **"Mở máy"** → **✅ Mở máy được**
5. Bấm **"Đóng máy"** → **✅ Đóng máy được**

## Bước 5: Xem logs nếu có lỗi

### Logs của Tomcat:

```powershell
cat E:\HUST\HomePS\tomcat\logs\catalina.out
```

### Hoặc ngắn hơn (last 50 lines):

```powershell
Get-Content E:\HUST\HomePS\tomcat\logs\catalina.out -Tail 50
```

## Shutdown Tomcat:

```powershell
cd E:\HUST\HomePS\tomcat\bin
.\shutdown.bat
```

## Troubleshooting

### Lỗi: "Cannot connect to HomePS database"
→ Kiểm tra:
1. PostgreSQL có chạy không: `psql -U postgres -h localhost`
2. Database `homeps` có tồn tại: `\l` (trong psql)
3. Biến môi trường DB_URL/DB_USER/DB_PASSWORD có đúng không

### Lỗi: "404 Not Found"
→ Kiểm tra:
1. URL có đúng: `http://localhost:8080/HomePS/` (chứ không phải `/HomePS`)
2. WAR file có ở `tomcat\webapps\HomePS.war` không

### Lỗi: "Port 8080 already in use"
→ Giải pháp:
1. Tìm process dùng port 8080: `netstat -ano | findstr :8080`
2. Kill process: `taskkill /PID <PID> /F`
3. Hoặc đổi port Tomcat trong `tomcat\conf\server.xml` (tìm `<Connector port="8080"`

## Mẹo

### Build + Deploy nhanh:

```powershell
cd E:\HUST\HomePS

# Build
.\maven\bin\mvn.cmd clean package -DskipTests -q

# Stop Tomcat (nếu đang chạy)
.\tomcat\bin\shutdown.bat

# Copy WAR
Copy-Item .\target\HomePS.war .\tomcat\webapps\ -Force

# Start Tomcat
start .\tomcat\bin\startup.bat
```

### View Database:

```powershell
# Connect to database
psql -U postgres -h localhost -d homeps

# Check all machines
SELECT * FROM mayps;

# Check active sessions
SELECT * FROM luotchoi WHERE thoigian_ket_thuc IS NULL;

# Xem chi tiết máy đang chơi
SELECT m.id, m.tenmay, m.tinhtrang, l.thoigian_bat_dau 
FROM mayps m 
LEFT JOIN luotchoi l ON m.id = l.mayid 
WHERE l.thoigian_ket_thuc IS NULL;
```


