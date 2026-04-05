<template>
  <div class="management-page">
    <div class="page-header">
      <h2>Quản lý Kích cỡ (Size)</h2>
      <button @click="openModal()" class="btn-add">+ Thêm Size mới</button>
    </div>

    <div class="filter-section">
      <input v-model="searchQuery" placeholder="Tìm kiếm tên size..." class="search-input" />
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên Size</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in filteredSizes" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>
            <button @click="openModal(item)" class="btn-edit">Sửa</button>
            <button @click="confirmDelete(item.id)" class="btn-delete">Xóa</button>
          </td>
        </tr>
        <tr v-if="filteredSizes.length === 0">
          <td colspan="3" class="text-center">Không tìm thấy dữ liệu.</td>
        </tr>
      </tbody>
    </table>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ editingId ? 'Cập nhật Size' : 'Thêm Size mới' }}</h3>
        <input v-model="formData.name" placeholder="Nhập tên size..." class="form-input" />
        <div class="modal-actions">
          <button @click="saveData" class="btn-save">Lưu</button>
          <button @click="isModalOpen = false" class="btn-cancel">Hủy</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useProductStore } from '@/store/productStore';

const productStore = useProductStore();
const searchQuery = ref('');
const isModalOpen = ref(false);
const editingId = ref(null);
const formData = ref({ name: '' });

// 1. Lấy dữ liệu khi mount
onMounted(() => {
  productStore.fetchAllAttributes(); // Hàm này bạn đã viết để load b, c, cl, s, o
});

// 2. Logic tìm kiếm (Tối ưu tại Frontend)
const filteredSizes = computed(() => {
  return productStore.sizes.filter(s => 
    s.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// 3. Mở Modal (Dùng chung cho Thêm & Sửa)
const openModal = (item = null) => {
  if (item) {
    editingId.value = item.id;
    formData.value = { name: item.name };
  } else {
    editingId.value = null;
    formData.value = { name: '' };
  }
  isModalOpen.value = true;
};

// 4. Lưu dữ liệu (Kết nối Store)
const saveData = async () => {
  if (!formData.value.name) return alert("Vui lòng nhập tên!");
  
  if (editingId.value) {
    // Gọi action update trong store (bạn cần bổ sung action này)
    console.log("Update ID:", editingId.value, formData.value);
  } else {
    // Gọi action create
    console.log("Create new:", formData.value);
  }
  isModalOpen.value = false;
};

// 5. Xóa (Soft Delete logic)
const confirmDelete = (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa?")) {
    // Gọi productStore.removeSize(id)
    console.log("Delete ID:", id);
  }
};
</script>

<style scoped>
/* CSS cơ bản cho bảng và modal - Tú có thể tùy chỉnh theo ý thích */
.data-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
.data-table th, .data-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }
.btn-add { background: #27ae60; color: white; padding: 10px 20px; border: none; cursor: pointer; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 30px; border-radius: 8px; width: 400px; }
</style>