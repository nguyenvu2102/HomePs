# HomePS

HomePS là hệ thống quản lý cửa hàng máy chơi game PlayStation, gồm quản lý máy, lượt chơi, dịch vụ, hóa đơn, sự kiện giảm giá, thống kê doanh thu, lịch sử sử dụng và quản trị hệ thống.

## Yêu cầu

- Docker Desktop
- Java 17 nếu chạy/test bằng Maven local
- PowerShell trên Windows

## Chạy nhanh bằng Docker

Ở thư mục gốc dự án:

```powershell
docker compose up -d --build
```

Mở trình duyệt:

```text
http://localhost:8080/
```

Tài khoản mặc định:

```text
Admin: admin / admin
Nhân viên: staff / staff
```

Docker sẽ tự chạy PostgreSQL và ứng dụng Tomcat. Database trong Docker dùng:

```text
DB: homeps
User: postgres
Password: postgres
```

## Test local bằng Maven

Nếu test với database local `HomePS` và mật khẩu PostgreSQL của máy:

```powershell
$env:DB_URL='jdbc:postgresql://localhost:5432/HomePS'
$env:DB_USER='postgres'
$env:DB_PASSWORD='Nguyenvu@2102'
.\maven\bin\mvn.cmd test
```

Nếu dùng database Docker:

```powershell
$env:DB_URL='jdbc:postgresql://localhost:5432/homeps'
$env:DB_USER='postgres'
$env:DB_PASSWORD='postgres'
.\maven\bin\mvn.cmd test
```

## Deploy lại sau khi sửa code

Sau khi sửa Java, HTML, CSS, JS hoặc SQL init:

```powershell
docker compose up -d --build
```

Sau đó refresh lại:

```text
http://localhost:8080/
```

## Các chức năng chính

- Quản lý máy PS: mở máy, đóng máy, tính tiền giờ.
- Dịch vụ: gọi đồ ăn, nước uống, snack cho máy đang chơi.
- Hóa đơn: tạo hóa đơn, áp dụng giảm giá, thanh toán.
- Sự kiện giảm giá: tạo/tắt sự kiện, áp dụng theo ngày hoặc khung giờ.
- Thống kê doanh thu: doanh thu theo ngày/tuần/tháng, theo máy, theo dịch vụ.
- Lịch sử sử dụng: tra cứu lượt chơi, nhân viên, máy, hóa đơn liên quan.
- Quản trị hệ thống: thêm/sửa máy, nhân viên, dịch vụ.

## Một số API đang dùng

```text
GET/POST /api/machines
GET/POST /api/services
GET      /api/invoices
GET/POST /api/events
GET      /api/stats
GET      /api/history
GET/POST /api/admin
POST     /login
POST     /hoadon
```

## Lỗi thường gặp

### Không vào được localhost:8080

Kiểm tra container:

```powershell
docker compose ps
```

Nếu app chưa chạy lại:

```powershell
docker compose up -d --build
```

### Sai mật khẩu database khi test

Với Docker dùng mật khẩu:

```text
postgres
```

Với database local trên máy đã test được mật khẩu:

```text
Nguyenvu@2102
```

### Port 5432 hoặc 8080 bị chiếm

Kiểm tra tiến trình hoặc đổi port trong `docker-compose.yml`.

### Dữ liệu seed bị lệch ID

File `src/main/resources/db/init.sql` đã đồng bộ sequence cho bảng `nhanvien`. Nếu tạo database mới bằng Docker, dữ liệu mặc định sẽ tự khởi tạo.

## Dọn file build/log

Các thư mục sau là file sinh ra khi chạy, không cần commit:

```text
target/
tomcat/logs/
tomcat/work/
tomcat/temp/
```

Các mục này đã được thêm vào `.gitignore`.
