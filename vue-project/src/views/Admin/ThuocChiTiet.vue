<template>
  <div class="modal fade show d-block" v-if="show" @click.self="$emit('close')">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header bg-success text-white">
          <h5 class="modal-title">🩺 Chi tiết thuốc</h5>
          <button class="btn-close btn-close-white" @click="$emit('close')"></button>
        </div>
        <div class="modal-body p-4">
          <div class="row mb-3">
            <!-- Ảnh chính -->
            <div class="col-md-4 text-center">
              <img
                v-if="thuoc.hinhAnhChinh"
                :src="getFullImageUrl(thuoc.hinhAnhChinh)"
                class="rounded shadow-sm mb-2"
                width="100%"
              />
              <p class="text-muted small mb-0">Ảnh chính</p>
            </div>

            <!-- Ảnh phụ (thumbnail) -->
            <div class="col-md-8">
              <div class="d-flex flex-wrap gap-2">
                <img
                  v-for="(thumb, index) in thuoc.thumbnails"
                  :key="index"
                  :src="getFullImageUrl(thumb)"
                  class="rounded shadow-sm"
                  width="80"
                  height="80"
                />
              </div>
              <p class="text-muted small mt-1">Ảnh phụ</p>
            </div>
          </div>

          <table class="table table-bordered align-middle">
            <tbody>
              <tr>
                <th width="20%">Tên thuốc</th>
                <td>{{ thuoc.tenThuoc }}</td>
                <th>Hoạt chất</th>
                <td>{{ thuoc.hoatChat }}</td>
              </tr>
              <tr>
                <th>Đơn vị tính</th>
                <td>{{ thuoc.donViTinh }}</td>
                <th>Giá bán</th>
                <td>{{ formatCurrency(thuoc.giaBan) }}</td>
              </tr>
              <tr>
                <th>Dạng bào chế</th>
                <td>{{ thuoc.dangBaoChe }}</td>
                <th>Quy cách</th>
                <td>{{ thuoc.quyCach }}</td>
              </tr>
              <tr>
                <th>Thành phần</th>
                <td>{{ thuoc.thanhPhan }}</td>
                <th>Xuất xứ</th>
                <td>{{ thuoc.xuatXu }}</td>
              </tr>
              <tr>
                <th>Danh mục</th>
                <td>{{ thuoc.tenDanhMuc || '-' }}</td>
                <th>Nhà cung cấp</th>
                <td>{{ thuoc.tenNhaCungCap || '-' }}</td>
              </tr>
              <tr>
                <th>Trạng thái</th>
                <td
                  colspan="3"
                  :class="thuoc.trangThai ? 'text-success fw-bold' : 'text-danger fw-bold'"
                >
                  {{ thuoc.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                </td>
              </tr>
              <tr>
                <th>Công dụng</th>
                <td colspan="3">{{ thuoc.congDung }}</td>
              </tr>
              <tr>
                <th>Hướng dẫn sử dụng</th>
                <td colspan="3">{{ thuoc.huongDanSuDung }}</td>
              </tr>
              <tr>
                <th>Mô tả thêm</th>
                <td colspan="3">{{ thuoc.moTaThem }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="$emit('close')">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  thuoc: Object,
  show: Boolean,
})

function getFullImageUrl(path) {
  if (!path) return ''
  return `http://localhost:8080/${path.startsWith('/') ? path.slice(1) : path}`
}

function formatCurrency(value) {
  if (!value) return '0đ'
  return Number(value).toLocaleString('vi-VN') + 'đ'
}
</script>

<style scoped>
.modal-backdrop {
  z-index: 1040;
}
.modal {
  z-index: 1050;
}
th {
  background-color: #f8f9fa;
  white-space: nowrap;
}
img {
  object-fit: cover;
}
</style>
