<template>
  <div>
    <Header />
    <Navbar />

    <div class="checkout-wrapper">
      <div class="go-back-btn mb-3">
        <router-link to="/gio-hang" class="btn btn-outline-success">
          <i class="bi bi-arrow-left-circle me-2"></i>Quay lại giỏ hàng
        </router-link>
      </div>

      <div class="checkout-form card p-4">
        <h5 class="fw-bold mb-4">Xác nhận đơn hàng</h5>

        <!-- Danh sách sản phẩm -->
        <h6 class="mb-3">Sản phẩm đã chọn</h6>
        <div
          class="product-item d-flex align-items-center mb-3 p-3 bg-light rounded"
          v-for="item in cartItems"
          :key="item.maBienThe"
        >
          <img :src="item.thumbnails?.[0] || item.img" class="product-img me-3" alt="Sản phẩm" />
          <div class="flex-grow-1">
            <h6 class="mb-1">{{ item.tenSP || item.name }}</h6>
            <div class="text-muted small">{{ item.quyCach || '---' }}</div>
          </div>
          <div class="text-end">
            <div class="fw-bold text-success">{{ formatPrice(item.giaBan || item.price) }}</div>
            <div class="text-muted">SL: {{ item.soLuong }}</div>
          </div>
        </div>

        <!-- Tổng tiền -->
        <div class="text-end mt-3">
          <h5 class="fw-bold">
            Tổng tiền: <span class="text-success">{{ formatPrice(totalPrice) }}</span>
          </h5>
        </div>

        <!-- Thông tin người nhận -->
        <h6 class="mt-4 mb-2">Thông tin người nhận</h6>
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <input v-model="form.name" type="text" class="form-control" placeholder="Họ tên" />
          </div>
          <div class="col-md-6">
            <input v-model="form.phone" type="text" class="form-control" placeholder="SĐT" />
          </div>
          <div class="col-12">
            <input
              v-model="form.address"
              type="text"
              class="form-control"
              placeholder="Địa chỉ nhận hàng"
            />
          </div>
        </div>

        <!-- Phương thức thanh toán -->
        <h6 class="mb-2">Phương thức thanh toán</h6>
        <div class="form-check mb-2" v-for="m in paymentMethods" :key="m.value">
          <input
            class="form-check-input"
            type="radio"
            name="payment"
            :value="m.value"
            v-model="form.payment"
            :id="m.value"
          />
          <label class="form-check-label" :for="m.value">{{ m.label }}</label>
        </div>

        <!-- Ghi chú -->
        <textarea
          v-model="form.note"
          class="form-control mt-3"
          rows="2"
          placeholder="Ghi chú (tuỳ chọn)"
        ></textarea>

        <!-- Nút đặt hàng -->
        <button class="btn btn-success w-100 mt-4" @click="datHang">
          <i class="bi bi-cart-check me-2"></i>Hoàn tất đặt hàng
        </button>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import Header from '../User/Header.vue'
import Navbar from '../User/Navbar.vue'
import Footer from '../User/Footer.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const cartItems = ref([])
const storageKey = ref('cart_temp')

const form = ref({
  name: '',
  phone: '',
  address: '',
  payment: 'cod',
  note: '',
})

const paymentMethods = [
  { value: 'cod', label: 'Thanh toán khi nhận hàng (COD)' },
  { value: 'online', label: 'Thanh toán Online (VNPay, Momo...)' },
]

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  storageKey.value = maKH ? `cart_${maKH}` : 'cart_temp'

  // Load giỏ hàng từ localStorage theo đúng user
  const cart = JSON.parse(localStorage.getItem(storageKey.value)) || []
  cartItems.value = cart

  // Nếu đã đăng nhập thì tự động đổ thông tin
  if (userInfo) {
    form.value.name = userInfo.hoTen || ''
    form.value.phone = userInfo.soDienThoai || ''
    form.value.address = userInfo.diaChi || ''
  }
})

// Tính tổng tiền chính xác tránh NaN
const totalPrice = computed(() =>
  cartItems.value.reduce((total, sp) => {
    const price = sp.giaBan || sp.price || 0
    return total + sp.soLuong * price
  }, 0),
)

function formatPrice(val) {
  return val?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || ''
}

function datHang() {
  if (!form.value.name || !form.value.phone || !form.value.address) {
    alert('❗ Vui lòng điền đầy đủ thông tin!')
    return
  }

  // Tạm thời hiển thị alert cho demo
  alert(`✅ Đặt hàng thành công!\nHình thức: ${form.value.payment}`)

  // Xóa giỏ hàng sau khi đặt
  localStorage.removeItem(storageKey.value)

  // Chuyển hướng
  router.push('/dat-hang-thanh-cong')
}
</script>

<style scoped>
.checkout-wrapper {
  max-width: 720px;
  margin: auto;
  padding: 40px 20px;
}
.product-img {
  width: 70px;
  height: 70px;
  object-fit: contain;
  border-radius: 6px;
}
</style>
