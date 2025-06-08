package poly.NhaThuocTamAn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.KhachHangDTO;

import poly.NhaThuocTamAn.service.KhachHangService;

import java.util.List;

@RestController
@RequestMapping("/api/khachhang")
@CrossOrigin("*")
public class KhachHangController {

    @Autowired
    private KhachHangService service;

    @GetMapping
    public List<KhachHangDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/search")
    public List<KhachHangDTO> search(
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String sdt,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String diaChi) {
        return service.search(hoTen, sdt, email, diaChi);
    }

    @GetMapping("/{id}")
    public KhachHangDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PutMapping("/trang-thai/{id}")
    public KhachHangDTO updateStatus(@PathVariable Integer id, @RequestParam boolean status) {
        return service.updateStatus(id, status);
    }
}

