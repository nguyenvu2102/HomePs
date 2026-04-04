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

INSERT INTO mayps (tenmay, tinhtrang, ghichu)
SELECT 'May ' || i, 'BINH_THUONG', NULL
FROM generate_series(1, 12) AS i
WHERE NOT EXISTS (SELECT 1 FROM mayps);

INSERT INTO dichvu (tendichvu, dongia, loai)
SELECT seed.tendichvu, seed.dongia, seed.loai
FROM (
         VALUES ('My tom', 15000, 'DO_AN'),
                ('Coca', 12000, 'NUOC'),
                ('Tra da', 5000, 'NUOC')
     ) AS seed(tendichvu, dongia, loai)
WHERE NOT EXISTS (SELECT 1 FROM dichvu);
