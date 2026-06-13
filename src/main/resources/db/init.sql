CREATE TABLE IF NOT EXISTS mayps (
    id SERIAL PRIMARY KEY,
    tenmay VARCHAR(100) NOT NULL,
    tinhtrang VARCHAR(30) NOT NULL DEFAULT 'BINH_THUONG',
    ghichu VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS nhanvien (
    id SERIAL PRIMARY KEY,
    tennhanvien VARCHAR(120) NOT NULL,
    sodienthoai VARCHAR(20),
    chucvu VARCHAR(30) NOT NULL,
    trangthai VARCHAR(20) NOT NULL DEFAULT 'DANG_LAM'
);

CREATE TABLE IF NOT EXISTS luotchoi (
    id SERIAL PRIMARY KEY,
    mayid INT NOT NULL REFERENCES mayps(id),
    nhanvienid INT NOT NULL REFERENCES nhanvien(id),
    thoigianbatdau TIMESTAMP NOT NULL,
    thoigianketthuc TIMESTAMP,
    dongiagio NUMERIC(12,2) NOT NULL,
    tongtiengio NUMERIC(12,2) NOT NULL DEFAULT 0,
    trangthai VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS dichvu (
    id SERIAL PRIMARY KEY,
    tendichvu VARCHAR(120) NOT NULL,
    dongia NUMERIC(12,2) NOT NULL CHECK (dongia >= 0),
    loai VARCHAR(20) NOT NULL DEFAULT 'KHAC'
);

INSERT INTO nhanvien (id, tennhanvien, sodienthoai, chucvu, trangthai)
VALUES (1, 'Admin', '0000000000', 'TRONG_MAY', 'DANG_LAM')
ON CONFLICT (id) DO NOTHING;

INSERT INTO nhanvien (id, tennhanvien, sodienthoai, chucvu, trangthai)
VALUES (2, 'Nhan vien 1', '0000000001', 'NHAN_VIEN', 'DANG_LAM')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('nhanvien', 'id'), COALESCE((SELECT MAX(id) FROM nhanvien), 1), true);

INSERT INTO mayps (tenmay, tinhtrang, ghichu)
SELECT 'May ' || i, 'BINH_THUONG', NULL
FROM generate_series(1, 20) AS i
WHERE NOT EXISTS (
    SELECT 1
    FROM mayps
    WHERE tenmay = 'May ' || i
);

INSERT INTO dichvu (tendichvu, dongia, loai)
SELECT seed.tendichvu, seed.dongia, seed.loai
FROM (
         VALUES ('My tom', 15000, 'DO_AN'),
                ('Coca', 12000, 'NUOC'),
                ('Tra da', 5000, 'NUOC')
     ) AS seed(tendichvu, dongia, loai)
WHERE NOT EXISTS (SELECT 1 FROM dichvu);

-- Bảng Hoá đơn
CREATE TABLE IF NOT EXISTS hoadon (
    id SERIAL PRIMARY KEY,
    luotchoiid INT NOT NULL REFERENCES luotchoi(id),
    ngaytao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tienchoi NUMERIC(12,2) NOT NULL DEFAULT 0,
    tiendichvu NUMERIC(12,2) NOT NULL DEFAULT 0,
    tienkhuyenmai NUMERIC(12,2) NOT NULL DEFAULT 0,
    tongtien NUMERIC(12,2) NOT NULL DEFAULT 0,
    trangthai VARCHAR(30) NOT NULL DEFAULT 'CHUA_THANH_TOAN'
);

-- Bảng Chi tiết hoá đơn (dịch vụ)
CREATE TABLE IF NOT EXISTS chitiet_hoadon (
    id SERIAL PRIMARY KEY,
    hoadonid INT NOT NULL REFERENCES hoadon(id),
    dichvuid INT NOT NULL REFERENCES dichvu(id),
    tendichvu VARCHAR(120) NOT NULL,
    soluong INT NOT NULL DEFAULT 1,
    dongia NUMERIC(12,2) NOT NULL,
    thanhtien NUMERIC(12,2) NOT NULL
);

-- Bảng Sự kiện (khuyến mãi)
CREATE TABLE IF NOT EXISTS sukien (
    id SERIAL PRIMARY KEY,
    tensukien VARCHAR(200) NOT NULL,
    mota TEXT,
    phantramgiamgia INT NOT NULL DEFAULT 0 CHECK (phantramgiamgia >= 0 AND phantramgiamgia <= 100),
    loaisukien VARCHAR(30) NOT NULL,
    gioapdung VARCHAR(20),
    ngayapdung VARCHAR(20),
    ngayBatDau TIMESTAMP NOT NULL,
    ngayKetThuc TIMESTAMP NOT NULL,
    trangthai BOOLEAN DEFAULT true
);
