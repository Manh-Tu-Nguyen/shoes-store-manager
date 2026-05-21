<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Thiết lập Thuộc tính</h2>
        <div class="type-switcher mt-3">
          <label class="mr-2 font-bold">Loại thuộc tính: </label>
          <select v-model="currentType" @change="fetchData" class="select-custom">
            <option value="brand">Thương hiệu</option>
            <option value="category">Danh mục</option>
            <option value="color">Màu sắc</option>
            <option value="size">Kích cỡ</option>
            <option value="origin">Xuất xứ</option>
          </select>
        </div>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="btn-primary">
          <span class="icon">+</span> Thêm {{ currentLabel }}
        </button>
      </div>
    </div>

    <DataTable :isEmpty="items.length === 0" :colCount="5">
      <template #header>
        <th>Mã</th>
        <th>Tên {{ currentLabel }}</th>
        <th>Trạng thái</th>
        <th>Ngày tạo</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="item in items" :key="item.id">
          <td><span class="badge-code">{{ item.code }}</span></td>
          <td class="font-bold">{{ item.name }}</td>
          <td>
            <span :class="['status-dot', item.status ? 'active' : 'inactive']"></span>
            {{ item.status ? 'Hoạt động' : 'Ngưng dùng' }}
          </td>
          <td>{{ item.createdAt }}</td>
          <td>
            <ActionButtons 
              @edit="editItem(item)" 
              @delete="confirmDelete(item.id)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <!-- MODAL ĐÃ ĐƯỢC VÁ LẠI CSS -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm mới' }} {{ currentLabel }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-group">
            <label>Tên {{ currentLabel }} <span class="text-danger">*</span></label>
            <input v-model="formData.name" type="text" placeholder="Nhập tên..." required />
          </div>
          
          <!-- Riêng Origin cần nhập mã thủ công -->
          <div class="form-group" v-if="currentType === 'origin'">
             <label>Mã xuất xứ <span class="text-danger">*</span></label>
             <input v-model="formData.code" type="text" :disabled="isEditMode" placeholder="VD: VN, USA..." required />
          </div>

          <div class="form-group">
            <label>Trạng thái</label>
            <select v-model="formData.status">
              <option :value="true">Hoạt động</option>
              <option :value="false">Ngưng dùng</option>
            </select>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy bỏ</button>
            <button type="submit" class="btn-primary">
               {{ isEditMode ? 'Lưu thay đổi' : 'Tạo mới' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import Swal from 'sweetalert2';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';

// Import API (Đảm bảo bạn đã thêm hàm create/update/delete vào các file API này)
import brandApi from '@/api/brandApi';
import categoryApi from '@/api/categoryApi';
import colorApi from '@/api/colorApi';
import sizeApi from '@/api/sizeApi';
import originApi from '@/api/originApi';

const currentType = ref('brand');
const items = ref([]);
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);
const formData = reactive({ name: '', status: true, code: '' });

const typeMap = {
  brand: { api: brandApi, label: 'Thương hiệu' },
  category: { api: categoryApi, label: 'Danh mục' },
  color: { api: colorApi, label: 'Màu sắc' },
  size: { api: sizeApi, label: 'Kích cỡ' },
  origin: { api: originApi, label: 'Xuất xứ' },
};

const currentLabel = computed(() => typeMap[currentType.value].label);

const fetchData = async () => {
  try {
    const res = await typeMap[currentType.value].api.getAll();
    items.value = res.data?.data || res.data || [];
  } catch (e) { 
    console.error(e);
    items.value = [];
  }
};

onMounted(fetchData);

const openAddModal = () => {
  isEditMode.value = false;
  Object.assign(formData, { name: '', status: true, code: '' });
  isModalOpen.value = true;
};

const editItem = (item) => {
  isEditMode.value = true;
  currentId.value = item.id;
  Object.assign(formData, { ...item });
  isModalOpen.value = true;
};

const closeModal = () => isModalOpen.value = false;

const handleSubmit = async () => {
  try {
    const api = typeMap[currentType.value].api;
    if (isEditMode.value) {
      await api.update(currentId.value, formData);
      Swal.fire({ icon: 'success', title: 'Đã cập nhật!', timer: 1500, showConfirmButton: false });
    } else {
      await api.create(formData);
      Swal.fire({ icon: 'success', title: 'Đã thêm mới!', timer: 1500, showConfirmButton: false });
    }
    fetchData();
    closeModal();
  } catch (e) { 
    Swal.fire('Lỗi!', e.response?.data?.message || 'Thao tác thất bại', 'error'); 
  }
};

const confirmDelete = (id) => {
  Swal.fire({
    title: `Xóa mềm ${currentLabel.value}?`,
    text: "Trạng thái sẽ chuyển sang Ngưng dùng!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    confirmButtonText: 'Đồng ý'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await typeMap[currentType.value].api.delete(id);
        Swal.fire('Thành công!', '', 'success');
        fetchData();
      } catch (e) {
        Swal.fire('Lỗi!', 'Không thể thực hiện', 'error');
      }
    }
  });
};
</script>

<style scoped>
/* --- CSS GIAO DIỆN CHÍNH --- */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.mt-3 { margin-top: 12px; }
.select-custom { padding: 8px 12px; border-radius: 6px; border: 1px solid #cbd5e1; outline: none; background: white; cursor: pointer; font-weight: 500;}
.select-custom:focus { border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
.badge-code { background: #f1f5f9; color: #475569; padding: 4px 8px; border-radius: 4px; font-family: monospace; font-weight: bold;}
.font-bold { font-weight: 600; }
.text-danger { color: #ef4444; }

/* --- CSS TRẠNG THÁI --- */
.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.status-dot.active { background-color: #22c55e; box-shadow: 0 0 8px #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }

/* --- CSS NÚT BẤM --- */
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 600;}
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 600;}

/* --- CSS MODAL (QUAN TRỌNG NHẤT) --- */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5); backdrop-filter: blur(4px);
  display: flex; justify-content: center; align-items: center; z-index: 2000;
}
.modal-content {
  background: white; width: 450px; border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
  animation: slideDown 0.3s ease-out;
}
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }

.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; }
.close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #475569; font-size: 14px; }
.form-group input, .form-group select {
  width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none;
}
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; }
</style>