<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Sổ địa chỉ khách hàng</h2>
        <p class="text-muted">ID Khách hàng: {{ customerId }}</p>
      </div>
      <div class="header-actions">
        <button @click="goBack" class="btn-secondary mr-2">⬅ Quay lại</button>
        <button @click="openAddModal" class="btn-primary">+ Thêm địa chỉ</button>
      </div>
    </div>

    <DataTable :isEmpty="customerStore.addresses?.length === 0" :colCount="6">
      <template #header>
        <th>Người nhận</th>
        <th>Số điện thoại</th>
        <th>Địa chỉ chi tiết</th>
        <th>Khu vực (Phường/Xã - Tỉnh/TP)</th>
        <th>Ghi chú</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="addr in customerStore.addresses" :key="addr.id">
          <td class="font-bold">{{ addr.consigneeName }}</td>
          <td>{{ addr.consigneePhone }}</td>
          <td>{{ addr.streetDetail }}</td>
          <td>{{ addr.ward }} - {{ addr.city }}</td>
          <td class="text-xs">{{ addr.note || '---' }}</td>
          <td>
            <ActionButtons @edit="editAddress(addr)" @delete="confirmDelete(addr.id)" />
          </td>
        </tr>
      </template>
    </DataTable>

    <!-- MODAL THÊM/SỬA ĐỊA CHỈ -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm' }} địa chỉ nhận hàng</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-row">
            <div class="form-group"><label>Tên người nhận <span class="text-danger">*</span></label><input v-model="formData.consigneeName" required /></div>
            <div class="form-group"><label>Số điện thoại <span class="text-danger">*</span></label><input v-model="formData.consigneePhone" required /></div>
          </div>
          <div class="form-row">
            <div class="form-group"><label>Tỉnh/Thành phố <span class="text-danger">*</span></label><input v-model="formData.city" required /></div>
            <div class="form-group"><label>Phường/Xã <span class="text-danger">*</span></label><input v-model="formData.ward" required /></div>
          </div>
          <div class="form-group">
            <label>Địa chỉ chi tiết <span class="text-danger">*</span></label>
            <input v-model="formData.streetDetail" placeholder="Số nhà, tên đường..." required />
          </div>
          <div class="form-group">
            <label>Ghi chú giao hàng</label>
            <textarea v-model="formData.note" rows="2"></textarea>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy</button>
            <button type="submit" class="btn-primary">Lưu địa chỉ</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';
import { useCustomerStore } from '@/store/customerStore';

const route = useRoute();
const router = useRouter();
const customerStore = useCustomerStore();

const customerId = route.params.id;
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = { customerId: Number(customerId), consigneeName: '', consigneePhone: '', city: '', ward: '', streetDetail: '', note: '' };
const formData = reactive({ ...initialForm });

onMounted(() => customerStore.fetchAddressesByCustomerId(customerId));

const goBack = () => router.push('/admin/customers');
const closeModal = () => isModalOpen.value = false;

const openAddModal = () => {
  isEditMode.value = false;
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editAddress = (addr) => {
  isEditMode.value = true;
  currentId.value = addr.id;
  Object.assign(formData, { ...addr });
  isModalOpen.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEditMode.value) await customerStore.updateAddress(currentId.value, formData);
    else await customerStore.createAddress(formData);
    
    Swal.fire({ icon: 'success', title: 'Thành công!', timer: 1000, showConfirmButton: false });
    customerStore.fetchAddressesByCustomerId(customerId);
    closeModal();
  } catch (e) { Swal.fire('Lỗi!', 'Vui lòng kiểm tra lại dữ liệu', 'error'); }
};

const confirmDelete = (id) => {
  Swal.fire({ title: 'Xóa địa chỉ này?', icon: 'warning', showCancelButton: true }).then(async (res) => {
    if (res.isConfirmed) {
      await customerStore.deleteAddress(id);
      customerStore.fetchAddressesByCustomerId(customerId);
    }
  });
};
</script>
<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.product-thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }
.badge-code { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.font-bold { font-weight: 600; color: #1e293b; }
.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.status-dot.active { background-color: #22c55e; box-shadow: 0 0 8px #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }
.text-danger { color: red; }
.col-span-2 { grid-column: span 2; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; width: 600px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; }
/* --- CSS CHO KHU VỰC UPLOAD/PREVIEW ẢNH --- */
.image-input-container {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  background: #f8fafc;
  padding: 15px;
  border-radius: 8px;
  border: 1px dashed #cbd5e1;
  margin-bottom: 15px;
}

.image-preview-box {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 2px dashed #cbd5e1;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  flex-shrink: 0; /* Không cho khung ảnh bị bóp méo */
}

.image-preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* Giữ tỷ lệ ảnh đẹp, lấp đầy khung */
}

.empty-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #94a3b8;
  font-size: 12px;
  font-weight: 500;
}

.empty-image .icon {
  font-size: 28px;
  margin-bottom: 5px;
}

.flex-1 { 
  flex: 1; 
}

textarea { 
  resize: none; /* Không cho người dùng kéo giãn textarea làm hỏng layout */
  width: 100%; 
  padding: 10px 12px; 
  border: 1px solid #cbd5e1; 
  border-radius: 6px; 
  font-family: inherit; 
  font-size: 14px; 
  transition: border-color 0.2s;
}

textarea:focus { 
  outline: none; 
  border-color: #3b82f6; 
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); 
}
</style>