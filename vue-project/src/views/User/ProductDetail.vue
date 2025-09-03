<template>
  <div class="product-detail-bg">
    <Header />
    <Navbar />

    <div class="container py-4">
      <div class="product-card" v-if="product">
        <div class="row g-4 align-items-start">
          <div class="col-md-5 text-center">
            <img :src="currentImage" class="product-img" alt="Ảnh chính" ref="productImageRef" />
            <div
              v-if="product.thumbnails?.length"
              class="product-thumbs-slider mt-3 position-relative"
            >
              <button class="btn-slider left" @click="scrollThumbnails('left')">‹</button>
              <div class="product-thumbs d-flex justify-content-center">
                <img
                  v-for="(thumb, index) in product.thumbnails"
                  :key="index"
                  :src="getImageUrl(thumb)"
                  :alt="'ảnh phụ ' + (index + 1)"
                  :class="{ active: currentThumbIndex === index }"
                  @click="setCurrentImage(index)"
                />
              </div>
              <button class="btn-slider right" @click="scrollThumbnails('right')">›</button>
            </div>
          </div>

          <div class="col-md-7">
            <h5>
              {{ selectedBienThe?.tenBienThe || product.tenThuoc }}
            </h5>

            <div class="rating">
              <span v-for="n in 5" :key="n">
                <i
                  class="bi"
                  :class="
                    n <= Math.round(averageRating)
                      ? 'bi-star-fill text-warning'
                      : 'bi-star text-muted'
                  "
                ></i>
              </span>
              <small>{{ totalReviews }} đánh giá</small>
            </div>

            <div class="price my-2">{{ formatCurrency(product.giaBan) }}</div>

            <ul class="list-unstyled">
              <li><strong>Xuất xứ:</strong> {{ product.xuatXu }}</li>
              <li><strong>Danh mục:</strong> {{ product.tenDanhMuc }}</li>
              <li><strong>Dạng bào chế:</strong> {{ product.dangBaoChe }}</li>
              <li><strong>Thành phần:</strong> {{ product.thanhPhan }}</li>
              <li>
                <strong>Tồn kho:</strong> <span class="text-danger fw-bold">{{ soLuongTon }}</span>
              </li>
            </ul>

            <!-- Phần CHỌN LOẠI đã sửa -->
            <div class="mb-3" v-if="product?.tenDonViTinh">
              <label class="fw-bold">Chọn loại:</label>
              <div class="d-flex flex-wrap gap-2 mt-1">
                <!-- Loại chính -->
                <div
                  class="variant-box"
                  :class="{ active: !selectedBienThe }"
                  @click="toggleBienThe(null)"
                >
                  {{ product?.tenDonViTinh }}
                  <span v-if="!selectedBienThe" class="check-icon">✔</span>
                </div>

                <!-- Biến thể -->
                <div
                  v-for="bt in danhSachBienThe"
                  :key="bt?.maBienThe"
                  v-if="
                    !(
                      bt?.tenDonViTinh === product?.tenDonViTinh &&
                      bt?.moTaQuyCach === product?.moTaQuyCach
                    )
                  "
                  class="variant-box"
                  :class="{ active: selectedBienThe?.maBienThe === bt?.maBienThe }"
                  @click="toggleBienThe(bt)"
                >
                  <template
                    v-if="
                      bt?.tenDonViTinh === product?.tenDonViTinh &&
                      bt?.moTaQuyCach !== product?.moTaQuyCach
                    "
                  >
                    <!-- Chỉ khác quy cách -->
                    {{ bt?.moTaQuyCach }}
                  </template>

                  <template
                    v-else-if="
                      bt?.tenDonViTinh !== product?.tenDonViTinh &&
                      bt?.moTaQuyCach === product?.moTaQuyCach
                    "
                  >
                    <!-- Chỉ khác đơn vị tính -->
                    {{ bt?.tenDonViTinh }}
                  </template>

                  <template
                    v-else-if="
                      bt?.tenDonViTinh !== product?.tenDonViTinh &&
                      bt?.moTaQuyCach !== product?.moTaQuyCach
                    "
                  >
                    <!-- Khác cả 2 → Mặc định chỉ hiển thị quy cách -->
                    {{ bt?.moTaQuyCach }}
                  </template>

                  <span v-if="selectedBienThe?.maBienThe === bt?.maBienThe" class="check-icon">
                    ✔
                  </span>
                </div>
              </div>
            </div>

            <!-- Chọn số lượng -->
            <!-- Chọn số lượng -->
            <div class="mb-3">
              <label class="fw-bold me-3">Chọn số lượng</label>
              <div class="quantity-selector">
                <button @click="giamSoLuong" class="btn-adjust">−</button>
                <input
                  type="number"
                  class="qty-input"
                  v-model="soLuongInput"
                  @blur="validateSoLuong"
                  min="1"
                  :max="soLuongTon"
                />

                <button @click="tangSoLuong" class="btn-adjust">+</button>
              </div>
            </div>

            <!-- Nút mua hàng -->
            <div class="d-flex flex-column flex-md-row gap-3">
              <button v-if="!isDangBan" class="btn btn-secondary flex-fill" disabled>
                NGỪNG BÁN
              </button>

              <template v-else-if="soLuongTon > 0">
                <button class="btn btn-cart flex-fill" @click="themVaoGio">
                  <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                </button>
                <button class="btn btn-buy flex-fill" @click="muaNgay">MUA NGAY<br /></button>
              </template>

              <button v-else class="btn btn-secondary flex-fill" disabled>HẾT HÀNG</button>

              <button class="btn btn-compare flex-fill" @click="themVaoSoSanh">
                <img
                  src="https://cdn.nhathuoclongchau.com.vn/unsafe/40x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/smalls/48px_fe435f0cad.png"
                  alt="So sánh"
                  class="compare-icon me-1"
                />
                So sánh
              </button>
            </div>
          </div>
        </div>

        <ProductIntroduction />
        <ProductRelated :maDM="product?.maDM" :excludeId="product?.maThuoc" />
        <DanhGia :key="maDanhGiaKey" />
      </div>
    </div>

    <Footer />
    <CompareBar />
    <ToastMessage ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, provide, computed } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import Header from './Header.vue'
import Navbar from './Navbar.vue'
import ProductIntroduction from './ProductIntroduction.vue'
import ProductRelated from './ProductRelated.vue'
import DanhGia from './DanhGia.vue'
import Footer from './Footer.vue'
import CompareBar from './CompareBar.vue'
import ToastMessage from '../ToastMessage.vue'
import { CartStore } from './CartStore.js'

const averageRating = ref(0)
const totalReviews = ref(0)

async function fetchRating() {
  if (!product.value?.maThuoc) return
  try {
    const res = await axios.get(`http://localhost:8080/api/danh-gia/thong-ke`, {
      params: { maThuoc: product.value.maThuoc },
    })
    averageRating.value = res.data.averageRating || 0
    totalReviews.value = res.data.totalReviews || 0
  } catch (err) {
    console.error('Lỗi lấy thống kê đánh giá:', err)
  }
}

const maDanhGiaKey = computed(() => product.value?.maThuoc || Math.random())

const isDangBan = computed(() => {
  return selectedBienThe.value
    ? selectedBienThe.value.trangThai !== false
    : product.value?.trangThai !== false
})

const route = useRoute()
const router = useRouter()
let id = route.params.id

const product = ref(null)
const currentImage = ref('')
const danhSachBienThe = ref([])
const selectedBienThe = ref(null)
const currentThumbIndex = ref(0)

// Số lượng và input số lượng
const soLuong = ref(1)
const soLuongInput = ref('1') // giữ dạng chuỗi để nhập liệu tự do
const soLuongTon = ref(0)

watch(soLuongInput, (val) => {
  const parsed = parseInt(val, 10)
  if (!isNaN(parsed)) {
    soLuong.value = parsed
  }
})

function validateSoLuong() {
  let parsed = parseInt(soLuongInput.value, 10)
  if (isNaN(parsed) || parsed < 1) {
    parsed = 1
  } else if (parsed > soLuongTon.value) {
    parsed = soLuongTon.value
  }
  soLuong.value = parsed
  soLuongInput.value = parsed.toString()
}

function tangSoLuong() {
  if (soLuong.value < soLuongTon.value) {
    soLuong.value++
    soLuongInput.value = soLuong.value.toString()
  }
}

function giamSoLuong() {
  if (soLuong.value > 1) {
    soLuong.value--
    soLuongInput.value = soLuong.value.toString()
  }
}

const productImageRef = ref(null)
const toastRef = ref(null)

provide('product', product)
provide('selectedBienThe', selectedBienThe)

onMounted(async () => {
  await fetchProduct()
  await fetchBienThe()
  if (product.value?.maThuoc) {
    await fetchRating()
  }
  if (route.query.review === 'true') {
    setTimeout(() => {
      const reviewSection = document.querySelector('.review-wrapper')
      if (reviewSection) {
        reviewSection.scrollIntoView({ behavior: 'smooth' })
      }
    }, 300)
  }
})

watch([selectedBienThe, product], async () => {
  if (product.value) await fetchTonKho()
})

watch(
  () => route.params.id,
  async (newId) => {
    id = newId
    await fetchProduct()
    await fetchBienThe()
    if (product.value?.maThuoc) {
      await fetchRating()
    }
  },
)

async function fetchProduct() {
  try {
    const res = await axios.get(`http://localhost:8080/api/thuoc/${id}`)
    product.value = res.data
    product.value.giaBanGoc = product.value.giaBan
    currentImage.value = getImageUrl(
      product.value.hinhAnhChinh || product.value.thumbnails?.[0] || '',
    )
  } catch (err) {
    console.error('Lỗi tải sản phẩm:', err)
  }
}

async function fetchBienThe() {
  try {
    const res = await axios.get(`http://localhost:8080/api/bienthe/thuoc/${id}`)
    danhSachBienThe.value = res.data.filter((bt) => bt.trangThai === true || bt.trangThai === 1)
  } catch (err) {
    console.error('Lỗi lấy biến thể:', err)
  }
}

async function fetchTonKho() {
  try {
    const res = await axios.get(`http://localhost:8080/api/kho-hang/so-luong-ton`, {
      params: {
        maThuoc: selectedBienThe.value ? null : product.value?.maThuoc,
        maBienThe: selectedBienThe.value?.maBienThe || null,
      },
    })
    soLuongTon.value = res.data
  } catch (err) {
    console.error('Lỗi lấy số lượng tồn:', err)
  }
}

function toggleBienThe(bt) {
  if (!bt) {
    selectedBienThe.value = null
    product.value.giaBan = product.value.giaBanGoc
    currentImage.value = getImageUrl(product.value.hinhAnhChinh)
  } else {
    selectedBienThe.value = bt
    product.value.giaBan = bt.giaBan
    currentImage.value = getImageUrl(bt.hinhAnh || product.value.hinhAnhChinh)
  }
}

function scrollThumbnails(direction) {
  const total = product.value.thumbnails.length
  currentThumbIndex.value =
    direction === 'left'
      ? (currentThumbIndex.value - 1 + total) % total
      : (currentThumbIndex.value + 1) % total
  currentImage.value = getImageUrl(product.value.thumbnails[currentThumbIndex.value])
}

function setCurrentImage(index) {
  currentThumbIndex.value = index
  currentImage.value = getImageUrl(product.value.thumbnails[index])
}

function flyToCart() {
  const img = productImageRef.value
  const cartIcon = document.querySelector('.bi-cart') || document.querySelector('.cart-icon')
  if (!img || !cartIcon) return

  const imgRect = img.getBoundingClientRect()
  const cartRect = cartIcon.getBoundingClientRect()
  const clone = img.cloneNode(true)

  clone.style.position = 'fixed'
  clone.style.left = imgRect.left + 'px'
  clone.style.top = imgRect.top + 'px'
  clone.style.width = imgRect.width + 'px'
  clone.style.height = imgRect.height + 'px'
  clone.style.transition = 'all 0.8s ease-in-out'
  clone.style.zIndex = 1000
  document.body.appendChild(clone)

  getComputedStyle(clone).opacity

  clone.style.left = cartRect.left + 'px'
  clone.style.top = cartRect.top + 'px'
  clone.style.width = '30px'
  clone.style.height = '30px'
  clone.style.opacity = 0.5

  setTimeout(() => {
    clone.remove()
  }, 900)
}

function themVaoGio() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  const storageKey = maKH ? `cart_${maKH}` : 'cart_temp'
  const cart = JSON.parse(localStorage.getItem(storageKey)) || []

  const key = selectedBienThe.value
    ? { maBienThe: selectedBienThe.value.maBienThe }
    : { maThuoc: product.value.maThuoc }

  const exists = cart.find(
    (item) =>
      (item.maBienThe && item.maBienThe === key.maBienThe) ||
      (item.maThuoc && item.maThuoc === key.maThuoc),
  )

  if (exists) {
    exists.soLuong += soLuong.value
  } else {
    cart.push({
      ...key,
      soLuong: soLuong.value,
    })
  }

  localStorage.setItem(storageKey, JSON.stringify(cart))
  CartStore.updateCount()
  toastRef.value?.show('✅ Đã thêm vào giỏ hàng', 'success')
  flyToCart()
}

function themVaoSoSanh() {
  const key = 'compare_list'
  const list = JSON.parse(localStorage.getItem(key)) || []

  const currentItem = selectedBienThe.value
    ? {
        maBienThe: selectedBienThe.value.maBienThe,
        maThuoc: product.value.maThuoc,
        tenThuoc: product.value.tenThuoc,
        giaBan: selectedBienThe.value.giaBan,
        hinhAnh: selectedBienThe.value.hinhAnh || product.value.hinhAnhChinh,
        donViTinh: selectedBienThe.value.donViTinh,
        loai: 'Biến thể',
        moTaThem: product.value.moTaThem,
        thanhPhan: product.value.thanhPhan,
        congDung: product.value.congDung,
        huongDanSuDung: product.value.huongDanSuDung,
      }
    : {
        maThuoc: product.value.maThuoc,
        tenThuoc: product.value.tenThuoc,
        giaBan: product.value.giaBan,
        hinhAnh: product.value.hinhAnhChinh,
        donViTinh: product.value.donViTinh,
        loai: 'Thuốc chính',
        moTaThem: product.value.moTaThem,
        thanhPhan: product.value.thanhPhan,
        congDung: product.value.congDung,
        huongDanSuDung: product.value.huongDanSuDung,
      }

  const exists = list.find((sp) =>
    selectedBienThe.value
      ? sp.maBienThe === currentItem.maBienThe
      : sp.maThuoc === currentItem.maThuoc && !sp.maBienThe,
  )

  if (!exists) {
    if (list.length >= 4) {
      toastRef.value?.show('Chỉ có thể so sánh tối đa 4 sản phẩm!', 'warning')
      return
    }

    list.push(currentItem)
    localStorage.setItem(key, JSON.stringify(list))
    window.dispatchEvent(new Event('compare-updated'))
    toastRef.value?.show('Đã thêm vào danh sách so sánh', 'success')
  } else {
    toastRef.value?.show('Sản phẩm đã có trong danh sách so sánh', 'info')
  }
}

function muaNgay() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const maKH = userInfo?.maKH || null
  const storageKey = maKH ? `cart_${maKH}` : 'cart_temp'
  const cart = JSON.parse(localStorage.getItem(storageKey)) || []

  const exists = cart.find((item) =>
    selectedBienThe.value
      ? item.maBienThe === selectedBienThe.value.maBienThe
      : item.maThuoc === product.value.maThuoc && !item.maBienThe,
  )

  const tongSoLuong = exists ? exists.soLuong + soLuong.value : soLuong.value

  if (tongSoLuong > soLuongTon.value) {
    toastRef.value?.show(
      `Không thể mua ${soLuong.value} sản phẩm. Tồn kho còn lại: ${soLuongTon.value - (exists?.soLuong || 0)}`,
      'warning',
    )
    return
  }

  themVaoGio()
  router.push('/gio-hang')
}

function getImageUrl(path) {
  return path?.startsWith('http') ? path : `http://localhost:8080/${path}`
}

function formatCurrency(v) {
  return v?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || ''
}
</script>

<style scoped>
.product-detail-bg {
  background-color: #f5f5f5;
  min-height: 100vh;
}
.product-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  max-width: 960px;
  margin: auto;
}
.product-img {
  width: 100%;
  height: 300px;
  object-fit: contain;
  background-color: #fff;
  padding: 8px;
}
.product-thumbs-slider {
  position: relative;
  padding: 0 20px;
}
.product-thumbs {
  scroll-behavior: smooth;
  overflow-x: auto;
  display: flex;
  justify-content: center;
}
.product-thumbs img {
  width: 90px;
  height: 90px;
  object-fit: contain;
  border: 2px solid #ccc;
  border-radius: 6px;
  margin: 6px;
  cursor: pointer;
  transition: 0.2s;
}
.product-thumbs img.active {
  border-color: #43a047;
}
.btn-slider {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: white;
  border: 1px solid #ccc;
  font-size: 1.5rem;
  font-weight: bold;
  color: #444;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  cursor: pointer;
}
.btn-slider.left {
  left: -18px;
}
.btn-slider.right {
  right: -18px;
}
.price {
  color: red;
  font-size: 1.5rem;
  font-weight: bold;
}
.rating {
  color: red;
}
.badge-yeuthich {
  background: #43a047;
  color: white;
  padding: 4px 8px;
  font-size: 0.8rem;
  border-radius: 4px;
}
.variant-box {
  border: 2px solid #ccc;
  padding: 10px 16px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: bold;
  background: white;
  position: relative;
}
.variant-box.active {
  border-color: #d32f2f;
  color: #d32f2f;
}
.check-icon {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #d32f2f;
  color: white;
  width: 18px;
  height: 18px;
  font-size: 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-buy,
.btn-cart {
  padding: 12px;
  font-size: 1.1rem;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  transition: background 0.2s;
}
.btn-cart {
  background-color: #4caf50;
  color: white;
}
.btn-buy {
  background-color: #ffa000;
  color: white;
}
.btn-cart:hover {
  background-color: #388e3c;
}
.btn-buy:hover {
  background-color: #ff8f00;
}
.quantity-selector {
  display: inline-flex;
  border: 1px solid #ccc;
  border-radius: 20px;
  overflow: hidden;
  align-items: center;
  height: 36px;
}
.btn-adjust {
  background: white;
  border: none;
  width: 36px;
  height: 100%;
  font-size: 20px;
  color: #555;
  cursor: pointer;
  border-right: 1px solid #ccc;
}
.btn-adjust:last-child {
  border-right: none;
  border-left: 1px solid #ccc;
}
.btn-adjust:hover {
  background: #f5f5f5;
}
.qty-input {
  width: 50px;
  text-align: center;
  border: none;
  font-weight: bold;
  font-size: 16px;
}
.qty-input:focus {
  outline: none;
}
.btn-compare {
  background-color: white;
  color: #2196f3;
  border: 2px solid #2196f3;
  font-weight: bold;
  padding: 10px;
  font-size: 1rem;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.btn-compare:hover {
  background-color: #2196f3;
  color: white;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}
</style>
