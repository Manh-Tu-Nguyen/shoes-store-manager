<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Voucher</h2>
        <p class="text-muted">Quản lý các chương trình khuyến mãi và giảm giá</p>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="btn-primary">
          <span class="icon">+</span> Thêm Voucher
        </button>
      </div>
    </div>

    <DataTable :isEmpty="vouchers?.length === 0" :colCount="10">
      <template #header>
        <th>Mã</th><th>Tên</th><th>Giá trị</th><th>Đơn hàng tối thiểu</th><th>Số lượng</th><th>Ngày bắt đầu</th><th>Ngày kết thúc</th><th>Loại</th><th>Trạng thái</th><th class="text-center">Hành động</th>
      </template>
      <template #body>
        <tr v-for="v in vouchers" :key="v.id">
          <td><span class="badge-code">{{ v.code }}</span></td>
          <td class="font-bold">{{ v.name }}</td>
          <td>{{ v.value.toLocaleString() }}{{ v.type ? '%' : ' ₫' }}</td>
          <td>{{ v.minOrderValue.toLocaleString() }} ₫</td>
          <td>{{ v.quantity }}</td>
          <td class="text-xs">{{ formatDateTime(v.startDate) }}</td>
          <td class="text-xs">{{ formatDateTime(v.endDate) }}</td>
          <td><span :class="['badge', v.type ? 'bg-blue' : 'bg-green']">{{ v.type ? 'Phần trăm' : 'Tiền mặt' }}</span></td>
          <td>
            <span :class="['status-dot', v.status ? 'active' : 'inactive']"></span>
            {{ v.status ? 'Đang áp dụng' : 'Đã khóa' }}
          </td>
          <td>
            <ActionButtons :showEdit="true" :showDelete="true" @edit="editVoucher(v)" @delete="confirmDelete(v)" />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 700px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Voucher' : 'Thêm Voucher Mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-row">
            <div class="form-group col-span-2">
              <label>Tên chương trình <span class="text-danger">*</span></label>
              <input v-model.trim="formData.name" type="text" placeholder="Ví dụ: Giảm giá mùa hè" />
              <span class="error-msg" v-if="errors.name">{{ errors.name }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Giá trị giảm <span class="text-danger">*</span></label>
              <input v-model.number="formData.value" type="number" />
            </div>
            <div class="form-group">
              <label>Loại giảm giá</label>
              <select v-model="formData.type">
                <option :value="true">Phần trăm (%)</option>
                <option :value="false">Tiền mặt (VNĐ)</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Đơn hàng tối thiểu</label>
              <input v-model.number="formData.minOrderValue" type="number" />
            </div>
            <div class="form-group">
              <label>Giảm tối đa</label>
              <input v-model.number="formData.maxDiscountValue" type="number" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Ngày bắt đầu <span class="text-danger">*</span></label>
              <input v-model="formData.startDate" type="datetime-local" />
              <span class="error-msg" v-if="errors.startDate">{{ errors.startDate }}</span>
            </div>
            <div class="form-group">
              <label>Ngày kết thúc <span class="text-danger">*</span></label>
              <input v-model="formData.endDate" type="datetime-local" />
              <span class="error-msg" v-if="errors.endDate">{{ errors.endDate }}</span>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy</button>
            <button type="submit" class="btn-primary">Lưu</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import Swal from 'sweetalert2';
import voucherApi from '@/api/voucherApi';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';

const vouchers = ref([]);
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = { name: '', minOrderValue: 0, maxDiscountValue: 0, startDate: '', endDate: '', value: 0, quantity: 0, type: true, status: true };
const formData = reactive({ ...initialForm });
const errors = reactive({});

onMounted(() => fetchVouchers());

const fetchVouchers = async () => {
  try {
    const res = await voucherApi.getAll(); 
    // Logic bóc vỏ dữ liệu nếu dùng Axios Interceptor trả về data trực tiếp
    vouchers.value = (res?.data || Array.isArray(res)) ? (res.data || res) : [];
  } catch (err) {
    console.error(err);
  }
};

const openAddModal = () => { isEditMode.value = false; Object.assign(formData, initialForm); isModalOpen.value = true; };

const editVoucher = (v) => { 
  isEditMode.value = true; currentId.value = v.id;
  Object.assign(formData, v); isModalOpen.value = true; 
};

const handleSubmit = async () => {
  try {
    if (isEditMode.value) await voucherApi.update(currentId.value, formData);
    else await voucherApi.create(formData);
    Swal.fire('Thành công!', '', 'success');
    closeModal(); fetchVouchers();
  } catch (e) { Swal.fire('Lỗi', e.response?.data?.message || 'Có lỗi xảy ra', 'error'); }
};

const confirmDelete = async (v) => {
  const res = await Swal.fire({ title: 'Vô hiệu hóa voucher?', icon: 'warning', showCancelButton: true });
  if (res.isConfirmed) { await voucherApi.delete(v.id); fetchVouchers(); }
};

const closeModal = () => isModalOpen.value = false;

// Format ngày tháng để hiển thị dễ nhìn hơn
const formatDateTime = (d) => {
  if (!d) return '---';
  if (Array.isArray(d)) {
    // Java LocalDateTime trả về [YYYY, MM, DD, HH, MM]
    return `${d[2]}/${d[1]}/${d[0]} ${d[3]}:${d[4] < 10 ? '0'+d[4] : d[4]}`;
  }
  return new Date(d).toLocaleString();
};
</script>

<style scoped>
/* Bạn có thể tái sử dụng file CSS của trang Sản phẩm */
.badge { padding: 4px 8px; border-radius: 4px; color: white; font-size: 12px; }
.bg-blue { background: #3b82f6; }
.bg-green { background: #10b981; }
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
.input-disabled { background-color: #f1f5f9 !important; color: #94a3b8 !important; cursor: not-allowed; }
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
.modal-content { background: white; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; border-top-left-radius: 12px; border-top-right-radius: 12px; }
.close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; border-bottom-left-radius: 12px; border-bottom-right-radius: 12px; }
.image-input-container { display: flex; gap: 20px; align-items: flex-start; background: #f8fafc; padding: 15px; border-radius: 8px; border: 1px dashed #cbd5e1; margin-bottom: 15px; }
.image-preview-box { width: 120px; height: 120px; border-radius: 8px; border: 2px dashed #cbd5e1; overflow: hidden; display: flex; justify-content: center; align-items: center; background: white; flex-shrink: 0; }
.image-preview-box img { width: 100%; height: 100%; object-fit: cover; }
.empty-image { display: flex; flex-direction: column; align-items: center; color: #94a3b8; font-size: 12px; font-weight: 500; }
.empty-image .icon { font-size: 28px; margin-bottom: 5px; }
.flex-1 { flex: 1; }
textarea { resize: none; width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
textarea:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
</style>