<template>
  <div>
    <Header />
    <Navbar />
  </div>

  <div>
    <div class="container py-5" style="max-width: 600px">
      <h4 class="text-success mb-4">🔍 Tra cứu đơn hàng</h4>

      <!-- Ô nhập mã đơn hàng -->
      <div class="input-group mb-3">
        <input v-model="maDon" class="form-control" placeholder="Nhập mã đơn hàng..." />
        <button class="btn btn-success" @click="traCuu">Tra cứu</button>
      </div>

      <!-- Lỗi -->
      <div v-if="error" class="alert alert-danger">{{ error }}</div>
    </div>

    <!-- Modal chi tiết đơn -->
    <div
      v-if="donHang"
      class="modal fade show d-block"
      tabindex="-1"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #{{ donHang.maDonHang }}</h5>
            <button type="button" class="btn-close" @click="donHang = null"></button>
          </div>

          <div class="modal-body">
            <div class="mb-2">
              <strong>Ngày đặt:</strong> {{ formatDate(donHang.ngayDat) }}<br />
              <strong>Trạng thái:</strong>
              <span :class="getStatusClass(donHang.trangThai)">
                {{ getStatusLabel(donHang.trangThai) }}
              </span>
            </div>

            <div class="mb-3">
              <strong>Thông tin người nhận:</strong>
              <div>Họ tên: {{ donHang.hoTenNguoiNhan }}</div>
              <div>SĐT: {{ donHang.soDienThoaiNhan }}</div>
              <div>Địa chỉ: {{ donHang.diaChiNhan }}</div>
            </div>

            <h6 class="fw-bold mb-2">Danh sách sản phẩm</h6>
            <table class="table table-bordered table-sm text-center align-middle">
              <thead class="table-light">
                <tr>
                  <th>Ảnh</th>
                  <th>Sản phẩm</th>
                  <th>Đơn giá</th>
                  <th>SL</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="ct in donHang.chiTietHienThi" :key="ct.maThuoc || ct.maBienThe">
                  <td>
                    <img
                      :src="getImage(ct)"
                      class="img-thumbnail"
                      style="width: 60px; height: 60px; object-fit: cover"
                    />
                  </td>
                  <td>{{ ct.tenThuoc }}</td>
                  <td>{{ formatMoney(ct.donGia) }}</td>
                  <td>{{ ct.soLuong }}</td>
                  <td>{{ formatMoney(ct.donGia * ct.soLuong) }}</td>
                </tr>
              </tbody>
            </table>

            <div class="mt-3">
              <div><strong>Giảm giá:</strong> {{ formatMoney(donHang.giamGia) }}</div>
              <div class="fw-bold fs-5 text-danger">
                Tổng thanh toán: {{ formatMoney(donHang.thanhToan) }}
              </div>
            </div>

            <div class="mt-2"><strong>Ghi chú:</strong> {{ donHang.ghiChu || '---' }}</div>

            <div v-if="donHang.trangThai === 4 && donHang.lyDoHuy" class="mt-2 text-danger">
              <strong>Lý do hủy đơn:</strong> {{ donHang.lyDoHuy }}
            </div>

            <div class="mt-2">
              <strong>Hình thức thanh toán:</strong>
              {{
                donHang.hinhThucThanhToan === 'cod'
                  ? 'Thanh toán khi nhận hàng'
                  : 'Đã thanh toán Online'
              }}
            </div>

            <!-- Thông báo lỗi khi huỷ đơn -->
            <div v-if="huyError" class="alert alert-danger mt-3">{{ huyError }}</div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" @click="donHang = null">Đóng</button>
            <!-- Nút hủy đơn chỉ hiện khi trạng thái = 0 -->
            <button
              v-if="donHang.trangThai === 0"
              class="btn btn-danger"
              @click="showHuyModal = true"
            >
              Huỷ đơn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal nhập lý do hủy -->
    <div
      v-if="showHuyModal"
      class="modal fade show d-block"
      tabindex="-1"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Huỷ đơn hàng #{{ donHang.maDonHang }}</h5>
            <button type="button" class="btn-close" @click="showHuyModal = false"></button>
          </div>
          <div class="modal-body">
            <label class="form-label">Lý do huỷ:</label>
            <textarea v-model="lyDoHuy" class="form-control" rows="3"></textarea>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showHuyModal = false">Đóng</button>
            <button class="btn btn-danger" @click="huyDonHang">Xác nhận huỷ</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from './Header.vue'
import Navbar from './Navbar.vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const maDon = ref('')
const donHang = ref(null)
const error = ref('')
const huyError = ref('')
const showHuyModal = ref(false)
const lyDoHuy = ref('')
const route = useRoute()

onMounted(() => {
  const maFromUrl = route.query.ma
  if (maFromUrl) {
    maDon.value = maFromUrl
    traCuu()
  }
})

function traCuu() {
  error.value = ''
  donHang.value = null

  if (!maDon.value) {
    error.value = 'Vui lòng nhập mã đơn hàng.'
    return
  }

  axios
    .get(`http://localhost:8080/api/donhang/dto/${maDon.value}`)
    .then((res) => {
      donHang.value = res.data
    })
    .catch(() => {
      error.value = 'Không tìm thấy đơn hàng với mã đã nhập.'
    })
}

function huyDonHang() {
  huyError.value = ''
  if (!lyDoHuy.value.trim()) {
    huyError.value = 'Vui lòng nhập lý do huỷ đơn.'
    return
  }

  axios
    .put(`http://localhost:8080/api/donhang/huy/${donHang.value.maDonHang}`, lyDoHuy.value, {
      headers: { 'Content-Type': 'application/json' },
    })
    .then((res) => {
      donHang.value = res.data
      showHuyModal.value = false
      lyDoHuy.value = ''
      alert('Huỷ đơn thành công!')
    })
    .catch((err) => {
      huyError.value = err.response?.data || 'Không thể huỷ đơn. Vui lòng thử lại.'
    })
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('vi-VN')
}

function formatMoney(val) {
  return (val ?? 0).toLocaleString('vi-VN', {
    style: 'currency',
    currency: 'VND',
  })
}

function getImage(ct) {
  const base = 'http://localhost:8080/'
  if (ct.hinhAnh) return base + ct.hinhAnh
  if (ct.hinhAnhChinh) return base + ct.hinhAnhChinh
  return 'https://cdn-icons-png.flaticon.com/512/891/891462.png'
}

function getStatusLabel(status) {
  switch (status) {
    case 0:
      return 'Chờ xác nhận'
    case 1:
      return 'Đã xác nhận'
    case 2:
      return 'Đang giao'
    case 3:
      return 'Hoàn tất'
    case 4:
      return 'Đã huỷ'
    default:
      return 'Không rõ'
  }
}

function getStatusClass(status) {
  switch (status) {
    case 0:
      return 'badge bg-secondary'
    case 1:
      return 'badge bg-primary'
    case 2:
      return 'badge bg-warning text-dark'
    case 3:
      return 'badge bg-success'
    case 4:
      return 'badge bg-dark'
    default:
      return 'badge bg-light text-dark'
  }
}
</script>

<style scoped>
.modal-dialog {
  max-width: 800px;
}
</style>
