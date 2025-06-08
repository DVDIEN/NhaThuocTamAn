<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">📦 Quản Lý Kho Hàng</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-4">
        <input
          v-model="keyword"
          type="text"
          class="form-control"
          placeholder="Tìm theo tên thuốc hoặc biến thể..."
        />
      </div>
      <div class="col-md-3">
        <select v-model="filterStatus" class="form-select">
          <option value="">Tất cả trạng thái</option>
          <option value="het">❌ Hết hàng</option>
          <option value="saphet">⚠️ Sắp hết</option>
          <option value="binhthuong">✅ Bình thường</option>
        </select>
      </div>
    </div>

    <!-- Bảng -->
    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle">
        <thead class="table-success text-center">
          <tr>
            <th>Mã biến thể</th>
            <th>Tên thuốc</th>
            <th>Biến thể</th>
            <th>Số lượng tồn</th>
            <th>Ngày cập nhật</th>
            <th>Ghi chú</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="kho in filteredKhoHang" :key="kho.id">
            <td class="text-center">{{ kho.maBienThe }}</td>
            <td>{{ kho.tenThuoc }}</td>
            <td>{{ kho.tenBienThe }}</td>
            <td class="text-center fw-bold">
              <span :class="getStockClass(kho.soLuongTon)">
                {{ kho.soLuongTon }}
              </span>
            </td>
            <td>{{ formatDate(kho.ngayCapNhat) }}</td>
            <td>{{ kho.ghiChu || '...' }}</td>
            <td class="text-center">
              <button class="btn btn-sm btn-primary me-2" @click="openEditModal(kho)">
                <i class="bi bi-pencil-square"></i> Sửa
              </button>
              <router-link
                :to="`/admin/lich-su-nhap-xuat?maBienThe=${kho.maBienThe}`"
                class="btn btn-sm btn-outline-secondary"
              >
                <i class="bi bi-clock-history"></i> Lịch sử
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal chỉnh sửa -->
    <div v-if="editModal" class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title text-success">📝 Cập nhật kho hàng</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <p><strong>Biến thể:</strong> {{ selectedKho.tenBienThe }}</p>
            <div class="mb-3">
              <label class="form-label">Số lượng tồn</label>
              <input type="number" v-model.number="selectedKho.soLuongTon" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Ghi chú</label>
              <textarea v-model="selectedKho.ghiChu" class="form-control"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="closeModal">Đóng</button>
            <button class="btn btn-success" @click="saveUpdate">Lưu</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="editModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      khoHang: [],
      keyword: '',
      filterStatus: '',
      editModal: false,
      selectedKho: null,
    }
  },
  computed: {
    filteredKhoHang() {
      return this.khoHang.filter((k) => {
        const keywordMatch = (k.tenThuoc + ' ' + k.tenBienThe)
          .toLowerCase()
          .includes(this.keyword.toLowerCase())

        let statusMatch = true
        if (this.filterStatus === 'het') {
          statusMatch = k.soLuongTon === 0
        } else if (this.filterStatus === 'saphet') {
          statusMatch = k.soLuongTon > 0 && k.soLuongTon <= 30
        } else if (this.filterStatus === 'binhthuong') {
          statusMatch = k.soLuongTon > 30
        }

        return keywordMatch && statusMatch
      })
    },
  },
  methods: {
    async fetchKhoHang() {
      this.khoHang = [
        {
          id: 1,
          maBienThe: 1001,
          tenThuoc: 'Paracetamol',
          tenBienThe: 'Viên 500mg',
          soLuongTon: 120,
          ngayCapNhat: '2024-06-01T08:30:00',
          ghiChu: 'Hàng mới nhập',
        },
        {
          id: 2,
          maBienThe: 1002,
          tenThuoc: 'Amoxicillin',
          tenBienThe: 'Hộp 250mg',
          soLuongTon: 15,
          ngayCapNhat: '2024-06-02T10:45:00',
          ghiChu: '',
        },
        {
          id: 3,
          maBienThe: 1003,
          tenThuoc: 'Vitamin C',
          tenBienThe: 'Tuýp 10 viên',
          soLuongTon: 0,
          ngayCapNhat: '2024-06-03T14:10:00',
          ghiChu: 'Khuyến mãi tháng 6',
        },
      ]
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString()
    },
    openEditModal(kho) {
      this.selectedKho = { ...kho }
      this.editModal = true
    },
    closeModal() {
      this.editModal = false
      this.selectedKho = null
    },
    async saveUpdate() {
      alert('✅ Đã lưu thay đổi (giả lập)')
      this.fetchKhoHang()
      this.closeModal()
    },
    getStockClass(soLuong) {
      if (soLuong === 0) return 'text-danger'
      if (soLuong <= 30) return 'text-warning fw-bold'
      return 'text-success'
    },
  },
  mounted() {
    this.fetchKhoHang()
  },
}
</script>
