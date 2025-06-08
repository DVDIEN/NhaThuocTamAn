<template>
  <div class="card p-4 shadow-sm">
    <h5 class="fw-bold mb-4">Đơn hàng của bạn</h5>

    <ul class="nav nav-tabs mb-4">
      <li class="nav-item" v-for="status in tabs" :key="status.value">
        <button
          class="nav-link"
          :class="{ active: currentStatus === status.value }"
          @click="currentStatus = status.value"
        >
          {{ status.label }}
        </button>
      </li>
    </ul>

    <div v-for="order in filteredOrders" :key="order.id" class="order-item mb-4 border rounded p-3">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <div>
          <strong>Mã đơn hàng: #{{ order.id }}</strong
          ><br />
          <small class="text-muted">Ngày đặt: {{ order.date }}</small>
        </div>
        <span :class="statusClass(order.status)">{{ getStatusLabel(order.status) }}</span>
      </div>

      <div class="d-flex align-items-center gap-3">
        <img
          :src="order.productImage"
          class="img-thumbnail"
          style="width: 80px; height: 80px; object-fit: cover"
        />
        <div class="flex-grow-1">
          <div class="fw-semibold">{{ order.product }}</div>
          <div class="text-muted small">Số lượng: {{ order.quantity }}</div>
        </div>
        <div class="fw-bold text-success">{{ formatPrice(order.total) }}</div>
      </div>

      <div class="text-end mt-3 d-flex gap-2 justify-content-end">
        <button class="btn btn-outline-primary btn-sm" @click="viewDetail(order)">
          <i class="bi bi-eye me-1"></i> Xem chi tiết
        </button>
        <button
          v-if="order.status === 'cho-xac-nhan'"
          class="btn btn-outline-danger btn-sm"
          @click="cancelOrder(order)"
        >
          <i class="bi bi-x-circle me-1"></i> Hủy đơn
        </button>
        <button
          v-if="order.status === 'da-giao'"
          class="btn btn-outline-success btn-sm"
          @click="reviewOrder(order)"
        >
          <i class="bi bi-star me-1"></i> Đánh giá
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const currentStatus = ref('cho-xac-nhan')

const tabs = [
  { value: 'cho-xac-nhan', label: 'Chờ xác nhận' },
  { value: 'cho-lay-hang', label: 'Chờ lấy hàng' },
  { value: 'cho-giao-hang', label: 'Chờ giao hàng' },
  { value: 'da-giao', label: 'Đã giao' },
  { value: 'tra-hang', label: 'Trả hàng' },
  { value: 'da-huy', label: 'Đã huỷ' },
]

const orders = ref([
  {
    id: 1001,
    date: '01/04/2025',
    status: 'cho-xac-nhan',
    product: 'Siro ho Prospan',
    quantity: 2,
    total: 198000,
    productImage:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/375x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/DSC_03594_d9d39b4d3f.jpg',
  },
  {
    id: 1002,
    date: '15/04/2025',
    status: 'da-giao',
    product: 'Vitamin C 500mg',
    quantity: 1,
    total: 95000,
    productImage:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/375x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/DSC_09456_d8bf7244c8.jpg',
  },
  {
    id: 1003,
    date: '25/04/2025',
    status: 'da-huy',
    product: 'Khẩu trang y tế',
    quantity: 5,
    total: 75000,
    productImage:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/375x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/IMG_0188_f4db0565ff.jpg',
  },
])

const filteredOrders = computed(() =>
  orders.value.filter((order) => order.status === currentStatus.value),
)

const formatPrice = (price) => price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })

const statusClass = (status) => {
  switch (status) {
    case 'da-giao':
      return 'badge bg-success'
    case 'cho-giao-hang':
      return 'badge bg-warning text-dark'
    case 'cho-lay-hang':
      return 'badge bg-info text-dark'
    case 'cho-xac-nhan':
      return 'badge bg-secondary'
    case 'tra-hang':
      return 'badge bg-danger'
    case 'da-huy':
      return 'badge bg-dark'
    default:
      return 'badge bg-light text-dark'
  }
}

const getStatusLabel = (status) => {
  const found = tabs.find((t) => t.value === status)
  return found ? found.label : status
}

const viewDetail = (order) => {
  alert(`Xem chi tiết đơn hàng #${order.id}`)
}

const cancelOrder = (order) => {
  alert(`Bạn đã hủy đơn hàng #${order.id}`)
}

const reviewOrder = (order) => {
  alert(`Bạn đã chọn đánh giá đơn hàng #${order.id}`)
}
</script>
