<template>
  <div class="container my-5 border p-4 bg-white rounded shadow-sm">
    <h4 class="text-center mb-4 fw-bold text-primary">📦 PHIẾU GIAO HÀNG</h4>

    <div v-if="donHang">
      <div class="mb-3">
        <div><strong>Mã đơn hàng:</strong> #{{ donHang.maDonHang }}</div>
        <div><strong>Ngày đặt:</strong> {{ formatDate(donHang.ngayDat) }}</div>
      </div>

      <div class="mb-3">
        <strong>Thông tin người nhận</strong>
        <div>Khách hàng: {{ donHang.khachHang?.hoTen || donHang.hoTenNguoiNhan || '---' }}</div>
        <div>Địa chỉ: {{ donHang.diaChiNhan || '---' }}</div>
        <div>SĐT: {{ donHang.soDienThoaiNhan || '---' }}</div>
      </div>

      <h6 class="fw-bold mt-4 mb-2">Danh sách sản phẩm</h6>
      <table class="table table-bordered table-sm text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>Sản phẩm</th>
            <th>SL</th>
            <th>Đơn giá</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ct in donHang.chiTiet" :key="ct.maDHCT">
            <td>{{ ct.bienTheThuoc?.thuoc?.tenThuoc || ct.thuoc?.tenThuoc || '---' }}</td>
            <td>{{ ct.soLuong }}</td>
            <td>{{ formatMoney(ct.donGia) }}</td>
          </tr>
        </tbody>
      </table>

      <div class="mt-3">
        <div class="fw-bold fs-5 text-danger">
          Tổng thanh toán: {{ formatMoney(donHang.thanhToan) }}
        </div>
      </div>

      <div class="mt-3"><strong>Ghi chú:</strong> {{ donHang.ghiChu || '---' }}</div>

      <div class="mt-2">
        <strong>Hình thức thanh toán:</strong>
        {{
          donHang.hinhThucThanhToan === 'cod'
            ? donHang.daThanhToan
              ? 'Đã thanh toán tại quầy'
              : 'Thanh toán khi nhận hàng'
            : 'Đã thanh toán Online'
        }}
      </div>

      <div class="text-center mt-4 d-print-none">
        <button class="btn btn-primary" @click="printOrder">
          <i class="bi bi-printer me-2"></i>In phiếu
        </button>
      </div>
    </div>

    <div v-else class="text-center">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3">Đang tải thông tin đơn hàng...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const maDonHang = route.params.id
const donHang = ref(null)

onMounted(async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/donhang/${maDonHang}`)
    donHang.value = res.data
    setTimeout(() => window.print(), 1000) // Auto print sau khi load
  } catch (err) {
    alert('❌ Không thể tải đơn hàng!')
    console.error(err)
  }
})

function formatDate(date) {
  return new Date(date).toLocaleDateString('vi-VN')
}

function formatMoney(val) {
  return (val ?? 0).toLocaleString('vi-VN', {
    style: 'currency',
    currency: 'VND',
  })
}

function printOrder() {
  window.print()
}
</script>

<style scoped>
@media print {
  button {
    display: none;
  }
  .border {
    border: none !important;
    box-shadow: none !important;
  }
}
</style>
