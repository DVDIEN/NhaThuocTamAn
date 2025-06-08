<template>
  <div class="card shadow-sm p-4">
    <h5 class="text-success mb-3">🔐 Đổi mật khẩu</h5>

    <form @submit.prevent="handleChangePassword">
      <div class="mb-3">
        <label for="oldPassword" class="form-label">Mật khẩu hiện tại</label>
        <input
          v-model="oldPassword"
          type="password"
          class="form-control"
          id="oldPassword"
          required
        />
      </div>

      <div class="mb-3">
        <label for="newPassword" class="form-label">Mật khẩu mới</label>
        <input
          v-model="newPassword"
          type="password"
          class="form-control"
          id="newPassword"
          required
        />
      </div>

      <div class="mb-3">
        <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
        <input
          v-model="confirmPassword"
          type="password"
          class="form-control"
          id="confirmPassword"
          required
        />
      </div>

      <div v-if="errorMessage" class="alert alert-danger py-2">
        {{ errorMessage }}
      </div>
      <div v-if="successMessage" class="alert alert-success py-2">
        {{ successMessage }}
      </div>

      <button type="submit" class="btn btn-success">
        <i class="bi bi-arrow-repeat me-1"></i> Cập nhật mật khẩu
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')

function handleChangePassword() {
  errorMessage.value = ''
  successMessage.value = ''

  if (newPassword.value !== confirmPassword.value) {
    errorMessage.value = '⚠️ Mật khẩu xác nhận không khớp.'
    return
  }

  // Gửi API đổi mật khẩu (ví dụ)
  // Bạn cần thay thế bằng API thật
  fakeChangePasswordApi(oldPassword.value, newPassword.value)
    .then(() => {
      successMessage.value = '✅ Mật khẩu đã được cập nhật thành công.'
      oldPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    })
    .catch(() => {
      errorMessage.value = '❌ Mật khẩu hiện tại không đúng hoặc có lỗi xảy ra.'
    })
}

// Mô phỏng API đổi mật khẩu
function fakeChangePasswordApi(oldPass, newPass) {
  return new Promise((resolve, reject) => {
    // Giả lập kiểm tra thành công nếu oldPass là '123456'
    setTimeout(() => {
      oldPass === '123456' ? resolve() : reject()
    }, 1000)
  })
}
</script>
