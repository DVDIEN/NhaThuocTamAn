<template>
  <div>
    <Header />
    <Navbar />

    <main class="container py-4">
      <div class="row">
        <!-- Bộ lọc bên trái -->
        <div class="col-md-3">
          <div class="border rounded p-3 bg-light">
            <h5 class="mb-3"><i class="bi bi-funnel-fill me-2"></i>Bộ lọc nâng cao</h5>
            <div v-for="(boLoc, index) in boLocs" :key="index" class="mb-3">
              <button
                class="btn btn-link text-dark p-0 w-100 text-start"
                @click="toggleBoLoc(index)"
              >
                {{ boLoc.tieuDe }}
                <i
                  :class="boLoc.moRong ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"
                  class="float-end"
                ></i>
              </button>
              <div v-if="boLoc.moRong" class="mt-2">
                <div v-for="(item, i) in boLoc.options" :key="i">
                  <input type="checkbox" class="form-check-input me-2" disabled />{{ item }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Danh sách sản phẩm -->
        <div class="col-md-9">
          <h4 class="mb-3">Kết quả tìm kiếm cho "{{ keyword }}"</h4>
          <div class="row g-3">
            <router-link
              v-for="sp in sanPhamHienThi"
              :key="sp.maThuoc"
              :to="`/chi-tiet/${sp.maThuoc}`"
              class="col-lg-3 col-md-4 col-sm-6 text-decoration-none text-dark"
            >
              <div class="card h-100 shadow-sm product-card">
                <div class="position-relative image-wrapper">
                  <img :src="getFullImage(sp.hinhAnhChinh)" class="product-img" />
                </div>
                <div class="card-body d-flex flex-column">
                  <h6 class="card-title">{{ sp.tenThuoc }}</h6>
                  <p class="mb-1 text-danger fw-bold">{{ formatCurrency(sp.giaBan) }}</p>
                  <button class="btn btn-primary w-100 mt-auto">Xem chi tiết</button>
                </div>
              </div>
            </router-link>
          </div>

          <!-- Nút xem thêm -->
          <div v-if="conSanPhamChuaHien" class="text-center mt-4">
            <button class="btn btn-outline-success" @click="soLuongHienThi += 8">
              Xem thêm {{ sanPhamList.length - soLuongHienThi }} sản phẩm
            </button>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script>
import Header from '../User/Header.vue'
import Navbar from '../User/Navbar.vue'
import Footer from '../User/Footer.vue'

export default {
  name: 'SearchResult',
  components: { Header, Navbar, Footer },
  data() {
    return {
      keyword: this.$route.query.keyword || '',
      sanPhamList: [],
      soLuongHienThi: 12,
      boLocs: [
        {
          tieuDe: 'Đối tượng sử dụng',
          moRong: false,
          options: ['Trẻ em', 'Người lớn', 'Phụ nữ mang thai'],
        },
        {
          tieuDe: 'Giá bán',
          moRong: false,
          options: ['Dưới 100k', '100k - 300k', 'Trên 300k'],
        },
        {
          tieuDe: 'Mùi vị',
          moRong: false,
          options: ['Cam', 'Dâu', 'Không mùi'],
        },
        {
          tieuDe: 'Nước sản xuất',
          moRong: false,
          options: ['Việt Nam', 'Úc', 'Mỹ'],
        },
        {
          tieuDe: 'Chỉ định',
          moRong: false,
          options: ['Bổ sung canxi', 'Tăng đề kháng'],
        },
        {
          tieuDe: 'Thương hiệu',
          moRong: false,
          options: ['Kudos', 'Brauer'],
        },
      ],
    }
  },
  computed: {
    sanPhamHienThi() {
      return this.sanPhamList.slice(0, this.soLuongHienThi)
    },
    conSanPhamChuaHien() {
      return this.soLuongHienThi < this.sanPhamList.length
    },
  },
  methods: {
    toggleBoLoc(index) {
      this.boLocs[index].moRong = !this.boLocs[index].moRong
    },
    formatCurrency(value) {
      return Number(value).toLocaleString('vi-VN') + 'đ'
    },
    getFullImage(path) {
      if (!path) return 'https://via.placeholder.com/150'
      return `http://localhost:8080${path.startsWith('/') ? path : '/' + path}`
    },
    async fetchSearchResults() {
      try {
        const res = await fetch(`http://localhost:8080/api/thuoc/search?keyword=${this.keyword}`)
        const data = await res.json()
        this.sanPhamList = data
      } catch (err) {
        console.error('Lỗi khi tìm kiếm sản phẩm:', err)
      }
    },
  },
  mounted() {
    this.fetchSearchResults()
  },
}
</script>

<style scoped>
/* Tái sử dụng toàn bộ CSS từ giao diện trước */
.card-title {
  font-size: 0.95rem;
  height: 2.8em;
  overflow: hidden;
  line-height: 1.4;
}
.product-card {
  transition:
    transform 0.2s ease-in-out,
    border-color 0.2s;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  background-color: #fff;
}
.product-card:hover {
  transform: translateY(-5px);
  border-color: #28a745;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}
.image-wrapper {
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fafafa;
  position: relative;
}
.product-img {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}
.product-card:hover .product-img {
  transform: scale(1.05);
}
.card-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 12px 16px;
}
.card-body button {
  margin-top: auto;
  font-weight: 500;
  font-size: 0.9rem;
  padding: 8px 12px;
  border-radius: 6px;
}
.btn-link {
  text-decoration: none !important;
  color: #000 !important;
}
.btn-link:hover {
  color: #28a745 !important;
}
</style>
