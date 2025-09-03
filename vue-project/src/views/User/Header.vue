<template>
  <header class="navbar-top py-3">
    <div class="container d-flex flex-wrap justify-content-between align-items-center">
      <!-- Logo -->
      <router-link to="/" class="d-flex align-items-center text-decoration-none">
        <img
          src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiqUlj1LB8gUERYiFqhQZfT6YFZa2VRvqfwA&s"
          width="40"
          class="me-2"
        />
        <strong class="fs-5 text-white">NHÀ THUỐC TÂM AN</strong>
      </router-link>

      <!-- Tìm kiếm -->
      <div class="d-flex align-items-center flex-grow-1 mx-3" style="max-width: 500px">
        <div class="flex-grow-1">
          <TimKiem />
        </div>
      </div>

      <!-- Tài khoản & Giỏ hàng -->
      <div class="d-flex align-items-center gap-3">
        <!-- Nếu chưa đăng nhập -->
        <router-link v-if="!UserStore.userInfo" to="/dang-nhap" class="btn-login">
          <i class="bi bi-person"></i> Đăng nhập
        </router-link>

        <!-- Nếu đã đăng nhập -->
        <div class="dropdown" v-else>
          <button
            class="btn btn-account dropdown-toggle d-flex align-items-center gap-2"
            type="button"
            data-bs-toggle="dropdown"
          >
            <img :src="getAvatar()" width="36" height="36" class="rounded-circle" />
            <span class="fw-semibold d-none d-md-inline text-white">
              {{ UserStore.userInfo.hoTen }}
            </span>
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

        <!-- Giỏ hàng -->
        <router-link to="/gio-hang" class="btn-cart">
          <i class="bi bi-cart3 cart-icon"></i> Giỏ hàng
          <span class="cart-badge">{{ CartStore.cartCount }}</span>
        </router-link>

        <!-- Tra cứu đơn -->
        <router-link to="/tra-cuu-don-hang" class="btn-cart">
          <i class="bi bi-search"></i> Tra cứu
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { UserStore } from './UserStore'
import TimKiem from './TimKiem.vue'
import { CartStore } from './CartStore'

const serverUrl = 'http://localhost:8080'
const defaultAvatar = 'https://cdn-icons-png.flaticon.com/512/149/149071.png'

function getAvatar() {
  const path = UserStore.userInfo?.hinhAnh
  if (!path) return defaultAvatar
  if (path.startsWith('http')) return path
  return `${serverUrl}${path}`
}

function dangXuat() {
  UserStore.clearUser()
  localStorage.removeItem('cart_temp')
  CartStore.updateCount()
  location.reload()
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
  padding: 8px 16px;
  border-radius: 30px;
  background-color: transparent;
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
}
.btn-cart {
  position: relative;
  background-color: rgba(0, 100, 0, 0.4);
  color: white;
  border-radius: 30px;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
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
</style>
