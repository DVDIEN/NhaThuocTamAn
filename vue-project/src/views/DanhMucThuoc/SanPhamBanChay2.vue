<template>
  <section>
    <div class="container">
      <h2 class="section-title d-flex align-items-center">
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/24x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/smalls/tpcn_vitamin_khoang_chat_level_2_91b99b5a64.png"
          alt="Vitamin Icon"
          width="24"
          height="24"
          class="me-2"
        />
        Vitamin và Khoáng chất
      </h2>

      <div class="row">
        <div class="col-2-4 mb-4" v-for="item in products" :key="item.maThuoc">
          <router-link :to="`/chi-tiet/${item.maThuoc}`" class="text-decoration-none">
            <div class="card h-100">
              <img
                :src="getFullImage(item.hinhAnhChinh)"
                class="card-img-top"
                :alt="item.tenThuoc"
              />
              <div class="card-body">
                <h5 class="card-title">{{ item.tenThuoc }}</h5>
                <p class="card-text text-success fw-bold">Giá: {{ formatCurrency(item.giaBan) }}</p>
                <button class="btn btn-green w-100">Xem chi tiết</button>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const products = ref([])

function getFullImage(path) {
  if (!path) return 'https://via.placeholder.com/150'
  return `http://localhost:8080${path.startsWith('/') ? path : '/' + path}`
}

function formatCurrency(value) {
  return Number(value).toLocaleString('vi-VN') + 'đ'
}

async function fetchSanPhamVaSapXepBanChay(limit = 10) {
  try {
    const url = `http://localhost:8080/api/thuoc/search?daXoa=false&trangThai=true`
    const res = await fetch(url)
    const data = await res.json()

    products.value = data
      .filter((sp) => !sp.daXoa && sp.trangThai === true) // lọc lại ở frontend cho chắc
      .sort((a, b) => (b.soLuongDaBan || 0) - (a.soLuongDaBan || 0))
      .slice(0, limit)
  } catch (err) {
    console.error('Lỗi khi lấy danh sách thuốc:', err)
  }
}

onMounted(() => {
  fetchSanPhamVaSapXepBanChay(10)
})
</script>

<style scoped>
.col-2-4 {
  flex: 0 0 auto;
  width: 20%;
}
@media (max-width: 768px) {
  .col-2-4 {
    width: 50%;
  }
}
@media (max-width: 576px) {
  .col-2-4 {
    width: 100%;
  }
}
.card {
  border-radius: 12px;
  overflow: hidden;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.05);
}
.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 24px rgba(0, 100, 0, 0.4);
}
.card-img-top {
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  object-fit: cover;
}
.btn-green {
  background-color: #2e7d32;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}
.btn-green:hover {
  background-color: #1b5e20;
}
.section-title {
  margin-top: 20px;
  margin-bottom: 20px;
  font-weight: bold;
}
.card-title {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.6;
  height: calc(1.6em * 2);
}
</style>
