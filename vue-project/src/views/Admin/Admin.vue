<template>
  <div class="dashboard container-fluid py-4">
    <h4 class="text-success mb-4"><i class="bi bi-speedometer2 me-2"></i>Dashboard thống kê</h4>

    <!-- Nút in báo cáo -->
    <div class="text-end mb-3">
      <button class="btn btn-outline-primary" @click="printReport">🖨️ In báo cáo PDF</button>
    </div>

    <!-- Bộ lọc ngày -->
    <div class="mb-3 d-flex justify-content-end align-items-center gap-3">
      <label class="mb-0 fw-bold">📅 Từ ngày:</label>
      <input type="date" class="form-control w-auto" v-model="fromDate" @change="loadAllData" />
      <label class="mb-0 fw-bold">Đến ngày:</label>
      <input type="date" class="form-control w-auto" v-model="toDate" @change="loadAllData" />
    </div>

    <!-- Cards thống kê -->
    <div class="row g-4 mb-4">
      <div class="col-xl-3 col-md-6" v-for="card in cards" :key="card.title">
        <div
          class="card shadow-sm border-0 h-100 text-white"
          :class="card.bg"
          @click="handleCardClick(card.key)"
        >
          <div class="card-body d-flex align-items-center justify-content-between">
            <div>
              <h6 class="text-white-50 mb-1">{{ card.title }}</h6>
              <h4 class="fw-bold">{{ card.value }}</h4>
            </div>
            <i :class="card.icon" class="fs-1 opacity-75"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng chi tiết -->
    <div v-if="selectedCard" class="card mb-4 shadow-sm border-0">
      <div class="card-header d-flex justify-content-between align-items-center">
        <strong>
          <i class="bi" :class="selectedCard === 'banchay' ? 'bi-fire' : 'bi-box-seam'"></i>
          {{ selectedCard === 'banchay' ? 'Top sản phẩm bán chạy' : 'Top tồn kho cao' }}
        </strong>
        <button class="btn btn-sm btn-outline-danger" @click="selectedCard = null">Đóng</button>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-bordered align-middle text-center">
          <thead class="table-light">
            <tr>
              <th>STT</th>
              <th>Ảnh</th>
              <th>Tên sản phẩm</th>
              <th>{{ selectedCard === 'banchay' ? 'Đã bán' : 'Tồn kho' }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in tableData" :key="index">
              <td>{{ index + 1 }}</td>
              <td>
                <img
                  :src="getImageUrl(item.hinhAnh)"
                  alt="Ảnh"
                  width="50"
                  height="50"
                  style="object-fit: cover; border-radius: 6px"
                />
              </td>
              <td>{{ item.ten }}</td>
              <td>{{ item.soLuong }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Biểu đồ -->
    <div class="row g-4 mb-4">
      <div class="col-md-8">
        <div class="card shadow-sm border-0 h-100">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span><i class="bi bi-bar-chart-line-fill me-2"></i>Doanh thu theo thời gian</span>
            <select v-model="selectedType" @change="loadChart" class="form-select w-auto">
              <option value="ngay">7 ngày</option>
              <option value="tuan">Tuần</option>
              <option value="thang">Tháng</option>
              <option value="nam">Năm</option>
            </select>
          </div>
          <div class="card-body" style="height: 360px">
            <Bar :data="chartData" :options="chartOptions" />
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card shadow-sm border-0 h-100">
          <div class="card-header d-flex align-items-center">
            <i class="bi bi-pie-chart-fill me-2"></i>
            <strong>Trạng thái đơn hàng</strong>
          </div>
          <div
            class="card-body d-flex justify-content-center align-items-center"
            style="height: 360px"
          >
            <div style="max-width: 300px; width: 100%">
              <Pie :data="chartTrangThaiData" :options="pieOptions" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Biểu đồ phiếu nhập -->
    <div class="row mb-4">
      <div class="col-md-12">
        <div class="card shadow-sm border-0">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span><i class="bi bi-truck me-2"></i>Biểu đồ tiền nhập hàng theo thời gian</span>
          </div>
          <div class="card-body" style="height: 360px">
            <Bar :data="chartPhieuNhap" :options="chartOptions" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Bar, Pie } from 'vue-chartjs'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale,
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, ArcElement, CategoryScale, LinearScale)

// 📌 In báo cáo PDF
const printReport = async () => {
  const element = document.querySelector('.dashboard')
  const canvas = await html2canvas(element, { scale: 2 })
  const imgData = canvas.toDataURL('image/png')

  const pdf = new jsPDF('p', 'mm', 'a4')
  const imgProps = pdf.getImageProperties(imgData)
  const pdfWidth = pdf.internal.pageSize.getWidth()
  const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width

  pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight)
  pdf.save(`BaoCaoThongKe_${new Date().toLocaleDateString('vi-VN')}.pdf`)
}

// 📌 Biến
const cards = ref([
  { title: 'Tổng sản phẩm', value: 0, icon: 'bi bi-capsule', bg: 'bg-success' },
  { title: 'Đơn hàng hôm nay', value: 0, icon: 'bi bi-bag-check', bg: 'bg-primary' },
  { title: 'Khách hàng', value: 0, icon: 'bi bi-people', bg: 'bg-info' },
  { title: 'Doanh thu hôm nay', value: '0₫', icon: 'bi bi-currency-dollar', bg: 'bg-warning' },
  {
    title: 'Top 5 bán chạy',
    value: 'Xem chi tiết',
    icon: 'bi bi-graph-up-arrow',
    bg: 'bg-danger',
    key: 'banchay',
  },
  {
    title: 'Top tồn kho cao',
    value: 'Xem chi tiết',
    icon: 'bi bi-graph-down-arrow',
    bg: 'bg-secondary',
    key: 'bankem',
  },
])

const fromDate = ref('')
const toDate = ref('')
const selectedType = ref('ngay')
const selectedCard = ref(null)
const tableData = ref([])
const chartData = ref({ labels: [], datasets: [] })
const chartPhieuNhap = ref({ labels: [], datasets: [] })
const chartTrangThaiData = ref({ labels: [], datasets: [] })

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: true } },
  scales: {
    y: { beginAtZero: true, ticks: { callback: (v) => v.toLocaleString('vi-VN') + ' ₫' } },
  },
}

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'right', align: 'center' } },
}

const colorMap = { 0: '#ffc107', 1: '#0d6efd', 2: '#fd7e14', 3: '#28a745', 4: '#dc3545' }

const getImageUrl = (fileName) => {
  if (!fileName) return '/images/default.png'
  if (fileName.startsWith('uploads/')) return `http://localhost:8080/${fileName}`
  return `http://localhost:8080/uploads/${fileName}`
}

const handleCardClick = async (key) => {
  if (!key) return
  selectedCard.value = key
  const url =
    key === 'banchay'
      ? 'http://localhost:8080/api/thong-ke/top-ban-chay'
      : 'http://localhost:8080/api/thong-ke/ton-kho-top'
  const res = await axios.get(url)
  tableData.value = res.data.map((item) => ({
    ten: item.tenThuoc || item.ten,
    soLuong: item.soLuong || item.soLuongTon,
    hinhAnh: item.hinhAnh,
  }))
}

const convertTrangThai = (val) =>
  ['Chờ xác nhận', 'Đã xác nhận', 'Đang giao', 'Hoàn tất', 'Đã hủy'][val] || 'Không xác định'

const buildDateParam = () => {
  if (fromDate.value && toDate.value) return `?from=${fromDate.value}&to=${toDate.value}`
  if (fromDate.value) return `?ngay=${fromDate.value}`
  return ''
}

const loadChart = async () => {
  const param = buildDateParam()
  const res = await axios.get(
    `http://localhost:8080/api/thong-ke/doanh-thu?loai=${selectedType.value}${param.replace('?', '&')}`,
  )
  chartData.value = {
    labels: Object.keys(res.data),
    datasets: [
      { label: 'Doanh thu (VNĐ)', backgroundColor: '#4caf50', data: Object.values(res.data) },
    ],
  }
  await loadPhieuNhapChart()
}

const loadPhieuNhapChart = async () => {
  const param = buildDateParam()
  const res = await axios.get(
    `http://localhost:8080/api/thong-ke/phieu-nhap?kieu=${selectedType.value}${param.replace('?', '&')}`,
  )
  chartPhieuNhap.value = {
    labels: Object.keys(res.data),
    datasets: [
      {
        label: 'Giá trị phiếu nhập (VNĐ)',
        backgroundColor: '#0d6efd',
        data: Object.values(res.data),
      },
    ],
  }
}

const loadTrangThaiDonHang = async () => {
  const param = buildDateParam()
  const res = await axios.get(`http://localhost:8080/api/thong-ke/trang-thai-don${param}`)
  const labels = [],
    data = [],
    backgroundColor = []
  res.data.forEach((item) => {
    labels.push(convertTrangThai(item.trangThai))
    data.push(item.soLuong)
    backgroundColor.push(colorMap[item.trangThai] || '#6c757d')
  })
  chartTrangThaiData.value = {
    labels,
    datasets: [{ label: 'Số lượng đơn hàng', backgroundColor, data }],
  }
}

const loadAllData = async () => {
  const res = await axios.get(`http://localhost:8080/api/thong-ke/cards`)
  cards.value[0].value = res.data.tongSanPham
  cards.value[1].value = res.data.donHangHomNay
  cards.value[2].value = res.data.tongKhachHang
  cards.value[3].value = res.data.doanhThuHomNay.toLocaleString('vi-VN') + '₫'
  await loadChart()
  await loadTrangThaiDonHang()
}

onMounted(loadAllData)
</script>

<style scoped>
.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}
@media print {
  button,
  select,
  input {
    display: none !important;
  }
  .dashboard {
    padding: 0;
  }
}
</style>
