package poly.NhaThuocTamAn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "KhachHang")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKH;

    @Column(nullable = false, unique = true, length = 50)
    private String tenTaiKhoan;

    @Column(nullable = false, length = 100)
    private String matKhau;

    @Column(nullable = false)
    private String hoTen;

    private String diaChi;

    private String email;

    @Column(length = 15)
    private String soDienThoai;

    private String hinhAnh;

    private Boolean trangThai = true;

  
}

