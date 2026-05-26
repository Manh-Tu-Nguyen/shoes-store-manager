<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Sản phẩm</h2>
        <p class="text-muted">Quản lý thông tin chung của các dòng sản phẩm</p>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="btn-primary">
          <span class="icon">+</span> Thêm Sản Phẩm
        </button>
      </div>
    </div>

    <DataTable 
      :isEmpty="productStore.products.length === 0" 
      :colCount="8"
    >
      <template #header>
        <th>Hình ảnh</th>
        <th>Mã SP</th>
        <th>Tên sản phẩm</th>
        <th>Thương hiệu</th>
        <th>Danh mục</th>
        <th>Xuất xứ</th>
        <th>Trạng thái</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="p in productStore.products" :key="p.id">
          <td><img :src="p.image || 'https://placehold.co/50x50?text=No+Image'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ p.code }}</span></td>
          <td class="font-bold">{{ p.name }}</td>
          <td>{{ p.brand?.name || '---' }}</td>
          <td>{{ p.category?.name || '---' }}</td>
          <td>{{ p.origin?.name || '---' }}</td>
          <td>
            <span :class="['status-dot', p.status ? 'active' : 'inactive']"></span>
            {{ p.status ? 'Đang kinh doanh' : 'Ngừng kinh doanh' }}
          </td>
          <td>
            <ActionButtons 
              :showVariants="true"
              :showEdit="true"
              :showDelete="true"
              @variants="goToVariants(p.id)"
              @edit="editProduct(p)" 
              @delete="confirmDelete(p)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content" style="width: 650px;">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-row">
            <div class="form-group" v-if="isEditMode">
              <label>Mã Sản Phẩm</label>
              <input :value="formData.code" type="text" disabled class="input-disabled" />
            </div>
            <div class="form-group" :class="{ 'col-span-2': !isEditMode }">
              <label>Tên sản phẩm <span class="text-danger">*</span></label>
              <input v-model.trim="formData.name" type="text" placeholder="Ví dụ: Nike Air Zoom 40" />
              <span class="error-msg" v-if="errors.name">{{ errors.name }}</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
  <label>Thương hiệu <span class="text-danger">*</span></label>
  <select v-model="formData.brandId" :disabled="productStore.brands.length === 0">
    <option value="">{{ productStore.brands.length === 0 ? 'Loading hoặc lỗi dữ liệu...' : '-- Chọn thương hiệu --' }}</option>
    <option v-for="b in productStore.brands" :key="b.id" :value="b.id">{{ b.name }}</option>
  </select>
  <span class="error-msg" v-if="errors.brandId">{{ errors.brandId }}</span>
</div>

<div class="form-group">
  <label>Danh mục <span class="text-danger">*</span></label>
  <select v-model="formData.categoryId" :disabled="productStore.categories.length === 0">
    <option value="">{{ productStore.categories.length === 0 ? 'Loading hoặc lỗi dữ liệu...' : '-- Chọn danh mục --' }}</option>
    <option v-for="c in productStore.categories" :key="c.id" :value="c.id">{{ c.name }}</option>
  </select>
  <span class="error-msg" v-if="errors.categoryId">{{ errors.categoryId }}</span>
</div>

<div class="form-group">
  <label>Xuất xứ <span class="text-danger">*</span></label>
  <select v-model="formData.originId" :disabled="productStore.origins.length === 0">
    <option value="">{{ productStore.origins.length === 0 ? 'Loading hoặc lỗi dữ liệu...' : '-- Chọn xuất xứ --' }}</option>
    <option v-for="o in productStore.origins" :key="o.id" :value="o.id">{{ o.name }}</option>
  </select>
  <span class="error-msg" v-if="errors.originId">{{ errors.originId }}</span>
</div>
            <div class="form-group">
              <label>Trạng thái kinh doanh</label>
              <select v-model="formData.status">
                <option :value="true">Đang kinh doanh</option>
                <option :value="false">Ngừng kinh doanh</option>
              </select>
            </div>
          </div>

          <div class="image-input-container">
            <div class="image-preview-box">
              <img 
                v-if="formData.image" 
                :src="formData.image" 
                alt="Preview" 
                @error="e => e.target.src = 'https://placehold.co/120x120?text=Lỗi+Đường+Dẫn'" 
              />
              <div v-else class="empty-image">
                <span class="icon">📸</span>
                <span>Chưa có ảnh</span>
              </div>
            </div>

            <div class="form-group flex-1">
              <label>Link ảnh đại diện dòng sản phẩm (URL)</label>
              <textarea 
                v-model.trim="formData.image" 
                rows="3" 
                placeholder="Dán đường dẫn hình ảnh từ internet (https://...) vào đây để hiển thị cấu trúc xem trước..."
              ></textarea>
              <span class="error-msg" v-if="errors.image">{{ errors.image }}</span>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy bỏ</button>
            <button type="submit" class="btn-primary">
              {{ isEditMode ? 'Lưu thay đổi' : 'Khởi tạo sản phẩm' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';
import { useProductStore } from '@/store/productStore';

const router = useRouter();
const productStore = useProductStore();

const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = { brandId: '', categoryId: '', originId: '', name: '', code: 'AUTO_CODE', image: '', status: true };
const formData = reactive({ ...initialForm });
const errors = reactive({});

onMounted(async () => {
  try {
    await Promise.all([
      productStore.fetchAllAttributes(),
      productStore.fetchProducts()
    ]);
  } catch (err) {
    console.error("Lỗi khởi tạo danh sách sản phẩm:", err);
  }
});

const goToVariants = (productId) => {
  router.push(`/admin/products/${productId}/variants`);
};

const openAddModal = () => {
  isEditMode.value = false;
  currentId.value = null;
  clearErrors();
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editProduct = (product) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = product.id;
  Object.assign(formData, { 
    ...product,
    brandId: product.brand?.id || '',
    categoryId: product.category?.id || '',
    originId: product.origin?.id || ''
  });
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  clearErrors();
};

const clearErrors = () => {
  Object.keys(errors).forEach(key => delete errors[key]);
};

// TẦNG KIỂM SOÁT VÀ ĐÁNH CHẶN DỮ LIỆU LỖI TẠI CLIENT (DEFENSIVE LAYER)
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!formData.name) { errors.name = 'Tên dòng sản phẩm không được để trống'; isValid = false; }
  else if (formData.name.length < 3) { errors.name = 'Tên sản phẩm tối thiểu phải từ 3 ký tự'; isValid = false; }

  if (!formData.brandId) { errors.brandId = 'Vui lòng xác định thương hiệu sản phẩm'; isValid = false; }
  if (!formData.categoryId) { errors.categoryId = 'Vui lòng xác định danh mục phân loại'; isValid = false; }
  if (!formData.originId) { errors.originId = 'Vui lòng xác định xuất xứ quốc gia'; isValid = false; }

  if (formData.image) {
    try {
      // Sử dụng API URL bản địa của JavaScript - Tuyệt đối an toàn, tốc độ tính bằng nano-giây
      const url = new URL(formData.image);
      if (url.protocol !== 'http:' && url.protocol !== 'https:') {
        errors.image = 'Đường dẫn phải bắt đầu bằng http:// hoặc https://';
        isValid = false;
      }
    } catch (e) {
      errors.image = 'Đường dẫn hình ảnh không hợp lệ!';
      isValid = false;
    }
  }

  return isValid;
};

// ĐIỀU HƯỚNG GIAO DỊCH QUA HỘP THOẠI XÁC MINH CỦA SWEETALERT2
const handleSubmit = async () => {
  if (!validateForm()) return;

  const titleText = isEditMode.value ? 'Cập nhật dòng sản phẩm?' : 'Khởi tạo dòng sản phẩm mới?';
  const descText = isEditMode.value
    ? `Hồ sơ thông tin chung của dòng sản phẩm [${formData.name}] sẽ bị ghi đè dữ liệu.`
    : `Dòng sản phẩm [${formData.name}] sẽ được đăng ký vào core hệ thống.`;

  Swal.fire({
    title: titleText,
    text: descText,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận',
    cancelButtonText: 'Hủy bỏ'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        if (isEditMode.value) {
          await productStore.updateProduct(currentId.value, formData);
          Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã cập nhật cấu trúc sản phẩm.', timer: 1200, showConfirmButton: false });
        } else {
          await productStore.createProduct(formData);
          Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã khởi tạo thực thể sản phẩm cha.', timer: 1200, showConfirmButton: false });
        }
        await productStore.fetchProducts();
        closeModal();
      } catch (error) {
        const msg = error.response?.data?.message || 'Thao tác thất bại.';
    Swal.fire({ icon: 'error', title: 'Lỗi trùng lặp!', text: msg });
      }
    }
  });
};

// LUỒNG XOÁ MỀM ĐỂ BẢO VỆ TOÀN VẸN THÔNG TIN KHO BIẾN THỂ (SKU)
const confirmDelete = (product) => {
  Swal.fire({
    title: 'Ngừng kinh doanh dòng sản phẩm?',
    text: `Toàn bộ các biến thể chi tiết (Màu sắc, Size) thuộc về dòng [${product.name}] sẽ bị ẩn khỏi luồng hiển thị bán hàng trực tuyến Online.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận ngừng bán',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await productStore.deleteProduct(product.id);
        Swal.fire('Đã chuyển đổi!', 'Trạng thái dòng sản phẩm đã chuyển sang Ngừng kinh doanh.', 'success');
        await productStore.fetchProducts();
      } catch(err) {
        Swal.fire('Lỗi kiến trúc!', err.response?.data?.message || 'Không thể vô hiệu hóa sản phẩm cha do ràng buộc giao dịch quầy POS.', 'error');
      }
    }
  });
};
</script>

<style scoped>
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