<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">Quản lý đơn hàng</h4>

    <!-- Cảnh báo đơn mới -->
    <div
      v-if="donHangMoi.length"
      class="alert alert-warning d-flex justify-content-between align-items-center"
    >
      <div>🛒 Có {{ donHangMoi.length }} đơn hàng mới chờ xử lý!</div>
      <button class="btn btn-sm btn-primary" @click="filter.trangThai = '0'">Xử lý ngay</button>
    </div>

    <!-- Bộ lọc -->
    <div class="card shadow-sm mb-3">
      <div class="card-body">
        <div class="row g-3 align-items-end">
          <div class="col-md-3">
            <label class="form-label">Trạng thái</label>
            <select v-model="filter.trangThai" class="form-select">
              <option value="">Tất cả</option>
              <option value="0">Chờ xử lý</option>
              <option value="1">Xác nhận</option>
              <option value="2">Đang giao</option>
              <option value="3">Hoàn tất</option>
              <option value="4">Hủy</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Thanh toán</label>
            <select v-model="filter.thanhToan" class="form-select">
              <option value="">Tất cả</option>
              <option value="1">Đã thanh toán</option>
              <option value="0">Chưa thanh toán</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Từ ngày</label>
            <input type="date" v-model="filter.tuNgay" class="form-control" />
          </div>
          <div class="col-md-3">
            <label class="form-label">Đến ngày</label>
            <input type="date" v-model="filter.denNgay" class="form-control" />
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng đơn hàng -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <table class="table table-bordered mb-0">
          <thead class="table-success text-center">
            <tr>
              <th>Mã đơn</th>
              <th>Khách hàng</th>
              <th>Ngày đặt</th>
              <th>Hình thức</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th>Thanh toán</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="don in donHangLoc" :key="don.maDonHang">
              <td>{{ don.maDonHang }}</td>
              <td>{{ getTenKhachHang(don.maKH) }}</td>
              <td>{{ don.ngayDat }}</td>
              <td>{{ don.hinhThuc }}</td>
              <td>{{ formatCurrency(don.tongTien) }}</td>

              <!-- Trạng thái -->
              <td>
                <template v-if="don.trangThai === 0 && don.hinhThuc === 'COD'">
                  <button class="btn btn-success btn-sm me-1" @click="capNhatTrangThai(don, 1)">
                    ✅ Xác nhận
                  </button>
                  <button class="btn btn-danger btn-sm" @click="capNhatTrangThai(don, 4)">
                    ❌ Hủy
                  </button>
                </template>

                <template v-else-if="don.trangThai === 1">
                  <button class="btn btn-info btn-sm me-1" @click="capNhatTrangThai(don, 2)">
                    🚚 Giao hàng
                  </button>
                  <button class="btn btn-danger btn-sm" @click="capNhatTrangThai(don, 4)">
                    ❌ Hủy
                  </button>
                </template>

                <template v-else-if="don.trangThai === 2">
                  <template v-if="don.hinhThuc === 'COD' && !don.daThanhToan">
                    <button class="btn btn-outline-primary btn-sm" @click="danhDauThanhToan(don)">
                      💵 Đã thu tiền
                    </button>
                  </template>
                  <template v-else-if="don.daThanhToan">
                    <button class="btn btn-success btn-sm" @click="capNhatTrangThai(don, 3)">
                      ✅ Hoàn tất
                    </button>
                  </template>
                </template>

                <template v-else>
                  <span class="badge" :class="trangThaiClass(don.trangThai)">
                    {{ trangThaiLabel(don.trangThai) }}
                  </span>
                </template>
              </td>

              <!-- Thanh toán -->
              <td>
                <span :class="don.daThanhToan ? 'text-success' : 'text-danger'">
                  {{ don.daThanhToan ? 'Đã thanh toán' : 'Chưa thanh toán' }}
                </span>
              </td>

              <!-- Hành động -->
              <td>
                <router-link
                  :to="`/admin/chi-tiet-don-hang/${don.maDonHang}`"
                  class="btn btn-sm btn-outline-primary"
                >
                  Chi tiết
                </router-link>
              </td>
            </tr>
            <tr v-if="donHangLoc.length === 0">
              <td colspan="8">Không có đơn hàng phù hợp.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watchEffect } from 'vue'

const danhSachDonHang = ref([
  {
    maDonHang: 101,
    maKH: 1,
    ngayDat: '2024-05-10',
    tongTien: 500000,
    trangThai: 2,
    daThanhToan: false,
    hinhThuc: 'COD',
  },
  {
    maDonHang: 102,
    maKH: 2,
    ngayDat: '2024-05-12',
    tongTien: 750000,
    trangThai: 2,
    daThanhToan: true,
    hinhThuc: 'ONLINE',
  },
])

const danhSachKH = ref([
  { maKH: 1, hoTen: 'Nguyễn Văn A' },
  { maKH: 2, hoTen: 'Trần Thị B' },
])

const filter = ref({
  trangThai: '',
  thanhToan: '',
  tuNgay: '',
  denNgay: '',
})

const getTenKhachHang = (maKH) => danhSachKH.value.find((k) => k.maKH === maKH)?.hoTen || 'Không rõ'

const formatCurrency = (v) => v.toLocaleString('vi-VN') + '₫'

const donHangLoc = computed(() => {
  return danhSachDonHang.value.filter((don) => {
    const matchTrangThai =
      filter.value.trangThai === '' || don.trangThai.toString() === filter.value.trangThai
    const matchThanhToan =
      filter.value.thanhToan === '' || don.daThanhToan.toString() === filter.value.thanhToan
    const matchTuNgay =
      !filter.value.tuNgay || new Date(don.ngayDat) >= new Date(filter.value.tuNgay)
    const matchDenNgay =
      !filter.value.denNgay || new Date(don.ngayDat) <= new Date(filter.value.denNgay)
    return matchTrangThai && matchThanhToan && matchTuNgay && matchDenNgay
  })
})

const donHangMoi = computed(() => danhSachDonHang.value.filter((d) => d.trangThai === 0))

const capNhatTrangThai = (don, newTrangThai) => {
  don.trangThai = newTrangThai
  alert(`Đơn hàng ${don.maDonHang} đã chuyển sang: ${trangThaiLabel(newTrangThai)}`)
}

const danhDauThanhToan = (don) => {
  don.daThanhToan = true
  alert(`Đơn hàng ${don.maDonHang} đã được đánh dấu là ĐÃ THU TIỀN.`)
  if (don.trangThai === 2) {
    don.trangThai = 3
    alert(`Đơn hàng ${don.maDonHang} đã HOÀN TẤT.`)
  }
}

watchEffect(() => {
  for (const don of danhSachDonHang.value) {
    if (don.hinhThuc === 'ONLINE' && don.daThanhToan && don.trangThai === 2) {
      don.trangThai = 3
    }
  }
})

const trangThaiLabel = (t) =>
  ['Chờ xử lý', 'Xác nhận', 'Đang giao', 'Hoàn tất', 'Hủy'][t] || 'Không rõ'
const trangThaiClass = (t) =>
  ['bg-warning', 'bg-primary', 'bg-info', 'bg-success', 'bg-danger'][t] || ''
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
