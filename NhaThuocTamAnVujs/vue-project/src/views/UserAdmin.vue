<template>
  <div class="container mt-4">
    <h4 class="mb-3 text-success">Quản lý tài khoản người dùng</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-3">
        <input v-model="filter.username" class="form-control" placeholder="Tài khoản..." />
      </div>
      <div class="col-md-3">
        <input v-model="filter.hoTen" class="form-control" placeholder="Họ tên..." />
      </div>
      <div class="col-md-3">
        <select v-model="filter.vaiTro" class="form-select">
          <option value="">Tất cả vai trò</option>
          <option value="1">Admin</option>
          <option value="2">Nhân viên</option>
          <option value="3">Khách hàng</option>
        </select>
      </div>
      <div class="col-md-3">
        <select v-model="filter.trangThai" class="form-select">
          <option value="">Tất cả trạng thái</option>
          <option value="1">Hoạt động</option>
          <option value="0">Đã khóa</option>
        </select>
      </div>
    </div>

    <!-- Bảng -->
    <table class="table table-bordered table-hover">
      <thead class="table-success text-center">
        <tr>
          <th>ID</th>
          <th>Tài khoản</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>SĐT</th>
          <th>Vai trò</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody class="text-center align-middle">
        <tr v-for="u in usersFiltered" :key="u.userID">
          <td>{{ u.userID }}</td>
          <td>{{ u.username }}</td>
          <td>{{ u.info?.hoTen || '---' }}</td>
          <td>{{ u.info?.email || '---' }}</td>
          <td>{{ u.info?.soDienThoai || '---' }}</td>
          <td>
            <span :class="vaiTroClass(u.vaiTro)">{{ vaiTroLabel(u.vaiTro) }}</span>
          </td>
          <td>
            <span :class="u.trangThai ? 'text-success' : 'text-danger'">
              {{ u.trangThai ? 'Hoạt động' : 'Đã khóa' }}
            </span>
          </td>
          <td>
            <button
              class="btn btn-sm"
              :class="u.trangThai ? 'btn-danger' : 'btn-success'"
              @click="toggleStatus(u)"
            >
              {{ u.trangThai ? 'Khóa' : 'Mở khóa' }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
// import axios from 'axios' // nếu dùng backend thực

const users = ref([
  {
    userID: 1,
    username: 'admin01',
    vaiTro: 1,
    trangThai: true,
    info: { hoTen: 'Nguyễn Văn A', email: 'admin@gmail.com', soDienThoai: '0123456789' },
  },
  {
    userID: 2,
    username: 'nv02',
    vaiTro: 2,
    trangThai: true,
    info: { hoTen: 'Trần Văn B', email: 'nv02@gmail.com', soDienThoai: '0987654321' },
  },
  {
    userID: 3,
    username: 'kh01',
    vaiTro: 3,
    trangThai: false,
    info: { hoTen: 'Lê Thị C', email: 'kh01@gmail.com', soDienThoai: '0909090909' },
  },
])

const filter = ref({
  username: '',
  hoTen: '',
  vaiTro: '',
  trangThai: '',
})

const usersFiltered = computed(() =>
  users.value.filter((u) => {
    const matchUser = u.username.toLowerCase().includes(filter.value.username.toLowerCase())
    const matchName =
      u.info?.hoTen?.toLowerCase().includes(filter.value.hoTen.toLowerCase()) ?? false
    const matchRole = filter.value.vaiTro === '' || u.vaiTro.toString() === filter.value.vaiTro
    const matchStatus =
      filter.value.trangThai === '' || u.trangThai.toString() === filter.value.trangThai
    return matchUser && matchName && matchRole && matchStatus
  }),
)

function vaiTroLabel(v) {
  return v === 1 ? 'Admin' : v === 2 ? 'Nhân viên' : 'Khách hàng'
}

function vaiTroClass(v) {
  return v === 1 ? 'text-danger fw-bold' : v === 2 ? 'text-primary fw-semibold' : 'text-muted'
}

function toggleStatus(user) {
  user.trangThai = !user.trangThai
  alert(`✅ Đã ${user.trangThai ? 'mở khóa' : 'khóa'} tài khoản: ${user.username}`)

  // Gọi API thực tế nếu có:
  // await axios.patch(`/api/users/${user.userID}/status`, { trangThai: user.trangThai })
}
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
