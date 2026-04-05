<template>
  <div class="management-page">
    <div class="page-header">
      <h2>Quản lý Màu sắc (Color)</h2>
      <button @click="openModal()" class="btn-add">+ Thêm Màu mới</button>
    </div>

    <div class="filter-section">
      <input v-model="searchQuery" placeholder="Tìm kiếm tên màu..." class="search-input" />
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên Màu sắc</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in filteredColors" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td class="actions">
            <button @click="openModal(item)" class="btn-edit">Sửa</button>
            <button @click="confirmDelete(item.id)" class="btn-delete">Xóa</button>
          </td>
        </tr>
        <tr v-if="filteredColors.length === 0">
          <td colspan="3" class="text-center">Không tìm thấy màu sắc nào.</td>
        </tr>
      </tbody>
    </table>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ editingId ? 'Cập nhật Màu sắc' : 'Thêm Màu sắc mới' }}</h3>
        <div class="form-group">
          <label>Tên màu:</label>
          <input v-model="formData.name" placeholder="Ví dụ: Đỏ, Xanh Navy..." class="form-input" />
        </div>
        <div class="modal-actions">
          <button @click="saveData" class="btn-save" :disabled="productStore.isLoading">Lưu</button>
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

onMounted(() => {
  productStore.fetchAllAttributes();
});

const filteredColors = computed(() => {
  return (productStore.colors || []).filter(c => 
    c.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

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

const saveData = async () => {
  if (!formData.value.name.trim()) return alert("Vui lòng nhập tên màu!");
  
  let res;
  if (editingId.value) {
    res = await productStore.updateColor(editingId.value, formData.value);
  } else {
    res = await productStore.addColor(formData.value);
  }

  if (res?.success) {
    isModalOpen.value = false;
    alert(res.message);
  }
};

const confirmDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa màu sắc này?")) {
    const res = await productStore.removeColor(id);
    if (res?.success) alert("Xóa thành công!");
  }
};
</script>