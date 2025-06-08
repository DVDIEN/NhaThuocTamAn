<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">Quản lý biến thể thuốc</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-4">
        <input
          v-model="maThuocFilter"
          type="number"
          class="form-control"
          placeholder="Lọc theo mã thuốc..."
        />
      </div>
      <div class="col-md-4">
        <button class="btn btn-success" @click="openModal()">➕ Thêm biến thể</button>
      </div>
    </div>

    <!-- Danh sách biến thể -->
    <div class="card shadow-sm">
      <div class="card-body">
        <table class="table table-bordered table-hover">
          <thead class="table-success text-center">
            <tr>
              <th>Mã thuốc</th>
              <th>Mã biến thể</th>
              <th>Tên biến thể</th>
              <th>Số lượng</th>
              <th>Giá bán</th>
              <th>Ảnh</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="bt in bienTheLoc" :key="bt.maBienThe">
              <td>{{ bt.maThuoc }}</td>
              <td>{{ bt.maBienThe }}</td>
              <td>{{ bt.tenBienThe }}</td>
              <td>{{ bt.soLuong }}</td>
              <td>{{ formatCurrency(bt.giaBan) }}</td>
              <td>
                <img
                  v-if="bt.hinhAnh"
                  :src="bt.hinhAnh"
                  alt="ảnh"
                  width="60"
                  height="60"
                  class="rounded"
                />
              </td>
              <td>
                <button class="btn btn-warning btn-sm me-2" @click="openModal(bt)">Sửa</button>
                <button class="btn btn-danger btn-sm" @click="xoaBienThe(bt.maBienThe)">Xóa</button>
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
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.maBienThe ? 'Sửa' : 'Thêm' }} biến thể</h5>
            <button class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <input
              v-model.number="form.maThuoc"
              type="number"
              class="form-control mb-3"
              placeholder="Mã thuốc"
            />
            <input
              v-model="form.tenBienThe"
              type="text"
              class="form-control mb-3"
              placeholder="Tên biến thể"
            />
            <input
              v-model.number="form.soLuong"
              type="number"
              class="form-control mb-3"
              placeholder="Số lượng"
            />
            <input
              v-model.number="form.giaBan"
              type="number"
              class="form-control mb-3"
              placeholder="Giá bán"
            />
            <input type="file" accept="image/*" class="form-control mb-2" @change="uploadImage" />
            <img
              v-if="form.hinhAnh"
              :src="form.hinhAnh"
              class="rounded mt-2"
              width="100"
              height="100"
            />
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" @click="luuBienThe">Lưu</button>
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

const bienTheList = ref([
  {
    maBienThe: 1,
    maThuoc: 1,
    tenBienThe: 'Loại nhỏ',
    soLuong: 50,
    giaBan: 20000,
    hinhAnh: '',
  },
])

const maThuocFilter = ref('')
const showModal = ref(false)
const form = ref({})

function openModal(bt = null) {
  form.value = bt
    ? { ...bt }
    : {
        maBienThe: null,
        maThuoc: null,
        tenBienThe: '',
        soLuong: 0,
        giaBan: 0,
        hinhAnh: '',
      }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function luuBienThe() {
  if (form.value.maBienThe) {
    const index = bienTheList.value.findIndex((bt) => bt.maBienThe === form.value.maBienThe)
    bienTheList.value[index] = { ...form.value }
  } else {
    const newId = Math.max(...bienTheList.value.map((bt) => bt.maBienThe), 0) + 1
    bienTheList.value.push({ ...form.value, maBienThe: newId })
  }
  closeModal()
}

function xoaBienThe(id) {
  if (confirm('Bạn có chắc chắn muốn xóa biến thể này?')) {
    bienTheList.value = bienTheList.value.filter((bt) => bt.maBienThe !== id)
  }
}

function uploadImage(e) {
  const file = e.target.files[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = () => {
    form.value.hinhAnh = reader.result
  }
  reader.readAsDataURL(file)
}

const formatCurrency = (value) => value.toLocaleString('vi-VN') + '₫'

const bienTheLoc = computed(() => {
  return bienTheList.value.filter(
    (bt) => !maThuocFilter.value || bt.maThuoc === Number(maThuocFilter.value),
  )
})
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
