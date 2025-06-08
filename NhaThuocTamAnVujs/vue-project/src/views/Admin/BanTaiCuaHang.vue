<template>
  <div class="container pt-3 pb-4">
    <h5 class="mb-3 text-success">Bán hàng tại quầy</h5>

    <!-- Nút tạo hóa đơn -->
    <div class="text-end mb-2">
      <button class="btn btn-success btn-sm" @click="moChonThuoc = true">➕ Tạo hóa đơn mới</button>
    </div>

    <!-- Chọn thuốc -->
    <div v-if="moChonThuoc" class="mb-4">
      <!-- Tìm kiếm -->
      <div class="row mb-2">
        <div class="col-md-6">
          <input
            v-model="search"
            type="text"
            class="form-control form-control-sm"
            placeholder="Tìm theo tên thuốc hoặc hoạt chất..."
          />
        </div>
      </div>

      <!-- Danh sách thuốc -->
      <div class="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-2">
        <div
          class="col"
          v-for="thuoc in danhSachLoc"
          :key="thuoc.maThuoc"
          @click="chonThuoc(thuoc)"
          style="cursor: pointer"
        >
          <div class="card h-100 p-2 shadow-sm">
            <img
              :src="thuoc.hinhAnh"
              class="card-img-top rounded"
              style="height: 90px; object-fit: contain"
            />
            <div class="card-body p-2">
              <div class="fw-bold text-success small">{{ thuoc.tenThuoc }}</div>
              <div class="text-muted small">Hoạt chất: {{ thuoc.hoatChat }}</div>
              <div class="small">Giá bán: {{ formatCurrency(thuoc.giaBan) }}</div>
            </div>
          </div>
        </div>
        <div v-if="!danhSachLoc.length" class="text-muted text-center w-100">
          Không tìm thấy thuốc phù hợp
        </div>
      </div>
    </div>

    <!-- Hóa đơn -->
    <div class="card shadow-sm" v-if="hoaDonChiTiet.length">
      <div class="card-body p-0">
        <table class="table table-bordered table-sm mb-0">
          <thead class="table-success text-center">
            <tr>
              <th>#</th>
              <th>Tên thuốc</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
              <th></th>
            </tr>
          </thead>
          <tbody class="text-center align-middle">
            <tr v-for="(ct, index) in hoaDonHienTai" :key="index">
              <td>{{ (trangHienTai - 1) * kichThuocTrang + index + 1 }}</td>
              <td>{{ ct.tenThuoc }}</td>
              <td>
                <input
                  v-model.number="ct.soLuong"
                  type="number"
                  min="1"
                  class="form-control form-control-sm"
                />
              </td>
              <td>{{ formatCurrency(ct.donGia) }}</td>
              <td>{{ formatCurrency(ct.soLuong * ct.donGia) }}</td>
              <td>
                <button class="btn btn-sm btn-danger" @click="xoaThuoc(index)">Xóa</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Tổng tiền + phân trang -->
      <div class="card-footer d-flex justify-content-between align-items-center bg-light px-3 py-2">
        <strong class="text-danger">Tổng: {{ formatCurrency(tongTien) }}</strong>
        <div>
          <button
            class="btn btn-sm btn-outline-secondary me-1"
            @click="trangHienTai--"
            :disabled="trangHienTai === 1"
          >
            ◀
          </button>
          <span class="small">Trang {{ trangHienTai }} / {{ soTrang }}</span>
          <button
            class="btn btn-sm btn-outline-secondary ms-1"
            @click="trangHienTai++"
            :disabled="trangHienTai === soTrang"
          >
            ▶
          </button>
        </div>
      </div>
    </div>

    <!-- Nút thanh toán -->
    <div class="text-end mt-3" v-if="hoaDonChiTiet.length">
      <button class="btn btn-primary btn-sm" @click="thanhToan">✅ Thanh toán & In hóa đơn</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const moChonThuoc = ref(false)
const search = ref('')
const trangHienTai = ref(1)
const kichThuocTrang = 5

const danhSachThuoc = ref([
  {
    maThuoc: 1,
    tenThuoc: 'Paracetamol 500mg',
    hoatChat: 'Paracetamol',
    giaBan: 15000,
    hinhAnh:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/144x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/DSC_04209_5816b3a584.jpg',
  },
  {
    maThuoc: 2,
    tenThuoc: 'Amoxicillin 500mg',
    hoatChat: 'Amoxicillin',
    giaBan: 30000,
    hinhAnh:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/144x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/IMG_6464_4e4f351816.jpg',
  },
  {
    maThuoc: 3,
    tenThuoc: 'Ibuprofen 200mg',
    hoatChat: 'Ibuprofen',
    giaBan: 25000,
    hinhAnh:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/144x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/00500234_hoat_huyet_truong_phuc_3x10_3439_6293_large_fce5c74dce.jpg',
  },
  {
    maThuoc: 4,
    tenThuoc: 'Vitamin C',
    hoatChat: 'Ascorbic Acid',
    giaBan: 20000,
    hinhAnh:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/144x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/DSC_09160_f0ac0588b5.jpg',
  },
  {
    maThuoc: 5,
    tenThuoc: 'Cetirizine',
    hoatChat: 'Cetirizine',
    giaBan: 18000,
    hinhAnh:
      'https://cdn.nhathuoclongchau.com.vn/unsafe/144x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/DSC_09589_44c5e2fc09.jpg',
  },
])

const hoaDonChiTiet = ref([])

const danhSachLoc = computed(() => {
  const tuKhoa = search.value.toLowerCase()
  const ketQua = danhSachThuoc.value.filter(
    (t) => t.tenThuoc.toLowerCase().includes(tuKhoa) || t.hoatChat.toLowerCase().includes(tuKhoa),
  )
  return tuKhoa ? ketQua : ketQua.slice(0, 3)
})

const chonThuoc = (thuoc) => {
  const daCo = hoaDonChiTiet.value.find((t) => t.maThuoc === thuoc.maThuoc)
  if (daCo) {
    daCo.soLuong += 1
  } else {
    hoaDonChiTiet.value.push({
      maThuoc: thuoc.maThuoc,
      tenThuoc: thuoc.tenThuoc,
      soLuong: 1,
      donGia: thuoc.giaBan,
    })
  }
}

const xoaThuoc = (index) => {
  hoaDonChiTiet.value.splice(index, 1)
  if (trangHienTai.value > soTrang.value) {
    trangHienTai.value = soTrang.value
  }
}

const tongTien = computed(() =>
  hoaDonChiTiet.value.reduce((sum, ct) => sum + ct.soLuong * ct.donGia, 0),
)

const formatCurrency = (v) => v.toLocaleString('vi-VN') + '₫'

const soTrang = computed(() => Math.ceil(hoaDonChiTiet.value.length / kichThuocTrang))

const hoaDonHienTai = computed(() => {
  const start = (trangHienTai.value - 1) * kichThuocTrang
  return hoaDonChiTiet.value.slice(start, start + kichThuocTrang)
})

const thanhToan = () => {
  alert('✅ Hóa đơn đã được tạo!\n🧾 Tổng tiền: ' + formatCurrency(tongTien.value))
  hoaDonChiTiet.value = []
  moChonThuoc.value = false
  search.value = ''
  trangHienTai.value = 1
}
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
.card-img-top {
  height: 90px !important;
  object-fit: contain;
}
.card {
  transition: 0.2s;
}
.card:hover {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.15);
  transform: scale(1.01);
}
</style>
