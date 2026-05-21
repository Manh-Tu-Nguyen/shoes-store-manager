<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Ca làm việc</h2>
        <p class="text-muted">Cấu hình thời gian làm việc cho nhân viên</p>
      </div>
      <div class="header-actions">
        <button @click="goBack" class="btn-secondary mr-2">⬅ Về ds Nhân viên</button>
        <button @click="openAddModal" class="btn-primary">+ Thêm Ca mới</button>
      </div>
    </div>

    <DataTable :isEmpty="employeeStore.workShifts?.length === 0" :colCount="4">
      <template #header>
        <th>Tên ca làm việc</th>
        <th>Giờ bắt đầu</th>
        <th>Giờ kết thúc</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="ws in employeeStore.workShifts" :key="ws.id">
          <td class="font-bold">{{ ws.name }}</td>
          <td><span class="badge-time">{{ ws.startTime }}</span></td>
          <td><span class="badge-time">{{ ws.endTime }}</span></td>
          <td>
            <ActionButtons @edit="editWorkShift(ws)" @delete="confirmDelete(ws.id)" />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 500px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm' }} Ca làm việc</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-group">
            <label>Tên ca (VD: Ca Sáng, Ca Tối) <span class="text-danger">*</span></label>
            <input v-model="formData.name" required />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Giờ bắt đầu <span class="text-danger">*</span></label>
              <input v-model="formData.startTime" type="time" required />
            </div>
            <div class="form-group">
              <label>Giờ kết thúc <span class="text-danger">*</span></label>
              <input v-model="formData.endTime" type="time" required />
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
import { useEmployeeStore } from '@/store/employeeStore';

const router = useRouter();
const employeeStore = useEmployeeStore();

const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

// Form data. Lưu ý: Input time của HTML5 trả về format "HH:mm" (hoặc "HH:mm:ss") khớp với Java LocalTime
const initialForm = { name: '', startTime: '', endTime: '' };
const formData = reactive({ ...initialForm });

onMounted(() => employeeStore.fetchWorkShifts());

const goBack = () => router.push('/admin/employees');
const closeModal = () => isModalOpen.value = false;

const openAddModal = () => {
  isEditMode.value = false;
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editWorkShift = (ws) => {
  isEditMode.value = true;
  currentId.value = ws.id;
  // Format lại giờ để đưa vào ô input time (tránh trường hợp Java trả về có cả mili giây)
  Object.assign(formData, { 
    name: ws.name, 
    startTime: ws.startTime.substring(0, 5), 
    endTime: ws.endTime.substring(0, 5) 
  });
  isModalOpen.value = true;
};

const handleSubmit = async () => {
  try {
    // Basic validation
    if (formData.startTime >= formData.endTime) {
      return Swal.fire('Lỗi', 'Giờ kết thúc phải lớn hơn giờ bắt đầu!', 'error');
    }

    if (isEditMode.value) await employeeStore.updateWorkShift(currentId.value, formData);
    else await employeeStore.createWorkShift(formData);
    
    Swal.fire({ icon: 'success', title: 'Thành công!', timer: 1000, showConfirmButton: false });
    employeeStore.fetchWorkShifts();
    closeModal();
  } catch (e) { Swal.fire('Lỗi!', e.response?.data?.message || 'Có lỗi xảy ra', 'error'); }
};

const confirmDelete = (id) => {
  Swal.fire({ 
    title: 'Xóa vĩnh viễn ca này?', 
    text: 'Không thể khôi phục. Đảm bảo không có nhân viên nào đang làm ca này.',
    icon: 'warning', 
    showCancelButton: true,
    confirmButtonColor: '#ef4444'
  }).then(async (res) => {
    if (res.isConfirmed) {
      try {
        await employeeStore.deleteWorkShift(id);
        employeeStore.fetchWorkShifts();
        Swal.fire('Đã xóa', '', 'success');
      } catch (e) {
        Swal.fire('Không thể xóa', e.response?.data?.message || 'Lỗi hệ thống', 'error');
      }
    }
  });
};
</script>

<style scoped>
.badge-time {
  background-color: #f3f4f6;
  color: #1f2937;
  padding: 4px 10px;
  border-radius: 6px;
  font-family: monospace;
  font-weight: bold;
  border: 1px solid #e5e7eb;
}
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