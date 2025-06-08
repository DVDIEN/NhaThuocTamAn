package poly.NhaThuocTamAn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DangKyDTO;
import com.example.demo.dto.DangNhapDTO;
import com.example.demo.dto.MaXacThucDTO;

import poly.NhaThuocTamAn.model.KhachHang;
import poly.NhaThuocTamAn.repository.KhachHangRepository;
import poly.NhaThuocTamAn.service.EmailService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKy(@RequestBody DangKyDTO dto) {
        if (khachHangRepo.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã được đăng ký tài khoản"));
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        KhachHang kh = KhachHang.builder()
            .hoTen(dto.getHoTen())
            .email(dto.getEmail())
            .matKhau(dto.getMatKhau())
            .maXacThuc(code)
            .thoiGianHetHanMa(LocalDateTime.now().plusMinutes(10))
            .trangThai(true)
            .daXacThuc(false)
            .build();

        khachHangRepo.save(kh);

        emailService.sendCode(dto.getEmail(), "Mã xác thực đăng ký", "Mã xác thực của bạn là: " + code);
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công! Mã xác thực đã gửi email."));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody DangNhapDTO dto) {
        Optional<KhachHang> opt = khachHangRepo.findByEmailAndMatKhau(dto.getEmail(), dto.getMatKhau());

        if (opt.isEmpty())
            return ResponseEntity.status(401).body(Map.of("message", "Email hoặc mật khẩu không đúng!"));

        KhachHang kh = opt.get();
        if (!kh.getTrangThai())
            return ResponseEntity.status(403)
                .body(Map.of("message", "❌ Tài khoản của bạn đang bị khóa!"));

        if (!kh.getDaXacThuc())
            return ResponseEntity.status(403)
            		.body(Map.of("message", "❌ Tài khoản chưa được xác thực!", "email", kh.getEmail()));


        return ResponseEntity.ok(kh);
    }

    @PostMapping("/xac-thuc-email")
    public ResponseEntity<?> xacThucEmail(@RequestBody MaXacThucDTO dto) {
        Optional<KhachHang> opt = khachHangRepo.findByEmail(dto.getEmail());
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "Email không tồn tại"));

        KhachHang kh = opt.get();
        if (!kh.getMaXacThuc().equals(dto.getCode()))
            return ResponseEntity.badRequest().body(Map.of("message", "Mã xác thực không đúng"));

        if (kh.getThoiGianHetHanMa().isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body(Map.of("message", "Mã xác thực đã hết hạn"));

        kh.setDaXacThuc(true);
        kh.setMaXacThuc(null);
        kh.setThoiGianHetHanMa(null);
        khachHangRepo.save(kh);

        return ResponseEntity.ok(Map.of("message", "Xác thực thành công!"));
    }
}
