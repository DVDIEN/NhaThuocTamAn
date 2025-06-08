<template>
  <header class="navbar-top py-3">
    <div class="container d-flex flex-wrap justify-content-between align-items-center">
      <!-- Logo -->
      <div class="d-flex align-items-center">
        <img
          src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiqUlj1LB8gUERYiFqhQZfT6YFZa2VRvqfwA&s"
          width="40"
          class="me-2"
        />
        <strong class="fs-5 text-white">NHÀ THUỐC TÂM AN</strong>
      </div>

      <!-- Tìm kiếm và nút quét mã -->
      <div class="d-flex align-items-center flex-grow-1 mx-3" style="max-width: 500px">
        <div class="flex-grow-1">
          <TimKiem />
        </div>
        <button class="btn-scan ms-2" @click="moQuetMa" title="Quét mã sản phẩm">
          <i class="bi bi-qr-code-scan fs-4"></i>
        </button>
      </div>

      <!-- Tài khoản & Giỏ hàng -->
      <div class="d-flex align-items-center gap-3">
        <router-link v-if="!userInfo" to="/dang-nhap" class="btn-login">
          <i class="bi bi-person"></i> Đăng nhập
        </router-link>

        <div class="dropdown" v-else>
          <button
            class="btn btn-account dropdown-toggle d-flex align-items-center gap-2"
            type="button"
            data-bs-toggle="dropdown"
          >
            <img
              :src="userInfo.avata || defaultAvatar"
              width="36"
              height="36"
              class="rounded-circle"
            />
            <span class="fw-semibold d-none d-md-inline text-white">{{ userInfo.hoTen }}</span>
          </button>
          <ul class="dropdown-menu dropdown-menu-end mt-2">
            <li>
              <router-link class="dropdown-item" to="/tai-khoan/thong-tin-ca-nhan">
                <i class="bi bi-person me-2"></i> Thông tin cá nhân
              </router-link>
            </li>
            <li><hr class="dropdown-divider" /></li>
            <li>
              <a class="dropdown-item text-danger" href="#" @click.prevent="dangXuat">
                <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
              </a>
            </li>
          </ul>
        </div>

        <router-link to="/gio-hang" class="btn-cart">
          <i class="bi bi-cart3"></i> Giỏ hàng
          <span class="cart-badge">1</span>
        </router-link>
      </div>
    </div>

    <!-- Modal quét mã -->
    <div v-if="showScanner" class="modal-scanner" @click.self="dongQuetMa">
      <div class="scanner-box p-3 bg-white rounded shadow">
        <h5 class="mb-3 text-center">📷 Quét mã sản phẩm</h5>

        <!-- Ẩn input, dùng label để gọi -->
        <input type="file" id="fileInput" accept="image/*" class="d-none" @change="xuLyAnhQuet" />
        <label for="fileInput" class="btn-upload w-100 text-center">
          <i class="bi bi-image fs-2 mb-2 d-block"></i>
          <span>Chọn ảnh từ máy</span>
        </label>

        <p class="mt-3 text-muted text-center" style="font-size: 0.9rem">
          Hệ thống sẽ tự quét mã QR hoặc mã vạch từ ảnh bạn chọn.
        </p>

        <div class="text-center mt-3">
          <button class="btn btn-sm btn-secondary" @click="dongQuetMa">Đóng</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TimKiem from './TimKiem.vue'

const userInfo = ref(null)
const defaultAvatar = 'https://cdn-icons-png.flaticon.com/512/149/149071.png'
const showScanner = ref(false)

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
})

function dangXuat() {
  localStorage.removeItem('userInfo')
  location.reload()
}

function moQuetMa() {
  showScanner.value = true
}

function dongQuetMa() {
  showScanner.value = false
}

function xuLyAnhQuet(event) {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    const imgData = reader.result
    alert('📷 Đã chọn ảnh. Gửi ảnh này lên server để quét mã và tìm sản phẩm.')
    dongQuetMa()
  }
  reader.readAsDataURL(file)
}
</script>

<style scoped>
.navbar-top {
  background-color: #43a047;
  color: white;
}
.btn-login {
  font-size: 1.1rem;
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 30px;
  background-color: transparent;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.2s ease;
}
.btn-login:hover,
.btn-account:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.btn-account {
  background-color: transparent;
  border: none;
  color: white;
}
.btn-cart {
  position: relative;
  background-color: rgba(0, 100, 0, 0.4);
  color: white;
  border-radius: 30px;
  padding: 8px 16px;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
}
.cart-badge {
  background-color: orange;
  color: white;
  font-size: 0.7rem;
  border-radius: 50%;
  padding: 2px 6px;
  position: absolute;
  top: -6px;
  right: -6px;
}
.btn-scan {
  background-color: rgba(255, 255, 255, 0.15);
  border: none;
  padding: 6px 10px;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}
.btn-scan:hover {
  background-color: rgba(255, 255, 255, 0.3);
}
.modal-scanner {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}
.scanner-box {
  width: 350px;
  max-width: 95%;
}
.btn-upload {
  border: 2px dashed #43a047;
  padding: 30px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #333;
  background-color: #f9f9f9;
}
.btn-upload:hover {
  background-color: #e0f2e9;
  border-color: #2e7d32;
  color: #2e7d32;
}
</style>
