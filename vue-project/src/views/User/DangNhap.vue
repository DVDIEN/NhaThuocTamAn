<template>
  <div class="login-page d-flex min-vh-100">
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
      <h1 class="fw-bold display-5 text-center">Chào mừng bạn đến với nhà thuốc tâm an</h1>
      <p class="mt-3 fs-5 text-center">Giải pháp toàn diện cho ngành dược</p>
    </div>

    <!-- Bên phải: form đăng nhập -->
    <div class="form-section d-flex flex-column justify-content-center align-items-center">
      <div class="login-box w-100" style="max-width: 400px">
        <div class="text-center mb-4">
          <img
            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiqUlj1LB8gUERYiFqhQZfT6YFZa2VRvqfwA&s"
            alt="icon"
            class="rounded-circle"
            width="70"
          />
          <h4 class="fw-bold mt-3 text-success">Nhà Thuốc Tâm An</h4>
          <p class="text-medium text-dark">Vui lòng đăng nhập để tiếp tục</p>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="mb-3">
            <label class="form-label">Email đăng nhập</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-person"></i></span>
              <input
                type="text"
                v-model="email"
                class="form-control"
                placeholder="Nhập email"
                required
              />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Mật khẩu</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-lock"></i></span>
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="password"
                class="form-control"
                placeholder="Nhập mật khẩu"
                required
              />
              <button type="button" class="btn btn-outline-secondary" @click="togglePassword">
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
          </div>

          <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" id="remember" />
              <label class="form-check-label" for="remember">Ghi nhớ đăng nhập</label>
            </div>
            <router-link to="/quen-mat-khau" class="text-success text-decoration-none">
              Quên mật khẩu?
            </router-link>
          </div>

          <div v-if="error" class="alert alert-danger text-center">{{ error }}</div>

          <button
            type="submit"
            class="btn btn-success w-100 d-flex justify-content-center align-items-center"
            :disabled="isLoading"
          >
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
            <span>{{ isLoading ? 'Đang đăng nhập...' : 'Đăng nhập' }}</span>
          </button>
        </form>

        <p class="text-center mt-3">
          Chưa có tài khoản?
          <router-link to="/dang-ky" class="text-success text-decoration-none">
            Đăng ký ngay
          </router-link>
        </p>
      </div>
    </div>

    <!-- ToastMessage dùng chung -->
    <ToastMessage ref="toastRef" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { UserStore } from './UserStore'
import { CartStore } from './CartStore'
import ToastMessage from '../ToastMessage.vue'

const email = ref('')
const password = ref('')
const error = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const router = useRouter()
const toastRef = ref(null)

function togglePassword() {
  showPassword.value = !showPassword.value
}

async function handleLogin() {
  error.value = ''

  if (!email.value || !password.value) {
    error.value = 'Vui lòng nhập email và mật khẩu'
    return
  }

  try {
    isLoading.value = true
    const res = await axios.post('http://localhost:8080/api/auth/dang-nhap', {
      email: email.value,
      matKhau: password.value,
    })

    const user = res.data
    let userDetail = null

    if (user.vaiTro === 3) {
      userDetail = await axios.get(`http://localhost:8080/api/khachhang/${user.maKH}`)
      localStorage.setItem('maKH', user.maKH)
    } else if (user.vaiTro === 2) {
      userDetail = await axios.get(`http://localhost:8080/api/nhanvien/${user.maNV}`)
    } else if (user.vaiTro === 1) {
      userDetail = await axios.get(`http://localhost:8080/api/nhanvien/${user.maNV}`)
    } else {
      error.value = 'Không xác định được vai trò người dùng.'
      return
    }

    UserStore.setUser(userDetail.data)
    localStorage.setItem('userInfo', JSON.stringify(userDetail.data))

    if (user.vaiTro === 3) {
      const tempCart = JSON.parse(localStorage.getItem('cart_temp') || '[]')
      const key = `cart_${user.maKH}`
      const existingCart = JSON.parse(localStorage.getItem(key) || '[]')

      tempCart.forEach((item) => {
        const found = existingCart.find((sp) => sp.id === item.id)
        if (found) found.soLuong += item.soLuong
        else existingCart.push(item)
      })

      if (tempCart.length > 0) {
        localStorage.setItem(key, JSON.stringify(existingCart))
        localStorage.removeItem('cart_temp')
      }

      CartStore.init()
    }

    toastRef.value.show('✅ Đăng nhập thành công!', 'success')
    setTimeout(() => {
      router.push(user.vaiTro === 1 || user.vaiTro === 2 ? '/admin' : '/home')
    }, 1200)
  } catch (err) {
    const res = err.response
    if (res?.status === 403) {
      if (res.data?.message?.includes('chưa được xác thực')) {
        toastRef.value.show('⚠️ Tài khoản chưa được xác thực. Vui lòng kiểm tra email.', 'warning')
        router.push(`/xac-thuc-email?email=${encodeURIComponent(res.data?.email || email.value)}`)
        return
      }
      error.value = res.data?.message
      toastRef.value.show(error.value, 'error')
      return
    }
    error.value = res?.data?.message || 'Email hoặc mật khẩu không đúng.'
    toastRef.value.show(error.value, 'error')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css');

.login-page {
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
  font-weight: 500;
}

.login-box {
  background-color: #ffffff;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

/* Loader overlay */
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
