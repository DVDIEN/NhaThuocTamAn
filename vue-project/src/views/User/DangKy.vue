<template>
  <div class="register-page d-flex min-vh-100">
    <!-- Loader toàn trang -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-success" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Bên trái: giới thiệu -->
    <div
      class="intro-section d-flex flex-column justify-content-center align-items-center text-white"
    >
      <h1 class="fw-bold display-5 text-center">Hệ thống Quản lý Dược phẩm</h1>
      <p class="mt-3 fs-5 text-center">Giải pháp toàn diện cho ngành dược</p>
    </div>

    <!-- Bên phải: form đăng ký -->
    <div class="form-section d-flex flex-column justify-content-center align-items-center">
      <div class="register-box w-100" style="max-width: 450px">
        <div class="text-center mb-4">
          <i class="bi bi-person-plus-fill text-success" style="font-size: 2.5rem"></i>
          <h4 class="mt-2 text-success fw-bold">Đăng ký tài khoản</h4>
          <p class="text-dark text-medium">Vui lòng điền đầy đủ thông tin để tạo tài khoản</p>
        </div>

        <form @submit.prevent="handleRegister" novalidate>
          <!-- Họ tên -->
          <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-person-fill"></i></span>
              <input
                v-model="hoTen"
                type="text"
                class="form-control"
                placeholder="Nhập họ tên..."
              />
            </div>
          </div>

          <!-- Email -->
          <div class="mb-3">
            <label class="form-label">Email</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-envelope-fill"></i></span>
              <input
                v-model="email"
                type="email"
                class="form-control"
                placeholder="Nhập email..."
              />
            </div>
          </div>

          <!-- Mật khẩu -->
          <div class="mb-3">
            <label class="form-label">Mật khẩu</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="password"
                class="form-control"
                placeholder="Tối thiểu 6 ký tự"
              />
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="showPassword = !showPassword"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
          </div>

          <!-- Xác nhận mật khẩu -->
          <div class="mb-3">
            <label class="form-label">Xác nhận mật khẩu</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-shield-lock-fill"></i></span>
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="confirmPassword"
                class="form-control"
                placeholder="Nhập lại mật khẩu"
              />
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="showPassword = !showPassword"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
          </div>

          <!-- Thông báo lỗi cục bộ (giữ lại) -->
          <div v-if="error" class="alert alert-danger text-center">{{ error }}</div>

          <!-- Nút đăng ký -->
          <button
            type="submit"
            class="btn btn-success w-100 d-flex align-items-center justify-content-center"
            :disabled="isLoading"
          >
            <span
              v-if="isLoading"
              class="spinner-border spinner-border-sm me-2"
              role="status"
              aria-hidden="true"
            ></span>
            <span>{{ isLoading ? 'Đang xử lý...' : 'Đăng ký' }}</span>
          </button>
        </form>

        <p class="text-center mt-3">
          Đã có tài khoản?
          <router-link to="/dang-nhap" class="fw-bold text-success text-decoration-none"
            >Đăng nhập</router-link
          >
        </p>
      </div>
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
import axios from 'axios'
import { useRouter } from 'vue-router'

const hoTen = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const error = ref('')

const isLoading = ref(false)
const toastMessage = ref('')
const toastType = ref('success') // 'success' | 'danger' | 'warning' | 'info'
const toastRef = ref(null)
const router = useRouter()

const toastBgClass = computed(() => {
  return {
    'text-bg-success': toastType.value === 'success',
    'text-bg-danger': toastType.value === 'danger',
    'text-bg-warning': toastType.value === 'warning',
    'text-bg-info': toastType.value === 'info',
  }
})

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

async function handleRegister() {
  error.value = ''

  // ✅ validate dữ liệu
  if (!hoTen.value || !email.value || !password.value || !confirmPassword.value) {
    error.value = '❗ Vui lòng điền đầy đủ thông tin!'
    return
  }
  // validate email regex
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    error.value = '❗ Email không hợp lệ!'
    return
  }
  if (password.value.length < 6) {
    error.value = '❗ Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }
  if (password.value !== confirmPassword.value) {
    error.value = '❗ Mật khẩu và xác nhận không khớp!'
    return
  }

  const khachHang = { hoTen: hoTen.value, email: email.value, matKhau: password.value }

  try {
    isLoading.value = true
    await axios.post('http://localhost:8080/api/auth/dang-ky', khachHang)
    showToast('success', '🎉 Đăng ký thành công! Mã xác thực đã được gửi đến email.')
    // Điều hướng sau 1.2s
    setTimeout(() => {
      router.push(`/xac-thuc-email?email=${encodeURIComponent(email.value)}`)
    }, 1200)
  } catch (err) {
    // ✅ lấy thông điệp từ backend (ví dụ: "Email đã tồn tại trong Nhân viên/Khách hàng")
    const msg = err?.response?.data?.message || '❌ Đăng ký thất bại! Vui lòng thử lại.'
    error.value = msg
    showToast('danger', msg)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css');

.register-page {
  display: flex;
  height: 100vh;
}
.intro-section {
  background-image: url('https://images.pexels.com/photos/7615574/pexels-photo-7615574.jpeg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  width: 50%;
  padding: 40px;
  color: white;
}
.intro-section h1,
.intro-section p {
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.6);
}
.form-section {
  width: 50%;
  padding: 40px;
  background-color: #ffffff;
}
.register-box {
  background-color: #ffffff;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}
.text-medium {
  font-weight: 500;
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

/* Toast: dùng lớp .show để hiện, ẩn thì bỏ .show */
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
