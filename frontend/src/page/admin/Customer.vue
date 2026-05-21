<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Khách hàng</h2>
        <p class="text-muted">Danh sách tài khoản khách hàng trên hệ thống</p>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="btn-primary">+ Thêm Khách Hàng</button>
      </div>
    </div>

    <DataTable :isEmpty="customerStore.customers.length === 0" :colCount="8">
      <template #header>
        <th>Ảnh</th>
        <th>Mã KH</th>
        <th>Họ tên</th>
        <th>Liên hệ</th>
        <th>Ngày sinh</th>
        <th>Giới tính</th>
        <th>Trạng thái</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="c in customerStore.customers" :key="c.id">
          <td><img :src="c.image || 'https://placehold.co/50x50?text=User'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ c.code }}</span></td>
          <td class="font-bold">{{ c.lastName }} {{ c.firstName }}</td>
          <td>
            <div class="text-xs">{{ c.email }}</div>
            <div class="font-bold text-blue-600">{{ c.phoneNumber }}</div>
          </td>
          <td>{{ c.birthday }}</td>
          <td>{{ c.gender ? 'Nam' : 'Nữ' }}</td>
          <td>
            <span :class="['status-dot', c.status ? 'active' : 'inactive']"></span>
            {{ c.status ? 'Hoạt động' : 'Bị khóa' }}
          </td>
          <td>
            <!-- Nút địa chỉ (Gắn icon GPS/Home) -->
            <ActionButtons 
              :showEdit="true" :showDelete="true"
              @edit="editCustomer(c)" @delete="confirmDelete(c.id)"
            />
            <button @click="viewAddresses(c.id)" class="btn-icon-alt" title="Địa chỉ nhận hàng">📍</button>
          </td>
        </tr>
      </template>
    </DataTable>

    <!-- MODAL THÊM/SỬA (Tương tự Product) -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 700px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm mới' }} Khách hàng</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-row">
            <div class="form-group"><label>Họ <span class="text-danger">*</span></label><input v-model="formData.lastName" required /></div>
            <div class="form-group"><label>Tên <span class="text-danger">*</span></label><input v-model="formData.firstName" required /></div>
          </div>
          <div class="form-row">
            <div class="form-group"><label>Email</label><input v-model="formData.email" type="email" /></div>
            <div class="form-group"><label>Số điện thoại</label><input v-model="formData.phoneNumber" /></div>
          </div>
          <div class="form-row">
            <div class="form-group"><label>Ngày sinh</label><input v-model="formData.birthday" type="date" /></div>
            <div class="form-group">
              <label>Giới tính</label>
              <select v-model="formData.gender">
                <option :value="true">Nam</option><option :value="false">Nữ</option>
              </select>
            </div>
          </div>
          
          <!-- IMAGE PREVIEW (Copy từ Product.vue sang) -->
          <div class="image-input-container">
             <div class="image-preview-box">
                <img :src="formData.image || 'https://placehold.co/120x120?text=User'" @error="e => e.target.src = 'https://placehold.co/120x120?text=Error'" />
             </div>
             <div class="form-group flex-1">
                <label>Link ảnh đại diện</label>
                <textarea v-model="formData.image" rows="3"></textarea>
             </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy</button>
            <button type="submit" class="btn-primary">Lưu thông tin</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';
import { useCustomerStore } from '@/store/customerStore';

const router = useRouter();
const customerStore = useCustomerStore();
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const formData = reactive({ lastName: '', firstName: '', email: '', phoneNumber: '', birthday: '', gender: true, image: '', status: true });

onMounted(() => customerStore.fetchCustomers());

const viewAddresses = (id) => router.push(`/admin/customers/${id}/addresses`);

const openAddModal = () => {
  isEditMode.value = false;
  Object.assign(formData, { lastName: '', firstName: '', email: '', phoneNumber: '', birthday: '', gender: true, image: '', status: true });
  isModalOpen.value = true;
};

const editCustomer = (c) => {
  isEditMode.value = true;
  currentId.value = c.id;
  Object.assign(formData, { ...c });
  isModalOpen.value = true;
};

const closeModal = () => isModalOpen.value = false;

const handleSubmit = async () => {
  try {
    if (isEditMode.value) await customerStore.updateCustomer(currentId.value, formData);
    else await customerStore.createCustomer(formData);
    Swal.fire('Thành công!', '', 'success');
    customerStore.fetchCustomers();
    closeModal();
  } catch (e) { Swal.fire('Lỗi!', 'Kiểm tra lại dữ liệu', 'error'); }
};

const confirmDelete = (id) => {
  Swal.fire({ title: 'Khóa tài khoản này?', icon: 'warning', showCancelButton: true }).then(async (res) => {
    if (res.isConfirmed) {
      await customerStore.deleteCustomer(id);
      customerStore.fetchCustomers();
    }
  });
};
</script>

<style scoped>
.btn-icon-alt { 
  background: #f1f5f9; border: none; width: 32px; height: 32px; border-radius: 6px; 
  cursor: pointer; transition: 0.2s; margin-left: 5px;
}
.btn-icon-alt:hover { background: #e0e7ff; transform: translateY(-2px); }
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