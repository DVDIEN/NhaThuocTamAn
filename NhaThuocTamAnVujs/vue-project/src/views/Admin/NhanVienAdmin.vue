<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">📋 Quản lý nhân viên</h4>

    <!-- Bộ lọc -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <input
              v-model="filter.hoTen"
              type="text"
              class="form-control"
              placeholder="Lọc theo họ tên..."
            />
          </div>
          <div class="col-md-3">
            <input
              v-model="filter.soDienThoai"
              type="text"
              class="form-control"
              placeholder="Lọc theo SĐT..."
            />
          </div>
          <div class="col-md-3">
            <select v-model="filter.vaiTro" class="form-select">
              <option value="">Vai trò</option>
              <option value="1">Admin</option>
              <option value="2">Nhân viên</option>
            </select>
          </div>
          <div class="col-md-3">
            <select v-model="filter.gioiTinh" class="form-select">
              <option value="">Giới tính</option>
              <option value="1">Nam</option>
              <option value="0">Nữ</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Nút thêm -->
    <div class="mb-3 text-end">
      <button class="btn btn-success" @click="openModal()">➕ Thêm nhân viên</button>
    </div>

    <!-- Bảng danh sách -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <table class="table table-bordered table-hover mb-0">
          <thead class="table-success text-center">
            <tr>
              <th>Mã NV</th>
              <th>Tên TK</th>
              <th>Email</th>
              <th>Họ tên</th>
              <th>SĐT</th>
              <th>Địa chỉ</th>
              <th>Giới tính</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th>Ảnh</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="nv in pagedData" :key="nv.maNV">
              <td>{{ nv.maNV }}</td>
              <td>{{ nv.tenTaiKhoan }}</td>
              <td>{{ nv.email }}</td>
              <td>{{ nv.hoTen }}</td>
              <td>{{ nv.soDienThoai }}</td>
              <td>{{ nv.diaChi }}</td>
              <td>{{ nv.gioiTinh === 1 ? 'Nam' : 'Nữ' }}</td>
              <td>
                <span class="badge bg-primary">{{ nv.vaiTro === 1 ? 'Admin' : 'Nhân viên' }}</span>
              </td>
              <td>
                <span :class="nv.trangThai ? 'text-success' : 'text-danger'">
                  {{ nv.trangThai ? 'Hoạt động' : 'Đã khóa' }}
                </span>
              </td>
              <td>
                <img
                  v-if="nv.avata"
                  :src="nv.avata"
                  width="40"
                  height="40"
                  class="rounded-circle"
                />
              </td>
              <td>
                <button class="btn btn-warning btn-sm me-1" @click="openModal(nv)" title="Sửa">
                  ✏️
                </button>
                <button
                  class="btn btn-sm me-1"
                  :class="nv.trangThai ? 'btn-outline-danger' : 'btn-outline-success'"
                  @click="toggleTrangThai(nv.maNV)"
                >
                  {{ nv.trangThai ? 'Khoá' : 'Mở' }}
                </button>
                <button class="btn btn-danger btn-sm" @click="xoaNhanVien(nv.maNV)">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Phân trang -->
    <div class="d-flex justify-content-between align-items-center mt-3">
      <div>Hiển thị {{ pagedData.length }} / {{ danhSachLoc.length }} nhân viên</div>
      <nav>
        <ul class="pagination pagination-sm mb-0">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="currentPage--">«</button>
          </li>
          <li
            class="page-item"
            v-for="page in totalPages"
            :key="page"
            :class="{ active: page === currentPage }"
          >
            <button class="page-link" @click="currentPage = page">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="currentPage++">»</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Modal thêm/sửa nhân viên -->
    <div
      v-if="showModal"
      class="modal fade show d-block"
      tabindex="-1"
      style="background: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <form @submit.prevent="saveNhanVien">
            <div class="modal-header">
              <h5 class="modal-title text-success">
                {{ form.maNV ? 'Cập nhật' : 'Thêm' }} nhân viên
              </h5>
              <button type="button" class="btn-close" @click="closeModal"></button>
            </div>
            <div class="modal-body">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Tên tài khoản</label>
                  <input v-model="form.tenTaiKhoan" type="text" class="form-control" required />
                </div>
                <div class="col-md-6">
                  <label class="form-label">Email</label>
                  <input v-model="form.email" type="email" class="form-control" required />
                </div>
                <div class="col-md-6">
                  <label class="form-label">Họ tên</label>
                  <input v-model="form.hoTen" type="text" class="form-control" required />
                </div>
                <div class="col-md-6">
                  <label class="form-label">Số điện thoại</label>
                  <input v-model="form.soDienThoai" type="text" class="form-control" required />
                </div>
                <div class="col-md-6">
                  <label class="form-label">Địa chỉ</label>
                  <input v-model="form.diaChi" type="text" class="form-control" />
                </div>
                <div class="col-md-3">
                  <label class="form-label">Giới tính</label>
                  <select v-model="form.gioiTinh" class="form-select" required>
                    <option value="">-- Chọn --</option>
                    <option value="1">Nam</option>
                    <option value="0">Nữ</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label">Vai trò</label>
                  <select v-model="form.vaiTro" class="form-select" required>
                    <option value="1">Admin</option>
                    <option value="2">Nhân viên</option>
                  </select>
                </div>
                <div class="col-12">
                  <label class="form-label">Link ảnh (base64 hoặc URL)</label>
                  <input v-model="form.avata" type="text" class="form-control" />
                  <div class="mt-2">
                    <img
                      v-if="form.avata"
                      :src="form.avata"
                      class="rounded-circle"
                      width="60"
                      height="60"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="closeModal">Hủy</button>
              <button type="submit" class="btn btn-success">Lưu</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const danhSachNV = ref([
  {
    maNV: 1,
    tenTaiKhoan: 'admin1',
    email: 'admin@email.com',
    hoTen: 'Nguyễn Văn A',
    soDienThoai: '0123456789',
    diaChi: 'Hà Nội',
    gioiTinh: 1,
    vaiTro: 1,
    avata:
      'data:image/jpeg;base64,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',
    trangThai: 1,
  },
  {
    maNV: 2,
    tenTaiKhoan: 'nv1',
    email: 'nv1@email.com',
    hoTen: 'Trần Thị B',
    soDienThoai: '0987654321',
    diaChi: 'Hồ Chí Minh',
    gioiTinh: 0,
    vaiTro: 2,
    avata:
      'data:image/jpeg;base64,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',
    trangThai: 1,
  },
])

const filter = ref({ hoTen: '', soDienThoai: '', vaiTro: '', gioiTinh: '' })

const danhSachLoc = computed(() => {
  return danhSachNV.value.filter((nv) => {
    const matchTen = nv.hoTen.toLowerCase().includes(filter.value.hoTen.toLowerCase())
    const matchSDT = nv.soDienThoai.includes(filter.value.soDienThoai)
    const matchVaiTro = filter.value.vaiTro === '' || nv.vaiTro.toString() === filter.value.vaiTro
    const matchGioiTinh =
      filter.value.gioiTinh === '' || nv.gioiTinh?.toString() === filter.value.gioiTinh
    return matchTen && matchSDT && matchVaiTro && matchGioiTinh
  })
})

// Phân trang
const currentPage = ref(1)
const pageSize = 20
const totalPages = computed(() => Math.ceil(danhSachLoc.value.length / pageSize))
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return danhSachLoc.value.slice(start, start + pageSize)
})

// Hành động
const showModal = ref(false)
const form = ref({})

const openModal = (nv = null) => {
  form.value = nv
    ? { ...nv }
    : {
        maNV: null,
        tenTaiKhoan: '',
        email: '',
        hoTen: '',
        soDienThoai: '',
        diaChi: '',
        gioiTinh: '',
        vaiTro: '2',
        avata: '',
        trangThai: 1,
      }
  showModal.value = true
}
const closeModal = () => (showModal.value = false)

const saveNhanVien = () => {
  const isNew = !form.value.maNV
  const exists = danhSachNV.value.some(
    (nv) =>
      nv.tenTaiKhoan.toLowerCase() === form.value.tenTaiKhoan.toLowerCase() &&
      (!form.value.maNV || nv.maNV !== form.value.maNV),
  )
  if (exists) return alert('Tên tài khoản đã tồn tại!')
  if (isNew) {
    const newID = Math.max(...danhSachNV.value.map((nv) => nv.maNV)) + 1
    danhSachNV.value.push({ ...form.value, maNV: newID })
  } else {
    const index = danhSachNV.value.findIndex((nv) => nv.maNV === form.value.maNV)
    danhSachNV.value[index] = { ...form.value }
  }
  closeModal()
}

const xoaNhanVien = (id) => {
  if (confirm('Bạn có chắc chắn muốn xóa nhân viên này?')) {
    danhSachNV.value = danhSachNV.value.filter((nv) => nv.maNV !== id)
  }
}

const toggleTrangThai = (maNV) => {
  const nv = danhSachNV.value.find((nv) => nv.maNV === maNV)
  if (nv) nv.trangThai = nv.trangThai ? 0 : 1
}
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
.table img {
  object-fit: cover;
  border: 1px solid #ccc;
}
</style>
