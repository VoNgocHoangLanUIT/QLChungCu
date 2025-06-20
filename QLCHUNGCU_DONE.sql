﻿CREATE TABLE CanHo (
    ma_can_ho NVARCHAR2(10) PRIMARY KEY,
    dien_tich NUMBER(10,2),
    trang_thai NVARCHAR2(50),
    chi_so_dien INT,
    chi_so_nuoc INT,
    da_xoa NUMBER(1,0) DEFAULT 0
);

CREATE TABLE CuDan (
    ma_cu_dan NVARCHAR2(10) PRIMARY KEY,
    ma_can_ho NVARCHAR2(10),
    ho_ten NVARCHAR2(100),
    so_dien_thoai NVARCHAR2(15),
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE CUDAN
ADD CONSTRAINT FK_CUDAN FOREIGN KEY (ma_can_ho) REFERENCES CanHo(ma_can_ho);

CREATE TABLE ODoXe (
    so_o_do NVARCHAR2(10),
    loai_o_do NVARCHAR2(50),
    loai_xe NVARCHAR2(50),
    trang_thai NVARCHAR2(50),
    bien_so_xe NVARCHAR2(50),
    da_xoa NUMBER(1,0) DEFAULT 0
);

CREATE TABLE NhanVien (
    ma_nhan_vien NVARCHAR2(10) PRIMARY KEY,
    ho_ten NVARCHAR2(100),
    ngay_sinh DATE,
    so_dien_thoai NVARCHAR2(15),
    gioi_tinh NVARCHAR2(30),
    dia_chi NVARCHAR2(200),
    phong_ban NVARCHAR2(30),
    luong NUMBER(15,2),
    da_xoa NUMBER(1,0) DEFAULT 0
);

CREATE TABLE PhanAnh (
    ma_phan_anh NVARCHAR2(10) PRIMARY KEY,
    tieu_de NVARCHAR2(50),
    ma_can_ho NVARCHAR2(10),
    ngay_phan_anh DATE DEFAULT SYSDATE,
    trang_thai NVARCHAR2(50),
    mo_ta NVARCHAR2(255),
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE PHANANH
ADD CONSTRAINT fk_PA_CH FOREIGN KEY (ma_can_ho) REFERENCES CanHo(ma_can_ho);

CREATE TABLE GiaoViec (
    ma_phan_anh NVARCHAR2(10),
    ma_nhan_vien NVARCHAR2(10),
    ngay_giao DATE DEFAULT SYSDATE,
    ngay_hoan_thanh DATE,
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE GIAOVIEC
ADD CONSTRAINT fk_GV_PA FOREIGN KEY (ma_phan_anh) REFERENCES PhanAnh(ma_phan_anh);
ALTER TABLE GIAOVIEC
ADD CONSTRAINT fk_GV_NV FOREIGN KEY (ma_nhan_vien) REFERENCES NhanVien(ma_nhan_vien);

CREATE TABLE DichVuNgoai (
    ma_dv_ngoai  NVARCHAR2(10) PRIMARY KEY,
    ten_dv_ngoai NVARCHAR2(100),
    ma_nha_cung_cap NVARCHAR2(100),
    don_vi NVARCHAR2(30),
    so_luong INT,
    gia NUMBER(18,2),
    mo_ta NVARCHAR2(255),
    da_xoa NUMBER(1,0) DEFAULT 0
);

CREATE TABLE HoaDon (
    ma_hoa_don NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ngay_lap DATE DEFAULT SYSDATE,
    tong_phi NUMBER(18,2),
    ma_cu_dan NVARCHAR2(10),
    tien_nhan NUMBER(18,2),
    trang_thai NVARCHAR2(30),
    tien_thua NUMBER(18,2),
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon FOREIGN KEY (ma_cu_dan) REFERENCES CuDan(ma_cu_dan);

CREATE TABLE CHITIET_DICHVU_HD  (
    ma_dv_ngoai  NVARCHAR2(10),
    ma_hoa_don NUMBER,
    SoLuong             NUMBER NOT NULL,
    DonGiaTaiThoiDiemDat NUMBER(10, 2) NOT NULL,
    ThanhTien           NUMBER(12, 2) NOT NULL,
    NGAY_DANG_KY DATE DEFAULT SYSDATE,
    khung_gio_dang_ky    NVARCHAR2(20) NOT NULL,
    da_xoa NUMBER(1,0) DEFAULT 0,
    CONSTRAINT pk_chitiethd PRIMARY KEY (ma_hoa_don, ma_dv_ngoai, khung_gio_dang_ky)
);
ALTER TABLE CHITIET_DICHVU_HD
ADD CONSTRAINT fk_CTHD_HD FOREIGN KEY (ma_hoa_don) REFERENCES HoaDon(ma_hoa_don);
ALTER TABLE CHITIET_DICHVU_HD
ADD CONSTRAINT fk_CTHD_DV FOREIGN KEY (ma_dv_ngoai) REFERENCES DichVuNgoai(ma_dv_ngoai);

CREATE TABLE ACCOUNT (
    ACCOUNT_ID    NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USERNAME      VARCHAR2(50) UNIQUE NOT NULL,
    PASSWORD_HASH VARCHAR2(200) NOT NULL,
    EMAIL        VARCHAR2(150) UNIQUE NOT NULL,
    ma_cu_dan NVARCHAR2(10),
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE ACCOUNT
ADD CONSTRAINT FK_ACCOUNT FOREIGN KEY (ma_cu_dan) REFERENCES CuDan(ma_cu_dan);


-- 5. Tạo bảng ROLE_GROUP
CREATE TABLE ROLE_GROUP (
    ROLE_GROUP_ID NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME_ROLE_GROUP VARCHAR2(100) NOT NULL,
    da_xoa NUMBER(1,0) DEFAULT 0
);
INSERT INTO ROLE_GROUP(NAME_ROLE_GROUP) VALUES ('admin');
INSERT INTO ROLE_GROUP(NAME_ROLE_GROUP) VALUES ('resident');

-- 7. Tạo bảng ACCOUNT_ASSIGN_ROLE_GROUP
CREATE TABLE ACCOUNT_ASSIGN_ROLE_GROUP (
    ACCOUNT_ID    NUMBER(10) NOT NULL,
    ROLE_GROUP_ID NUMBER(10) NOT NULL,
    da_xoa NUMBER(1,0) DEFAULT 0,
    CONSTRAINT PK_ACCOUNT_ASSIGN_ROLE_GROUP PRIMARY KEY (ACCOUNT_ID, ROLE_GROUP_ID),
    CONSTRAINT FK_ACCOUNT1 FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID),
    CONSTRAINT FK_ROLE_GROUP_ASSIGN FOREIGN KEY (ROLE_GROUP_ID) REFERENCES ROLE_GROUP(ROLE_GROUP_ID)
);



CREATE OR REPLACE PROCEDURE ADD_PARKINGSLOT (
    p_so_o_do IN NVARCHAR2,
    p_loai_o_do IN NVARCHAR2,
    p_loai_xe IN NVARCHAR2,
    p_trang_thai IN NVARCHAR2,
    p_bien_so_xe IN NVARCHAR2
)
AS
BEGIN
    INSERT INTO ODoXe (
        so_o_do, 
        loai_o_do, 
        loai_xe, 
        trang_thai, 
        bien_so_xe
    )
    VALUES (
        p_so_o_do,
        p_loai_o_do,
        p_loai_xe,
        p_trang_thai,
        p_bien_so_xe  
    );
END;
/

--Sua o do xe
CREATE OR REPLACE PROCEDURE UPDATE_PARKINGSLOT (
    p_so_o_do IN NVARCHAR2,      -- khóa để tìm ô đỗ cần update
    p_loai_o_do IN NVARCHAR2,    -- dữ liệu mới
    p_loai_xe IN NVARCHAR2,
    p_trang_thai IN NVARCHAR2,
    p_bien_so_xe IN NVARCHAR2
)
AS
BEGIN
    UPDATE ODoXe
    SET 
        loai_o_do = p_loai_o_do,
        loai_xe = p_loai_xe,
        trang_thai = p_trang_thai,
        bien_so_xe = p_bien_so_xe
    WHERE 
        so_o_do = p_so_o_do; -- chỉ update ô đỗ nào có đúng số ô đỗ
END;
/


CREATE TABLE DichVuBatBuoc (
    ma_dv_bat_buoc NVARCHAR2(10) PRIMARY KEY,
    ten_dv_bat_buoc NVARCHAR2(100),
    ma_nha_cung_cap NVARCHAR2(100),
    don_vi NVARCHAR2(30),
    gia NUMBER(18,2),
    da_xoa NUMBER(1,0) DEFAULT 0
);

CREATE TABLE ThongBao (
    ma_thong_bao INT PRIMARY KEY,
    ma_cu_dan INT,
    tieu_de NVARCHAR2(30),
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    trang_thai NVARCHAR2(50),
    noi_dung NVARCHAR2(500),
    da_xoa NUMBER(1,0) DEFAULT 0
);
ALTER TABLE THONGBAO
ADD CONSTRAINT fk_TB_CD FOREIGN KEY (ma_cu_dan) REFERENCES CuDan(ma_cu_dan);
