<template>
  <div
    class="modal fade show d-block"
    style="background: rgba(0, 0, 0, 0.5)"
    @click.self="emitClose"
  >
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title text-primary">📜 Lịch sử đơn hàng #{{ maDonHang }}</h5>
          <button class="btn-close" @click="emitClose"></button>
        </div>
        <div class="modal-body">
          <ul class="timeline list-unstyled ps-0">
            <li v-for="(item, index) in dataLichSu" :key="index" class="mb-4 position-relative">
              <div class="timeline-point bg-success"></div>
              <div class="ms-4">
                <div class="fw-bold">
                  {{ formatTime(item.thoiGian) }} —
                  <span class="text-primary">{{ trangThaiLabel(item.trangThaiMoi) }}</span>
                </div>
                <div class="text-muted small">
                  ({{ trangThaiLabel(item.trangThaiCu) }} ➔ {{ trangThaiLabel(item.trangThaiMoi) }})
                </div>
                <div>{{ item.ghiChu || '---' }}</div>
              </div>
            </li>
            <li v-if="dataLichSu.length === 0" class="text-muted">Chưa có lịch sử.</li>
          </ul>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="emitClose">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  maDonHang: Number,
  dataLichSu: Array,
})
const emit = defineEmits(['close'])

const emitClose = () => emit('close')

const formatTime = (t) => new Date(t).toLocaleString('vi-VN')
const trangThaiLabel = (t) =>
  ['Chờ xử lý', 'Đã xác nhận', 'Đang giao', 'Hoàn tất', 'Đã hủy'][t] || 'Không rõ'
</script>

<style scoped>
.timeline {
  position: relative;
  border-left: 2px solid #198754;
  padding-left: 20px;
}
.timeline-point {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  position: absolute;
  left: -7px;
  top: 3px;
}
.modal {
  z-index: 1050;
}
</style>
