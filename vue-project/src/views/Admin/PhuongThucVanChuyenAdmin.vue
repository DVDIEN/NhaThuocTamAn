<template>
  <div class="container py-4">
    <h4 class="mb-3 text-success fw-bold">🚚 Quản lý phương thức vận chuyển</h4>

    <!-- Nút thêm -->
    <div class="mb-3 text-end">
      <button class="btn btn-success" @click="openModal()">
        <i class="bi bi-plus-circle me-1"></i> Thêm phương thức
      </button>
    </div>

    <!-- Bảng danh sách -->
    <table class="table table-hover shadow-sm">
      <thead class="table-success">
        <tr>
          <th>ID</th>
          <th>Tên phương thức</th>
          <th>Mô tả</th>
          <th>Phí vận chuyển</th>
          <th>Thời gian dự kiến</th>
          <th>Trạng thái</th>
          <th class="text-center">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in ptvcList" :key="item.maPTVC">
          <td>{{ item.maPTVC }}</td>
          <td>{{ item.tenPTVC }}</td>
          <td>{{ item.moTa }}</td>
          <td>{{ formatPrice(item.phiVanChuyen) }}</td>
          <td>{{ item.thoiGianDuKien }}</td>
          <td>
            <span class="badge" :class="item.trangThai ? 'bg-success' : 'bg-danger'">
              {{ item.trangThai ? 'Hoạt động' : 'Ngừng' }}
            </span>
          </td>
          <td class="text-center">
            <button class="btn btn-sm btn-primary me-2" @click="editItem(item)">
              <i class="bi bi-pencil-square"></i>
            </button>
            <button
              v-if="item.trangThai"
              class="btn btn-sm btn-danger"
              @click="deleteItem(item.maPTVC)"
            >
              <i class="bi bi-trash"></i>
            </button>
            <button v-else class="btn btn-sm btn-warning" @click="restoreItem(item.maPTVC)">
              <i class="bi bi-arrow-clockwise"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal thêm/sửa -->
    <div class="modal fade" id="ptvcModal" tabindex="-1" aria-hidden="true" ref="modalRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-success text-white">
            <h5 class="modal-title">
              {{ editing ? 'Sửa phương thức' : 'Thêm phương thức' }}
            </h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveItem">
              <div class="mb-3">
                <label class="form-label">Tên phương thức</label>
                <input v-model="form.tenPTVC" type="text" class="form-control" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Mô tả</label>
                <input v-model="form.moTa" type="text" class="form-control" />
              </div>
              <div class="mb-3">
                <label class="form-label">Phí vận chuyển</label>
                <input
                  v-model.number="form.phiVanChuyen"
                  type="number"
                  class="form-control"
                  required
                />
              </div>
              <div class="mb-3">
                <label class="form-label">Thời gian dự kiến</label>
                <input v-model="form.thoiGianDuKien" type="text" class="form-control" />
              </div>
              <div class="text-end">
                <button type="submit" class="btn btn-success">Lưu lại</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const ptvcList = ref([])
const form = ref({
  maPTVC: null,
  tenPTVC: '',
  moTa: '',
  phiVanChuyen: 0,
  thoiGianDuKien: '',
})
const editing = ref(false)
const modalRef = ref(null)

const api = 'http://localhost:8080/api/phuong-thuc-van-chuyen'

const fetchData = async () => {
  const res = await axios.get(api)
  ptvcList.value = res.data
}

const openModal = () => {
  editing.value = false
  form.value = { maPTVC: null, tenPTVC: '', moTa: '', phiVanChuyen: 0, thoiGianDuKien: '' }
  new bootstrap.Modal(modalRef.value).show()
}

const editItem = (item) => {
  editing.value = true
  form.value = { ...item }
  new bootstrap.Modal(modalRef.value).show()
}

const saveItem = async () => {
  if (editing.value) {
    await axios.put(`${api}/${form.value.maPTVC}`, form.value)
  } else {
    await axios.post(api, form.value)
  }
  fetchData()
  bootstrap.Modal.getInstance(modalRef.value).hide()
}

const deleteItem = async (id) => {
  if (confirm('Bạn có chắc muốn ngừng phương thức này?')) {
    await axios.delete(`${api}/${id}`)
    fetchData()
  }
}

const restoreItem = async (id) => {
  await axios.put(`${api}/restore/${id}`)
  fetchData()
}

const formatPrice = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

onMounted(fetchData)
</script>
