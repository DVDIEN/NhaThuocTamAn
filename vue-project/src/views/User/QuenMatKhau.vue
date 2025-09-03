<template>
  <div class="forgot-container d-flex justify-content-center align-items-center min-vh-100">
    <!-- Loader toàn trang -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-success" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <div
      class="card shadow p-4 text-center"
      style="max-width: 420px; width: 100%; border-radius: 16px"
    >
      <div class="mb-4">
        <img
          src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiqUlj1LB8gUERYiFqhQZfT6YFZa2VRvqfwA&s"
          alt="icon"
          width="64"
          class="mb-3"
        />
        <h4 class="fw-bold">Quên Mật Khẩu</h4>
        <p class="text-muted small">
          Nhập địa chỉ email đã đăng ký để nhận<br />mã khôi phục mật khẩu
        </p>
      </div>

      <form @submit.prevent="xuLyDatLaiMatKhau" class="text-start">
        <!-- Email -->
        <div class="mb-3">
          <label class="form-label">Email</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-envelope-fill"></i></span>
            <input
              v-model="email"
              type="email"
              class="form-control"
              placeholder="Nhập email của bạn"
              required
            />
          </div>
        </div>

        <!-- Mật khẩu mới -->
        <div class="mb-3">
          <label class="form-label">Mật khẩu mới</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
            <input
              :type="hienMatKhauMoi ? 'text' : 'password'"
              v-model="matKhauMoi"
              class="form-control"
              placeholder="Tối thiểu 6 ký tự"
            />
            <button
              type="button"
              class="btn btn-outline-secondary"
              @click="chuyenTrangThaiMatKhauMoi"
            >
              <i :class="hienMatKhauMoi ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
            </button>
          </div>
        </div>

        <!-- Xác nhận mật khẩu -->
        <div class="mb-3">
          <label class="form-label">Xác nhận mật khẩu</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
            <input
              :type="hienXacNhanMatKhau ? 'text' : 'password'"
              v-model="xacNhanMatKhau"
              class="form-control"
              placeholder="Nhập lại mật khẩu"
            />
            <button
              type="button"
              class="btn btn-outline-secondary"
              @click="chuyenTrangThaiXacNhanMatKhau"
            >
              <i :class="hienXacNhanMatKhau ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
            </button>
          </div>
        </div>

        <!-- Mã xác thực -->
        <div class="mb-3">
          <label class="form-label">Mã xác thực</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-shield-lock-fill"></i></span>
            <input
              v-model="maXacThuc"
              type="text"
              class="form-control"
              placeholder="Nhập mã xác thực"
            />
            <button
              class="btn btn-outline-success"
              type="button"
              :disabled="demNguoc > 0 || isLoading"
              @click="guiMa"
            >
              {{ demNguoc > 0 ? `Gửi lại (${demNguoc}s)` : 'Gửi mã' }}
            </button>
          </div>
        </div>

        <!-- Thông báo -->
        <div v-if="thongBaoLoi" class="alert alert-danger py-2">
          <i class="bi bi-exclamation-triangle me-2"></i> {{ thongBaoLoi }}
        </div>
        <div v-if="thongBaoThanhCong" class="alert alert-success py-2">
          <i class="bi bi-check-circle me-2"></i> {{ thongBaoThanhCong }}
        </div>

        <!-- Nút -->
        <button
          type="submit"
          class="btn btn-success w-100 rounded mt-2 d-flex justify-content-center align-items-center"
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
          <span>{{ isLoading ? 'Đang xử lý...' : 'Đặt lại mật khẩu' }}</span>
        </button>

        <div class="text-center mt-3">
          <router-link to="/dang-nhap" class="text-decoration-none small text-success">
            ← Quay lại đăng nhập
          </router-link>
        </div>
      </form>
    </div>

    <!-- Toast -->
    <div
      class="toast position-fixed top-0 end-0 m-3 p-0 shadow"
      :class="toastBgClass"
      role="alert"
      aria-live="polite"
      aria-atomic="true"
      ref="toastRef"
      style="z-index: 2100; min-width: 280px"
    >
      <div class="toast-body d-flex justify-content-between align-items-center">
        <span class="fw-semibold">{{ toastMessage }}</span>
        <button type="button" class="btn-close btn-close-white ms-2" @click="hideToast"></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const dieuHuong = useRouter()

const email = ref('')
const maXacThuc = ref('')
const matKhauMoi = ref('')
const xacNhanMatKhau = ref('')
const hienMatKhauMoi = ref(false)
const hienXacNhanMatKhau = ref(false)

const thongBaoLoi = ref('')
const thongBaoThanhCong = ref('')
const demNguoc = ref(0)
let boDemThoiGian = null

const isLoading = ref(false)
const toastMessage = ref('')
const toastType = ref('success')
const toastRef = ref(null)

const toastBgClass = computed(() => ({
  'text-bg-success': toastType.value === 'success',
  'text-bg-danger': toastType.value === 'danger',
  'text-bg-warning': toastType.value === 'warning',
  'text-bg-info': toastType.value === 'info',
}))

let toastTimer = null
function showToast(type, message, duration = 3000) {
  toastType.value = type
  toastMessage.value = message
  const el = toastRef.value
  if (el) {
    el.classList.add('show')
    clearTimeout(toastTimer)
    toastTimer = setTimeout(() => hideToast(), duration)
  }
}
function hideToast() {
  const el = toastRef.value
  if (el) el.classList.remove('show')
  clearTimeout(toastTimer)
}

function chuyenTrangThaiMatKhauMoi() {
  hienMatKhauMoi.value = !hienMatKhauMoi.value
}
function chuyenTrangThaiXacNhanMatKhau() {
  hienXacNhanMatKhau.value = !hienXacNhanMatKhau.value
}
function batDauDemNguoc() {
  demNguoc.value = 60
  boDemThoiGian = setInterval(() => {
    if (demNguoc.value > 0) demNguoc.value--
    else clearInterval(boDemThoiGian)
  }, 1000)
}

function guiMa() {
  thongBaoLoi.value = ''
  if (!email.value) {
    thongBaoLoi.value = 'Vui lòng nhập email trước khi gửi mã xác thực!'
    return
  }
  isLoading.value = true
  axios
    .post('http://localhost:8080/api/forgot/send-code', { email: email.value })
    .then(() => {
      showToast('success', '✅ Mã xác thực đã được gửi qua email!')
      batDauDemNguoc()
    })
    .catch((err) => {
      const msg = err.response?.data?.message || 'Không thể gửi mã. Vui lòng thử lại sau.'
      thongBaoLoi.value = msg
      showToast('danger', msg)
    })
    .finally(() => {
      isLoading.value = false
    })
}

function xuLyDatLaiMatKhau() {
  thongBaoLoi.value = ''
  thongBaoThanhCong.value = ''

  if (!email.value || !maXacThuc.value || !matKhauMoi.value || !xacNhanMatKhau.value) {
    thongBaoLoi.value = 'Vui lòng điền đầy đủ các trường.'
    showToast('danger', thongBaoLoi.value)
    return
  }
  if (matKhauMoi.value.length < 6) {
    thongBaoLoi.value = 'Mật khẩu phải có ít nhất 6 ký tự.'
    showToast('danger', thongBaoLoi.value)
    return
  }
  if (matKhauMoi.value !== xacNhanMatKhau.value) {
    thongBaoLoi.value = 'Mật khẩu xác nhận không khớp.'
    showToast('danger', thongBaoLoi.value)
    return
  }

  isLoading.value = true
  axios
    .post('http://localhost:8080/api/forgot/verify-code', {
      email: email.value,
      code: maXacThuc.value,
    })
    .then(() =>
      axios.post('http://localhost:8080/api/forgot/reset-pass', {
        email: email.value,
        newPassword: matKhauMoi.value,
      }),
    )
    .then(() => {
      thongBaoThanhCong.value = 'Đặt lại mật khẩu thành công!'
      showToast('success', thongBaoThanhCong.value)
      setTimeout(() => dieuHuong.push('/dang-nhap'), 1500)
    })
    .catch((err) => {
      const msg = err.response?.data?.message || 'Mã xác thực không hợp lệ hoặc đã hết hạn.'
      thongBaoLoi.value = msg
      showToast('danger', msg)
    })
    .finally(() => {
      isLoading.value = false
    })
}
</script>

<style scoped>
.forgot-container {
  background: linear-gradient(to right, #f1f8e9, #e3f2fd);
  padding: 20px;
}
.card {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}
input.form-control:focus {
  border-color: #2e7d32;
  box-shadow: 0 0 0 0.2rem rgba(46, 125, 50, 0.25);
}
button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Loader toàn trang */
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Toast */
.toast {
  opacity: 0;
  transition:
    opacity 0.2s ease-in-out,
    transform 0.2s;
  transform: translateY(-6px);
}
.toast.show {
  opacity: 1;
  transform: translateY(0);
}
</style>
