# HomePS - Hệ Thống Quản Lý Cửa Hàng Game

## 📋 Giới Thiệu

Một hệ thống quản lý cửa hàng game (PS/máy tính) toàn diện được xây dựng bằng Java Web Technology (JSP, Servlet) và PostgreSQL.

## 🎯 Tính Năng Chính

### 1. **Quản Lý Máy Chơi**
- Xem danh sách tất cả máy
- Quản lý trạng thái máy (Bình thường, Đang chơi, Hỏng)
- Cập nhật thông tin máy
- Giá cơ bản: 30.000 VND/giờ

### 2. **Quản Lý Lượt Chơi**
- Khách bắt đầu chơi: tính thời gian từ khi nhận máy
- Kết thúc lượt chơi: tính tiền dựa trên thời gian chơi
- Lưu lịch sử lượt chơi cho từng máy

### 3. **Quản Lý Dịch Vụ**
- Thêm/sửa/xóa dịch vụ (đồ ăn, nước, v.v)
- Khách gọi dịch vụ trong lúc chơi
- Tính toàn bộ chi phí dịch vụ

### 4. **Hoá Đơn**
- Tạo hoá đơn cho mỗi lượt chơi
- Chi tiết hóa đơn: thời gian chơi + dịch vụ
- Quản lý trạng thái thanh toán

### 5. **Sự Kiện & Khuyến Mãi**
- Tạo sự kiện giảm giá theo giờ (ví dụ: 11:00-14:00 giảm 20%)
- Tạo sự kiện theo ngày lễ (ví dụ: Tết giảm 30%)
- Tự động áp dụng khi khách thanh toán

### 6. **Thống Kê & Báo Cáo**
- Thống kê theo ngày: doanh thu từng máy, tổng doanh thu
- Thống kê theo tuần: so sánh giữa các tuần
- Thống kê theo tháng: xu hướng doanh thu
- Phân tích: tiền chơi vs tiền dịch vụ

## 🛠️ Công Nghệ Sử Dụng

- **Backend**: Java 17, Jakarta Servlet/JSP
- **Database**: PostgreSQL
- **Build Tool**: Maven 3.9.14
- **Application Server**: Apache Tomcat
- **Web Framework**: Pure Servlet/JSP (No Framework)

## 📦 Cấu Trúc Dự Án

```
HomePS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/       # Xử lý request
│   │   │   ├── dao/              # Truy cập cơ sở dữ liệu
│   │   │   ├── model/            # Các lớp dữ liệu
│   │   │   └── utils/            # Tiện ích (DBConnection)
│   │   ├── resources/
│   │   │   └── db/
│   │   │       └── init.sql      # Script khởi tạo DB
│   │   └── webapp/
│   │       ├── index.jsp         # Trang chủ
│   │       └── WEB-INF/          # Cấu hình web
│   └── test/
├── tomcat/                       # Apache Tomcat
├── maven/                        # Maven binary
└── pom.xml                       # Cấu hình Maven
```

## 🚀 Cách Chạy

### 1. **Yêu Cầu**
- Java 17+
- PostgreSQL 12+
- Windows/Linux/macOS

### 2. **Cấu Hình Cơ Sở Dữ Liệu**

Tạo database:
```bash
createdb HomePS
```

Chạy script init:
```bash
psql -U postgres -d HomePS -f src/main/resources/db/init.sql
```

### 3. **Cấu Hình Biến Môi Trường** (tùy chọn)

```bash
# Windows
set HOMEPS_DB_URL=jdbc:postgresql://localhost:5432/HomePS
set HOMEPS_DB_USER=postgres
set HOMEPS_DB_PASS=postgres

# Linux/macOS
export HOMEPS_DB_URL=jdbc:postgresql://localhost:5432/HomePS
export HOMEPS_DB_USER=postgres
export HOMEPS_DB_PASS=postgres
```

Nếu không set, sẽ dùng mặc định:
- URL: `jdbc:postgresql://localhost:5432/HomePS`
- User: `postgres`
- Password: `postgres`

### 4. **Build & Deploy**

```bash
# Build project
./maven/bin/mvn clean package -DskipTests

# Copy WAR file
copy target\HomePS.war tomcat\webapps\

# Start Tomcat
tomcat\bin\startup.bat              # Windows
./tomcat/bin/startup.sh             # Linux/macOS
```

### 5. **Truy Cập Ứng Dụng**

```
http://localhost:8080/HomePS/home
```

## 📡 API Endpoints

| Endpoint | Phương Thức | Mô Tả |
|----------|-------------|-------|
| `/home` | GET | Danh sách máy & quản lý |
| `/home` | POST | Mở/Đóng máy |
| `/hoadon?action=view` | GET | Xem hoá đơn |
| `/hoadon` | POST | Thêm dịch vụ/Thanh toán |
| `/sukien?action=list` | GET | Danh sách sự kiện |
| `/sukien` | POST | Tạo/Sửa/Xóa sự kiện |
| `/thongke?loai=ngay` | GET | Thống kê theo ngày |
| `/thongke?loai=tuan` | GET | Thống kê theo tuần |
| `/thongke?loai=thang` | GET | Thống kê theo tháng |

## 💾 Cơ Sở Dữ Liệu

### Các Bảng Chính

1. **mayps** - Máy chơi
2. **nhanvien** - Nhân viên
3. **luotchoi** - Lượt chơi của khách
4. **dichvu** - Dịch vụ
5. **hoadon** - Hoá đơn
6. **chitiet_hoadon** - Chi tiết hoá đơn
7. **sukien** - Sự kiện/Khuyến mãi

## 🔧 Các Model Class

- `MayPS` - Thông tin máy
- `NhanVien` - Thông tin nhân viên
- `LuotChoi` - Thông tin lượt chơi
- `DichVu` - Thông tin dịch vụ
- `HoaDon` - Hoá đơn khách
- `ChiTietHoaDon` - Chi tiết dịch vụ trong hoá đơn
- `SuKien` - Sự kiện khuyến mãi
- `ThongKe` - Dữ liệu thống kê

## 📝 Các DAO Class

Mỗi model có một DAO class tương ứng để xử lý CRUD operations:
- `MayPSDAO`
- `NhanVienDAO`
- `LuotChoiDAO`
- `DichVuDAO`
- `HoaDonDAO`
- `ChiTietHoaDonDAO`
- `SuKienDAO`
- `ThongKeDAO`

## 🎮 Các Controller Class

- `HomeController` - Quản lý máy
- `HoaDonController` - Quản lý hoá đơn
- `SuKienController` - Quản lý sự kiện
- `ThongKeController` - Thống kê
- `DichVuController` - Quản lý dịch vụ

## 📊 Quy Trình Thanh Toán

1. **Khách vào cửa hàng**: Nhân viên mở máy
   ```
   POST /home
   action=open
   mayId=1
   nhanVienId=1
   ```

2. **Khách gọi dịch vụ**: Thêm vào hoá đơn
   ```
   POST /hoadon
   action=addService
   luotChoiId=1
   dichVuId=1
   soLuong=1
   ```

3. **Khách rời quán**: Đóng máy & thanh toán
   ```
   POST /home
   action=close
   mayId=1
   ```

4. **Hệ thống tính tiền**:
   - Thời gian chơi × 30.000/giờ
   - + Tổng tiền dịch vụ
   - - Khuyến mãi (nếu có)
   = **Tổng tiền**

## 🎁 Ví Dụ Sự Kiện

### Sự Kiện Giờ Vàng (11:00-14:00 giảm 20%)
```
- Tên: Giờ Vàng Trưa
- Mô tả: Khuyến mãi đặc biệt 11h-14h hàng ngày
- Giảm giá: 20%
- Loại: THEO_GIO
- Giờ áp dụng: 11:00-14:00
- Ngày áp dụng: NULL (tất cả ngày)
- Ngày bắt đầu: 2025-04-01
- Ngày kết thúc: 2025-12-31
```

### Sự Kiện Tết (Giảm 30%)
```
- Tên: Tết Âm Lịch
- Mô tả: Khuyến mãi Tết Nguyên Đán
- Giảm giá: 30%
- Loại: THEO_NGAY
- Ngày áp dụng: 2025-02-29
- Ngày bắt đầu: 2025-02-28
- Ngày kết thúc: 2025-03-02
```

## 📈 Báo Cáo Thống Kê

### Thống Kê Theo Ngày
- Số lượt chơi trên từng máy
- Doanh thu từ chơi
- Doanh thu từ dịch vụ
- Tiền khuyến mãi
- Tổng doanh thu

### Thống Kê Theo Tuần
- So sánh giữa các máy
- Xu hướng doanh thu
- Hiệu suất từng máy

### Thống Kê Theo Tháng
- Phân tích chi tiết từng tháng
- So sánh tháng trước/sau
- Dự báo doanh thu

## ⚙️ Cấu Hình Tomcat

File cấu hình chính: `tomcat/conf/server.xml`

Cổng mặc định: **8080**

Để thay đổi cổng:
```xml
<Connector port="9090" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

## 🐛 Khắc Phục Sự Cố

### Lỗi: "Cannot connect to HomePS database"
- Kiểm tra PostgreSQL đang chạy
- Kiểm tra biến môi trường HOMEPS_DB_*
- Kiểm tra tên database là "HomePS"

### Lỗi: "Port 8080 already in use"
- Thay đổi cổng Tomcat trong `server.xml`
- Hoặc dừng ứng dụng khác sử dụng cổng 8080

### Lỗi: "WAR file not deploying"
- Kiểm tra tên file là `HomePS.war`
- Xóa thư mục `tomcat/webapps/HomePS` (nếu có)
- Khởi động lại Tomcat

## 📞 Support

Để sửa lỗi hoặc cần hỗ trợ:
1. Kiểm tra log: `tomcat/logs/catalina.log`
2. Kiểm tra database connection
3. Verify tất cả bảng được tạo trong PostgreSQL

## 📄 License

Dự án này được tạo cho mục đích học tập tại trường ĐH Bách Khoa Hà Nội.

---

**Version**: 1.0.0  
**Last Updated**: April 4, 2026

