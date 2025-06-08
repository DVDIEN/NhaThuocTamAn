<template>
  <div class="card p-4 shadow-sm">
    <div class="row align-items-center">
      <div class="col-md-3 text-center mb-3 mb-md-0">
        <img
          :src="user.avatar"
          alt="Avatar"
          class="rounded-circle img-thumbnail"
          style="width: 120px; height: 120px; object-fit: cover"
        />
      </div>
      <div class="col-md-9">
        <h4 class="fw-bold mb-2">{{ user.fullName }}</h4>
        <p class="mb-1"><i class="bi bi-envelope me-2"></i>{{ user.email }}</p>
        <p class="mb-1"><i class="bi bi-telephone me-2"></i>{{ user.phone }}</p>
        <p class="mb-1"><i class="bi bi-calendar3 me-2"></i>{{ user.birthDate }}</p>
        <p class="mb-1"><i class="bi bi-geo-alt me-2"></i>{{ user.address }}</p>
        <button class="btn btn-outline-primary mt-3" @click="editing = true">
          <i class="bi bi-pencil-square me-1"></i> Chỉnh sửa
        </button>
      </div>
    </div>
  </div>

  <!-- Overlay Form chỉnh sửa -->
  <div v-if="editing" class="overlay">
    <div class="edit-form card p-4 shadow">
      <h5 class="fw-bold mb-3">Chỉnh sửa thông tin</h5>
      <div class="row g-3">
        <div class="col-md-6">
          <input v-model="form.fullName" type="text" class="form-control" placeholder="Họ và tên" />
        </div>
        <div class="col-md-6">
          <input v-model="form.email" type="email" class="form-control" placeholder="Email" />
        </div>
        <div class="col-md-6">
          <input
            v-model="form.phone"
            type="text"
            class="form-control"
            placeholder="Số điện thoại"
          />
        </div>
        <div class="col-md-6">
          <input v-model="form.birthDate" type="date" class="form-control" />
        </div>
        <div class="col-12">
          <input v-model="form.address" type="text" class="form-control" placeholder="Địa chỉ" />
        </div>
      </div>
      <div class="text-end mt-3">
        <button class="btn btn-secondary me-2" @click="editing = false">Hủy</button>
        <button class="btn btn-success" @click="saveInfo">Lưu</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const user = ref({
  fullName: 'Nguyễn Văn A',
  email: 'nguyenvana@gmail.com',
  phone: '0987654321',
  birthDate: '2000-01-01',
  address: '123 Đường ABC, Quận 1, TP.HCM',
  avatar: 'https://i.pravatar.cc/300',
})

const editing = ref(false)

const form = ref({ ...user.value })

const saveInfo = () => {
  user.value = { ...form.value }
  editing.value = false
  alert('✅ Cập nhật thông tin thành công!')
}
</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.edit-form {
  background: white;
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
}
</style>
