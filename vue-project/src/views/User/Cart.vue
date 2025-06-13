<template>
  <div>
    <Header />
    <Navbar />

    <main class="cart-page container my-4">
      <router-link to="/home" class="back-link">
        <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
      </router-link>

      <div v-if="gioHang.length === 0" class="alert alert-warning mt-4">
        Giỏ hàng đang trống. <router-link to="/home">Mua sắm ngay</router-link>
      </div>

      <div v-else class="row mt-3">
        <div class="col-md-8">
          <div class="cart-box p-3 bg-white rounded shadow-sm">
            <h5>Chọn tất cả ({{ gioHang.length }})</h5>
            <hr />
            <div v-for="(sp, index) in gioHang" :key="index" class="d-flex align-items-center mb-3">
              <input type="checkbox" class="form-check-input me-2" checked />
              <img
                :src="sp.thumbnails?.[0] || sp.img"
                alt="Sản phẩm"
                class="me-3"
                style="width: 60px; height: 60px; object-fit: contain"
              />
              <div class="flex-grow-1">
                <div class="fw-semibold">{{ sp.tenSP || sp.name }}</div>
                <div class="text-muted small">{{ sp.quyCach || '---' }}</div>
              </div>
              <div class="text-end me-3">
                <div class="fw-bold text-success">{{ formatCurrency(sp.giaBan || sp.price) }}</div>
              </div>
              <div class="d-flex align-items-center">
                <button class="btn btn-outline-secondary btn-sm" @click="giamSoLuong(index)">
                  −
                </button>
                <span class="px-2">{{ sp.soLuong }}</span>
                <button class="btn btn-outline-secondary btn-sm" @click="tangSoLuong(index)">
                  +
                </button>
              </div>
              <i class="bi bi-trash3 ms-3 text-danger" @click="xoaSanPham(index)" role="button"></i>
            </div>

            <div class="alert alert-light d-flex align-items-center gap-2">
              <i class="bi bi-percent text-primary"></i>
              Giảm ngay 20% áp dụng đến 31/05
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="p-3 bg-white rounded shadow-sm">
            <div class="d-flex justify-content-between">
              <strong>Tổng tiền</strong>
              <span>{{ formatCurrency(tongTien) }}</span>
            </div>
            <div class="d-flex justify-content-between">
              <span>Giảm giá trực tiếp</span>
              <span class="text-danger">−{{ formatCurrency(giamGia) }}</span>
            </div>
            <div class="d-flex justify-content-between fw-semibold mt-2">
              <span>Tiết kiệm được</span>
              <span class="text-success">{{ formatCurrency(giamGia) }}</span>
            </div>
            <hr />
            <div class="d-flex justify-content-between align-items-center">
              <small class="text-muted text-decoration-line-through">{{
                formatCurrency(tongTien)
              }}</small>
              <span class="fw-bold fs-5 text-success">{{ formatCurrency(tongTienSauGiam) }}</span>
            </div>

            <button class="btn btn-green w-100 mt-3" @click="thanhToan">Mua hàng</button>

            <p class="small text-muted mt-2">
              Bằng việc tiến hành đặt mua hàng, bạn đồng ý với
              <a href="#">Điều khoản dịch vụ</a> và
              <a href="#">Chính sách xử lý dữ liệu cá nhân</a> của Nhà thuốc.
            </p>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from '../User/Header.vue'
import Navbar from '../User/Navbar.vue'
import Footer from '../User/Footer.vue'
import CartStore from './CartStore.js'
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const gioHang = ref([])
const storageKey = ref('cart_temp') // tạo key riêng cho mỗi user

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  storageKey.value = maKH ? `cart_${maKH}` : 'cart_temp'

  const cart = JSON.parse(localStorage.getItem(storageKey.value)) || []
  gioHang.value = cart
})

function formatCurrency(v) {
  return v?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || ''
}

const tongTien = computed(() =>
  gioHang.value.reduce((tong, sp) => {
    const price = sp.giaBan || sp.price || 0
    return tong + sp.soLuong * price
  }, 0),
)

const giamGia = computed(() => Math.round(tongTien.value * 0.2))
const tongTienSauGiam = computed(() => tongTien.value - giamGia.value)

function giamSoLuong(index) {
  if (gioHang.value[index].soLuong > 1) {
    gioHang.value[index].soLuong--
    capNhatLocal()
  }
}

function tangSoLuong(index) {
  gioHang.value[index].soLuong++
  capNhatLocal()
}

function xoaSanPham(index) {
  gioHang.value.splice(index, 1)
  capNhatLocal()
}

function capNhatLocal() {
  localStorage.setItem(storageKey.value, JSON.stringify(gioHang.value))
  CartStore.updateCount() // ✅ Cập nhật lại số lượng trên Header
}

function thanhToan() {
  router.push('/thong-tin-dat-hang')
}
</script>

<style scoped>
.cart-page {
  font-family: 'Segoe UI', sans-serif;
}
.back-link {
  display: inline-block;
  background-color: #2e7d32;
  color: white;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 6px;
  text-decoration: none;
  transition: background-color 0.2s ease;
}
.back-link:hover {
  background-color: #1b5e20;
}
.btn-green {
  background-color: #2e7d32;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}
.btn-green:hover {
  background-color: #1b5e20;
}
</style>
