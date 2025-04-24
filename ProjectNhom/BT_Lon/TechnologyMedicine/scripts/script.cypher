// Tạo ràng buộc duy nhất
CREATE CONSTRAINT IF NOT EXISTS FOR (t:Thuoc) REQUIRE t.idThuoc IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (dvt:DonViTinh) REQUIRE dvt.idDVT IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (dm:DanhMuc) REQUIRE dm.idDM IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (xx:XuatXu) REQUIRE xx.idXX IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (kh:KhachHang) REQUIRE kh.idKH IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (otp:OTP) REQUIRE otp.idOTP IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (ncc:NhaCungCap) REQUIRE ncc.idNCC IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (km:KhuyenMai) REQUIRE km.idKM IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (nv:NhanVien) REQUIRE nv.idNV IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (vt:VaiTro) REQUIRE vt.idVT IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (tk:TaiKhoan) REQUIRE tk.idTK IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (tk:TaiKhoan) REQUIRE tk.username IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (hd:HoaDon) REQUIRE hd.idHD IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (pn:PhieuNhap) REQUIRE pn.idPN IS UNIQUE;

// Tạo chỉ mục để tối ưu hiệu suất truy vấn
CREATE INDEX IF NOT EXISTS FOR (nv:NhanVien) ON (nv.hoTen);
CREATE INDEX IF NOT EXISTS FOR (kh:KhachHang) ON (kh.hoTen);
CREATE INDEX IF NOT EXISTS FOR (t:Thuoc) ON (t.tenThuoc);
CREATE INDEX IF NOT EXISTS FOR (ncc:NhaCungCap) ON (ncc.tenNCC);
CREATE INDEX IF NOT EXISTS FOR (kh:KhachHang) ON (kh.hangMuc);
CREATE INDEX IF NOT EXISTS FOR (km:KhuyenMai) ON (km.hangMuc);

// Import Đơn Vị Tính
LOAD CSV WITH HEADERS FROM 'file:///donvitinh.csv' AS row
CREATE (dvt:DonViTinh {
  idDVT: row.idDVT,
  ten: row.ten
});

// Import Danh Mục
LOAD CSV WITH HEADERS FROM 'file:///danhmuc.csv' AS row
CREATE (dm:DanhMuc {
  idDM: row.idDM,
  ten: row.ten
});

// Import Xuất Xứ
LOAD CSV WITH HEADERS FROM 'file:///xuatxu.csv' AS row
CREATE (xx:XuatXu {
  idXX: row.idXX,
  ten: row.ten
});

// Import Thuốc
LOAD CSV WITH HEADERS FROM 'file:///thuoc.csv' AS row
CREATE (t:Thuoc {
  idThuoc: row.idThuoc,
  tenThuoc: row.tenThuoc,
  hinhAnh: row.hinhAnh,
  thanhPhan: row.thanhPhan,
  idDVT: row.idDVT,
  idDM: row.idDM,
  idXX: row.idXX,
  soLuongTon: toInteger(row.soLuongTon),
  giaNhap: toFloat(row.giaNhap),
  donGia: toFloat(row.donGia),
  hanSuDung: date(row.hanSuDung)
});

// Import Khách Hàng
LOAD CSV WITH HEADERS FROM 'file:///khachhang.csv' AS row
CREATE (kh:KhachHang {
  idKH: row.idKH,
  hoTen: row.hoTen,
  sdt: row.sdt,
  email: row.email,
  gioiTinh: row.gioiTinh,
  ngayThamGia: date(row.ngayThamGia),
  hangMuc: row.hangMuc,
  tongChiTieu: toFloat(row.tongChiTieu)
});

// Import Nhà Cung Cấp
LOAD CSV WITH HEADERS FROM 'file:///nhacungcap.csv' AS row
CREATE (ncc:NhaCungCap {
  idNCC: row.idNCC,
  tenNCC: row.tenNCC,
  sdt: row.sdt,
  diaChi: row.diaChi
});

// Import Khuyến Mãi
LOAD CSV WITH HEADERS FROM 'file:///khuyenmai.csv' AS row
CREATE (km:KhuyenMai {
  idKM: row.idKM,
  loai: row.loai,
  mucGiamGia: toFloat(row.mucGiamGia),
  hangMuc: CASE WHEN row.hangMuc = '' THEN null ELSE row.hangMuc END,
  idThuoc: CASE WHEN row.idThuoc = '' THEN null ELSE row.idThuoc END,
  trangThai: row.trangThai
});

// Import Nhân Viên
LOAD CSV WITH HEADERS FROM 'file:///nhanvien.csv' AS row
CREATE (nv:NhanVien {
  idNV: row.idNV,
  hoTen: row.hoTen,
  sdt: row.sdt,
  gioiTinh: row.gioiTinh,
  namSinh: toInteger(row.namSinh),
  ngayVaoLam: date(row.ngayVaoLam),
  email: row.email
});

// Import Vai Trò
LOAD CSV WITH HEADERS FROM 'file:///vaitro.csv' AS row
CREATE (vt:VaiTro {
  idVT: row.idVT,
  ten: row.ten
});

// Import Tài Khoản
LOAD CSV WITH HEADERS FROM 'file:///taikhoan.csv' AS row
CREATE (tk:TaiKhoan {
  idTK: row.idTK,
  username: row.username,
  password: row.password,
  idNV: row.idNV,
  idVT: row.idVT
});

// Import Hóa Đơn
LOAD CSV WITH HEADERS FROM 'file:///hoadon.csv' AS row
CREATE (hd:HoaDon {
  idHD: row.idHD,
  idKH: row.idKH,
  idNV: row.idNV,
  tongTien: toFloat(row.tongTien),
  ngayLap: datetime(row.ngayLap)
});

// Import Chi Tiết Hóa Đơn
LOAD CSV WITH HEADERS FROM 'file:///chitiethoadon.csv' AS row
MATCH (hd:HoaDon {idHD: row.idHD})
MATCH (t:Thuoc {idThuoc: row.idThuoc})
CREATE (hd)-[:CO_CHI_TIET {
  soLuong: toInteger(row.soLuong),
  donGia: toFloat(row.donGia)
}]->(t);

// Import Phiếu Nhập
LOAD CSV WITH HEADERS FROM 'file:///phieunhap.csv' AS row
CREATE (pn:PhieuNhap {
  idPN: row.idPN,
  thoiGian: datetime(row.thoiGian),
  idNV: row.idNV,
  idNCC: row.idNCC,
  tongTien: toFloat(row.tongTien)
});

// Import Chi Tiết Phiếu Nhập
LOAD CSV WITH HEADERS FROM 'file:///chitietphieunhap.csv' AS row
MATCH (pn:PhieuNhap {idPN: row.idPN})
MATCH (t:Thuoc {idThuoc: row.idThuoc})
CREATE (pn)-[:CO_CHI_TIET {
  soLuong: toInteger(row.soLuong),
  donGia: toFloat(row.donGia)
}]->(t);

// Tạo mối quan hệ giữa Thuốc và Đơn Vị Tính, Danh Mục, Xuất Xứ
MATCH (t:Thuoc), (dvt:DonViTinh) WHERE t.idDVT = dvt.idDVT
CREATE (t)-[:CO_DON_VI_TINH]->(dvt);

MATCH (t:Thuoc), (dm:DanhMuc) WHERE t.idDM = dm.idDM
CREATE (t)-[:THUOC_DANH_MUC]->(dm);

MATCH (t:Thuoc), (xx:XuatXu) WHERE t.idXX = xx.idXX
CREATE (t)-[:CO_XUAT_XU]->(xx);

// Tạo mối quan hệ giữa Khuyến Mãi và Khách Hàng, Thuốc
MATCH (km:KhuyenMai), (kh:KhachHang)
WHERE km.hangMuc IS NOT NULL AND kh.hangMuc = km.hangMuc AND km.trangThai = 'Đang áp dụng'
CREATE (km)-[:AP_DUNG_CHO]->(kh);

MATCH (km:KhuyenMai), (t:Thuoc)
WHERE km.idThuoc IS NOT NULL AND t.idThuoc = km.idThuoc AND km.trangThai = 'Đang áp dụng'
CREATE (km)-[:AP_DUNG_CHO_THUOC]->(t);

// Tạo mối quan hệ giữa Tài Khoản và Nhân Viên, Vai Trò
MATCH (tk:TaiKhoan), (nv:NhanVien) WHERE tk.idNV = nv.idNV
CREATE (tk)-[:THUOC_VE]->(nv);

MATCH (tk:TaiKhoan), (vt:VaiTro) WHERE tk.idVT = vt.idVT
CREATE (tk)-[:CO_VAI_TRO]->(vt);

// Tạo mối quan hệ giữa Hóa Đơn và Khách Hàng, Nhân Viên
MATCH (hd:HoaDon), (kh:KhachHang) WHERE hd.idKH = kh.idKH
CREATE (hd)-[:CUA_KHACH_HANG]->(kh);

MATCH (hd:HoaDon), (nv:NhanVien) WHERE hd.idNV = nv.idNV
CREATE (hd)-[:DO_NHAN_VIEN]->(nv);

// Tạo mối quan hệ giữa Phiếu Nhập và Nhà Cung Cấp, Nhân Viên
MATCH (pn:PhieuNhap), (ncc:NhaCungCap) WHERE pn.idNCC = ncc.idNCC
CREATE (pn)-[:TU_NHA_CUNG_CAP]->(ncc);

MATCH (pn:PhieuNhap), (nv:NhanVien) WHERE pn.idNV = nv.idNV
CREATE (pn)-[:DO_NHAN_VIEN]->(nv);