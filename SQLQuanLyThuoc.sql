-- Tạo cơ sở dữ liệu QuanLyThuoc
CREATE DATABASE QuanLyThuoc;
GO

-- Sử dụng cơ sở dữ liệu QuanLyThuoc
USE QuanLyThuoc;
GO

-- Tạo bảng KhachHang
CREATE TABLE KhachHang (
    maKhachHang NVARCHAR(50) PRIMARY KEY,
    tenKhachHang NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    gioiTinh NVARCHAR(10)
);

-- Tạo bảng NhanVien
CREATE TABLE NhanVien (
    maNhanVien NVARCHAR(50) PRIMARY KEY,
    tenNhanVien NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    gioiTinh NVARCHAR(10),
    namSinh INT,
    chucVu BIT
);

-- Tạo bảng TaiKhoan
CREATE TABLE TaiKhoan (
    maTaiKhoan NVARCHAR(50) PRIMARY KEY,
    tenDangNhap NVARCHAR(100),
    matKhau NVARCHAR(255),
    maNhanVien NVARCHAR(50),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Tạo bảng Thuoc
CREATE TABLE Thuoc (
    maThuoc NVARCHAR(50) PRIMARY KEY,
    tenThuoc NVARCHAR(100),
    hinhAnh NVARCHAR(MAX),
    xuatXu NVARCHAR(100),
    thanhPhan NVARCHAR(255),
    soLuongTon INT,
    giaNhap FLOAT,
    thue FLOAT,
    donGia FLOAT,
    danhMuc NVARCHAR(100)
);

-- Tạo bảng NhaCungCap
CREATE TABLE NhaCungCap (
    maNCC NVARCHAR(50) PRIMARY KEY,
    tenNhaCungCap NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    email NVARCHAR(100),
    diaChi NVARCHAR(255)
);

-- Tạo bảng HoaDon
CREATE TABLE HoaDon (
    maHoaDon NVARCHAR(50) PRIMARY KEY,
    thoiGian DATETIME,
    trangThai BIT,
    maKhachHang NVARCHAR(50),
    maNhanVien NVARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Tạo bảng ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    maCTHD NVARCHAR(50) PRIMARY KEY,
    maHoaDon NVARCHAR(50),
    maThuoc NVARCHAR(50),
    soLuong INT,
    donGia FLOAT,
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);

-- Tạo bảng PhieuNhapThuoc
CREATE TABLE PhieuNhapThuoc (
    maPhieu NVARCHAR(50) PRIMARY KEY,
    thoiGian DATETIME,
    trangThai BIT,
    maNhanVien NVARCHAR(50),
    maNCC NVARCHAR(50),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maNCC) REFERENCES NhaCungCap(maNCC)
);

-- Tạo bảng ChiTietPhieuNhapThuoc
CREATE TABLE ChiTietPhieuNhapThuoc (
    maCTPhieu NVARCHAR(50) PRIMARY KEY,
    maPhieu NVARCHAR(50),
    maThuoc NVARCHAR(50),
    soLuong INT,
    donGia FLOAT,
    FOREIGN KEY (maPhieu) REFERENCES PhieuNhapThuoc(maPhieu),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO

-- Chèn dữ liệu mẫu vào bảng KhachHang
INSERT INTO KhachHang (maKhachHang, tenKhachHang, soDienThoai, gioiTinh)
VALUES 
('KH001', 'Nguyễn Văn A', '0912345678', 'Nam'),
('KH002', 'Trần Thị B', '0987654321', 'Nữ'),
('KH003', 'Phạm Văn C', '0901122334', 'Nam');

-- Chèn dữ liệu mẫu vào bảng NhanVien
INSERT INTO NhanVien (maNhanVien, tenNhanVien, soDienThoai, gioiTinh, namSinh, chucVu)
VALUES 
('NV001', 'Lê Văn D', '0932123456', 'Nam', 1990, 1),
('NV002', 'Hoàng Thị E', '0977654321', 'Nữ', 1995, 0);

-- Chèn dữ liệu mẫu vào bảng TaiKhoan
INSERT INTO TaiKhoan (maTaiKhoan, tenDangNhap, matKhau, maNhanVien)
VALUES 
('TK001', 'le.d', 'password123', 'NV001'),
('TK002', 'hoang.e', 'securepass', 'NV002');

-- Chèn dữ liệu mẫu vào bảng Thuoc
INSERT INTO Thuoc (maThuoc, tenThuoc, hinhAnh, xuatXu, thanhPhan, soLuongTon, giaNhap, thue, donGia, danhMuc)
VALUES 
('T001', 'Paracetamol', NULL, 'Việt Nam', 'Paracetamol 500mg', 100, 2000, 0.05, 2500, 'Thuốc giảm đau'),
('T002', 'Aspirin', NULL, 'Pháp', 'Acid Acetylsalicylic', 50, 3000, 0.1, 3500, 'Thuốc chống viêm'),
('T003', 'Vitamin C', NULL, 'Mỹ', 'Vitamin C 500mg', 200, 1500, 0.05, 1800, 'Thuốc bổ sung');

-- Chèn dữ liệu mẫu vào bảng NhaCungCap
INSERT INTO NhaCungCap (maNCC, tenNhaCungCap, soDienThoai, email, diaChi)
VALUES 
('NCC001', 'Công ty Dược A', '0912233445', 'duoca@example.com', 'Hà Nội'),
('NCC002', 'Công ty Dược B', '0988765432', 'duocb@example.com', 'TP.HCM');

-- Chèn dữ liệu mẫu vào bảng HoaDon
INSERT INTO HoaDon (maHoaDon, thoiGian, trangThai, maKhachHang, maNhanVien)
VALUES 
('HD001', '2025-01-20 10:30:00', 1, 'KH001', 'NV001'),
('HD002', '2025-01-21 15:00:00', 0, 'KH002', 'NV002');

-- Chèn dữ liệu mẫu vào bảng ChiTietHoaDon
INSERT INTO ChiTietHoaDon (maCTHD, maHoaDon, maThuoc, soLuong, donGia)
VALUES 
('CTHD001', 'HD001', 'T001', 2, 2500),
('CTHD002', 'HD001', 'T003', 1, 1800),
('CTHD003', 'HD002', 'T002', 3, 3500);

-- Chèn dữ liệu mẫu vào bảng PhieuNhapThuoc
INSERT INTO PhieuNhapThuoc (maPhieu, thoiGian, trangThai, maNhanVien, maNCC)
VALUES 
('PN001', '2025-01-15 08:00:00', 1, 'NV001', 'NCC001'),
('PN002', '2025-01-18 09:00:00', 1, 'NV002', 'NCC002');

-- Chèn dữ liệu mẫu vào bảng ChiTietPhieuNhapThuoc
INSERT INTO ChiTietPhieuNhapThuoc (maCTPhieu, maPhieu, maThuoc, soLuong, donGia)
VALUES 
('CTPN001', 'PN001', 'T001', 50, 2000),
('CTPN002', 'PN001', 'T003', 100, 1500),
('CTPN003', 'PN002', 'T002', 30, 3000);
