<template>
  <div class="container py-4">
    <h4 class="text-success mb-3">🧾 Quản Lý Hóa Đơn</h4>

    <!-- Bộ lọc tìm kiếm -->
    <div class="row g-2 mb-3">
      <div class="col-md-4">
        <input v-model="keyword" class="form-control" placeholder="Tìm theo tên khách hàng..." />
      </div>
      <div class="col-md-3">
        <input type="date" v-model="fromDate" class="form-control" placeholder="Từ ngày" />
      </div>
      <div class="col-md-3">
        <input type="date" v-model="toDate" class="form-control" placeholder="Đến ngày" />
      </div>
    </div>

    <!-- Danh sách hóa đơn -->
    <table class="table table-bordered table-hover align-middle">
      <thead class="table-success text-center">
        <tr>
          <th>Mã HĐ</th>
          <th>Khách hàng</th>
          <th>Ngày lập</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="hd in filteredHoaDon" :key="hd.maHD" class="text-center">
          <td>{{ hd.maHD }}</td>
          <td class="text-start">{{ hd.tenKhach }}</td>
          <td>{{ formatDate(hd.ngayLap) }}</td>
          <td>{{ formatCurrency(hd.tongTien) }}</td>
          <td>
            <span :class="hd.trangThai === 'Đã thanh toán' ? 'text-success' : 'text-danger'">
              {{ hd.trangThai }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary" @click="xemChiTiet(hd.maHD)">
              Xem chi tiết
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const keyword = ref('')
const fromDate = ref('')
const toDate = ref('')

const hoaDonList = ref([
  {
    maHD: 1,
    tenKhach: 'Nguyễn Văn A',
    ngayLap: '2024-05-10',
    tongTien: 350000,
    trangThai: 'Đã thanh toán',
  },
  {
    maHD: 2,
    tenKhach: 'Trần Thị B',
    ngayLap: '2024-05-12',
    tongTien: 220000,
    trangThai: 'Chưa thanh toán',
  },
  {
    maHD: 3,
    tenKhach: 'Phạm Văn C',
    ngayLap: '2024-05-20',
    tongTien: 480000,
    trangThai: 'Đã thanh toán',
  },
])

const filteredHoaDon = computed(() =>
  hoaDonList.value.filter((hd) => {
    const matchKeyword = hd.tenKhach.toLowerCase().includes(keyword.value.toLowerCase())
    const matchFrom = !fromDate.value || new Date(hd.ngayLap) >= new Date(fromDate.value)
    const matchTo = !toDate.value || new Date(hd.ngayLap) <= new Date(toDate.value)
    return matchKeyword && matchFrom && matchTo
  }),
)

const formatDate = (d) => new Date(d).toLocaleDateString('vi-VN')
const formatCurrency = (v) => v.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })

const xemChiTiet = (maHD) => {
  router.push(`/admin/chi-tiet-hoa-don/${maHD}`)
}
</script>

<style scoped>
.table td {
  vertical-align: middle;
}
</style>
