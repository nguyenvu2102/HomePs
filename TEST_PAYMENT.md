# 🎮 Hướng dẫn Test Chức năng Tính Tiền & Đếm Thời gian

## 📋 Những gì đã thêm

### 1. **Đếm thời gian chơi LIVE**
   - Khi máy đang chơi, bên phải panel sẽ hiển thị thời gian đã chơi (update mỗi giây)
   - Format: `Xh Ym` (ví dụ: `0h 5m`)

### 2. **Ước tính tiền LIVE**
   - Dựa trên đơn giá giờ (mặc định 30.000đ/h)
   - Tự động cập nhật mỗi giây khi máy đang chơi
   - Công thức: `tiền = (phút chơi / 60) × đơn giá`

### 3. **Hóa đơn thanh toán**
   - Khi bấm "Đóng máy", hiển thị dialog hóa đơn
   - Hiển thị:
     - Tên máy
     - Thời gian chơi
     - Tổng tiền chơi
     - Dịch vụ khác (mở rộng sau)
     - Khuyến mãi (mở rộng sau)
     - **TỔNG CỘNG** (to, in đậm)

---

## 🧪 Cách test

### Bước 1: Start Tomcat + Database

```powershell
# 1. Start PostgreSQL (nếu chưa chạy)
# Kiểm tra: psql -U postgres -h localhost

# 2. Tạo database nếu chưa có (xem SETUP_LOCAL.md)

# 3. Start Tomcat
cd E:\HUST\HomePS\tomcat\bin
.\startup.bat

# 4. Chờ 10-15 giây cho Tomcat start xong
Start-Sleep -Seconds 15
```

### Bước 2: Test trên Browser

```
http://localhost:8080/HomePS/
```

### Bước 3: Test chức năng

**Test 1: Xem danh sách máy**
1. Mở app → Danh sách máy hiển thị
2. Nhấp chọn một máy → Panel chi tiết hiển thị bên phải
3. Nếu máy trống → Nút "Mở máy" xanh
4. Nếu máy đang chơi → Nút "Đóng máy" đỏ

**Test 2: Mở máy**
1. Chọn một máy TRỐNG
2. Nút "Mở máy" (xanh)
3. Click → Confirm dialog → ✅ Mở máy
4. **Kết quả:**
   - Máy chuyển sang trạng thái "Đang chơi"
   - Panel chi tiết hiển thị:
     - ✅ **Thông tin lượt chơi**
     - ✅ **Bắt đầu lúc** (timestamp)
     - ✅ **Thời gian chơi** (live update mỗi giây)
     - ✅ **Đơn giá** (30.000đ/h)
     - ✅ **Ước tính tiền** (live update)

**Test 3: Theo dõi thời gian chơi LIVE**
1. Sau khi mở máy, quan sát panel chi tiết
2. Thời gian chơi sẽ tự động tăng:
   - `0h 0m` → `0h 1m` → `0h 2m` → ... (update mỗi giây)
3. Tiền cũng tự động tăng theo:
   - `0đ` → `500đ` → `1,000đ` → ... (nếu chơi 1 phút = 500đ)

**Test 4: Đóng máy & thanh toán**
1. Sau khi máy chơi 1-2 phút
2. Click nút "Đóng máy" (đỏ)
3. Confirm dialog → ✅ Đóng máy
4. **Kết quả:**
   - Dialog "📋 Hoá đơn thanh toán" hiển thị
   - Hiển thị:
     ```
     Máy: PS XX
     Thời gian chơi: Xh Ym
     Đơn giá: 30.000đ/h
     Tổng tiền chơi: XXXXđ
     Dịch vụ khác: 0đ
     Khuyến mãi: -0đ
     ────────────────────
     Tổng cộng: XXXXđ
     ```
5. Bấm "Xác nhận thanh toán" → ✅ Thanh toán xong
6. Máy chuyển lại trạng thái "Trống"

**Test 5: Thêm máy mới**
1. Nút "Thêm máy mới" (trên thanh header)
2. Dialog hiển thị
3. Nhập tên máy: `PS 99` + Ghi chú (tùy)
4. Click "Thêm máy"
5. **Kết quả:**
   - ✅ Alert: "Thêm máy thành công"
   - Danh sách machines refresh, máy mới xuất hiện

---

## 💰 Công thức tính tiền

```
Thời gian chơi (phút) = (Lúc bây giờ - Lúc bắt đầu) / 60000 ms
Ước tính tiền = (phút / 60) × 30.000 đ
```

**Ví dụ:**
- Chơi 1 phút → `(1 / 60) × 30.000 = 500đ`
- Chơi 2 phút → `(2 / 60) × 30.000 = 1.000đ`
- Chơi 30 phút → `(30 / 60) × 30.000 = 15.000đ`
- Chơi 1 giờ → `(60 / 60) × 30.000 = 30.000đ`

---

## 📊 Dữ liệu Backend

Khi GET `/api/machines`, mỗi máy sẽ trả về:

```json
{
  "id": 1,
  "tenmay": "PS 01",
  "tinhtrang": "DANG_CHOI",
  "ghichu": null,
  "luotchoiId": 123,
  "thoiGianBatDau": 1715000000000,
  "minutesElapsed": 5,
  "estimatedCost": 2500.00,
  "donGiaGio": 30000.00
}
```

✅ `luotchoiId`, `thoiGianBatDau`, `minutesElapsed`, `estimatedCost` chỉ xuất hiện khi máy đang chơi

---

## 🐛 Troubleshooting

### Lỗi: Thời gian không update
→ Kiểm tra browser console (`F12` → `Console`)
→ Có error không? Gửi error message

### Lỗi: Tiền hiển thị sai
→ Kiểm tra công thức tính ở file `.../ApiMachineController.java` dòng 51

### Lỗi: Dialog thanh toán không hiển thị
→ Kiểm tra `showPaymentModal()` function ở HTML
→ Có error network? (F12 → Network)

### Database error
→ Kiểm tra:
```powershell
psql -U postgres -h localhost -d homeps
SELECT * FROM luotchoi WHERE trangthai = 'DANG_CHOI';
```

---

## 🚀 Mở rộng sau

Có thể thêm:
- ✅ Tính tiền theo từng khoảng giờ (giờ cao điểm vs bình thường)
- ✅ Áp dụng khuyến mãi tự động
- ✅ Tạo hóa đơn lưu vào database
- ✅ Export PDF hoá đơn
- ✅ Thêm dịch vụ vào hóa đơn


