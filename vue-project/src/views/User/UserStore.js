// UserStore.js
import { reactive } from 'vue'

const stored = localStorage.getItem('userInfo')
export const UserStore = reactive({
  userInfo: stored ? JSON.parse(stored) : null,

  setUser(user) {
    this.userInfo = user
    localStorage.setItem('userInfo', JSON.stringify(user))
  },

  clearUser() {
    // ❌ KHÔNG xóa giỏ hàng theo maKH
    this.userInfo = null
    localStorage.removeItem('userInfo')
  },

  isAdmin() {
    return this.userInfo?.vaiTro === 1
  },

  isNhanVien() {
    return this.userInfo?.vaiTro === 2
  },

  isKhachHang() {
    return this.userInfo?.vaiTro === 3
  },
})
