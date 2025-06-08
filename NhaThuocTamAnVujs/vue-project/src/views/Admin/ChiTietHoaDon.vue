<template>
  <div class="container py-4">
    <div class="card shadow">
      <div class="card-body">
        <h4 class="text-success mb-4">
          🧾 Chi Tiết Hóa Đơn <span class="text-muted">#{{ id }}</span>
        </h4>

        <div class="row mb-3">
          <div class="col-md-6">
            <p><strong>👤 Khách hàng:</strong> {{ hoaDon.tenKhach }}</p>
            <p><strong>📅 Ngày lập:</strong> {{ formatDate(hoaDon.ngayLap) }}</p>
          </div>
          <div class="col-md-6">
            <p>
              <strong>📌 Trạng thái:</strong>
              <span
                :class="
                  hoaDon.trangThai === 'Đã thanh toán'
                    ? 'text-success fw-bold'
                    : 'text-danger fw-bold'
                "
              >
                {{ hoaDon.trangThai }}
              </span>
            </p>
            <div v-if="hoaDon.khuyenMai">
              <p><strong>🎁 Khuyến mãi:</strong> {{ hoaDon.khuyenMai.tenKM }}</p>
              <p>
                <strong>💸 Giảm:</strong>
                {{
                  hoaDon.khuyenMai.loai === 'Phần trăm'
                    ? `Giảm ${hoaDon.khuyenMai.giaTri}%`
                    : `Giảm ${formatCurrency(hoaDon.khuyenMai.giaTri)}`
                }}
              </p>
            </div>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-bordered table-hover table-striped align-middle">
            <thead class="table-light text-center">
              <tr>
                <th>#</th>
                <th>Tên sản phẩm</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ct, index) in hoaDon.chiTiet" :key="index" class="text-center">
                <td>{{ index + 1 }}</td>
                <td class="text-start">{{ ct.tenSP }}</td>
                <td>{{ ct.soLuong }}</td>
                <td>{{ formatCurrency(ct.donGia) }}</td>
                <td class="fw-bold text-success">{{ formatCurrency(ct.donGia * ct.soLuong) }}</td>
              </tr>
              <tr class="fw-bold text-end">
                <td colspan="4">Tạm tính</td>
                <td class="text-center text-primary">{{ formatCurrency(tinhTong()) }}</td>
              </tr>
              <tr v-if="hoaDon.khuyenMai" class="text-end text-danger">
                <td colspan="4">Giảm giá</td>
                <td class="text-center">-{{ formatCurrency(tinhGiamGia()) }}</td>
              </tr>
              <tr class="fw-bold text-end table-success">
                <td colspan="4">Thanh toán</td>
                <td class="text-center">{{ formatCurrency(tinhThanhToan()) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="text-end mt-4">
          <button class="btn btn-outline-secondary me-2 no-print" @click="inHoaDon">
            🖨 In hóa đơn
          </button>
          <router-link to="/admin/hoa-don" class="btn btn-secondary no-print"
            >← Quay lại</router-link
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const id = route.query.id

const hoaDon = ref({
  tenKhach: 'Nguyễn Văn A',
  ngayLap: '2024-05-10',
  trangThai: 'Đã thanh toán',
  khuyenMai: {
    tenKM: 'Mùa Hè Rực Rỡ',
    loai: 'Phần trăm',
    giaTri: 10,
  },
  chiTiet: [
    { tenSP: 'Paracetamol', soLuong: 2, donGia: 50000 },
    { tenSP: 'Vitamin C', soLuong: 1, donGia: 70000 },
  ],
})

const formatDate = (d) => new Date(d).toLocaleDateString('vi-VN')
const formatCurrency = (v) =>
  v.toLocaleString('vi-VN', {
    style: 'currency',
    currency: 'VND',
  })

const tinhTong = () => hoaDon.value.chiTiet.reduce((sum, ct) => sum + ct.donGia * ct.soLuong, 0)
const tinhGiamGia = () => {
  const tong = tinhTong()
  const km = hoaDon.value.khuyenMai
  if (!km) return 0
  return km.loai === 'Phần trăm' ? (tong * km.giaTri) / 100 : km.giaTri
}
const tinhThanhToan = () => tinhTong() - tinhGiamGia()
const inHoaDon = () => window.print()
</script>

<style scoped>
.table td {
  vertical-align: middle;
}
@media print {
  .no-print {
    display: none !important;
  }
  body {
    background: white;
    margin: 0;
    padding: 0;
  }
}
</style>
