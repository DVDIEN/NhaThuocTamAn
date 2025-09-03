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
            <div
              class="row fw-bold text-secondary border-bottom pb-2 mb-3 align-items-center text-center"
            >
              <div class="col-md-5 text-start d-flex align-items-center">
                <input
                  type="checkbox"
                  class="form-check-input me-2"
                  v-model="chonTatCa"
                  @change="toggleTatCa"
                />
                <span>Chọn tất cả</span>
              </div>
              <div class="col-md-2">Giá</div>
              <div class="col-md-2">Số lượng</div>
              <div class="col-md-2">Đơn vị</div>
              <div class="col-md-1"></div>
            </div>

            <div
              v-for="(sp, index) in gioHang"
              :key="index"
              class="row mb-3 align-items-center text-center"
            >
              <div
                class="col-md-5 text-start d-flex align-items-center"
                :class="{ 'opacity-50': trangThaiTonKho[index]?.hetHang }"
              >
                <input
                  type="checkbox"
                  class="form-check-input me-2"
                  v-model="dsDaChon[index]"
                  :disabled="trangThaiTonKho[index]?.hetHang"
                />
                <img
                  :src="getImageUrl(sp.thongTin?.hinhAnh || sp.thongTin?.hinhAnhChinh)"
                  alt="Sản phẩm"
                  style="width: 60px; height: 60px; object-fit: contain"
                  class="me-2"
                />
                <div>
                  <div class="fw-semibold">
                    {{ sp.thongTin?.tenThuoc }}
                    <span v-if="sp.thongTin?.moTaQuyCach"> - {{ sp.thongTin?.moTaQuyCach }}</span>
                  </div>

                  <div class="text-danger small" v-if="trangThaiTonKho[index]?.hetHang">
                    (Sản phẩm đã hết hàng)
                  </div>
                  <div class="text-warning small" v-else-if="trangThaiTonKho[index]?.vuotTon">
                    (Vượt tồn kho: còn {{ trangThaiTonKho[index]?.tonKho }})
                  </div>
                </div>
              </div>

              <div class="col-md-2 fw-bold text-success">
                {{ formatCurrency(sp.thongTin?.giaBan) }}
              </div>

              <div class="col-md-2 d-flex justify-content-center align-items-center gap-2">
                <div class="d-flex align-items-center border rounded px-1">
                  <button
                    class="btn btn-sm btn-outline-secondary border-0"
                    @click="giamSoLuong(index)"
                  >
                    −
                  </button>
                  <span class="px-2">{{ sp.soLuong }}</span>
                  <button
                    class="btn btn-sm btn-outline-secondary border-0"
                    @click="tangSoLuong(index)"
                  >
                    +
                  </button>
                </div>
              </div>

              <div class="col-md-2">
                <span>{{ sp.thongTin?.tenDonViTinh }}</span>
              </div>

              <div class="col-md-1">
                <i
                  class="bi bi-trash3 text-danger"
                  role="button"
                  title="Xóa sản phẩm"
                  @click="xoaSanPham(index)"
                ></i>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="p-3 bg-white rounded shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <strong>Tổng tiền</strong>
              <span class="fs-5 text-success fw-bold">{{ formatCurrency(tongTien) }}</span>
            </div>

            <button
              class="btn btn-green w-100 mt-3"
              @click="thanhToan"
              :disabled="sanPhamDaChon.length === 0"
            >
              Mua hàng
            </button>

            <p class="small text-muted mt-2">
              Bằng việc đặt mua, bạn đồng ý với <a href="#">Điều khoản dịch vụ</a> và
              <a href="#">Điều khoản bảo mật</a>.
            </p>
          </div>
        </div>
      </div>
    </main>

    <ToastMessage ref="toastRef" />
    <Footer />
  </div>
</template>

<script setup>
import Header from '../User/Header.vue'
import Navbar from '../User/Navbar.vue'
import Footer from '../User/Footer.vue'
import ToastMessage from '../ToastMessage.vue'
import { CartStore } from './CartStore.js'
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const toastRef = ref(null)

const gioHang = ref([]) // [{ maThuoc, maBienThe, soLuong, thongTin }]
const storageKey = ref('cart_temp')
const chonTatCa = ref(false)
const dsDaChon = ref([])
const trangThaiTonKho = ref([])

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  storageKey.value = maKH ? `cart_${maKH}` : 'cart_temp'

  const rawCart = JSON.parse(localStorage.getItem(storageKey.value)) || []
  gioHang.value = await dongBoThongTin(rawCart)

  if (gioHang.value.length > 0) {
    await kiemTraTonKho()
  }
})

async function dongBoThongTin(rawCart) {
  const ketQua = []
  for (const item of rawCart) {
    try {
      if (item.maBienThe) {
        const res = await axios.get(`http://localhost:8080/api/bienthe/${item.maBienThe}`)
        if (res.data && res.data.daXoa === false && res.data.trangThai !== false) {
          ketQua.push({ ...item, thongTin: res.data })
        } else {
          console.warn('Biến thể đã xóa/ngừng bán:', item)
        }
      } else {
        const res = await axios.get(`http://localhost:8080/api/thuoc/${item.maThuoc}`)
        if (res.data && res.data.daXoa === false && res.data.trangThai !== false) {
          ketQua.push({ ...item, thongTin: res.data })
        } else {
          console.warn('Thuốc đã xóa/ngừng bán:', item)
        }
      }
    } catch (err) {
      console.warn('Không tìm thấy sản phẩm:', item)
    }
  }

  if (ketQua.length < rawCart.length) {
    capNhatLocalSauDongBo(ketQua)
    toastRef.value?.show(
      '🗑 Một số sản phẩm đã bị xóa/ngừng bán và đã gỡ khỏi giỏ hàng.',
      'warning',
    )
  }

  return ketQua
}

function capNhatLocalSauDongBo(cart) {
  const raw = cart.map((item) => ({
    maThuoc: item.maThuoc || null,
    maBienThe: item.maBienThe || null,
    soLuong: item.soLuong,
  }))
  localStorage.setItem(storageKey.value, JSON.stringify(raw))
  CartStore.updateCount()
}

function capNhatLocal() {
  const raw = gioHang.value.map((item) => ({
    maThuoc: item.maThuoc || null,
    maBienThe: item.maBienThe || null,
    soLuong: item.soLuong,
  }))
  localStorage.setItem(storageKey.value, JSON.stringify(raw))
  CartStore.updateCount()
}

function showToast(msg, type = 'success') {
  toastRef.value?.show(msg, type)
}

async function kiemTraTonKho() {
  const result = []
  for (const sp of gioHang.value) {
    try {
      const res = await axios.get('http://localhost:8080/api/kho-hang/so-luong-ton', {
        params: {
          maThuoc: sp.maBienThe ? null : sp.maThuoc,
          maBienThe: sp.maBienThe || null,
        },
      })
      const tonKho = res.data
      result.push({
        hetHang: tonKho <= 0,
        vuotTon: sp.soLuong > tonKho,
        tonKho,
      })
    } catch (err) {
      result.push({ hetHang: false, vuotTon: false, tonKho: 9999 })
    }
  }

  trangThaiTonKho.value = result
  dsDaChon.value = gioHang.value.map((_, i) => !result[i].hetHang)
  chonTatCa.value = dsDaChon.value.every(Boolean)

  if (result.some((r) => r.hetHang)) {
    showToast('⚠ Một số sản phẩm đã hết hàng và không thể thanh toán.', 'danger')
  }
}

function toggleTatCa() {
  dsDaChon.value = gioHang.value.map(
    (_, i) => !trangThaiTonKho.value[i]?.hetHang && chonTatCa.value,
  )
}

watch(
  dsDaChon,
  (val) => {
    if (val.length === 0) return
    chonTatCa.value = val.every((v, i) => v || trangThaiTonKho.value[i]?.hetHang)
  },
  { deep: true },
)

const sanPhamDaChon = computed(() => gioHang.value.filter((_, i) => dsDaChon.value[i]))

const tongTien = computed(() =>
  sanPhamDaChon.value.reduce((tong, sp) => tong + sp.soLuong * (sp.thongTin?.giaBan || 0), 0),
)

function giamSoLuong(index) {
  if (gioHang.value[index].soLuong <= 1) {
    showToast('⚠ Số lượng tối thiểu là 1.', 'warning')
    return
  }
  gioHang.value[index].soLuong--
  capNhatLocal()
  kiemTraTonKho()
}

function tangSoLuong(index) {
  const tonKho = trangThaiTonKho.value[index]?.tonKho || 9999
  if (gioHang.value[index].soLuong >= tonKho) {
    showToast(`⚠ Tồn kho chỉ còn ${tonKho}.`, 'warning')
    return
  }
  gioHang.value[index].soLuong++
  capNhatLocal()
  kiemTraTonKho()
}

function xoaSanPham(index) {
  gioHang.value.splice(index, 1)
  dsDaChon.value.splice(index, 1)
  trangThaiTonKho.value.splice(index, 1)
  capNhatLocal()
  showToast('🗑 Đã xóa sản phẩm khỏi giỏ hàng.', 'success')
}

function thanhToan() {
  if (sanPhamDaChon.value.length === 0) {
    showToast('❗ Chưa chọn sản phẩm nào!', 'warning')
    return
  }

  const coLoi = sanPhamDaChon.value.some((sp) => {
    const index = gioHang.value.findIndex((item) => item === sp)
    return trangThaiTonKho.value[index]?.hetHang || trangThaiTonKho.value[index]?.vuotTon
  })
  if (coLoi) {
    showToast('❌ Có sản phẩm hết/vượt kho, không thể thanh toán.', 'danger')
    return
  }

  // ✅ Chuẩn hóa dữ liệu trước khi lưu sang trang checkout
  const selected = sanPhamDaChon.value.map((sp) => ({
    maThuoc: sp.maThuoc || null,
    maBienThe: sp.maBienThe || null,
    tenThuoc: sp.thongTin?.tenThuoc || '',
    moTaQuyCach: sp.thongTin?.moTaQuyCach || '',
    hinhAnh: sp.thongTin?.hinhAnh || sp.thongTin?.hinhAnhChinh || '',
    giaBan: sp.thongTin?.giaBan || 0,
    soLuong: sp.soLuong,
    tenDonViTinh: sp.thongTin?.tenDonViTinh || '',
  }))

  localStorage.setItem('selectedItems', JSON.stringify(selected))
  router.push('/thong-tin-dat-hang')
}

function formatCurrency(v) {
  return v?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || '0 ₫'
}

function getImageUrl(path) {
  return path?.startsWith('http') ? path : `http://localhost:8080/${path}`
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
}
.btn-green:hover {
  background-color: #1b5e20;
}
button.btn-sm {
  min-width: 30px;
  padding: 2px 6px;
}
.opacity-50 {
  opacity: 0.5;
}
</style>
