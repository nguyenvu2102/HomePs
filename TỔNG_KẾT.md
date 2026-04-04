# 📊 HomePS - Tóm Tắt Các Lớp Đã Tạo

## ✅ Model Classes (đã tạo)

### 1. **HoaDon.java**
- Đại diện cho hoá đơn của mỗi lượt chơi
- Gồm: ID, luotChoiId, thời gian, tiền chơi, tiền dịch vụ, khuyến mãi, tổng tiền, trạng thái

### 2. **ChiTietHoaDon.java**
- Chi tiết các dịch vụ trong hoá đơn
- Gồm: ID, hoaDonId, dichVuId, tên dịch vụ, số lượng, giá, thành tiền

### 3. **SuKien.java**
- Sự kiện giảm giá/khuyến mãi
- Hỗ trợ các loại: THEO_GIO, THEO_NGAY, THEO_TUAN, THEO_THANG
- Gồm: ID, tên sự kiện, mô tả, % giảm, loại, giờ áp dụng, ngày áp dụng, ngày bắt đầu/kết thúc, trạng thái

### 4. **ThongKe.java**
- Dữ liệu thống kê doanh thu
- Gồm: ngày, mayId, tên máy, số lượt chơi, doanh thu chơi, doanh thu dịch vụ, tiền khuyến mãi, tổng, loại thống kê

## ✅ DAO Classes (đã tạo)

### 1. **HoaDonDAO.java**
- create() - Tạo hoá đơn mới
- findByLuotChoiId() - Tìm hoá đơn theo lượt chơi
- findById() - Tìm hoá đơn theo ID
- getAllByDate() - Lấy tất cả hoá đơn của ngày
- updateStatus() - Cập nhật trạng thái

### 2. **ChiTietHoaDonDAO.java**
- create() - Thêm chi tiết dịch vụ
- getByHoaDonId() - Lấy tất cả dịch vụ trong hoá đơn

### 3. **SuKienDAO.java**
- create() - Tạo sự kiện
- getAll() - Lấy tất cả sự kiện
- getActiveByTime() - Lấy sự kiện hoạt động tại thời điểm nào đó
- findById() - Tìm sự kiện theo ID
- update() - Cập nhật sự kiện
- deactivate() - Vô hiệu hoá sự kiện

### 4. **ThongKeDAO.java**
- getThongKeTheoNgay() - Thống kê theo ngày
- getThongKeTheoTuan() - Thống kê theo tuần
- getThongKeTheoThang() - Thống kê theo tháng

### 5. **LuotChoiDAO.java** (cập nhật)
- findById() - Thêm mới để tìm lượt chơi theo ID

## ✅ Controller Classes (đã tạo)

### 1. **HoaDonController.java**
- Xử lý xem hoá đơn
- Thêm dịch vụ vào hoá đơn
- Thanh toán hoá đơn (áp dụng khuyến mãi)

### 2. **SuKienController.java**
- Hiển thị danh sách sự kiện
- Tạo sự kiện mới
- Cập nhật sự kiện
- Xóa/vô hiệu hoá sự kiện

### 3. **ThongKeController.java**
- Thống kê theo ngày
- Thống kê theo tuần
- Thống kê theo tháng

## ✅ Database Tables (đã tạo SQL)

```sql
-- Hoá đơn
CREATE TABLE hoadon (
    id SERIAL PRIMARY KEY,
    luotchoiid INT REFERENCES luotchoi(id),
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tienchoi NUMERIC(12,2),
    tiendichvu NUMERIC(12,2),
    tienkhuyenmai NUMERIC(12,2),
    tongtien NUMERIC(12,2),
    trangthai VARCHAR(30)
);

-- Chi tiết hoá đơn
CREATE TABLE chitiet_hoadon (
    id SERIAL PRIMARY KEY,
    hoadonid INT REFERENCES hoadon(id),
    dichvuid INT REFERENCES dichvu(id),
    tendichvu VARCHAR(120),
    soluong INT,
    dongia NUMERIC(12,2),
    thanhtien NUMERIC(12,2)
);

-- Sự kiện
CREATE TABLE sukien (
    id SERIAL PRIMARY KEY,
    tensukien VARCHAR(200),
    mota TEXT,
    phantramgiamgia INT,
    loaisukien VARCHAR(30),
    gioapdung VARCHAR(20),
    ngayapdung VARCHAR(20),
    ngayBatDau TIMESTAMP,
    ngayKetThuc TIMESTAMP,
    trangthai BOOLEAN
);
```

## 📋 API Endpoints Mới

| Endpoint | Method | Chức Năng |
|----------|--------|----------|
| `/hoadon?action=view&luotChoiId=1` | GET | Xem hoá đơn |
| `/hoadon` | POST | Thêm dịch vụ/Thanh toán |
| `/sukien?action=list` | GET | Danh sách sự kiện |
| `/sukien?action=detail&id=1` | GET | Chi tiết sự kiện |
| `/sukien` | POST | Tạo/Sửa/Xóa sự kiện |
| `/thongke?loai=ngay&ngay=2026-04-04` | GET | Thống kê ngày |
| `/thongke?loai=tuan&tuan=14&nam=2026` | GET | Thống kê tuần |
| `/thongke?loai=thang&thang=4&nam=2026` | GET | Thống kê tháng |

## 🔧 Cách Sử Dụng

### 1. Mở máy chơi
```
POST /home
Parameters:
- action=open
- mayId=1
- nhanVienId=1
```

### 2. Thêm dịch vụ
```
POST /hoadon
Parameters:
- action=addService
- luotChoiId=1
- dichVuId=1
- soLuong=1
```

### 3. Thanh toán
```
POST /home
Parameters:
- action=close
- mayId=1

OR

POST /hoadon
Parameters:
- action=checkout
- luotChoiId=1
```

### 4. Quản lý sự kiện
```
POST /sukien
Parameters:
- action=create (để tạo mới)
- tenSuKien=Giờ vàng trưa
- moTa=Khuyến mãi 11h-14h
- phanTramGiamGia=20
- loaiSuKien=THEO_GIO
- gioApDung=11:00-14:00
- ngayBatDau=2026-04-01
- ngayKetThuc=2026-12-31
```

### 5. Xem thống kê
```
GET /thongke?loai=ngay&ngay=2026-04-04
GET /thongke?loai=tuan&tuan=14&nam=2026
GET /thongke?loai=thang&thang=4&nam=2026
```

## 📊 Ví Dụ Dữ Liệu

### Sự kiện Giờ vàng
- Tên: Giờ vàng trưa
- Giảm giá: 20%
- Loại: THEO_GIO
- Giờ áp dụng: 11:00-14:00
- Tất cả ngày

### Sự kiện Tết
- Tên: Tết Âm Lịch
- Giảm giá: 30%
- Loại: THEO_NGAY
- Ngày: 2026-02-29

## 🎯 Quy Trình Thanh Toán

1. **Khách vào**: Mở máy (trạng thái → DANG_CHOI)
2. **Khách chơi & gọi dịch vụ**: Thêm chi tiết vào hoá đơn
3. **Khách ra**: Đóng máy (tính tiền, tạo hoá đơn)
4. **Hệ thống áp dụng khuyến mãi**: Tự động giảm giá dựa trên sự kiện
5. **Thanh toán**: Cập nhật trạng thái hoá đơn

## ✨ Tính Năng Đặc Biệt

✅ Áp dụng khuyến mãi tự động khi thanh toán  
✅ Hỗ trợ nhiều loại sự kiện (giờ, ngày, tuần, tháng)  
✅ Thống kê chi tiết theo các khoảng thời gian  
✅ Quản lý hoá đơn và dịch vụ  
✅ Lịch sử lượt chơi cho từng máy  

---

**Build Status**: ✅ SUCCESS  
**Last Compiled**: April 4, 2026, 21:28 UTC+7  
**WAR File**: target/HomePS.war (4.7 MB)

