<template>
  <div class="container my-5">
    <h3 class="text-center text-success mb-4">🛒 TẠO ĐƠN HÀNG</h3>

    <!-- Tra cứu khách hàng -->
    <div class="mb-3">
      <input
        v-model="searchKey"
        @keyup.enter="timKhachHang"
        @blur="timKhachHang"
        class="form-control"
        placeholder="🔍 Nhập số điện thoại hoặc email khách hàng (nếu có)..."
      />
    </div>
    <div v-if="form.maKH" class="alert alert-info">
      ✅ Đã tự động điền thông tin từ tài khoản khách hàng.
    </div>

    <!-- Thông tin người nhận -->
    <div class="card p-4 mb-4 shadow-sm">
      <h6 class="fw-bold mb-3">Thông tin người nhận</h6>
      <div class="row g-3 mb-3">
        <div class="col-md-6">
          <input v-model="form.name" class="form-control" placeholder="Họ tên khách hàng" />
        </div>
        <div class="col-md-6">
          <input v-model="form.phone" class="form-control" placeholder="Số điện thoại" />
        </div>
      </div>
      <div class="row g-3">
        <div class="col-md-6">
          <input
            v-model="form.email"
            class="form-control"
            placeholder="Email người nhận (nếu có)"
          />
        </div>
        <div class="col-md-6">
          <input v-model="form.address" class="form-control" placeholder="Địa chỉ nhận hàng" />
        </div>
      </div>
    </div>

    <!-- Chọn sản phẩm -->
    <div class="card p-4 mb-4 shadow-sm">
      <h6 class="fw-bold mb-3">Chọn sản phẩm</h6>
      <input v-model="keyword" class="form-control mb-3" placeholder="🔍 Tìm sản phẩm..." />
      <div class="table-responsive" style="max-height: 300px; overflow-y: auto">
        <table class="table table-bordered text-center align-middle">
          <thead class="table-light">
            <tr>
              <th>Tên thuốc</th>
              <th>Giá bán</th>
              <th>SL</th>
              <th>Thêm</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sp in filteredProducts" :key="sp.maThuoc || sp.maBienThe">
              <td>{{ sp.tenThuoc }}</td>
              <td>{{ formatPrice(sp.giaBan) }}</td>
              <td>
                <input type="number" v-model.number="sp.soLuong" min="1" class="form-control" />
              </td>
              <td>
                <button class="btn btn-success btn-sm" @click="themVaoDon(sp)">+</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Giỏ hàng -->
    <div v-if="cart.length" class="card p-4 mb-4 shadow-sm">
      <h6 class="fw-bold mb-3">Sản phẩm đã chọn</h6>
      <ul class="list-group">
        <li
          class="list-group-item d-flex justify-content-between align-items-center"
          v-for="(item, index) in cart"
          :key="item.maThuoc || item.maBienThe"
        >
          <div>
            {{ item.tenThuoc }} × {{ item.soLuong }}
            <span class="text-muted small ms-2">({{ formatPrice(item.giaBan) }})</span>
          </div>
          <div class="d-flex align-items-center gap-2">
            <span class="fw-bold">{{ formatPrice(item.giaBan * item.soLuong) }}</span>
            <button class="btn btn-sm btn-outline-danger" @click="xoaSanPham(index)">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </li>
      </ul>
      <div class="text-end mt-3 fw-bold">Tổng tiền: {{ formatPrice(tongTien) }}</div>
    </div>

    <!-- Ghi chú và thanh toán -->
    <div class="card p-4 shadow-sm">
      <textarea
        v-model="form.note"
        class="form-control mb-3"
        rows="2"
        placeholder="Ghi chú đơn hàng..."
      ></textarea>

      <div class="form-check mb-3">
        <input type="checkbox" class="form-check-input" id="paid" v-model="form.daThanhToan" />
        <label class="form-check-label" for="paid">Khách đã thanh toán</label>
      </div>

      <button class="btn btn-success w-100" @click="taoDon">Tạo đơn</button>
    </div>

    <!-- Overlay toàn trang -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner-border text-light" style="width: 3rem; height: 3rem"></div>
    </div>

    <ToastMessage ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import ToastMessage from '../ToastMessage.vue'

const toastRef = ref()
const searchKey = ref('')
const keyword = ref('')
const products = ref([])
const cart = ref([])
const isLoading = ref(false) // ✅ loading toàn trang

const form = ref({
  maKH: null,
  name: '',
  phone: '',
  address: '',
  email: '',
  note: '',
  daThanhToan: false,
})

onMounted(async () => {
  try {
    isLoading.value = true
    const [thuocRes, bienTheRes] = await Promise.all([
      axios.get('http://localhost:8080/api/thuoc'),
      axios.get('http://localhost:8080/api/bienthe'),
    ])

    const thuocList = thuocRes.data.map((p) => ({
      ...p,
      soLuong: 1,
      maThuoc: p.maThuoc,
      maBienThe: null,
      tenThuoc: p.tenThuoc,
      giaBan: p.giaBan,
    }))

    const bienTheList = bienTheRes.data.map((bt) => ({
      ...bt,
      soLuong: 1,
      maThuoc: bt.maThuoc,
      maBienThe: bt.maBienThe,
      tenThuoc: bt.tenBienThe, // ✅ chỉ hiển thị tên biến thể
      giaBan: bt.giaBan,
    }))

    products.value = [...thuocList, ...bienTheList]
  } catch (err) {
    console.error('❌ Lỗi khi lấy danh sách sản phẩm:', err)
  } finally {
    isLoading.value = false
  }
})

const filteredProducts = computed(() =>
  products.value.filter((p) => p.tenThuoc.toLowerCase().includes(keyword.value.toLowerCase())),
)

const tongTien = computed(() =>
  cart.value.reduce((sum, item) => sum + item.giaBan * item.soLuong, 0),
)

function formatPrice(val) {
  return val?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || '0 ₫'
}

async function themVaoDon(sp) {
  try {
    isLoading.value = true
    const res = await axios.get('http://localhost:8080/api/kho-hang/so-luong-ton', {
      params: {
        maThuoc: sp.maThuoc || null,
        maBienThe: sp.maBienThe || null,
      },
    })
    const tonKho = res.data

    if (tonKho === 0) {
      toastRef.value.show(`❌ <strong>${sp.tenThuoc}</strong> đã hết hàng.`, 'danger')
      return
    }

    if (sp.soLuong > tonKho) {
      toastRef.value.show(
        `❗ <strong>${sp.tenThuoc}</strong> chỉ còn <strong>${tonKho}</strong> sản phẩm.`,
        'warning',
      )
      return
    }

    const existed = cart.value.find((i) => i.maThuoc === sp.maThuoc && i.maBienThe === sp.maBienThe)
    if (existed) {
      const newSoLuong = existed.soLuong + sp.soLuong
      if (newSoLuong > tonKho) {
        toastRef.value.show(`❌ Số lượng vượt quá tồn kho.`, 'danger')
        return
      }
      existed.soLuong = newSoLuong
      toastRef.value.show(`✅ Đã cập nhật số lượng cho <strong>${sp.tenThuoc}</strong>.`, 'info')
    } else {
      cart.value.push({ ...sp })
      toastRef.value.show(`✅ Đã thêm <strong>${sp.tenThuoc}</strong> vào đơn.`, 'success')
    }

    sp.soLuong = 1
  } catch (err) {
    console.error(err)
    toastRef.value.show(`❌ Không kiểm tra được tồn kho.`, 'danger')
  } finally {
    isLoading.value = false
  }
}

function xoaSanPham(index) {
  const item = cart.value[index]
  const ten = item.tenThuoc || 'sản phẩm'
  const ok = confirm(`Bạn có chắc muốn xoá "${ten}" khỏi đơn hàng?`)
  if (ok) {
    cart.value.splice(index, 1)
    toastRef.value?.show(`🗑 Đã xoá <strong>${ten}</strong> khỏi giỏ hàng.`, 'info')
  }
}

async function taoDon() {
  if (!form.value.name || !form.value.phone || !form.value.address) {
    toastRef.value.show('❗ Vui lòng nhập đầy đủ thông tin khách hàng.', 'warning')
    return
  }

  if (!cart.value.length) {
    toastRef.value.show('❗ Vui lòng chọn ít nhất 1 sản phẩm.', 'warning')
    return
  }

  const dto = {
    maKH: form.value.maKH,
    hoTenNguoiNhan: form.value.name,
    soDienThoaiNhan: form.value.phone,
    diaChiNhan: form.value.address,
    emailNguoiNhan: form.value.email,
    ghiChu: form.value.note,
    daThanhToan: form.value.daThanhToan,
    hinhThucThanhToan: form.value.daThanhToan ? 'ol' : 'cod',
    chiTiet: cart.value.map((item) => ({
      maThuoc: item.maThuoc,
      maBienThe: item.maBienThe,
      soLuong: item.soLuong,
      donGia: item.giaBan,
    })),
  }

  try {
    isLoading.value = true
    await axios.post('http://localhost:8080/api/donhang/create', dto)
    toastRef.value.show('✅ Đặt hàng thành công!', 'success')
    cart.value = []
    Object.assign(form.value, {
      maKH: null,
      name: '',
      phone: '',
      address: '',
      email: '',
      note: '',
      daThanhToan: false,
    })
    window.scrollTo(0, 0)
  } catch (err) {
    console.error(err)
    toastRef.value.show('❌ Không thể tạo đơn hàng.', 'danger')
  } finally {
    isLoading.value = false
  }
}

async function timKhachHang() {
  const key = searchKey.value.trim()
  if (!key) return

  try {
    isLoading.value = true
    const res = await axios.get(
      `http://localhost:8080/api/khachhang/tim?key=${encodeURIComponent(key)}`,
    )
    const kh = res.data
    Object.assign(form.value, {
      maKH: kh.maKH,
      name: kh.hoTen,
      phone: kh.soDienThoai,
      address: kh.diaChi,
      email: kh.email,
    })
    toastRef.value.show('✅ Đã tìm thấy thông tin khách hàng.', 'info')
  } catch (err) {
    form.value.maKH = null
    toastRef.value.show('⚠️ Không tìm thấy khách hàng.', 'warning')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
input[type='number'] {
  width: 80px;
  margin: auto;
}
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
</style>
