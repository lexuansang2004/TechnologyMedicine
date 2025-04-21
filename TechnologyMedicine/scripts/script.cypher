// Tạo unique constraint cho các thuộc tính trong cơ sở dữ liệu Neo4j
CREATE CONSTRAINT unique_maNhanVien IF NOT EXISTS
FOR (nv:NhanVien)
REQUIRE nv.maNhanVien IS UNIQUE;

CREATE CONSTRAINT unique_maTaiKhoan IF NOT EXISTS
FOR (tk:TaiKhoan)
REQUIRE tk.maTaiKhoan IS UNIQUE;

CREATE CONSTRAINT unique_maKhachHang IF NOT EXISTS
FOR (kh:KhachHang)
REQUIRE kh.maKhachHang IS UNIQUE;

CREATE CONSTRAINT unique_maNhaCungCap IF NOT EXISTS
FOR (ncc:NhaCungCap)
REQUIRE ncc.maNhaCungCap IS UNIQUE;

CREATE CONSTRAINT unique_maThuoc IF NOT EXISTS
FOR (t:Thuoc)
REQUIRE t.maThuoc IS UNIQUE;

CREATE CONSTRAINT unique_maPhieuNhap IF NOT EXISTS
FOR (pn:PhieuNhap)
REQUIRE pn.maPhieuNhap IS UNIQUE;

CREATE CONSTRAINT unique_maHoaDon IF NOT EXISTS
FOR (hd:HoaDon)
REQUIRE hd.maHoaDon IS UNIQUE;


//Load dữ liệu từ các CSV file cho trước vào database.
//maNhanVien,tenNhanVien,soDienThoai,gioiTinh,namSinh,chucVu
LOAD CSV WITH HEADERS FROM 'file:///data/nhanVien.csv' AS row
WITH row
CREATE (nv:NhanVien {
maNhanVien: row.maNhanVien,
tenNhanVien: row.tenNhanVien,
soDienThoai: row.soDienThoai,
gioiTinh: row.gioiTinh,
namSinh: row.namSinh,
chucVu: row.chucVu
});

//maKhachHang,tenKhachHang,soDienThoai,gioiTinh
LOAD CSV WITH HEADERS FROM 'file:///data/khachHang.csv' AS row
WITH row
CREATE (kh:KhachHang {
    maKhachHang: row.maKhachHang,
    tenKhachHang: row.tenKhachHang,
    soDienThoai: row.soDienThoai,
    gioiTinh: row.gioiTinh
});

//maTaiKhoan,tenDangNhap,matKhau,maNhanVien
LOAD CSV WITH HEADERS FROM 'file:///data/taiKhoan.csv' AS row
WITH row
CREATE (tk:TaiKhoan {
    maTaiKhoan: row.maTaiKhoan,
    tenDangNhap: row.tenDangNhap,
    matKhau: row.matKhau,
    maNhanVien: row.maNhanVien
})

//maNhaCungCap,tenNhaCungCap,soDienThoai,diaChi,email
LOAD CSV WITH HEADERS FROM 'file:///data/nhaCungCap.csv' AS row
WITH row
CREATE (ncc:NhaCungCap {
    maNhaCungCap: row.maNhaCungCap,
    tenNhaCungCap: row.tenNhaCungCap,
    soDienThoai: row.soDienThoai,
    diaChi: row.diaChi,
    email: row.email
});

//maThuoc,tenThuoc,hinhAnh,xuatXu,thanhPhan,soLuongTon,giaNhap,donGia,tyLeThue,danhMuc,donViTinh
LOAD CSV WITH HEADERS FROM 'file:///data/thuoc.csv' AS row
WITH row
CREATE (t:Thuoc {
    maThuoc: row.maThuoc,
    tenThuoc: row.tenThuoc,
    hinhAnh: row.hinhAnh,
    xuatXu: row.xuatXu,
    thanhPhan: row.thanhPhan,
    soLuongTon: row.soLuongTon,
    giaNhap: row.giaNhap,
    donGia: row.donGia,
    tyLeThue: row.tyLeThue,
    danhMuc: row.danhMuc,
    donViTinh: row.donViTinh
});

//maPhieuNhap,thoGian,trangThai,maNhanVien,maNhaCungCap
LOAD CSV WITH HEADERS FROM 'file:///data/phieuNhapThuoc.csv' AS row
WITH row
CREATE (pn:PhieuNhap {
    maPhieuNhap: row.maPhieuNhap,
    thoGian: row.thoGian,
    trangThai: row.trangThai,
    maNhanVien: row.maNhanVien,
    maNhaCungCap: row.maNhaCungCap
});

//maHoaDon,thoiGian,trangThai,maKhachHang,maNhanVien
LOAD CSV WITH HEADERS FROM 'file:///data/hoaDonBanThuoc.csv' AS row
WITH row
CREATE (hd:HoaDon {
    maHoaDon: row.maHoaDon,
    thoiGian: row.thoiGian,
    trangThai: row.trangThai,
    maKhachHang: row.maKhachHang,
    maNhanVien: row.maNhanVien
});

// Tạo quan hệ giữa các node
//maHoaDon,maThuoc,donGia,soLuong
LOAD CSV WITH HEADERS FROM 'file:///data/chiTietHoaDon.csv' AS row
WITH row
WHERE row.maHoaDon IS NOT NULL AND row.maThuoc IS NOT NULL
MATCH (hd:HoaDon {maHoaDon: row.maHoaDon})
MATCH (t:Thuoc {maThuoc: row.maThuoc})
MERGE (hd)-[r:CHI_TIET_HOA_DON]->(t)
SET r.donGia = toInteger(row.donGia),
r.soLuong = toInteger(row.soLuong);


//maPhieuNhap,maThuoc,soLuong,donGia
LOAD CSV WITH HEADERS FROM 'file:///data/chiTietPhieuNhapThuoc.csv' AS row
WITH row
WHERE row.maPhieuNhap IS NOT NULL AND row.maThuoc IS NOT NULL
MATCH (pn:PhieuNhap {maPhieuNhap: row.maPhieuNhap})
MATCH (t:Thuoc {maThuoc: row.maThuoc})
MERGE (pn)-[r:CHI_TIET_PHIEU_NHAP]->(t)
SET r.soLuong = toInteger(row.soLuong),
r.donGia = toInteger(row.donGia);


// Các chức năng cho nhân viên
// GetAllNhanVien
MATCH (nv:NhanVien)
RETURN nv;
// GetNhanVienById
MATCH (nv:NhanVien {maNhanVien: "NV016"})
RETURN nv;
// Dang nhap tai khoan
MATCH (tk:TaiKhoan {tenDangNhap: "user6", matKhau: "12345678"})
RETURN tk.maNhanVien;
// Thêm nhân viên
CREATE (nv:NhanVien {
    maNhanVien: "NV001",
    tenNhanVien: "Nguyen Van A",
    soDienThoai: "0123456789",
    gioiTinh: true,
    namSinh: date("1990-01-01"),
    chucVu: true
});
