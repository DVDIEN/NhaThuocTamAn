<template>
  <div class="container py-4">
    <h4 class="text-success mb-3">🎁 Quản Lý Khuyến Mãi</h4>

    <!-- Tìm kiếm -->
    <div class="input-group mb-3">
      <input v-model="keyword" class="form-control" placeholder="Tìm tên khuyến mãi..." />
      <button class="btn btn-outline-secondary" @click="keyword = ''">Xóa</button>
    </div>

    <!-- Nút Thêm -->
    <div class="text-end mb-2">
      <button class="btn btn-success" @click="create">➕ Thêm khuyến mãi</button>
    </div>

    <!-- Danh sách khuyến mãi -->
    <table class="table table-bordered table-hover small align-middle">
      <thead class="table-success text-center">
        <tr>
          <th>Mã</th>
          <th>Tên KM</th>
          <th>Giá trị</th>
          <th>Thời gian</th>
          <th>Áp dụng</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="km in filteredKhuyenMai" :key="km.maKM" class="text-center">
          <td>{{ km.maKM }}</td>
          <td>{{ km.tenKM }}</td>
          <td>{{ km.loai === 'percent' ? km.giaTri + '%' : formatCurrency(km.giaTri) }}</td>
          <td>{{ formatDate(km.ngayBD) }} → {{ formatDate(km.ngayKT) }}</td>
          <td>{{ km.thuocApDung?.length || 0 }} thuốc</td>
          <td>
            <span :class="km.trangThai ? 'text-success' : 'text-danger'">
              {{ km.trangThai ? 'Áp dụng' : 'Ẩn' }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-warning me-1" @click="edit(km)">Sửa</button>
            <button class="btn btn-sm btn-danger me-1" @click="remove(km.maKM)">Xóa</button>
            <button class="btn btn-sm btn-outline-dark me-1" @click="toggle(km)">
              {{ km.trangThai ? 'Ẩn' : 'Hiện' }}
            </button>
            <button class="btn btn-sm btn-info" @click="xemThuocApDung(km)">👁️ Xem thuốc</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Form khuyến mãi -->
    <div v-if="showForm" class="modal-overlay">
      <div class="modal-content card p-4">
        <h5 class="mb-3 text-success">
          {{ isEdit ? '✏️ Sửa khuyến mãi' : '➕ Thêm khuyến mãi' }}
        </h5>
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Tên khuyến mãi</label>
            <input v-model="form.tenKM" class="form-control" />
          </div>
          <div class="col-md-3">
            <label class="form-label">Loại</label>
            <select v-model="form.loai" class="form-select">
              <option value="percent">Phần trăm</option>
              <option value="money">Giảm trực tiếp</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Giá trị</label>
            <input type="number" v-model="form.giaTri" class="form-control" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Ngày bắt đầu</label>
            <input type="date" v-model="form.ngayBD" class="form-control" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Ngày kết thúc</label>
            <input type="date" v-model="form.ngayKT" class="form-control" />
          </div>
          <div class="col-12">
            <label class="form-label">Thuốc áp dụng</label>
            <div class="d-flex justify-content-between align-items-center">
              <span>{{ form.thuocApDung.length }} thuốc đã chọn</span>
              <button class="btn btn-sm btn-outline-primary" @click="showThuocModal = true">
                ➕ Chọn thuốc
              </button>
            </div>
          </div>
        </div>
        <div class="text-end mt-3">
          <button class="btn btn-primary" @click="save">💾 Lưu</button>
          <button class="btn btn-secondary ms-2" @click="showForm = false">❌ Hủy</button>
        </div>
      </div>
    </div>

    <!-- Modal chọn thuốc -->
    <div v-if="showThuocModal" class="modal-overlay">
      <div class="modal-content card p-3">
        <h6>🔍 Chọn thuốc áp dụng</h6>
        <input
          v-model="searchThuoc"
          class="form-control form-control-sm mb-3"
          placeholder="Tìm thuốc..."
        />
        <table class="table table-sm table-bordered align-middle">
          <thead class="table-light">
            <tr>
              <th></th>
              <th>Ảnh</th>
              <th>Tên thuốc</th>
              <th>Mã thuốc</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="thuoc in filteredThuoc" :key="thuoc.maThuoc">
              <td class="text-center">
                <input type="checkbox" :value="thuoc.maThuoc" v-model="form.thuocApDung" />
              </td>
              <td class="text-center">
                <img
                  :src="thuoc.hinhAnh"
                  alt=""
                  style="width: 50px; height: 50px; object-fit: contain"
                />
              </td>
              <td>{{ thuoc.tenThuoc }}</td>
              <td>{{ thuoc.maThuoc }}</td>
            </tr>
          </tbody>
        </table>
        <div class="text-end">
          <button class="btn btn-sm btn-success" @click="showThuocModal = false">✅ Xong</button>
          <button class="btn btn-sm btn-secondary ms-2" @click="showThuocModal = false">
            ❌ Đóng
          </button>
        </div>
      </div>
    </div>

    <!-- Modal xem thuốc áp dụng -->
    <div v-if="showThuocApDungModal" class="modal-overlay">
      <div class="modal-content card p-3">
        <h6 class="mb-3">📋 Danh sách thuốc được áp dụng</h6>
        <table class="table table-sm table-bordered align-middle">
          <thead class="table-light">
            <tr>
              <th>Ảnh</th>
              <th>Tên thuốc</th>
              <th>Mã thuốc</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="thuoc in thuocApDungCurrent" :key="thuoc.maThuoc">
              <td class="text-center">
                <img :src="thuoc.hinhAnh" style="width: 50px; height: 50px; object-fit: contain" />
              </td>
              <td>{{ thuoc.tenThuoc }}</td>
              <td>{{ thuoc.maThuoc }}</td>
            </tr>
            <tr v-if="thuocApDungCurrent.length === 0">
              <td colspan="3" class="text-center text-muted">Không có thuốc nào</td>
            </tr>
          </tbody>
        </table>
        <div class="text-end">
          <button class="btn btn-sm btn-secondary" @click="showThuocApDungModal = false">
            ❌ Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const keyword = ref('')
const showForm = ref(false)
const isEdit = ref(false)
const showThuocModal = ref(false)
const showThuocApDungModal = ref(false)
const searchThuoc = ref('')
const thuocApDungCurrent = ref([])

const khuyenMaiList = ref(JSON.parse(localStorage.getItem('khuyenMaiList')) || [])

const danhSachThuoc = ref([
  {
    maThuoc: 1,
    tenThuoc: 'Paracetamol 500mg',
    hinhAnh: 'https://cdn.nhathuoclongchau.com.vn/...jpg',
  },
  { maThuoc: 2, tenThuoc: 'Vitamin C', hinhAnh: 'https://cdn.nhathuoclongchau.com.vn/...png' },
  { maThuoc: 3, tenThuoc: 'Amoxicillin', hinhAnh: 'https://cdn.nhathuoclongchau.com.vn/...jpg' },
  { maThuoc: 4, tenThuoc: 'Tiffy cảm cúm', hinhAnh: 'https://cdn.nhathuoclongchau.com.vn/...jpg' },
])

const form = ref({
  maKM: null,
  tenKM: '',
  loai: 'percent',
  giaTri: 0,
  ngayBD: '',
  ngayKT: '',
  trangThai: true,
  thuocApDung: [],
})

const filteredKhuyenMai = computed(() =>
  khuyenMaiList.value.filter((k) => k.tenKM.toLowerCase().includes(keyword.value.toLowerCase())),
)

const filteredThuoc = computed(() =>
  danhSachThuoc.value.filter((t) =>
    t.tenThuoc.toLowerCase().includes(searchThuoc.value.toLowerCase()),
  ),
)

const formatDate = (d) => new Date(d).toLocaleDateString('vi-VN')
const formatCurrency = (v) => v.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })

const create = () => {
  form.value = {
    maKM: Date.now(),
    tenKM: '',
    loai: 'percent',
    giaTri: 0,
    ngayBD: '',
    ngayKT: '',
    trangThai: true,
    thuocApDung: [],
  }
  isEdit.value = false
  showForm.value = true
}

const edit = (km) => {
  form.value = { ...km }
  isEdit.value = true
  showForm.value = true
}

const save = () => {
  const index = khuyenMaiList.value.findIndex((k) => k.maKM === form.value.maKM)
  if (isEdit.value && index !== -1) {
    khuyenMaiList.value[index] = { ...form.value }
  } else {
    khuyenMaiList.value.push({ ...form.value })
  }
  localStorage.setItem('khuyenMaiList', JSON.stringify(khuyenMaiList.value))
  showForm.value = false
}

const remove = (id) => {
  if (confirm('Xóa khuyến mãi này?')) {
    const index = khuyenMaiList.value.findIndex((k) => k.maKM === id)
    if (index !== -1) {
      khuyenMaiList.value.splice(index, 1)
      localStorage.setItem('khuyenMaiList', JSON.stringify(khuyenMaiList.value))
    }
  }
}

const toggle = (km) => {
  km.trangThai = !km.trangThai
  localStorage.setItem('khuyenMaiList', JSON.stringify(khuyenMaiList.value))
}

const xemThuocApDung = (km) => {
  thuocApDungCurrent.value = danhSachThuoc.value.filter((t) => km.thuocApDung.includes(t.maThuoc))
  showThuocApDungModal.value = true
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.modal-content {
  background: white;
  border-radius: 8px;
  max-height: 90vh;
  overflow-y: auto;
  width: 750px;
}
</style>
