<template>
  <div class="container py-4">
    <h4 class="mb-4 text-success">Danh Sách Phiếu Nhập</h4>

    <div class="text-end mb-3">
      <button class="btn btn-success" @click="moFormNhap = true">
        <i class="bi bi-plus-circle"></i> Tạo Phiếu Nhập
      </button>
    </div>

    <table class="table table-bordered align-middle">
      <thead class="table-success text-center">
        <tr>
          <th>Mã PN</th>
          <th>Ngày nhập</th>
          <th>Nhân viên</th>
          <th>Nhà cung cấp</th>
          <th>Tổng tiền</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(pn, index) in phieuNhaps" :key="pn.maPN">
          <td class="text-center">{{ pn.maPN }}</td>
          <td>{{ formatDate(pn.ngayNhap) }}</td>
          <td>{{ pn.tenNV }}</td>
          <td>{{ pn.tenNCC }}</td>
          <td class="text-end text-danger">{{ pn.tongTien.toLocaleString() }} đ</td>
          <td class="text-center">
            <button class="btn btn-sm btn-info me-1" @click="xemChiTiet(pn.maPN)">
              <i class="bi bi-eye"></i>
            </button>
            <button class="btn btn-sm btn-danger" @click="xoaPhieuNhap(index)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal tạo phiếu nhập -->
    <div v-if="moFormNhap" class="modal fade show d-block" style="background: rgba(0, 0, 0, 0.4)">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Tạo Phiếu Nhập</h5>
            <button type="button" class="btn-close" @click="moFormNhap = false"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Nhân viên</label>
                <input class="form-control" :value="currentUser.hoTen" disabled />
              </div>
              <div class="col-md-6">
                <label class="form-label">Nhà cung cấp</label>
                <select v-model="form.tenNCC" class="form-select">
                  <option disabled value="">-- Chọn nhà cung cấp --</option>
                  <option v-for="ncc in nhaCungCaps" :key="ncc.maNCC" :value="ncc.tenNCC">
                    {{ ncc.tenNCC }}
                  </option>
                </select>
              </div>
            </div>

            <div class="my-3">
              <label class="form-label">Thêm thuốc</label>
              <div class="row g-2">
                <div class="col-md-4">
                  <input
                    v-model="thuocNhap.tenThuoc"
                    class="form-control"
                    placeholder="Tên thuốc"
                  />
                </div>
                <div class="col-md-3">
                  <input
                    v-model.number="thuocNhap.soLuong"
                    class="form-control"
                    type="number"
                    placeholder="Số lượng"
                  />
                </div>
                <div class="col-md-3">
                  <input
                    v-model.number="thuocNhap.donGiaNhap"
                    class="form-control"
                    type="number"
                    placeholder="Đơn giá"
                  />
                </div>
                <div class="col-md-2">
                  <button class="btn btn-primary w-100" @click="themThuoc">Thêm</button>
                </div>
              </div>
            </div>

            <table class="table table-bordered">
              <thead class="table-light">
                <tr>
                  <th>#</th>
                  <th>Tên thuốc</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(t, i) in form.chiTiet" :key="i">
                  <td>{{ i + 1 }}</td>
                  <td>{{ t.tenThuoc }}</td>
                  <td class="text-center">{{ t.soLuong }}</td>
                  <td class="text-end">{{ t.donGiaNhap.toLocaleString() }}</td>
                  <td class="text-end">{{ (t.soLuong * t.donGiaNhap).toLocaleString() }}</td>
                  <td class="text-center">
                    <button class="btn btn-sm btn-danger" @click="form.chiTiet.splice(i, 1)">
                      Xóa
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="moFormNhap = false">Hủy</button>
            <button class="btn btn-success" :disabled="!coTheLuu" @click="luuPhieuNhap">
              Lưu Phiếu Nhập
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal chi tiết + in -->
    <div
      v-if="chiTietModalVisible"
      class="modal fade show d-block"
      style="background: rgba(0, 0, 0, 0.4)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi Tiết Phiếu Nhập #{{ selectedPN?.maPN }}</h5>
            <button type="button" class="btn-close" @click="chiTietModalVisible = false"></button>
          </div>
          <div class="modal-body" ref="printArea">
            <p><strong>Nhân viên:</strong> {{ selectedPN?.tenNV }}</p>
            <p><strong>Nhà cung cấp:</strong> {{ selectedPN?.tenNCC }}</p>
            <p><strong>Ngày nhập:</strong> {{ formatDate(selectedPN?.ngayNhap) }}</p>

            <table class="table table-striped">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Tên thuốc</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(ct, index) in selectedPN?.chiTiet || []" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td>{{ ct.tenThuoc }}</td>
                  <td class="text-center">{{ ct.soLuong }}</td>
                  <td class="text-end">{{ ct.donGiaNhap.toLocaleString() }}</td>
                  <td class="text-end">{{ (ct.soLuong * ct.donGiaNhap).toLocaleString() }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="chiTietModalVisible = false">Đóng</button>
            <button class="btn btn-primary" @click="inPhieuNhap">
              <i class="bi bi-printer"></i> In Phiếu Nhập
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const currentUser = ref({ maNV: 1, hoTen: 'Nguyễn Văn A' })
const phieuNhaps = ref([])
const moFormNhap = ref(false)
const chiTietModalVisible = ref(false)
const selectedPN = ref(null)
const printArea = ref(null)

const nhaCungCaps = ref([
  { maNCC: 1, tenNCC: 'Công ty Dược ABC' },
  { maNCC: 2, tenNCC: 'CTCP Dược phẩm XYZ' },
  { maNCC: 3, tenNCC: 'Nhà thuốc Minh Tâm' },
])

const form = ref({ tenNCC: '', chiTiet: [] })
const thuocNhap = ref({ tenThuoc: '', soLuong: 1, donGiaNhap: 0 })

const themThuoc = () => {
  if (thuocNhap.value.tenThuoc && thuocNhap.value.soLuong > 0 && thuocNhap.value.donGiaNhap > 0) {
    form.value.chiTiet.push({ ...thuocNhap.value })
    thuocNhap.value = { tenThuoc: '', soLuong: 1, donGiaNhap: 0 }
  }
}

const coTheLuu = computed(() => form.value.tenNCC && form.value.chiTiet.length)

const luuPhieuNhap = () => {
  const tongTien = form.value.chiTiet.reduce((sum, t) => sum + t.soLuong * t.donGiaNhap, 0)
  const newPN = {
    maPN: Date.now(),
    ngayNhap: new Date().toISOString().slice(0, 10),
    tenNV: currentUser.value.hoTen,
    tenNCC: form.value.tenNCC,
    tongTien,
    chiTiet: [...form.value.chiTiet],
  }
  phieuNhaps.value.unshift(newPN)
  form.value = { tenNCC: '', chiTiet: [] }
  moFormNhap.value = false
}

const xemChiTiet = (maPN) => {
  selectedPN.value = phieuNhaps.value.find((pn) => pn.maPN === maPN)
  chiTietModalVisible.value = true
}

const xoaPhieuNhap = (index) => {
  if (confirm('Bạn có chắc chắn muốn xoá phiếu nhập này không?')) {
    phieuNhaps.value.splice(index, 1)
  }
}

const inPhieuNhap = () => {
  const printWindow = window.open('', '', 'width=800,height=600')
  printWindow.document.write('<html><head><title>In phiếu nhập</title>')
  printWindow.document.write(
    '<style>table {width: 100%; border-collapse: collapse} th, td {border: 1px solid #ccc; padding: 6px;}</style>',
  )
  printWindow.document.write('</head><body>')
  printWindow.document.write('<h3 style="text-align:center">PHIẾU NHẬP KHO</h3>')
  printWindow.document.write(printArea.value.innerHTML)
  printWindow.document.write('</body></html>')
  printWindow.document.close()
  printWindow.print()
}

const formatDate = (str) => {
  const d = new Date(str)
  return d.toLocaleDateString('vi-VN')
}
</script>
