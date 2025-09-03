// ✅ src/stores/CartStore.js
import { reactive } from 'vue'

// Trả về key lưu giỏ hàng theo trạng thái đăng nhập
function getStorageKey() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  return maKH ? `cart_${maKH}` : 'cart_temp'
}

// Lấy dữ liệu giỏ hàng từ localStorage
function getCartData() {
  const key = getStorageKey()
  return JSON.parse(localStorage.getItem(key)) || []
}

export const CartStore = reactive({
  cartCount: 0,

  // ✅ Khởi tạo số lượng giỏ hàng
  init() {
    const cart = getCartData()
    this.cartCount = cart.reduce((sum, item) => sum + (item.soLuong || 1), 0)
  },

  // ✅ Gọi lại init() để cập nhật số lượng
  updateCount() {
    this.init()
  },

  // ✅ Xóa giỏ hàng hiện tại
  clearCart() {
    const key = getStorageKey()
    localStorage.removeItem(key)
    this.cartCount = 0
  },
})

export default CartStore
