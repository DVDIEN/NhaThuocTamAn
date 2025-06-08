<template>
  <div class="container py-4">
    <h4 class="mb-3 text-primary">📜 Lịch Sử Nhập Xuất Kho</h4>

    <!-- Thông báo đang lọc -->
    <p v-if="keyword" class="text-muted">
      🔍 Đang lọc theo mã biến thể: <strong>#{{ keyword }}</strong>
    </p>

    <!-- Bộ lọc -->
    <div class="row g-3 mb-3">
      <div class="col-md-4">
        <input
          v-model="keyword"
          type="text"
          class="form-control"
          placeholder="Tìm theo mã biến thể..."
        />
      </div>
      <div class="col-md-3">
        <select v-model="filterLoai" class="form-select">
          <option value="">-- Tất cả loại giao dịch --</option>
          <option value="Nhập">Nhập</option>
          <option value="Xuất">Xuất</option>
          <option value="Điều chỉnh">Điều chỉnh</option>
        </select>
      </div>
    </div>

    <!-- Bảng dữ liệu -->
    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle">
        <thead class="table-info text-center">
          <tr>
            <th>Thời gian</th>
            <th>Thuốc</th>
            <th>Biến thể</th>
            <th>Loại giao dịch</th>
            <th>Số lượng</th>
            <th>Nguồn phát sinh</th>
            <th>Ghi chú</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredList" :key="item.id">
            <td>{{ formatDate(item.thoiGian) }}</td>
            <td>{{ item.tenThuoc }}</td>
            <td>{{ item.tenBienThe }}</td>
            <td class="text-center fw-bold">
              <span :class="badgeClass(item.loaiGiaoDich)">
                {{ item.loaiGiaoDich }}
              </span>
            </td>
            <td class="text-center">{{ item.soLuong }}</td>
            <td>
              <div v-if="item.maPN">Phiếu nhập #{{ item.maPN }}</div>
              <div v-if="item.maHD">Hóa đơn #{{ item.maHD }}</div>
              <div v-if="item.maDonHang">Đơn hàng #{{ item.maDonHang }}</div>
            </td>
            <td>{{ item.ghiChu || '...' }}</td>
          </tr>
          <tr v-if="filteredList.length === 0">
            <td colspan="7" class="text-center text-muted">Không có dữ liệu</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      lichSu: [],
      keyword: '',
      filterLoai: '',
    }
  },
  computed: {
    filteredList() {
      return this.lichSu.filter((item) => {
        const matchMaBienThe = this.keyword ? item.maBienThe === parseInt(this.keyword) : true
        const matchLoai = this.filterLoai ? item.loaiGiaoDich === this.filterLoai : true
        return matchMaBienThe && matchLoai
      })
    },
  },
  methods: {
    fetchLichSu() {
      this.lichSu = [
        {
          id: 1,
          thoiGian: '2024-06-01T08:00:00',
          loaiGiaoDich: 'Nhập',
          soLuong: 100,
          tenThuoc: 'Paracetamol',
          tenBienThe: 'Viên 500mg',
          maBienThe: 1001,
          maPN: 101,
          maHD: null,
          maDonHang: null,
          ghiChu: 'Nhập kho đầu tháng',
        },
        {
          id: 2,
          thoiGian: '2024-06-02T09:15:00',
          loaiGiaoDich: 'Xuất',
          soLuong: -20,
          tenThuoc: 'Paracetamol',
          tenBienThe: 'Viên 500mg',
          maBienThe: 1001,
          maPN: null,
          maHD: 501,
          maDonHang: 301,
          ghiChu: 'Bán cho khách',
        },
        {
          id: 3,
          thoiGian: '2024-06-03T10:00:00',
          loaiGiaoDich: 'Nhập',
          soLuong: 60,
          tenThuoc: 'Amoxicillin',
          tenBienThe: 'Hộp 250mg',
          maBienThe: 1002,
          maPN: 102,
          maHD: null,
          maDonHang: null,
          ghiChu: '',
        },
      ]
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString()
    },
    badgeClass(loai) {
      switch (loai) {
        case 'Nhập':
          return 'badge bg-success'
        case 'Xuất':
          return 'badge bg-danger'
        case 'Điều chỉnh':
          return 'badge bg-warning text-dark'
        default:
          return 'badge bg-secondary'
      }
    },
  },
  mounted() {
    const maBienThe = this.$route.query.maBienThe
    if (maBienThe) {
      this.keyword = maBienThe
    }
    this.fetchLichSu()
  },
}
</script>
