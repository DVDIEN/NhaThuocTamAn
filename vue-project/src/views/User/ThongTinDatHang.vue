<template>
  <div>
    <Header />
    <Navbar />

    <div class="checkout-wrapper">
      <div class="checkout-form card p-4">
        <h5 class="fw-bold mb-4">Xác nhận đơn hàng</h5>

        <!-- Danh sách sản phẩm -->
        <div
          v-for="item in cartItems"
          :key="item.maBienThe || item.maThuoc"
          class="product-item d-flex align-items-center mb-3 p-2 bg-light rounded"
        >
          <img :src="getImageUrl(item.hinhAnh)" class="product-img me-3" />
          <div class="flex-grow-1">
            <h6 class="mb-1">{{ item.tenThuoc }}</h6>
            <small class="text-muted">SL: {{ item.soLuong }}</small>
          </div>
          <div class="fw-bold text-success">{{ formatPrice(item.giaBan) }}</div>
        </div>

        <!-- Tổng tiền -->
        <div class="text-end my-2">
          <p>
            Tạm tính: <strong>{{ formatPrice(tongTienGoc) }}</strong>
          </p>
          <p>
            Phí vận chuyển:
            <strong class="text-dark">
              {{ selectedPTVC ? formatPrice(selectedPTVC.phiVanChuyen) : '0 ₫' }}
            </strong>
          </p>
          <p v-if="appliedVoucher">
            Giảm giá:
            <strong class="text-danger">-{{ formatPrice(tienGiamGia) }}</strong>
          </p>
          <h5 class="fw-bold">
            Tổng cộng:
            <span class="text-success">{{ formatPrice(totalThanhToan) }}</span>
          </h5>
        </div>

        <!-- Mã giảm giá -->
        <h6 class="mb-2">Mã giảm giá</h6>
        <div class="input-group mb-3">
          <input
            v-model="selectedCode"
            class="form-control"
            placeholder="Nhấn để chọn mã giảm giá"
            readonly
          />
          <button class="btn btn-outline-success" @click="showVoucherModal = true">Chọn mã</button>
        </div>

        <div v-if="appliedVoucher" class="alert alert-success py-2">
          🎉 Đã áp dụng mã <strong>{{ appliedVoucher.maCode }}</strong> –
          {{
            appliedVoucher.loaiKM === 'percent'
              ? `Giảm ${appliedVoucher.giaTri}%`
              : `Giảm ${formatPrice(appliedVoucher.giaTri)}`
          }}
          <button class="btn btn-sm btn-link text-danger ms-2" @click="huyVoucher">❌ Hủy</button>
        </div>

        <!-- Phương thức vận chuyển -->
        <h6 class="mb-2 mt-3 d-flex justify-content-between align-items-center">
          <span>Phương thức vận chuyển</span>
          <a href="javascript:void(0)" @click="showPTVCModal = true" class="no-underline"
            >Xem tất cả</a
          >
        </h6>
        <div v-if="selectedPTVC" class="border rounded p-3 bg-light">
          <div class="d-flex justify-content-between align-items-center mb-1">
            <strong>{{ selectedPTVC.tenPTVC }}</strong>
            <span class="fw-bold text-success">
              {{
                selectedPTVC.phiVanChuyen === 0
                  ? 'Miễn phí'
                  : formatPrice(selectedPTVC.phiVanChuyen)
              }}
            </span>
          </div>
          <div class="text-muted small">Dự kiến: {{ selectedPTVC.thoiGianDuKien }}</div>
        </div>

        <!-- Thông tin người nhận -->
        <h6 class="mb-2 mt-3">Thông tin người nhận</h6>
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <input v-model="form.name" class="form-control" placeholder="Họ tên" />
          </div>
          <div class="col-md-6">
            <input v-model="form.phone" class="form-control" placeholder="SĐT" />
          </div>
          <div class="col-12">
            <input v-model="form.email" type="email" class="form-control" placeholder="Email" />
          </div>
          <div class="col-12">
            <input v-model="form.address" class="form-control" placeholder="Địa chỉ nhận hàng" />
          </div>
        </div>

        <!-- Phương thức thanh toán -->
        <h6 class="mb-2">Phương thức thanh toán</h6>
        <div
          class="form-check d-flex align-items-center mb-2"
          v-for="method in paymentMethods"
          :key="method.value"
        >
          <input
            class="form-check-input me-2"
            type="radio"
            :id="method.value"
            v-model="form.payment"
            :value="method.value"
          />
          <label class="form-check-label d-flex align-items-center" :for="method.value">
            <img
              :src="method.logo"
              alt="logo"
              class="me-2"
              style="width: 32px; height: 32px; object-fit: contain"
            />
            {{ method.label }}
          </label>
        </div>

        <textarea
          v-model="form.note"
          class="form-control mt-3"
          rows="2"
          placeholder="Ghi chú (tuỳ chọn)"
        ></textarea>

        <!-- Nút đặt hàng -->
        <button class="btn btn-success w-100 mt-4" @click="datHang">
          <i class="bi bi-cart-check me-2"></i> Hoàn tất đặt hàng
        </button>
      </div>
    </div>

    <!-- Modal chọn mã -->
    <div
      v-if="showVoucherModal"
      class="modal fade show d-block"
      tabindex="-1"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chọn mã giảm giá</h5>
            <button type="button" class="btn-close" @click="showVoucherModal = false"></button>
          </div>
          <div class="modal-body p-0">
            <ul class="list-group">
              <li
                v-for="voucher in filteredVouchers"
                :key="voucher.maKM"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                :class="{
                  'active-voucher': appliedVoucher && appliedVoucher.maKM === voucher.maKM,
                }"
                @click="chonVoucher(voucher)"
                style="cursor: pointer"
              >
                <div>
                  <div class="fw-bold">{{ voucher.maCode }}</div>
                  <div class="small">
                    {{
                      voucher.loaiKM === 'percent'
                        ? `Giảm ${voucher.giaTri}%`
                        : `Giảm ${formatPrice(voucher.giaTri)}`
                    }}
                    – Tối thiểu: {{ formatPrice(voucher.donHangToiThieu || 0) }} – HSD:
                    {{ new Date(voucher.ngayKetThuc).toLocaleDateString() }}
                  </div>
                </div>
                <i
                  v-if="appliedVoucher && appliedVoucher.maKM === voucher.maKM"
                  class="bi bi-check-circle-fill text-success fs-5"
                ></i>
              </li>
            </ul>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showVoucherModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal chọn PTVC -->
    <div
      v-if="showPTVCModal"
      class="modal fade show d-block"
      tabindex="-1"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chọn phương thức vận chuyển</h5>
            <button type="button" class="btn-close" @click="showPTVCModal = false"></button>
          </div>
          <div class="modal-body p-0">
            <ul class="list-group">
              <li
                v-for="pt in ptvcList"
                :key="pt.maPTVC"
                class="list-group-item list-group-item-action ptvc-item"
                style="cursor: pointer"
                @click="chonPTVC(pt)"
              >
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <div class="fw-bold">{{ pt.tenPTVC }}</div>
                    <small class="text-muted">Dự kiến: {{ pt.thoiGianDuKien }}</small>
                  </div>
                  <div class="fw-bold text-success">
                    {{ pt.phiVanChuyen === 0 ? 'Miễn phí' : formatPrice(pt.phiVanChuyen) }}
                  </div>
                </div>
              </li>
            </ul>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showPTVCModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Overlay Loading -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-light" role="status" style="width: 3rem; height: 3rem">
        <span class="visually-hidden">Loading...</span>
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
import axios from 'axios'
import { useRouter } from 'vue-router'
import CartStore from './CartStore'

const isLoading = ref(false)
const router = useRouter()
const form = ref({ name: '', phone: '', address: '', email: '', payment: 'cod', note: '' })

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const storageKey = ref(userInfo.value?.maKH ? `cart_${userInfo.value.maKH}` : 'cart_temp')

const cartItems = ref([])
const allVouchers = ref([])
const ptvcList = ref([])
const selectedCode = ref('')
const appliedVoucher = ref(null)
const selectedPTVC = ref(null)

const showVoucherModal = ref(false)
const showPTVCModal = ref(false)

onMounted(async () => {
  cartItems.value = JSON.parse(localStorage.getItem('selectedItems')) || []

  const khachHangId = userInfo.value?.maKH
  if (khachHangId) {
    try {
      const res = await axios.get(`http://localhost:8080/api/khachhang/${khachHangId}`)
      const fullInfo = res.data
      form.value.name = fullInfo.hoTen
      form.value.phone = fullInfo.soDienThoai
      form.value.address = fullInfo.diaChi
      form.value.email = fullInfo.email
    } catch (err) {
      console.error('Lỗi lấy thông tin KH:', err)
    }
  }

  axios
    .get('http://localhost:8080/api/khuyen-mai')
    .then((res) => (allVouchers.value = res.data))
    .catch(() => (allVouchers.value = []))

  // ✅ Load phương thức vận chuyển
  try {
    const res = await axios.get('http://localhost:8080/api/phuong-thuc-van-chuyen/active')
    ptvcList.value = res.data || []
    if (ptvcList.value.length > 0) {
      selectedPTVC.value = ptvcList.value.reduce((min, cur) =>
        cur.phiVanChuyen < min.phiVanChuyen ? cur : min,
      )
    }
  } catch (err) {
    console.error('Lỗi tải PTVC:', err)
  }
})

const paymentMethods = [
  {
    value: 'cod',
    label: 'Thanh toán khi nhận hàng (COD)',
    logo: 'data:image/jpeg;base64,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', // icon COD
  },
  {
    value: 'vnpay',
    label: 'Thanh toán VNPay',
    logo: 'https://vinadesign.vn/uploads/images/2023/05/vnpay-logo-vinadesign-25-12-57-55.jpg', // logo VNPay
  },
  {
    value: 'momo',
    label: 'Thanh toán MoMo',
    logo: 'https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png', // logo MoMo
  },
]

const tongTienGoc = computed(() =>
  cartItems.value.reduce((sum, sp) => sum + sp.soLuong * (sp.giaBan || 0), 0),
)

const tienGiamGia = computed(() => {
  if (!appliedVoucher.value) return 0
  const v = appliedVoucher.value
  const giam = v.loaiKM === 'percent' ? (tongTienGoc.value * v.giaTri) / 100 : v.giaTri
  return v.giaTriToiDa ? Math.min(giam, v.giaTriToiDa) : giam
})

const totalThanhToan = computed(() => {
  const phi = selectedPTVC.value?.phiVanChuyen || 0
  return tongTienGoc.value - tienGiamGia.value + phi
})

const filteredVouchers = computed(() =>
  allVouchers.value.filter(
    (v) =>
      new Date(v.ngayBatDau) <= new Date() &&
      new Date(v.ngayKetThuc) >= new Date() &&
      v.soLuong - (v.daSuDung || 0) > 0 &&
      tongTienGoc.value >= (v.donHangToiThieu || 0),
  ),
)

function chonVoucher(voucher) {
  appliedVoucher.value = voucher
  selectedCode.value = voucher.maCode
  showVoucherModal.value = false
}

function huyVoucher() {
  appliedVoucher.value = null
  selectedCode.value = ''
}

function chonPTVC(pt) {
  selectedPTVC.value = pt
  showPTVCModal.value = false
}

function formatPrice(val) {
  return typeof val === 'number'
    ? val.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
    : '0 ₫'
}

function getImageUrl(path) {
  if (!path) return 'https://cdn-icons-png.flaticon.com/512/891/891462.png'
  return path.startsWith('http')
    ? path
    : `http://localhost:8080/${path.startsWith('uploads/') ? path : 'uploads/' + path}`
}

async function datHang() {
  if (!form.value.name || !form.value.phone || !form.value.address) {
    alert('❗ Vui lòng điền đầy đủ thông tin nhận hàng!')
    return
  }

  isLoading.value = true
  try {
    const dto = {
      maKH: userInfo.value?.maKH || null,
      maKM: appliedVoucher.value?.maKM || null,
      maPTVC: selectedPTVC.value?.maPTVC || null,
      diaChiNhan: form.value.address,
      soDienThoaiNhan: form.value.phone,
      hoTenNguoiNhan: form.value.name,
      emailNguoiNhan: form.value.email,
      ghiChu: form.value.note,
      hinhThucThanhToan: form.value.payment,
      daThanhToan: form.value.payment !== 'cod',
      chiTiet: cartItems.value.map((sp) => ({
        maThuoc: sp.maThuoc || null,
        maBienThe: sp.maBienThe || null,
        soLuong: sp.soLuong,
        donGia: Number(sp.giaBan),
      })),
    }

    const tongTien = totalThanhToan.value

    if (form.value.payment === 'cod') {
      const res = await axios.post('http://localhost:8080/api/donhang/create', dto)
      const maDonHang = res.data?.maDonHang
      const allCart = JSON.parse(localStorage.getItem(storageKey.value) || '[]')
      const remaining = allCart.filter(
        (item) =>
          !cartItems.value.some(
            (sp) =>
              sp.maThuoc === item.maThuoc && (sp.maBienThe || null) === (item.maBienThe || null),
          ),
      )
      localStorage.setItem(storageKey.value, JSON.stringify(remaining))
      localStorage.removeItem('selectedItems')
      CartStore.updateCount()

      alert('✅ Đặt hàng thành công (COD)!')
      if (!userInfo.value?.maKH) localStorage.setItem('guestOrderCode', maDonHang)
      router.push('/dat-hang-thanh-cong')
      return
    }

    const apiUrl =
      form.value.payment === 'vnpay'
        ? 'http://localhost:8080/api/thanh-toan/vnpay'
        : 'http://localhost:8080/api/thanh-toan/momo'

    const res = await axios.post(apiUrl, { donHangDTO: dto, tongTien })
    const paymentUrl = form.value.payment === 'vnpay' ? res.data.paymentUrl : res.data.payUrl

    if (paymentUrl) {
      const allCart = JSON.parse(localStorage.getItem(storageKey.value) || '[]')
      const remaining = allCart.filter(
        (item) =>
          !cartItems.value.some(
            (sp) =>
              sp.maThuoc === item.maThuoc && (sp.maBienThe || null) === (item.maBienThe || null),
          ),
      )
      localStorage.setItem(storageKey.value, JSON.stringify(remaining))
      localStorage.removeItem('selectedItems')
      CartStore.updateCount()

      window.location.href = paymentUrl
    } else {
      alert('❌ Không thể tạo URL thanh toán!')
    }
  } catch (err) {
    console.error('❌ Lỗi đặt hàng:', err)
    alert('❌ Không thể đặt hàng!')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.checkout-wrapper {
  max-width: 700px;
  margin: auto;
  padding: 40px 20px;
}
.product-img {
  width: 70px;
  height: 70px;
  object-fit: contain;
  border-radius: 6px;
}
.modal {
  z-index: 1050;
}
.active-voucher {
  background-color: #d1f7d1 !important;
  border-left: 4px solid #28a745;
}
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.no-underline {
  text-decoration: none !important;
  color: #000 !important; /* chữ đen */
}
.no-underline:hover {
  color: #000 !important; /* khi hover cũng giữ đen */
}
</style>
