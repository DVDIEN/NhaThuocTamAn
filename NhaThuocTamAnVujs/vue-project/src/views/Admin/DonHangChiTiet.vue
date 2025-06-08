<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">
      🧾 Chi tiết đơn hàng <span class="text-dark">#{{ donHang.maDonHang }}</span>
    </h4>

    <!-- Thông tin đơn -->
    <div class="card mb-4 shadow-sm border-0">
      <div class="card-header bg-light fw-bold text-secondary">📌 Thông tin đơn hàng</div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <p><strong>👤 Khách hàng:</strong> {{ getTenKhachHang(donHang.maKH) }}</p>
            <p><strong>📅 Ngày đặt:</strong> {{ donHang.ngayDat }}</p>
            <p><strong>🏠 Địa chỉ nhận:</strong> {{ donHang.diaChiNhan }}</p>
            <p><strong>📞 SĐT nhận:</strong> {{ donHang.soDienThoaiNhan }}</p>
            <p><strong>📝 Ghi chú:</strong> {{ donHang.ghiChu || 'Không có' }}</p>
          </div>
          <div class="col-md-6">
            <p>
              <strong>🚚 Trạng thái:</strong>
              <span class="badge" :class="trangThaiClass(donHang.trangThai)">
                {{ trangThaiLabel(donHang.trangThai) }}
              </span>
            </p>
            <p>
              <strong>💳 Thanh toán:</strong>
              <span :class="donHang.daThanhToan ? 'text-success' : 'text-danger'">
                {{ donHang.daThanhToan ? '✔ Đã thanh toán' : '✘ Chưa thanh toán' }}
              </span>
            </p>
            <p><strong>🔁 Hình thức:</strong> {{ donHang.hinhThuc }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 💡 Lịch sử đơn hàng -->
    <LichSuDonHang :maDonHang="donHang.maDonHang" />

    <!-- Bảng sản phẩm -->
    <div class="card shadow-sm border-0 mt-4">
      <div class="card-header bg-success text-white fw-bold d-flex justify-content-between">
        <span>💊 Sản phẩm đã đặt</span>
        <button class="btn btn-light btn-sm" @click="xuatPDF">
          <i class="bi bi-printer me-1"></i> In hóa đơn PDF
        </button>
      </div>
      <div class="card-body p-0">
        <table class="table table-bordered mb-0">
          <thead class="table-light text-center">
            <tr>
              <th>#</th>
              <th>Tên thuốc</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="(ct, index) in chiTiet" :key="index">
              <td>{{ index + 1 }}</td>
              <td>{{ getTenThuoc(ct.maThuoc) }}</td>
              <td>{{ ct.soLuong }}</td>
              <td>{{ formatCurrency(ct.donGia) }}</td>
              <td>{{ formatCurrency(ct.soLuong * ct.donGia) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="card-footer text-end bg-light">
        <strong class="me-2">Tổng tiền:</strong>
        <span class="text-danger fw-bold">{{ formatCurrency(tongTien) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import LichSuDonHang from '../LichSuDonHang.vue'

const route = useRoute()
const maDonHang = Number(route.params.maDonHang || 101)

const danhSachDonHang = [
  {
    maDonHang: 101,
    maKH: 1,
    ngayDat: '2024-05-10',
    trangThai: 2,
    daThanhToan: false,
    hinhThuc: 'COD',
    diaChiNhan: '123 Hà Nội',
    soDienThoaiNhan: '0912345678',
    ghiChu: '',
  },
  {
    maDonHang: 102,
    maKH: 2,
    ngayDat: '2024-05-12',
    trangThai: 2,
    daThanhToan: true,
    hinhThuc: 'ONLINE',
    diaChiNhan: '456 Sài Gòn',
    soDienThoaiNhan: '0987654321',
    ghiChu: '',
  },
]

const donHang = ref(danhSachDonHang.find((d) => d.maDonHang === maDonHang))

const chiTiet = ref([
  { maThuoc: 1, soLuong: 2, donGia: 100000 },
  { maThuoc: 2, soLuong: 1, donGia: 300000 },
])

const danhSachKH = ref([
  { maKH: 1, hoTen: 'Nguyễn Văn A' },
  { maKH: 2, hoTen: 'Trần Thị B' },
])

const getTenKhachHang = (maKH) => danhSachKH.value.find((k) => k.maKH === maKH)?.hoTen || 'Không rõ'

const getTenThuoc = (ma) => ['Paracetamol', 'Amoxicillin'][ma - 1] || 'Không rõ'

const formatCurrency = (v) => v.toLocaleString('vi-VN') + '₫'

const trangThaiLabel = (t) =>
  ['Chờ xử lý', 'Xác nhận', 'Đang giao', 'Hoàn tất', 'Hủy'][t] || 'Không rõ'
const trangThaiClass = (t) =>
  ['bg-warning', 'bg-primary', 'bg-info', 'bg-success', 'bg-danger'][t] || 'bg-secondary'

const tongTien = computed(() => chiTiet.value.reduce((sum, ct) => sum + ct.soLuong * ct.donGia, 0))

watchEffect(() => {
  if (
    donHang.value.hinhThuc === 'ONLINE' &&
    donHang.value.daThanhToan &&
    donHang.value.trangThai === 2
  ) {
    donHang.value.trangThai = 3
    alert(`Đơn hàng ${donHang.value.maDonHang} ONLINE đã hoàn tất tự động`)
  }
})

const xuatPDF = () => alert('Xuất PDF demo')
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
.card {
  border-radius: 12px;
}
</style>
