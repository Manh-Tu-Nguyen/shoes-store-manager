<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Nhân sự</h2>
        <p class="text-muted">Danh sách nhân viên nội bộ</p>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="btn-primary">+ Thêm Nhân Viên</button>
      </div>
    </div>

    <DataTable :isEmpty="employeeStore.employees.length === 0" :colCount="9">
      <template #header>
        <th>Ảnh</th>
        <th>Mã NV</th>
        <th>Thông tin</th>
        <th>Chức vụ</th>
        <th>Lương</th>
        <th>Ca làm việc</th>
        <th>Trạng thái</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="emp in employeeStore.employees" :key="emp.id">
          <td><img :src="emp.image || 'https://placehold.co/50x50?text=Staff'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ emp.code }}</span></td>
          <td>
             <div class="font-bold">{{ emp.lastName }} {{ emp.firstName }}</div>
             <div class="text-xs text-blue-600">{{ emp.account }}</div>
          </td>
          <td><span class="badge-role">{{ emp.role?.name || '---' }}</span></td>
          <td class="text-price font-bold">{{ emp.salary?.toLocaleString('vi-VN') }} ₫</td>
          <td>{{ emp.workShift?.name || '---' }}</td>
          <td>
            <span :class="['status-dot', emp.status ? 'active' : 'inactive']"></span>
            {{ emp.status ? 'Đang làm' : 'Đã nghỉ' }}
          </td>
          <td>
            <ActionButtons :showEdit="true" :showDelete="true" @edit="editEmployee(emp)" @delete="confirmDelete(emp)" />
            <button @click="goToWorkShift(emp.workShiftId)" class="btn-icon-alt" title="Chi tiết ca làm">⏱️</button>
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 750px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm' }} Nhân Viên</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-row">
            <div class="form-group">
              <label>Họ <span class="text-danger">*</span></label>
              <input v-model.trim="formData.lastName" type="text" placeholder="Nhập họ" />
              <span class="error-msg" v-if="errors.lastName">{{ errors.lastName }}</span>
            </div>
            <div class="form-group">
              <label>Tên <span class="text-danger">*</span></label>
              <input v-model.trim="formData.firstName" type="text" placeholder="Nhập tên" />
              <span class="error-msg" v-if="errors.firstName">{{ errors.firstName }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Tài khoản <span class="text-danger">*</span></label>
              <input v-model.trim="formData.account" :disabled="isEditMode" type="text" placeholder="Nhập tài khoản đăng nhập" />
              <span class="error-msg" v-if="errors.account">{{ errors.account }}</span>
            </div>
            <div class="form-group" v-if="!isEditMode">
              <label>Mật khẩu <span class="text-danger">*</span></label>
              <input v-model="formData.password" type="password" placeholder="Tối thiểu 6 ký tự" />
              <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Chức vụ (Role) <span class="text-danger">*</span></label>
              <select v-model="formData.roleId">
                <option value="">-- Chọn chức vụ --</option>
                <option v-for="r in employeeStore.roles" :key="r.id" :value="r.id">{{ r.name }}</option>
              </select>
              <span class="error-msg" v-if="errors.roleId">{{ errors.roleId }}</span>
            </div>
            <div class="form-group">
              <label>Ca làm việc <span class="text-danger">*</span></label>
              <select v-model="formData.workShiftId">
                <option value="">-- Chọn ca làm việc --</option>
                <option v-for="w in employeeStore.workShifts" :key="w.id" :value="w.id">{{ w.name }}</option>
              </select>
              <span class="error-msg" v-if="errors.workShiftId">{{ errors.workShiftId }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Lương cơ bản (₫) <span class="text-danger">*</span></label>
              <input v-model.number="formData.salary" type="number" placeholder="Nhập số lương" />
              <span class="error-msg" v-if="errors.salary">{{ errors.salary }}</span>
            </div>
            <div class="form-group">
              <label>Số điện thoại <span class="text-danger">*</span></label>
              <input v-model.trim="formData.phoneNumber" type="tel" placeholder="Ví dụ: 0912345678" />
              <span class="error-msg" v-if="errors.phoneNumber">{{ errors.phoneNumber }}</span>
            </div>
          </div>
          <div class="form-row">
             <div class="form-group">
               <label>Email <span class="text-danger">*</span></label>
               <input v-model.trim="formData.email" type="email" placeholder="example@gmail.com" />
               <span class="error-msg" v-if="errors.email">{{ errors.email }}</span>
             </div>
             <div class="form-group">
               <label>Ngày sinh <span class="text-danger">*</span></label>
               <input v-model="formData.birthday" type="date" />
               <span class="error-msg" v-if="errors.birthday">{{ errors.birthday }}</span>
             </div>
          </div>
          
          <div class="image-input-container">
             <div class="image-preview-box"><img :src="formData.image || 'https://placehold.co/120x120?text=Staff'" @error="e => e.target.src='https://placehold.co/120x120?text=Error'" /></div>
             <div class="form-group flex-1"><label>Link ảnh</label><textarea v-model.trim="formData.image" rows="3" placeholder="Nhập URL hình ảnh nhân viên"></textarea></div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy</button>
            <button type="submit" class="btn-primary">Lưu nhân sự</button>
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

const initialForm = { code: 'AUTO_CODE', lastName: '', firstName: '', email: '', phoneNumber: '', birthday: '', gender: true, account: '', password: '', salary: '', roleId: '', workShiftId: '', image: '', status: true };
const formData = reactive({ ...initialForm });
const errors = reactive({});

onMounted(() => employeeStore.fetchInitData());

const goToWorkShift = () => {
  router.push('/admin/work-shifts');
};

const openAddModal = () => {
  isEditMode.value = false;
  clearErrors();
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editEmployee = (emp) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = emp.id;
  Object.assign(formData, { ...emp, roleId: emp.role?.id || '', workShiftId: emp.workShift?.id || '', password: '' });
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  clearErrors();
};

const clearErrors = () => {
  Object.keys(errors).forEach(key => delete errors[key]);
};

// LUỒNG KIỂM TRA BẢO MẬT DỮ LIỆU ĐẦU VÀO TRƯỚC KHI GỬI
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!formData.lastName) { errors.lastName = 'Họ không được để trống'; isValid = false; }
  if (!formData.firstName) { errors.firstName = 'Tên không được để trống'; isValid = false; }
  
  if (!isEditMode.value) {
    if (!formData.account) { errors.account = 'Tài khoản không được để trống'; isValid = false; }
    if (!formData.password) { errors.password = 'Mật khẩu không được để trống'; isValid = false; }
    else if (formData.password.length < 6) { errors.password = 'Mật khẩu phải chứa ít nhất 6 ký tự'; isValid = false; }
  }

  if (!formData.roleId) { errors.roleId = 'Vui lòng chọn chức vụ'; isValid = false; }
  if (!formData.workShiftId) { errors.workShiftId = 'Vui lòng chọn ca làm việc'; isValid = false; }
  
  if (formData.salary === '' || formData.salary === null) { errors.salary = 'Lương không được để trống'; isValid = false; }
  else if (Number(formData.salary) <= 0) { errors.salary = 'Mức lương phải lớn hơn 0 ₫'; isValid = false; }

  const phoneRegex = /^(84|0[3|5|7|8|9])+([0-9]{8})\b$/;
  if (!formData.phoneNumber) { errors.phoneNumber = 'Số điện thoại không được để trống'; isValid = false; }
  else if (!phoneRegex.test(formData.phoneNumber)) { errors.phoneNumber = 'Số điện thoại không đúng định dạng VN (10 chữ số)'; isValid = false; }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!formData.email) { errors.email = 'Email không được để trống'; isValid = false; }
  else if (!emailRegex.test(formData.email)) { errors.email = 'Email không đúng định dạng'; isValid = false; }

  if (!formData.birthday) { errors.birthday = 'Ngày sinh không được để trống'; isValid = false; }
  else {
    const today = new Date();
    const birthDate = new Date(formData.birthday);
    if (birthDate >= today) { errors.birthday = 'Ngày sinh phải nằm trong quá khứ'; isValid = false; }
  }

  return isValid;
};

// THỰC THI THÊM/SỬA CÓ HỘP THOẠI XÁC MINH NHÂN THÂN VÀ QUY TRÌNH
const handleSubmit = async () => {
  if (!validateForm()) return;

  const titleAction = isEditMode.value ? 'Xác nhận cập nhật?' : 'Xác nhận thêm nhân viên?';
  const textAction = isEditMode.value 
    ? `Dữ liệu của nhân viên [${formData.lastName} ${formData.firstName}] sẽ được thay đổi trên toàn hệ thống.` 
    : `Tài khoản [${formData.account}] sẽ được tạo lập với chức vụ đã chọn.`;

  Swal.fire({
    title: titleAction,
    text: textAction,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy bỏ'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        if (isEditMode.value) await employeeStore.updateEmployee(currentId.value, formData);
        else await employeeStore.createEmployee(formData);
        
        Swal.fire('Thành công!', isEditMode.value ? 'Hồ sơ nhân sự đã được cập nhật.' : 'Nhân viên mới đã được thêm vào hệ thống.', 'success');
        employeeStore.fetchInitData();
        closeModal();
      } catch (e) {
        Swal.fire('Thất bại!', e.response?.data?.message || 'Kiểm tra lại xung đột dữ liệu (Email/Tài khoản đã tồn tại)', 'error');
      }
    }
  });
};

// THỰC THI XÓA (MỀM) CÓ ĐIỀU KIỆN RÀNG BUỘC CHẶT CHẼ
const confirmDelete = (emp) => {
  Swal.fire({
    title: `Đình chỉ công tác nhân viên?`,
    text: `Tài khoản của [${emp.lastName} ${emp.firstName}] sẽ bị vô hiệu hóa quyền truy cập vào tất cả hệ thống POS/Admin.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận đình chỉ',
    cancelButtonText: 'Hủy'
  }).then(async (res) => {
    if (res.isConfirmed) {
      try {
        await employeeStore.deleteEmployee(emp.id);
        Swal.fire('Đã đình chỉ!', 'Trạng thái tài khoản nhân viên đã được chuyển thành Đã nghỉ.', 'success');
        employeeStore.fetchInitData();
      } catch (e) {
        Swal.fire('Thất bại!', 'Không thể thực thi lệnh xóa mềm.', 'error');
      }
    }
  });
};
</script>

<style scoped>
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
.form-group input.error, .form-group select.error { border-color: #ef4444; }
input:disabled { background-color: #f1f5f9; color: #94a3b8; cursor: not-allowed; }

.badge-role { background: #fee2e2; color: #991b1b; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
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
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; display: flex; flex-direction: column;}
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; }
.image-input-container { display: flex; gap: 20px; align-items: flex-start; background: #f8fafc; padding: 15px; border-radius: 8px; border: 1px dashed #cbd5e1; margin-bottom: 15px; }
.image-preview-box { width: 120px; height: 120px; border-radius: 8px; border: 2px dashed #cbd5e1; overflow: hidden; display: flex; justify-content: center; align-items: center; background: white; flex-shrink: 0; }
.image-preview-box img { width: 100%; height: 100%; object-fit: cover; }
.flex-1 { flex: 1; }
textarea { resize: none; width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
textarea:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
</style>