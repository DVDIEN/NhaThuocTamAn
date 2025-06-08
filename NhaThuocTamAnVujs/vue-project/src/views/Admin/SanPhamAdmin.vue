<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">Quản lý sản phẩm</h4>

    <!-- Bộ lọc -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <input
              v-model="filter.tenThuoc"
              type="text"
              class="form-control"
              placeholder="Tên thuốc..."
            />
          </div>
          <div class="col-md-3">
            <input
              v-model="filter.hoatChat"
              type="text"
              class="form-control"
              placeholder="Hoạt chất..."
            />
          </div>
          <div class="col-md-3">
            <input
              v-model="filter.congDung"
              type="text"
              class="form-control"
              placeholder="Công dụng..."
            />
          </div>
          <div class="col-md-3">
            <select v-model="filter.trangThai" class="form-select">
              <option value="">Trạng thái</option>
              <option value="1">Hoạt động</option>
              <option value="0">Ngừng bán</option>
            </select>
          </div>
          <div class="col-md-3">
            <select v-model="filter.maDM" class="form-select">
              <option value="">Danh mục</option>
              <option v-for="dm in danhSachDanhMuc" :key="dm.maDM" :value="dm.maDM">
                {{ dm.tenDanhMuc }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <button class="btn btn-outline-secondary w-100" @click="resetFilter">🧹 Xóa lọc</button>
          </div>
        </div>
      </div>
    </div>

    <div class="mb-3">
      <button class="btn btn-success" @click="openModal()">➕ Thêm sản phẩm</button>
    </div>

    <!-- Danh sách sản phẩm -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <table class="table table-bordered table-hover mb-0">
          <thead class="table-success text-center">
            <tr>
              <th>#</th>
              <th>Ảnh</th>
              <th>Tên thuốc</th>
              <th>Hoạt chất</th>
              <th>Công dụng</th>
              <th>Đơn vị</th>
              <th>Giá nhập</th>
              <th>Giá bán</th>
              <th>Số lượng</th>
              <th>HSD</th>
              <th>Danh mục</th>
              <th>Nhà cung cấp</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="sp in danhSachLoc" :key="sp.maThuoc">
              <td>{{ sp.maThuoc }}</td>
              <td>
                <img
                  v-if="sp.hinhAnhList?.length"
                  :src="sp.hinhAnhList[0]"
                  alt="ảnh"
                  width="40"
                  height="40"
                  class="rounded"
                />
              </td>
              <td>{{ sp.tenThuoc }}</td>
              <td>{{ sp.hoatChat }}</td>
              <td>{{ sp.congDung }}</td>
              <td>{{ sp.donViTinh }}</td>
              <td>{{ formatCurrency(sp.giaNhap) }}</td>
              <td>{{ formatCurrency(sp.giaBan) }}</td>
              <td>{{ sp.soLuong }}</td>
              <td>{{ sp.hanSuDung }}</td>
              <td>{{ getTenDanhMuc(sp.maDM) }}</td>
              <td>{{ getTenNCC(sp.maNCC) }}</td>
              <td>
                <span :class="sp.trangThai ? 'text-success' : 'text-danger'">
                  {{ sp.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                </span>
              </td>
              <td>
                <button class="btn btn-sm btn-warning me-1" @click="openModal(sp)">Sửa</button>
                <button class="btn btn-sm btn-danger" @click="xoaSanPham(sp.maThuoc)">Xóa</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal thêm/sửa -->
    <div
      class="modal fade"
      :class="{ show: showModal }"
      :style="showModal ? 'display:block' : ''"
      @click.self="closeModal"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.maThuoc ? 'Sửa' : 'Thêm' }} sản phẩm</h5>
            <button class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <input
                  v-model="form.tenThuoc"
                  class="form-control"
                  placeholder="Tên thuốc"
                  required
                />
              </div>
              <div class="col-md-6">
                <input v-model="form.hoatChat" class="form-control" placeholder="Hoạt chất" />
              </div>
              <div class="col-md-12">
                <textarea
                  v-model="form.congDung"
                  class="form-control"
                  placeholder="Công dụng"
                ></textarea>
              </div>
              <div class="col-md-4">
                <input v-model="form.donViTinh" class="form-control" placeholder="Đơn vị tính" />
              </div>
              <div class="col-md-4">
                <input
                  v-model.number="form.giaNhap"
                  type="number"
                  class="form-control"
                  placeholder="Giá nhập"
                />
              </div>
              <div class="col-md-4">
                <input
                  v-model.number="form.giaBan"
                  type="number"
                  class="form-control"
                  placeholder="Giá bán"
                />
              </div>
              <div class="col-md-4">
                <input
                  v-model.number="form.soLuong"
                  type="number"
                  class="form-control"
                  placeholder="Số lượng"
                />
              </div>
              <div class="col-md-4">
                <input v-model="form.hanSuDung" type="date" class="form-control" />
              </div>
              <div class="col-md-4">
                <select v-model="form.trangThai" class="form-select">
                  <option :value="1">Hoạt động</option>
                  <option :value="0">Ngừng bán</option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label">Danh mục</label>
                <select v-model="form.maDM" class="form-select" required>
                  <option disabled value="">-- Chọn danh mục --</option>
                  <option v-for="dm in danhSachDanhMuc" :key="dm.maDM" :value="dm.maDM">
                    {{ dm.tenDanhMuc }}
                  </option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label">Nhà cung cấp</label>
                <select v-model="form.maNCC" class="form-select" required>
                  <option disabled value="">-- Chọn nhà cung cấp --</option>
                  <option v-for="ncc in danhSachNCC" :key="ncc.maNCC" :value="ncc.maNCC">
                    {{ ncc.tenNCC }}
                  </option>
                </select>
              </div>
              <div class="col-md-12">
                <label class="form-label">Thêm ảnh sản phẩm</label>
                <input type="file" class="form-control" multiple @change="handleImageUpload" />
                <div class="mt-2 d-flex gap-2 flex-wrap">
                  <img
                    v-for="(img, index) in form.hinhAnhList"
                    :key="index"
                    :src="img"
                    class="rounded border"
                    width="80"
                    height="80"
                  />
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" @click="saveSanPham">Lưu</button>
            <button class="btn btn-secondary" @click="closeModal">Hủy</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const danhSachSP = ref([
  {
    maThuoc: 1,
    tenThuoc: 'Paracetamol',
    hoatChat: 'Paracetamol 500mg',
    congDung: 'Giảm đau, hạ sốt',
    donViTinh: 'Hộp',
    giaNhap: 15000,
    giaBan: 25000,
    soLuong: 100,
    hanSuDung: '2025-12-31',
    hinhAnhList: [],
    maDM: 1,
    maNCC: 1,
    trangThai: 1,
  },
])

const danhSachDanhMuc = ref([
  { maDM: 1, tenDanhMuc: 'Thuốc giảm đau' },
  { maDM: 2, tenDanhMuc: 'Kháng sinh' },
  { maDM: 3, tenDanhMuc: 'Vitamin' },
])

const danhSachNCC = ref([
  { maNCC: 1, tenNCC: 'Dược phẩm A' },
  { maNCC: 2, tenNCC: 'Dược phẩm B' },
])

const filter = ref({
  tenThuoc: '',
  hoatChat: '',
  congDung: '',
  trangThai: '',
  maDM: '',
})

const showModal = ref(false)
const form = ref({})

function openModal(sp = null) {
  form.value = sp
    ? { ...sp }
    : {
        maThuoc: null,
        tenThuoc: '',
        hoatChat: '',
        congDung: '',
        donViTinh: '',
        giaNhap: 0,
        giaBan: 0,
        soLuong: 0,
        hanSuDung: '',
        hinhAnhList: [],
        maDM: '',
        maNCC: '',
        trangThai: 1,
      }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function saveSanPham() {
  if (form.value.maThuoc) {
    const index = danhSachSP.value.findIndex((sp) => sp.maThuoc === form.value.maThuoc)
    danhSachSP.value[index] = { ...form.value }
  } else {
    const newID = Math.max(...danhSachSP.value.map((sp) => sp.maThuoc), 0) + 1
    danhSachSP.value.push({ ...form.value, maThuoc: newID })
  }
  closeModal()
}

function xoaSanPham(id) {
  if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
    danhSachSP.value = danhSachSP.value.filter((sp) => sp.maThuoc !== id)
  }
}

function handleImageUpload(event) {
  const files = event.target.files
  if (!files.length) return

  for (let i = 0; i < files.length; i++) {
    const reader = new FileReader()
    reader.onload = () => {
      form.value.hinhAnhList.push(reader.result)
    }
    reader.readAsDataURL(files[i])
  }
}

function getTenDanhMuc(maDM) {
  const dm = danhSachDanhMuc.value.find((d) => d.maDM === maDM)
  return dm ? dm.tenDanhMuc : 'Không xác định'
}

function getTenNCC(maNCC) {
  const ncc = danhSachNCC.value.find((n) => n.maNCC === maNCC)
  return ncc ? ncc.tenNCC : 'Không xác định'
}

function resetFilter() {
  filter.value = {
    tenThuoc: '',
    hoatChat: '',
    congDung: '',
    trangThai: '',
    maDM: '',
  }
}

const formatCurrency = (value) => value.toLocaleString('vi-VN') + '₫'

const danhSachLoc = computed(() => {
  return danhSachSP.value.filter((sp) => {
    const matchTen = sp.tenThuoc.toLowerCase().includes(filter.value.tenThuoc.toLowerCase())
    const matchHoatChat = sp.hoatChat.toLowerCase().includes(filter.value.hoatChat.toLowerCase())
    const matchCongDung = sp.congDung.toLowerCase().includes(filter.value.congDung.toLowerCase())
    const matchTrangThai =
      filter.value.trangThai === '' || sp.trangThai.toString() === filter.value.trangThai
    const matchDanhMuc =
      filter.value.maDM === '' || sp.maDM.toString() === filter.value.maDM.toString()
    return matchTen && matchHoatChat && matchCongDung && matchTrangThai && matchDanhMuc
  })
})
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
