<template>
  <div class="verify-container d-flex justify-content-center align-items-center min-vh-100">
    <!-- Loader toàn trang -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-success" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <div class="card p-4 shadow-lg w-100" style="max-width: 460px; border-radius: 16px">
      <div class="text-center mb-3">
        <i class="bi bi-envelope-check-fill text-success" style="font-size: 3rem"></i>
        <h4 class="text-success fw-bold mt-2">Xác thực email</h4>
      </div>

      <div class="mb-3">
        <label class="form-label">🔐 Mã xác thực</label>
        <input v-model="code" class="form-control" placeholder="Nhập mã gồm 6 ký tự" />
      </div>

      <div v-if="error" class="alert alert-danger text-center py-2">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>{{ error }}
      </div>

      <button
        @click="xacThuc"
        class="btn btn-success w-100 mb-2 fw-bold d-flex justify-content-center align-items-center"
        :disabled="isLoading"
      >
        <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
        <span>{{ isLoading ? 'Đang xử lý...' : '✅ Xác thực' }}</span>
      </button>

      <button
        class="btn btn-link w-100 text-success"
        @click="guiLaiMa"
        :disabled="countdown > 0 || isLoading"
      >
        <span v-if="countdown === 0">📩 Gửi lại mã xác thực</span>
        <span v-else>⏳ Gửi lại mã sau {{ countdown }}s</span>
      </button>
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
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const email = route.query.email || ''
const code = ref('')
const error = ref('')
const countdown = ref(0)
let countdownInterval = null

// Loading + Toast
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

// Gửi mã xác thực
function guiLaiMa() {
  error.value = ''
  isLoading.value = true
  axios
    .post('http://localhost:8080/api/forgot/send-code', { email })
    .then(() => {
      showToast('success', '📨 Đã gửi lại mã xác thực!')
      startCountdown()
    })
    .catch(() => {
      error.value = 'Không thể gửi lại mã, thử lại sau.'
      showToast('danger', error.value)
    })
    .finally(() => {
      isLoading.value = false
    })
}

// Xác thực
function xacThuc() {
  error.value = ''
  if (!code.value) {
    error.value = 'Vui lòng nhập mã xác thực!'
    showToast('danger', error.value)
    return
  }

  isLoading.value = true
  axios
    .post('http://localhost:8080/api/auth/xac-thuc-email', { email, code: code.value })
    .then(() => {
      showToast('success', '✅ Xác thực thành công!')
      setTimeout(() => router.push('/dang-nhap'), 1500)
    })
    .catch(() => {
      error.value = '❌ Mã xác thực không đúng hoặc đã hết hạn!'
      showToast('danger', error.value)
    })
    .finally(() => {
      isLoading.value = false
    })
}

// Countdown
function startCountdown() {
  countdown.value = 60
  clearInterval(countdownInterval)
  countdownInterval = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownInterval)
    }
  }, 1000)
}
</script>

<style scoped>
.verify-container {
  background: linear-gradient(to right, #f1f8e9, #e3f2fd);
  padding: 20px;
}
.card {
  border-radius: 16px;
  background-color: #fff;
}
button[disabled] {
  opacity: 0.6;
  pointer-events: none;
}
input.form-control:focus {
  border-color: #2e7d32;
  box-shadow: 0 0 0 0.2rem rgba(46, 125, 50, 0.25);
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
