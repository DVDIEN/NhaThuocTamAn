package com.example.demo.dto;


public class KhachHangDTO {

    private Integer maKH;
    private String tenTaiKhoan;
    private String hoTen;
    private String diaChi;
    private String email;
    private String soDienThoai;
    private String hinhAnh;
    private Boolean trangThai;

    // Constructors
    public KhachHangDTO() {}

    public KhachHangDTO(Integer maKH, String tenTaiKhoan, String hoTen, String diaChi,
                        String email, String soDienThoai, String hinhAnh, Boolean trangThai) {
        this.maKH = maKH;
        this.tenTaiKhoan = tenTaiKhoan;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
    }

    // Getters & Setters
    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }
}

