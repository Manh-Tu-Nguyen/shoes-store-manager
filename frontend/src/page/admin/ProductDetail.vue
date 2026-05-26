<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Biến thể (SKU)</h2>
        <p class="text-muted">Đang xem các phân loại (Màu sắc/Kích cỡ) của Sản phẩm ID: {{ productId }}</p>
      </div>
      <div class="header-actions">
        <button @click="goBack" class="btn-secondary" style="margin-right: 10px;">
          ⬅ Quay lại
        </button>
        <button @click="openAddModal" class="btn-primary">
          <span class="icon">+</span> Thêm Biến Thể
        </button>
      </div>
    </div>

    <DataTable 
      :isEmpty="productStore.productDetails?.length === 0" 
      :colCount="8"
    >
      <template #header>
        <th>Hình ảnh</th>
        <th>Mã SKU</th>
        <th>Tên phân loại</th>
        <th>Màu sắc</th>
        <th>Kích cỡ</th>
        <th>Giá bán</th>
        <th>Tồn kho</th>
        <th>Trạng thái</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="pd in productStore.productDetails" :key="pd.id">
          <td><img :src="pd.image || 'https://placehold.co/50x50?text=No+Image'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ pd.code }}</span></td>
          <td class="font-bold">{{ pd.name }}</td>
          <td>{{ pd.color?.name || '---' }}</td>
          <td><span class="badge-size">{{ pd.size?.name || '---' }}</span></td>
          <td class="text-price font-bold">{{ pd.price?.toLocaleString('vi-VN') }} ₫</td>
          <td class="font-bold" :class="pd.quantity === 0 ? 'text-danger' : 'text-slate-700'">{{ pd.quantity }}</td>
          <td>
            <span :class="['status-dot', pd.status ? 'active' : 'inactive']"></span>
            {{ pd.status ? 'Đang bán' : 'Ngừng bán' }}
          </td>
          <td>
            <ActionButtons 
              :showEdit="true"
              :showDelete="true"
              @edit="editVariant(pd)" 
              @delete="confirmDelete(pd)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body" novalidate>
          <div class="form-row">
            <div class="form-group">
              <label>Màu sắc <span class="text-danger">*</span></label>
              <select v-model="formData.colorId" :disabled="isEditMode">
                <option value="">-- Chọn màu sắc --</option>
                <option v-for="c in productStore.colors" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>
              <span class="error-msg" v-if="errors.colorId">{{ errors.colorId }}</span>
            </div>
            <div class="form-group">
              <label>Kích cỡ <span class="text-danger">*</span></label>
              <select v-model="formData.sizeId" :disabled="isEditMode">
                <option value="">-- Chọn kích cỡ --</option>
                <option v-for="s in productStore.sizes" :key="s.id" :value="s.id">{{ s.name }}</option>
              </select>
              <span class="error-msg" v-if="errors.sizeId">{{ errors.sizeId }}</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Giá bán (₫) <span class="text-danger">*</span></label>
              <input v-model.number="formData.price" type="number" placeholder="Ví dụ: 350000" />
              <span class="error-msg" v-if="errors.price">{{ errors.price }}</span>
            </div>
            <div class="form-group">
              <label>Số lượng tồn kho <span class="text-danger">*</span></label>
              <input v-model.number="formData.quantity" type="number" placeholder="Ví dụ: 100" />
              <span class="error-msg" v-if="errors.quantity">{{ errors.quantity }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>Trạng thái kinh doanh biến thể</label>
            <select v-model="formData.status">
              <option :value="true">Đang bán</option>
              <option :value="false">Ngừng bán</option>
            </select>
          </div>

          <div class="image-input-container">
            <div class="image-preview-box">
              <img 
                v-if="formData.image" 
                :src="formData.image" 
                alt="Preview" 
                @error="e => e.target.src = 'https://placehold.co/120x120?text=Loi+Duong+Dan'"
              />
              <div v-else class="empty-image">
                <span class="icon">📸</span>
                <span>Chưa có ảnh</span>
              </div>
            </div>

            <div class="form-group flex-1">
              <label>Link ảnh biến thể phối màu (URL)</label>
              <textarea 
                v-model.trim="formData.image" 
                rows="3" 
                placeholder="Dán link ảnh chi tiết của phối màu này để nhân viên POS dễ nhận diện..."
              ></textarea>
              <span class="error-msg" v-if="errors.image">{{ errors.image }}</span>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy bỏ</button>
            <button type="submit" class="btn-primary">
              {{ isEditMode ? 'Lưu thay đổi' : 'Khởi tạo SKU' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import DataTable from '@/components/common/DataTable.vue';
import ActionButtons from '@/components/common/ActionButtons.vue';
import { useProductStore } from '@/store/productStore';

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();

const productId = route.params.id; 
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = { 
  productId: Number(productId), 
  colorId: '', 
  sizeId: '', 
  name: 'AUTO_NAME', 
  code: '', 
  price: '', 
  quantity: '', 
  image: '', 
  status: true 
};
const formData = reactive({ ...initialForm });
const errors = reactive({});

onMounted(async () => {
  try {
    if (!productId || isNaN(Number(productId))) {
      Swal.fire('Lỗi định danh!', 'Mã sản phẩm cha không hợp lệ.', 'error');
      goBack();
      return;
    }
    await Promise.all([
      productStore.fetchAllAttributes(),
      productStore.fetchProductVariants(productId)
    ]);
  } catch (err) {
    console.error("Lỗi khởi tạo luồng SKU:", err);
  }
});

const goBack = () => {
  router.push('/admin/products');
};

const openAddModal = () => {
  isEditMode.value = false;
  currentId.value = null;
  clearErrors();
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editVariant = (variant) => {
  isEditMode.value = true;
  clearErrors();
  currentId.value = variant.id;
  Object.assign(formData, { 
    ...variant,
    colorId: variant.color?.id || '',
    sizeId: variant.size?.id || '',
    name: variant.name || 'AUTO_NAME'
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

// TẦNG INTERCEPT VALIDATION CHẶT CHẼ DỮ LIỆU KHO (FINANCIAL & INVENTORY PROTECT)
const validateForm = () => {
  clearErrors();
  let isValid = true;

  if (!isEditMode.value) {
    if (!formData.colorId) { errors.colorId = 'Vui lòng xác định phối màu biến thể'; isValid = false; }
    if (!formData.sizeId) { errors.sizeId = 'Vui lòng xác định kích cỡ biến thể'; isValid = false; }
  }

  if (formData.price === '' || formData.price === null) { errors.price = 'Giá bán không được để trống'; isValid = false; }
  else if (Number(formData.price) <= 0) { errors.price = 'Giá bán sản phẩm phải lớn hơn 0 ₫'; isValid = false; }

  if (formData.quantity === '' || formData.quantity === null) { errors.quantity = 'Số lượng tồn kho không được để trống'; isValid = false; }
  else if (Number(formData.quantity) < 0) { errors.quantity = 'Số lượng vật lý tồn kho không được âm'; isValid = false; }

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

  const titleText = isEditMode.value ? 'Cập nhật thông số biến thể?' : 'Khởi tạo mã SKU mới?';
  const descText = isEditMode.value
    ? `Thông tin về giá bán và tồn kho vật lý của biến thể này sẽ bị thay đổi.`
    : `Mã SKU định danh độc lập sẽ được sinh tự động theo nguyên tắc tổ hợp hệ thống.`;

  Swal.fire({
    title: titleText,
    text: descText,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2563eb',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy bỏ'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        if (isEditMode.value) {
          await productStore.updateProductDetail(currentId.value, formData);
          Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã lưu thông số kho mới.', timer: 1200, showConfirmButton: false });
        } else {
          await productStore.createProductDetail(formData);
          Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã đồng bộ tổ hợp mã SKU mới.', timer: 1200, showConfirmButton: false });
        }
        await productStore.fetchProductVariants(productId);
        closeModal();
      } catch (error) {
        const msg = error.response?.data?.message || 'Thao tác thất bại.';
    Swal.fire({ icon: 'error', title: 'Lỗi trùng lặp!', text: msg });
      }
    }
  });
};

// LUỒNG XOÁ MỀM (VÔ HIỆU HÓA TRẠNG THÁI KINH DOANH SKU ĐỂ KHÔNG LÀM HỎNG LOGIC ĐƠN HÀNG CŨ)
const confirmDelete = (variant) => {
  Swal.fire({
    title: 'Ngừng kinh doanh phân loại này?',
    text: `Biến thể [${variant.name}] sẽ lập tức bị ẩn khỏi quầy bán hàng POS và trang thương mại điện tử trực tuyến.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#64748b',
    confirmButtonText: 'Xác nhận ngừng bán',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await productStore.deleteProductDetail(variant.id);
        Swal.fire('Thành công!', 'Đã tắt trạng thái kinh doanh của mã SKU này.', 'success');
        await productStore.fetchProductVariants(productId);
      } catch(err) {
        Swal.fire('Lỗi hệ thống!', err.response?.data?.message || 'Không thể vô hiệu hóa phân loại.', 'error');
      }
    }
  });
};
</script>

<style scoped>
.error-msg { color: #ef4444; font-size: 12px; font-weight: 500; margin-top: 4px; display: block; }
.badge-size { background: #f8fafc; color: #334155; padding: 2px 8px; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 12px; font-weight: bold; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
.header-actions { display: flex; gap: 10px; }
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.product-thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }
.badge-code { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.font-bold { font-weight: 600; color: #1e293b; }
.text-price { color: #b91c1c; font-weight: bold; }
.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.status-dot.active { background-color: #22c55e; box-shadow: 0 0 8px #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }
.text-danger { color: red; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; width: 600px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; border-top-left-radius: 12px; border-top-right-radius: 12px;}
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