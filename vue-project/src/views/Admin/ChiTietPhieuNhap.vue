<template>
  <div class="modal fade show d-block" style="background: rgba(0, 0, 0, 0.5)">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <!-- Header -->
        <div class="modal-header bg-success text-white">
          <h5>📑 Chi Tiết Phiếu Nhập Hàng #{{ phieuNhap?.maPN }}</h5>
          <button class="btn-close btn-close-white" @click="$emit('close')"></button>
        </div>

        <!-- Body -->
        <div class="modal-body" ref="printArea">
          <p><strong>Ngày nhập:</strong> {{ formatDate(phieuNhap.ngayNhap) }}</p>
          <p><strong>Nhân viên:</strong> {{ phieuNhap.nhanVien?.hoTen }}</p>
          <p><strong>Nhà cung cấp:</strong> {{ phieuNhap.nhaCungCap?.tenNCC }}</p>

          <table class="table table-bordered table-striped align-middle">
            <thead class="table-light text-center">
              <tr>
                <th>STT</th>
                <th>Tên sản phẩm</th>
                <th>ĐVT</th>
                <th>SL</th>
                <th>Đơn giá</th>
                <th>Ngày SX</th>
                <th>Hạn SD</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ct, i) in chiTietList" :key="i">
                <td>{{ i + 1 }}</td>
                <td class="text-start">
                  <div>
                    <strong>
                      {{ ct.tenBienThe || ct.tenThuoc || 'Không rõ' }}
                      <span v-if="ct.quyCachDongGoi"> - {{ ct.quyCachDongGoi }}</span>
                    </strong>
                  </div>
                  <div class="text-muted small">
                    {{ ct.maBienThe ? 'Mã biến thể: ' + ct.maBienThe : 'Mã thuốc: ' + ct.maThuoc }}
                  </div>
                </td>
                <td class="text-center">{{ ct.tenDonViTinh || 'N/A' }}</td>
                <td class="text-center">{{ ct.soLuong }}</td>
                <td class="text-end">{{ formatPrice(ct.donGiaNhap) }}</td>
                <td class="text-center">{{ formatDate(ct.ngaySanXuat) }}</td>
                <td
                  :class="['text-center', isSapHetHan(ct.hanSuDung) ? 'text-danger fw-bold' : '']"
                >
                  {{ formatDate(ct.hanSuDung) }}
                </td>
                <td class="text-end text-danger fw-bold">
                  {{ formatPrice(ct.soLuong * ct.donGiaNhap) }}
                </td>
              </tr>
              <tr>
                <td colspan="7" class="text-end fw-bold">Tổng cộng</td>
                <td class="text-end text-danger fw-bold">{{ formatPrice(tongTien) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="$emit('close')">
            <i class="bi bi-x-circle me-1"></i> Đóng
          </button>
          <button class="btn btn-success" @click="handlePrint">
            <i class="bi bi-printer me-1"></i> In phiếu nhập
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  phieuNhap: { type: Object, required: true },
  chiTietList: { type: Array, required: true },
})

const emit = defineEmits(['close', 'print'])
const printArea = ref(null)

const formatPrice = (n) => (n ?? 0).toLocaleString('vi-VN') + ' đ'
const formatDate = (d) => (d ? new Date(d).toLocaleDateString('vi-VN') : '')

const tongTien = computed(() =>
  props.chiTietList.reduce((sum, ct) => sum + ct.soLuong * ct.donGiaNhap, 0),
)

// 🔴 Kiểm tra hạn sử dụng còn <= 90 ngày → cảnh báo đỏ
const isSapHetHan = (han) => {
  if (!han) return false
  const hsd = new Date(han)
  const now = new Date()
  const diff = (hsd - now) / (1000 * 60 * 60 * 24) // ngày
  return diff <= 90
}

const handlePrint = () => {
  emit('print', printArea.value.innerHTML)
}
</script>
