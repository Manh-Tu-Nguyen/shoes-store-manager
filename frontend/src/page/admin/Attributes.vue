<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Thiết lập Thuộc tính</h2>
        <div class="type-switcher mt-3">
          <label class="mr-2 font-bold">Loại thuộc tính: </label>
          <select v-model="currentType" @change="handleTypeChange" class="select-custom">
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
          <td>{{ item.createdAt || '---' }}</td>
          <td>
            <ActionButtons 
              :showEdit="true" :showDelete="true"
              @edit="editItem(item)" 
              @delete="confirmDelete(item)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm mới' }} {{ currentLabel }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-group">
            <label>Tên {{ currentLabel }} <span class="text-danger">*</span></label>
            <input v-model.trim="formData.name" type="text" :placeholder="'Nhập tên ' + currentLabel.toLowerCase() + '...'" />
            <span class="error-msg" v-if="errors.name">{{ errors.name }}</span>
          </div>
          
          <div class="form-group" v-if="currentType === 'origin'">
             <label>Mã xuất xứ <span class="text-danger">*</span></label>
             <input 
               v-model.trim="formData.code" 
               type="text" 
               :disabled="isEditMode" 
               placeholder="Ví dụ: VN, USA, CN..." 
               @input="normalizeOriginCode"
             />
             <span class="error-msg" v-if="errors.code">{{ errors.code }}</span>
          </div>

          <div class="form-group">
            <label>Trạng thái kích hoạt</label>
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

const initialForm = { name: '', status: true, code: '' };
const formData = reactive({ ...initialForm });
const errors = reactive({});

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
    items.value = Array.isArray(res) ? res : (res.data?.data || res.data || []);
  } catch (e) { 
    console.error("Lỗi tải thuộc tính:", e);
    items.value = [];
  }
};

onMounted(fetchData);

const handleTypeChange = () => {
  clearErrors();
  fetchData();
};

const closeModal = () => {
  isModalOpen.value = false;
  clearErrors();
};

const clearErrors = () => {
  Object.keys(errors).forEach(key => delete errors[key]);
};

const openAddModal = () => {
  isEditMode.value = false;
  clearErrors();
  Object.assign(formData, { name: '', status: true, code: currentType.value === 'origin' ? '' : 'AUTO_CODE' });
  isModalOpen.value = true;
};

const editItem = (item) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = item.id;
  Object.assign(formData, { ...item });
  isModalOpen.value = true;
};

// ĐỒNG BỘ HOÁ ĐỊNH DẠNG MÃ XUẤT XỨ (ISO CODE STANDARDIZATION)
const normalizeOriginCode = () => {
  if (formData.code) {
    formData.code = formData.code.toUpperCase().replace(/[^A-Z]/g, ''); // Chỉ cho phép nhập chữ cái và tự động viết hoa
  }
};

// TẦNG INTERCEPT VALIDATION ĐẦU VÀO TẠI CLIENT
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!formData.name) {
    errors.name = `Tên ${currentLabel.value.toLowerCase()} không được để trống`;
    isValid = false;
  }

  if (currentType.value === 'origin') {
    if (!formData.code) {
      errors.code = 'Mã quốc gia xuất xứ không được để trống';
      isValid = false;
    } else if (formData.code.length < 2) {
      errors.code = 'Mã quốc gia yêu cầu tối thiểu 2 ký tự (Ví dụ: VN, US)';
      isValid = false;
    }
  }

  return isValid;
};

// THỰC THI GIAO DỊCH CÓ KIỂM SOÁT XÁC MINH CỦA SWEETALERT2
const handleSubmit = async () => {
  if (!validateForm()) return;

  // 1. Tước focus để tránh lỗi ARIA
  if (document.activeElement instanceof HTMLElement) {
    document.activeElement.blur();
  }

  const titleText = isEditMode.value 
    ? `Cập nhật ${currentLabel.value.toLowerCase()}?` 
    : `Xác nhận thêm ${currentLabel.value.toLowerCase()} mới?`;
  
  // 2. Hiện popup xác nhận
  const result = await Swal.fire({
    title: titleText,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận',
    cancelButtonText: 'Hủy bỏ'
  });

  if (result.isConfirmed) {
    try {
      const api = typeMap[currentType.value].api;
      
      // 3. Giao tiếp mạng
      if (isEditMode.value) {
        await api.update(currentId.value, formData);
      } else {
        await api.create(formData);
      }
      
      // 4. Giật sập Modal trước khi báo thành công
      closeModal(); 
      
      // 5. Báo thành công
      await Swal.fire({ 
        icon: 'success', 
        title: 'Thành công!', 
        text: isEditMode.value 
              ? `Đã cập nhật ${currentLabel.value.toLowerCase()} thành công.`
              : `Đã khởi tạo ${currentLabel.value.toLowerCase()} mới.`,
        timer: 1500, 
        showConfirmButton: false 
      });
      
      // 6. Làm mới dữ liệu
      fetchData();
      
    } catch (e) { 
      console.error("DEBUG - Lỗi bắt được tại khối Catch:", e);
      Swal.fire('Thất bại!', e.response?.data?.message || 'Tên thuộc tính bị trùng lặp hoặc dữ liệu không hợp lệ.', 'error'); 
    }
  }
};
// LUỒNG XOÁ MỀM (CHUYỂN TRẠNG THÁI NGƯNG DÙNG ĐỂ BẢO VỆ TOÀN VẸN SẢN PHẨM)
const confirmDelete = (item) => {
  Swal.fire({
    title: `Ngưng sử dụng ${currentLabel.value.toLowerCase()} này?`,
    text: `Các sản phẩm (SKU) đang liên kết với thuộc tính này có thể bị ảnh hưởng luồng hiển thị lọc sản phẩm trực tuyến.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận ngưng dùng',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await typeMap[currentType.value].api.delete(item.id);
        Swal.fire('Thành công!', `Đã chuyển đổi trạng thái của [${item.name}] sang ngưng hoạt động.`, 'success');
        fetchData();
      } catch (e) {
        Swal.fire('Lỗi!', 'Không thể thực thi lệnh ngưng sử dụng thuộc tính.', 'error');
      }
    }
  });
};
</script>

<style scoped>
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
input:disabled { background-color: #f1f5f9; color: #94a3b8; cursor: not-allowed; }

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

/* --- CSS MODAL --- */
.modal-overlay {
  position: fixed; 
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.4); 
  backdrop-filter: blur(2px);
  display: flex; justify-content: center; align-items: center; 
  
  /* SỬA TẠI ĐÂY: Đổi từ 2000 xuống 1000 */
  z-index: 1000; 
}
.modal-content {
  background: white; width: 450px; border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
  animation: slideDown 0.3s ease-out;
}
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }

.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; border-top-left-radius: 12px; border-top-right-radius: 12px; }
.close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8; }
.modal-body { padding: 20px; display: flex; flex-direction: column; }
.form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #475569; font-size: 14px; }
.form-group input, .form-group select {
  width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none; font-family: inherit; font-size: 14px;
}
.form-group input:focus, .form-group select:focus { border-color: #3b82f6; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; border-bottom-left-radius: 12px; border-bottom-right-radius: 12px; }
</style>