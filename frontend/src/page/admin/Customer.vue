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
            <div class="text-xs text-gray-500">{{ c.email || '---' }}</div>
            <div class="font-bold text-blue-600">{{ c.phoneNumber || '---' }}</div>
          </td>
          <td>{{ c.birthday || '---' }}</td>
          <td>{{ c.gender ? 'Nam' : 'Nữ' }}</td>
          <td>
            <span :class="['status-dot', c.status ? 'active' : 'inactive']"></span>
            {{ c.status ? 'Hoạt động' : 'Bị khóa' }}
          </td>
          <td>
            <ActionButtons 
              :showEdit="true" :showDelete="true"
              @edit="editCustomer(c)" @delete="confirmDelete(c)"
            />
            <button @click="viewAddresses(c.id)" class="btn-icon-alt" title="Địa chỉ nhận hàng">📍</button>
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 700px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật' : 'Thêm mới' }} Khách hàng</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-row">
            <div class="form-group">
              <label>Họ <span class="text-danger">*</span></label>
              <input v-model.trim="formData.lastName" type="text" placeholder="Nhập họ khách hàng" />
              <span class="error-msg" v-if="errors.lastName">{{ errors.lastName }}</span>
            </div>
            <div class="form-group">
              <label>Tên <span class="text-danger">*</span></label>
              <input v-model.trim="formData.firstName" type="text" placeholder="Nhập tên khách hàng" />
              <span class="error-msg" v-if="errors.firstName">{{ errors.firstName }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Email</label>
              <input v-model.trim="formData.email" type="email" placeholder="example@gmail.com (Không bắt buộc)" />
              <span class="error-msg" v-if="errors.email">{{ errors.email }}</span>
            </div>
            <div class="form-group">
              <label>Số điện thoại <span class="text-danger">*</span></label>
              <input v-model.trim="formData.phoneNumber" type="tel" placeholder="Ví dụ: 0987654321" />
              <span class="error-msg" v-if="errors.phoneNumber">{{ errors.phoneNumber }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Ngày sinh</label>
              <input v-model="formData.birthday" type="date" />
              <span class="error-msg" v-if="errors.birthday">{{ errors.birthday }}</span>
            </div>
            <div class="form-group">
              <label>Giới tính</label>
              <select v-model="formData.gender">
                <option :value="true">Nam</option>
                <option :value="false">Nữ</option>
              </select>
            </div>
          </div>

          <div class="account-section-box" v-if="!isEditMode">
            <p class="section-desc">Cấu hình tài khoản đăng nhập hệ thống Online (Tùy chọn)</p>
            <div class="form-row">
              <div class="form-group">
                <label>Tài khoản mua hàng</label>
                <input v-model.trim="formData.account" type="text" placeholder="Nhập username" />
                <span class="error-msg" v-if="errors.account">{{ errors.account }}</span>
              </div>
              <div class="form-group">
                <label>Mật khẩu</label>
                <input v-model="formData.password" type="password" placeholder="Tối thiểu 6 ký tự" />
                <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
              </div>
            </div>
          </div>
          
          <div class="image-input-container">
             <div class="image-preview-box">
                <img :src="formData.image || 'https://placehold.co/120x120?text=User'" @error="e => e.target.src = 'https://placehold.co/120x120?text=Error'" />
             </div>
             <div class="form-group flex-1">
                <label>Link ảnh đại diện</label>
                <textarea v-model.trim="formData.image" rows="3" placeholder="Nhập liên kết hình ảnh khách hàng"></textarea>
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

const initialForm = { code: 'AUTO_CODE', lastName: '', firstName: '', email: '', phoneNumber: '', birthday: '', gender: true, image: '', account: '', password: '', status: true };
const formData = reactive({ ...initialForm });
const errors = reactive({});

onMounted(() => customerStore.fetchCustomers());

const viewAddresses = (id) => router.push(`/admin/customers/${id}/addresses`);

const openAddModal = () => {
  isEditMode.value = false;
  clearErrors();
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editCustomer = (c) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = c.id;
  Object.assign(formData, { ...c, account: c.account || '', password: '' });
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  clearErrors();
};

const clearErrors = () => {
  Object.keys(errors).forEach(key => delete errors[key]);
};

// TẦNG INTERCEPT VALIDATION ĐẦU VÀO TẠI CLIENT
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!formData.lastName) { errors.lastName = 'Họ khách hàng không được để trống'; isValid = false; }
  if (!formData.firstName) { errors.firstName = 'Tên khách hàng không được để trống'; isValid = false; }
  
  if (!formData.phoneNumber) { errors.phoneNumber = 'Số điện thoại không được để trống'; isValid = false; }
  else {
    const phoneRegex = /^(84|0[3|5|7|8|9])+([0-9]{8})\b$/;
    if (!phoneRegex.test(formData.phoneNumber)) { errors.phoneNumber = 'Số điện thoại không đúng định dạng Việt Nam'; isValid = false; }
  }

  if (formData.email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) { errors.email = 'Cấu trúc địa chỉ email không hợp lệ'; isValid = false; }
  }

  if (formData.birthday) {
    const today = new Date();
    const birthDate = new Date(formData.birthday);
    if (birthDate >= today) { errors.birthday = 'Ngày sinh phải ở trong quá khứ'; isValid = false; }
  }

  // Khách hàng có thể không cần điền account, nhưng nếu điền mật khẩu thì bắt buộc kiểm tra độ dài
  if (!isEditMode.value && formData.account) {
    if (!formData.password) { errors.password = 'Vui lòng khởi tạo mật khẩu cho tài khoản này'; isValid = false; }
    else if (formData.password.length < 6) { errors.password = 'Mật khẩu hệ thống yêu cầu từ 6 ký tự trở lên'; isValid = false; }
  }

  return isValid;
};

// XỬ LÝ GIAO DỊCH VỚI HỘP THOẠI XÁC MINH CỦA SWEETALERT2
const handleSubmit = async () => {
  if (!validateForm()) return;

  const titleText = isEditMode.value ? 'Cập nhật thông tin khách hàng?' : 'Xác nhận tạo hồ sơ khách hàng?';
  const descText = isEditMode.value 
    ? `Hồ sơ của khách hàng [${formData.lastName} ${formData.firstName}] sẽ bị thay đổi trên toàn hệ thống giao dịch.`
    : `Khách hàng mới sẽ được ghi danh vào cơ sở dữ liệu bán hàng.`;

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
        if (isEditMode.value) await customerStore.updateCustomer(currentId.value, formData);
        else await customerStore.createCustomer(formData);

        Swal.fire('Thành công!', isEditMode.value ? 'Thông tin khách hàng đã được lưu lại.' : 'Đã đăng ký thông tin khách hàng mới.', 'success');
        customerStore.fetchCustomers();
        closeModal();
      } catch (e) {
        Swal.fire('Thất bại!', e.response?.data?.message || 'Có lỗi xảy ra, kiểm tra trùng lặp Số điện thoại/Email.', 'error');
      }
    }
  });
};

// LUỒNG XOÁ MỀM (KHÓA TÀI KHOẢN KHÁCH HÀNG)
const confirmDelete = (customer) => {
  Swal.fire({
    title: 'Đóng băng tài khoản khách hàng?',
    text: `Khách hàng [${customer.lastName} ${customer.firstName}] sẽ không thể đăng nhập mua hàng trực tuyến hoặc áp dụng tích điểm tại quầy POS.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận khóa',
    cancelButtonText: 'Hủy bỏ'
  }).then(async (res) => {
    if (res.isConfirmed) {
      try {
        await customerStore.deleteCustomer(customer.id);
        Swal.fire('Đã khóa!', 'Trạng thái tài khoản khách hàng đã chuyển sang: Bị khóa.', 'success');
        customerStore.fetchCustomers();
      } catch (e) {
        Swal.fire('Thất bại!', 'Không thể thay đổi trạng thái tài khoản.', 'error');
      }
    }
  });
};
</script>

<style scoped>
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
.account-section-box { background-color: #f8fafc; padding: 15px; border-radius: 8px; border: 1px solid #e2e8f0; margin-bottom: 15px; }
.section-desc { font-size: 12px; color: #64748b; font-weight: 600; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 0.5px; }

.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-icon-alt { background: #f1f5f9; border: none; width: 32px; height: 32px; border-radius: 6px; cursor: pointer; transition: 0.2s; margin-left: 5px; }
.btn-icon-alt:hover { background: #e0e7ff; transform: translateY(-2px); }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
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
.form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
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