package poly.NhaThuocTamAn.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.KhachHangDTO;

import poly.NhaThuocTamAn.model.KhachHang;
import poly.NhaThuocTamAn.repository.KhachHangRepository;
import poly.NhaThuocTamAn.service.KhachHangService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    private KhachHangRepository repo;

    private KhachHangDTO toDTO(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKH(kh.getMaKH());
        dto.setTenTaiKhoan(kh.getTenTaiKhoan());
        dto.setHoTen(kh.getHoTen());
        dto.setEmail(kh.getEmail());
        dto.setSoDienThoai(kh.getSoDienThoai());
        dto.setDiaChi(kh.getDiaChi());
        dto.setHinhAnh(kh.getHinhAnh());
        dto.setTrangThai(kh.getTrangThai());
        return dto;
    }

    @Override
    public List<KhachHangDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<KhachHangDTO> search(String hoTen, String sdt, String email, String diaChi) {
        List<KhachHang> results = repo.searchAdvanced(
                hoTen == null || hoTen.isBlank() ? null : hoTen,
                sdt == null || sdt.isBlank() ? null : sdt,
                email == null || email.isBlank() ? null : email,
                diaChi == null || diaChi.isBlank() ? null : diaChi
        );
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public KhachHangDTO getById(Integer id) {
        return repo.findById(id).map(this::toDTO).orElseThrow();
    }

    @Override
    public KhachHangDTO updateStatus(Integer id, boolean status) {
        KhachHang kh = repo.findById(id).orElseThrow();
        kh.setTrangThai(status);
        return toDTO(repo.save(kh));
    }
}

