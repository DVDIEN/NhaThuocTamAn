<template>
  <div>
    <!-- Nút mở modal -->
    <button class="btn btn-outline-secondary btn-sm" @click="moModal = true">
      🕓 Xem lịch sử đơn hàng
    </button>

    <!-- Modal -->
    <div
      class="modal fade show d-block"
      tabindex="-1"
      v-if="moModal"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title text-success">📜 Lịch sử đơn hàng #{{ maDonHang }}</h5>
            <button type="button" class="btn-close" @click="moModal = false"></button>
          </div>
          <div class="modal-body">
            <ul class="timeline list-unstyled ps-0">
              <li v-for="(item, index) in lichSu" :key="index" class="mb-4 position-relative">
                <div class="timeline-point bg-success"></div>
                <div class="ms-4">
                  <div class="fw-bold">
                    {{ formatTime(item.thoiGian) }} — {{ trangThaiLabel(item.trangThaiMoi) }}
                  </div>
                  <div class="text-muted small">{{ item.ghiChu }}</div>
                </div>
              </li>
              <li v-if="lichSu.length === 0" class="text-muted">Không có lịch sử đơn hàng.</li>
            </ul>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="moModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  maDonHang: Number,
})

const moModal = ref(false)
const lichSu = ref([])

// Giả lập dữ liệu lịch sử
onMounted(() => {
  lichSu.value = [
    {
      trangThaiCu: null,
      trangThaiMoi: 0,
      thoiGian: '2024-05-27T08:00:00',
      ghiChu: 'Khách đặt hàng',
    },
    {
      trangThaiCu: 0,
      trangThaiMoi: 1,
      thoiGian: '2024-05-27T09:00:00',
      ghiChu: 'Đơn đã được xác nhận',
    },
    {
      trangThaiCu: 1,
      trangThaiMoi: 2,
      thoiGian: '2024-05-27T10:30:00',
      ghiChu: 'Đơn đang giao',
    },
    {
      trangThaiCu: 2,
      trangThaiMoi: 3,
      thoiGian: '2024-05-27T15:00:00',
      ghiChu: 'Đơn hoàn tất',
    },
  ]
})

const formatTime = (t) => new Date(t).toLocaleString('vi-VN')
const trangThaiLabel = (t) =>
  ['Chờ xử lý', 'Xác nhận', 'Đang giao', 'Hoàn tất', 'Hủy'][t] || 'Không rõ'
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
