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
          <td class="font-bold text-blue-600">{{ addr.consigneePhone }}</td>
          <td>{{ addr.streetDetail }}</td>
          <td>{{ addr.ward }} - {{ addr.city }}</td>
          <td class="text-xs text-gray-500">{{ addr.note || '---' }}</td>
          <td>
            <ActionButtons :showEdit="true" :showDelete="true" @edit="editAddress(addr)" @delete="confirmDelete(addr)" />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm' }} địa chỉ nhận hàng</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-row">
            <div class="form-group">
              <label>Tên người nhận <span class="text-danger">*</span></label>
              <input v-model.trim="formData.consigneeName" type="text" placeholder="Nhập họ tên người nhận" />
              <span class="error-msg" v-if="errors.consigneeName">{{ errors.consigneeName }}</span>
            </div>
            <div class="form-group">
              <label>Số điện thoại người nhận <span class="text-danger">*</span></label>
              <input v-model.trim="formData.consigneePhone" type="tel" placeholder="Ví dụ: 0912345678" />
              <span class="error-msg" v-if="errors.consigneePhone">{{ errors.consigneePhone }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Tỉnh/Thành phố <span class="text-danger">*</span></label>
              <input v-model.trim="formData.city" type="text" placeholder="Nhập Tỉnh / Thành phố" />
              <span class="error-msg" v-if="errors.city">{{ errors.city }}</span>
            </div>
            <div class="form-group">
              <label>Phường/Xã <span class="text-danger">*</span></label>
              <input v-model.trim="formData.ward" type="text" placeholder="Nhập Phường / Xã" />
              <span class="error-msg" v-if="errors.ward">{{ errors.ward }}</span>
            </div>
          </div>
          <div class="form-group">
            <label>Địa chỉ chi tiết <span class="text-danger">*</span></label>
            <input v-model.trim="formData.streetDetail" type="text" placeholder="Số nhà, tên ngõ, tên đường..." />
            <span class="error-msg" v-if="errors.streetDetail">{{ errors.streetDetail }}</span>
          </div>
          <div class="form-group">
            <label>Ghi chú giao hàng</label>
            <textarea v-model.trim="formData.note" rows="2" placeholder="Ghi chú cho shipper (Ví dụ: Giao giờ hành chính)"></textarea>
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
const errors = reactive({});

onMounted(() => {
  if (!customerId || isNaN(Number(customerId))) {
    Swal.fire('Lỗi định danh!', 'Mã định danh khách hàng không hợp lệ.', 'error');
    goBack();
    return;
  }
  customerStore.fetchAddressesByCustomerId(customerId);
});

const goBack = () => router.push('/admin/customers');

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
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editAddress = (addr) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = addr.id;
  Object.assign(formData, { ...addr, customerId: Number(customerId) });
  isModalOpen.value = true;
};

// TẦNG KIỂM SOÁT CHẤT LƯỢNG DỮ LIỆU ĐỊA CHỈ (CLIENT VALIDATION)
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!formData.consigneeName) { errors.consigneeName = 'Tên người nhận không được để trống'; isValid = false; }
  
  if (!formData.consigneePhone) { errors.consigneePhone = 'Số điện thoại không được để trống'; isValid = false; }
  else {
    const phoneRegex = /^(84|0[3|5|7|8|9])+([0-9]{8})\b$/;
    if (!phoneRegex.test(formData.consigneePhone)) { errors.consigneePhone = 'Số điện thoại người nhận không hợp lệ'; isValid = false; }
  }

  if (!formData.city) { errors.city = 'Tỉnh/Thành phố không được để trống'; isValid = false; }
  if (!formData.ward) { errors.ward = 'Phường/Xã không được để trống'; isValid = false; }
  if (!formData.streetDetail) { errors.streetDetail = 'Địa chỉ chi tiết không được để trống'; isValid = false; }

  return isValid;
};

// THỰC THI GIAO DỊCH CÓ KIỂM SOÁT XÁC MINH NGHIỆP VỤ
const handleSubmit = async () => {
  if (!validateForm()) return;

  const titleText = isEditMode.value ? 'Cập nhật địa chỉ nhận hàng?' : 'Xác nhận thêm địa chỉ mới?';
  const descText = isEditMode.value
    ? `Thông tin vận chuyển cũ của khách hàng sẽ được thay đổi.`
    : `Địa chỉ này sẽ được lưu trữ vào sổ địa chỉ phục vụ cho việc lên đơn hàng trực tuyến.`;

  Swal.fire({
    title: titleText,
    text: descText,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        if (isEditMode.value) await customerStore.updateAddress(currentId.value, formData);
        else await customerStore.createAddress(formData);
        
        Swal.fire({ icon: 'success', title: 'Đã lưu địa chỉ!', timer: 1200, showConfirmButton: false });
        customerStore.fetchAddressesByCustomerId(customerId);
        closeModal();
      } catch (e) {
        Swal.fire('Thất bại!', e.response?.data?.message || 'Vui lòng kiểm tra lại kết nối hoặc dữ liệu đầu vào.', 'error');
      }
    }
  });
};

// LUỒNG XÓA CỨNG (VÌ ĐỊA CHỈ CHỈ LÀ THỰC THỂ PHỤ THUỘC)
const confirmDelete = (addr) => {
  Swal.fire({
    title: 'Xóa vĩnh viễn địa chỉ này?',
    text: `Hành động này sẽ loại bỏ địa chỉ [${addr.streetDetail}, ${addr.ward}, ${addr.city}] khỏi sổ địa chỉ của khách hàng.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận xóa',
    cancelButtonText: 'Hủy bỏ'
  }).then(async (res) => {
    if (res.isConfirmed) {
      try {
        await customerStore.deleteAddress(addr.id);
        Swal.fire({ icon: 'success', title: 'Đã xóa địa chỉ!', timer: 1000, showConfirmButton: false });
        customerStore.fetchAddressesByCustomerId(customerId);
      } catch (e) {
        Swal.fire('Thất bại!', 'Không thể thực thi lệnh xóa địa chỉ do ràng buộc dữ liệu đơn hàng.', 'error');
      }
    }
  });
};
</script>

<style scoped>
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
.mr-2 { margin-right: 8px; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.font-bold { font-weight: 600; color: #1e293b; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; width: 600px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; display: flex; flex-grow: 1; flex-direction: column; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; }
textarea { resize: none; width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
textarea:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
</style>