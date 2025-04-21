// Tạo ràng buộc
CREATE CONSTRAINT nhanvien_id IF NOT EXISTS FOR (n:NhanVien) REQUIRE n.idNV IS UNIQUE;
CREATE CONSTRAINT vaitro_id IF NOT EXISTS FOR (n:VaiTro) REQUIRE n.idVT IS UNIQUE;
CREATE CONSTRAINT taikhoan_id IF NOT EXISTS FOR (n:TaiKhoan) REQUIRE n.idTK IS UNIQUE;
CREATE CONSTRAINT khachhang_id IF NOT EXISTS FOR (n:KhachHang) REQUIRE n.idKH IS UNIQUE;
CREATE CONSTRAINT donvitinh_id IF NOT EXISTS FOR (n:DonViTinh) REQUIRE n.idDVT IS UNIQUE;
CREATE CONSTRAINT xuatxu_id IF NOT EXISTS FOR (n:XuatXu) REQUIRE n.idXX IS UNIQUE;
CREATE CONSTRAINT danhmuc_id IF NOT EXISTS FOR (n:DanhMuc) REQUIRE n.idDM IS UNIQUE;
CREATE CONSTRAINT thuoc_id IF NOT EXISTS FOR (n:Thuoc) REQUIRE n.idThuoc IS UNIQUE;
CREATE CONSTRAINT khuyenmai_id IF NOT EXISTS FOR (n:KhuyenMai) REQUIRE n.idKM IS UNIQUE;
CREATE CONSTRAINT hoadon_id IF NOT EXISTS FOR (n:HoaDon) REQUIRE n.idHD IS UNIQUE;
CREATE CONSTRAINT nhacungcap_id IF NOT EXISTS FOR (n:NhaCungCap) REQUIRE n.idNCC IS UNIQUE;
CREATE CONSTRAINT phieunhap_id IF NOT EXISTS FOR (n:PhieuNhap) REQUIRE n.idPN IS UNIQUE;

// Tạo chỉ mục
CREATE INDEX nhanvien_hoten IF NOT EXISTS FOR (n:NhanVien) ON (n.hoTen);
CREATE INDEX khachhang_hoten IF NOT EXISTS FOR (n:KhachHang) ON (n.hoTen);
CREATE INDEX khachhang_sdt IF NOT EXISTS FOR (n:KhachHang) ON (n.sdt);
CREATE INDEX thuoc_ten IF NOT EXISTS FOR (n:Thuoc) ON (n.tenThuoc);
CREATE INDEX nhacungcap_ten IF NOT EXISTS FOR (n:NhaCungCap) ON (n.tenNCC);

// 1. Nhân viên
LOAD CSV WITH HEADERS FROM 'file:///nhanvien.csv' AS row
CREATE (n:NhanVien {
  idNV: row.idNV, 
  hoTen: row.hoTen, 
  sdt: row.sdt, 
  gioiTinh: row.gioiTinh, 
  namSinh: toInteger(row.namSinh), 
  ngayVaoLam: date(row.ngayVaoLam)
});

// 2. Vai trò
LOAD CSV WITH HEADERS FROM 'file:///vaitro.csv' AS row
CREATE (v:VaiTro {
  idVT: row.idVT, 
  ten: row.ten
});

// 3. Tài khoản và mối quan hệ
LOAD CSV WITH HEADERS FROM 'file:///taikhoan.csv' AS row
CREATE (t:TaiKhoan {
  idTK: row.idTK, 
  username: row.username, 
  password: row.password
})
WITH t, row
MATCH (n:NhanVien {idNV: row.idNV})
MATCH (v:VaiTro {idVT: row.idVT})
CREATE (t)-[:THUOC_VE]->(n)
CREATE (t)-[:CO_VAI_TRO]->(v);

// 4. Khách hàng
LOAD CSV WITH HEADERS FROM 'file:///khachhang.csv' AS row
CREATE (k:KhachHang {
  idKH: row.idKH, 
  hoTen: row.hoTen, 
  sdt: row.sdt, 
  gioiTinh: row.gioiTinh, 
  ngayThamGia: date(row.ngayThamGia), 
  hangMuc: row.hangMuc, 
  tongChiTieu: toFloat(row.tongChiTieu)
});

// 5. Đơn vị tính
LOAD CSV WITH HEADERS FROM 'file:///donvitinh.csv' AS row
CREATE (d:DonViTinh {
  idDVT: row.idDVT, 
  ten: row.ten
});

// 6. Xuất xứ
LOAD CSV WITH HEADERS FROM 'file:///xuatxu.csv' AS row
CREATE (x:XuatXu {
  idXX: row.idXX, 
  ten: row.ten
});

// 7. Danh mục
LOAD CSV WITH HEADERS FROM 'file:///danhmuc.csv' AS row
CREATE (d:DanhMuc {
  idDM: row.idDM, 
  ten: row.ten
});

// 8. Thuốc và mối quan hệ
LOAD CSV WITH HEADERS FROM 'file:///thuoc.csv' AS row
CREATE (t:Thuoc {
  idThuoc: row.idThuoc, 
  tenThuoc: row.tenThuoc, 
  hinhAnh: row.hinhAnh, 
  thanhPhan: row.thanhPhan, 
  soLuongTon: toInteger(row.soLuongTon), 
  giaNhap: toFloat(row.giaNhap), 
  donGia: toFloat(row.donGia), 
  hanSuDung: date(row.hanSuDung)
})
WITH t, row
MATCH (d:DonViTinh {idDVT: row.idDVT})
MATCH (dm:DanhMuc {idDM: row.idDM})
MATCH (x:XuatXu {idXX: row.idXX})
CREATE (t)-[:CO_DON_VI_TINH]->(d)
CREATE (t)-[:THUOC_DANH_MUC]->(dm)
CREATE (t)-[:CO_XUAT_XU]->(x);

// 9. Khuyến mãi
LOAD CSV WITH HEADERS FROM 'file:///khuyenmai.csv' AS row
CREATE (k:KhuyenMai {
  idKM: row.idKM, 
  loai: row.loai, 
  mucGiamGia: toFloat(row.mucGiamGia), 
  hangMuc: row.hangMuc, 
  trangThai: row.trangThai
});

// 10. Nhà cung cấp
LOAD CSV WITH HEADERS FROM 'file:///nhacungcap.csv' AS row
CREATE (n:NhaCungCap {
  idNCC: row.idNCC, 
  tenNCC: row.tenNCC, 
  sdt: row.sdt, 
  diaChi: row.diaChi
});

// 11. Hóa đơn và mối quan hệ
LOAD CSV WITH HEADERS FROM 'file:///hoadon.csv' AS row
CREATE (h:HoaDon {
  idHD: row.idHD, 
  tongTien: toFloat(row.tongTien), 
  ngayLap: datetime(row.ngayLap)
})
WITH h, row
MATCH (k:KhachHang {idKH: row.idKH})
MATCH (n:NhanVien {idNV: row.idNV})
CREATE (h)-[:THUOC_VE_KHACH_HANG]->(k)
CREATE (h)-[:DUOC_TAO_BOI]->(n);

// 12. Chi tiết hóa đơn
LOAD CSV WITH HEADERS FROM 'file:///chitiethoadon.csv' AS row
MATCH (h:HoaDon {idHD: row.idHD})
MATCH (t:Thuoc {idThuoc: row.idThuoc})
CREATE (h)-[:CO_CHI_TIET {
  soLuong: toInteger(row.soLuong), 
  donGia: toFloat(row.donGia)
}]->(t);

// 13. Phiếu nhập và mối quan hệ
LOAD CSV WITH HEADERS FROM 'file:///phieunhap.csv' AS row
CREATE (p:PhieuNhap {
  idPN: row.idPN, 
  thoiGian: datetime(row.thoiGian), 
  tongTien: toFloat(row.tongTien)
})
WITH p, row
MATCH (n:NhanVien {idNV: row.idNV})
MATCH (ncc:NhaCungCap {idNCC: row.idNCC})
CREATE (p)-[:DUOC_TAO_BOI]->(n)
CREATE (p)-[:TU_NHA_CUNG_CAP]->(ncc);

// 14. Chi tiết phiếu nhập
LOAD CSV WITH HEADERS FROM 'file:///chitietphieunhap.csv' AS row
MATCH (p:PhieuNhap {idPN: row.idPN})
MATCH (t:Thuoc {idThuoc: row.idThuoc})
CREATE (p)-[:CO_CHI_TIET_NHAP {
  soLuong: toInteger(row.soLuong), 
  donGia: toFloat(row.donGia)
}]->(t);


