<template>
  <div class="container mt-4">
    <!-- Quay lại -->
    <router-link to="/admin/san-pham" class="btn btn-outline-secondary mb-3">
      ← Quay lại danh sách
    </router-link>

    <!-- Tiêu đề -->
    <h4 class="text-success fw-bold mb-4 d-flex align-items-center">
      <i class="bi bi-capsule me-2"></i>
      Chi tiết thuốc: <span class="ms-2 text-dark">{{ thuoc.tenThuoc }}</span>
    </h4>

    <!-- Thông tin thuốc -->
    <div class="card shadow-sm mb-4">
      <div class="row g-0">
        <!-- Ảnh -->
        <div class="col-md-4 text-center p-3 border-end">
          <img
            v-if="thuoc.hinhAnhChinh"
            :src="getImageUrl(thuoc.hinhAnhChinh)"
            alt="Ảnh chính"
            class="img-fluid rounded shadow-sm"
            style="max-height: 250px; object-fit: cover"
          />
          <div v-else class="text-muted fst-italic">Không có ảnh</div>
        </div>

        <!-- Thông tin chi tiết -->
        <div class="col-md-8 p-3">
          <div class="row">
            <div class="col-sm-6 mb-2"><strong>Mã thuốc:</strong> {{ thuoc.maThuoc }}</div>
            <div class="col-sm-6 mb-2">
              <strong>Danh mục:</strong> {{ thuoc.tenDanhMuc || '-' }}
            </div>
            <div class="col-sm-6 mb-2">
              <strong>Giá bán:</strong>
              <span class="text-danger fw-bold">{{ formatCurrency(thuoc.giaBan) }}</span>
            </div>
            <div class="col-sm-6 mb-2">
              <strong>Đơn vị tính:</strong> {{ thuoc.tenDonViTinh || '-' }}
            </div>
            <div class="col-sm-6 mb-2"><strong>Dạng bào chế:</strong> {{ thuoc.dangBaoChe }}</div>
            <div class="col-sm-6 mb-2">
              <strong>Quy cách:</strong> {{ thuoc.moTaQuyCach || '-' }}
            </div>
            <div class="col-sm-6 mb-2"><strong>Xuất xứ:</strong> {{ thuoc.xuatXu }}</div>
            <div class="col-sm-6 mb-2">
              <strong>Số lượng trong kho:</strong> {{ thuoc.soLuong }}
            </div>
            <div class="col-sm-6 mb-2">
              <strong>Tổng số lượng biến thể:</strong> {{ tongSoLuongBienThe }}
            </div>
            <div class="col-sm-6 mb-2">
              <strong>Trạng thái:</strong>
              <span class="badge" :class="thuoc.trangThai ? 'bg-success' : 'bg-danger'">
                {{ thuoc.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Thông tin bổ sung -->
    <div class="card shadow-sm mb-4">
      <div class="card-header bg-light fw-bold text-primary">🧪 Thông tin bổ sung</div>
      <div class="card-body">
        <p><strong>Thành phần:</strong> {{ thuoc.thanhPhan }}</p>
        <p><strong>Công dụng:</strong> {{ thuoc.congDung }}</p>
        <p><strong>Hướng dẫn sử dụng:</strong> {{ thuoc.huongDanSuDung }}</p>
        <p><strong>Mô tả thêm:</strong> {{ thuoc.moTaThem }}</p>
      </div>
    </div>

    <!-- Danh sách biến thể -->
    <div class="card shadow-sm">
      <div class="card-header bg-light fw-bold text-primary">🧬 Biến thể thuốc</div>
      <div class="card-body p-0">
        <table class="table table-bordered table-hover text-center mb-0">
          <thead class="table-success">
            <tr>
              <th>Mã biến thể</th>
              <th>Tên biến thể</th>
              <th>Đơn vị tính</th>
              <th>Quy cách</th>
              <th>Giá bán</th>
              <th>Số lượng</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="bienTheList.length === 0">
              <td colspan="7" class="text-muted">Không có biến thể nào.</td>
            </tr>
            <tr v-for="bt in bienTheList" :key="bt.maBienThe">
              <td>{{ bt.maBienThe }}</td>
              <td>{{ bt.tenBienThe }}</td>
              <td>{{ bt.tenDonViTinh || '-' }}</td>
              <td>{{ bt.moTaQuyCach || '-' }}</td>
              <td class="text-danger fw-bold">{{ formatCurrency(bt.giaBan) }}</td>
              <td>{{ bt.soLuong }}</td>
              <td>
                <span class="badge" :class="bt.trangThai ? 'bg-success' : 'bg-danger'">
                  {{ bt.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const maThuoc = route.params.maThuoc

const thuoc = ref({})
const bienTheList = ref([])

function getImageUrl(path) {
  return path ? `http://localhost:8080/${path.replace(/^\/+/g, '')}` : ''
}

function formatCurrency(val) {
  if (!val) return '-'
  return Number(val).toLocaleString('vi-VN') + 'đ'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

function fetchChiTietThuoc() {
  fetch(`http://localhost:8080/api/thuoc/${maThuoc}`)
    .then((res) => res.json())
    .then((data) => (thuoc.value = data))
}

function fetchBienThe() {
  fetch(`http://localhost:8080/api/bienthe/thuoc/${maThuoc}`)
    .then((res) => res.json())
    .then((data) => (bienTheList.value = data))
}

const tongSoLuongBienThe = computed(() =>
  bienTheList.value.reduce((sum, bt) => sum + (bt.soLuong || 0), 0),
)

onMounted(() => {
  fetchChiTietThuoc()
  fetchBienThe()
})
</script>
