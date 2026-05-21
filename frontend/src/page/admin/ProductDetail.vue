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

    <!-- TÁI SỬ DỤNG DATATABLE DÙNG CHUNG -->
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
        <th>Giá bán (VNĐ)</th>
        <th>Tồn kho</th>
        <th>Trạng thái</th>
        <th class="text-center">Hành động</th>
      </template>

      <template #body>
        <tr v-for="pd in productStore.productDetails" :key="pd.id">
          <td><img :src="pd.image || 'https://via.placeholder.com/50'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ pd.code }}</span></td>
          <td class="font-bold">{{ pd.name }}</td>
          <td>{{ pd.color?.name }}</td>
          <td>{{ pd.size?.name }}</td>
          <td class="text-price">{{ pd.price?.toLocaleString('vi-VN') }} ₫</td>
          <td>{{ pd.quantity }}</td>
          <td>
            <span :class="['status-dot', pd.status ? 'active' : 'inactive']"></span>
            {{ pd.status ? 'Đang bán' : 'Ngừng bán' }}
          </td>
          <td>
            <!-- ACTION BUTTONS: Không truyền showVariants vì đang ở trang Variants rồi -->
            <ActionButtons 
              :showEdit="true"
              :showDelete="true"
              @edit="editVariant(pd)" 
              @delete="confirmDelete(pd.id)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <!-- MODAL FORM THÊM/SỬA SKU -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body">
          <!-- Lưu ý: Không hiển thị trường Name và Code vì Backend tự sinh composite (Ví dụ: Nike Đỏ 39) -->
          
          <div class="form-row">
            <div class="form-group">
              <label>Màu sắc <span class="text-danger">*</span></label>
              <!-- Khóa ô Màu/Size khi đang Edit để tránh lỗi sai lệch mã SKU -->
              <select v-model="formData.colorId" required :disabled="isEditMode">
                <option value="" disabled>-- Chọn màu sắc --</option>
                <option v-for="c in productStore.colors" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Kích cỡ <span class="text-danger">*</span></label>
              <select v-model="formData.sizeId" required :disabled="isEditMode">
                <option value="" disabled>-- Chọn kích cỡ --</option>
                <option v-for="s in productStore.sizes" :key="s.id" :value="s.id">{{ s.name }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Giá bán (VNĐ) <span class="text-danger">*</span></label>
              <input v-model="formData.price" type="number" min="1" required placeholder="VD: 500000" />
            </div>
            <div class="form-group">
              <label>Số lượng tồn kho <span class="text-danger">*</span></label>
              <input v-model="formData.quantity" type="number" min="0" required placeholder="VD: 100" />
            </div>
          </div>

          <div class="form-group">
            <label>Trạng thái</label>
            <select v-model="formData.status">
              <option :value="true">Đang bán</option>
              <option :value="false">Ngừng bán</option>
            </select>
          </div>

          <!-- KHU VỰC NHẬP VÀ XEM TRƯỚC ẢNH REALTIME -->
          <div class="image-input-container">
            <div class="image-preview-box">
              <img 
                v-if="formData.image" 
                :src="formData.image" 
                alt="Preview" 
                @error="e => e.target.src = 'https://placehold.co/120x120?text=Loi+Link'"
              />
              <div v-else class="empty-image">
                <span class="icon">📸</span>
                <span>Chưa có ảnh</span>
              </div>
            </div>

            <div class="form-group flex-1">
              <label>Link ảnh biến thể (URL)</label>
              <textarea 
                v-model="formData.image" 
                rows="4" 
                placeholder="Dán link ảnh (https://...) vào đây để xem trước..."
              ></textarea>
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

// Trích xuất ID Sản phẩm cha từ đường dẫn (Ví dụ: /admin/products/1/variants -> productId = 1)
const productId = route.params.id; 

const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

// Lách luật @NotBlank ở Backend bằng cách gán "AUTO_NAME", Backend sẽ tự đè lên sau.
const initialForm = { 
  productId: Number(productId), 
  colorId: '', 
  sizeId: '', 
  name: 'AUTO_NAME', // Bẫy validation
  code: '', 
  price: '', 
  quantity: '', 
  image: '', 
  status: true 
};
const formData = reactive({ ...initialForm });

onMounted(async () => {
  // Load thuộc tính để đổ vào Dropdown Color/Size và Load danh sách Biến thể
  await Promise.all([
    productStore.fetchAllAttributes(),
    productStore.fetchProductVariants(productId)
  ]);
});

// Điều hướng trở lại trang danh sách sản phẩm cha
const goBack = () => {
  router.push('/admin/products');
};

const openAddModal = () => {
  isEditMode.value = false;
  currentId.value = null;
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editVariant = (variant) => {
  isEditMode.value = true;
  currentId.value = variant.id;
  // Bóc tách ID của color và size từ object nested để binding vào dropdown
  Object.assign(formData, { 
    ...variant,
    colorId: variant.color?.id || '',
    sizeId: variant.size?.id || '',
    name: variant.name || 'AUTO_NAME'
  });
  isModalOpen.value = true;
};

const closeModal = () => isModalOpen.value = false;

const handleSubmit = async () => {
  try {
    if (isEditMode.value) {
      await productStore.updateProductDetail(currentId.value, formData);
      Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã cập nhật SKU.', timer: 1500, showConfirmButton: false });
    } else {
      await productStore.createProductDetail(formData);
      Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã tạo SKU mới.', timer: 1500, showConfirmButton: false });
    }
    await productStore.fetchProductVariants(productId); // Reload bảng
    closeModal();
  } catch (error) {
    Swal.fire({ icon: 'error', title: 'Lỗi!', text: error.response?.data?.message || 'Thao tác thất bại.' });
  }
};

const confirmDelete = (id) => {
  Swal.fire({
    title: 'Xóa mềm Biến thể?',
    text: "Màu/Kích cỡ này sẽ không thể kinh doanh được nữa!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await productStore.deleteProductDetail(id);
        Swal.fire('Thành công!', 'Đã vô hiệu hóa SKU.', 'success');
        await productStore.fetchProductVariants(productId);
      } catch(err) {
        Swal.fire('Lỗi!', err.response?.data?.message || 'Không thể xóa lúc này.', 'error');
      }
    }
  })
};
</script>

<style scoped>
/* Tái sử dụng CSS layout + Flexbox hình ảnh */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
.header-actions { display: flex; gap: 10px; }
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-primary:hover { background: #1d4ed8; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary:hover { background: #cbd5e1; }
.product-thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }
.badge-code { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.font-bold { font-weight: 600; color: #1e293b; }
.text-price { color: #b91c1c; font-weight: bold; }
.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.status-dot.active { background-color: #22c55e; box-shadow: 0 0 8px #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }
.text-danger { color: red; }

/* Modal */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; width: 600px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); animation: slideDown 0.3s ease-out; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; }
.modal-header h3 { margin: 0; font-size: 18px; color: #1e293b; }
.close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8; transition: color 0.2s;}
.close-btn:hover { color: #ef4444; }
.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
.form-group input:disabled, .form-group select:disabled { background-color: #f1f5f9; cursor: not-allowed; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc; }

/* Image Preview Flexbox */
.image-input-container { display: flex; gap: 20px; align-items: flex-start; background: #f8fafc; padding: 15px; border-radius: 8px; border: 1px dashed #cbd5e1; margin-bottom: 15px; }
.image-preview-box { width: 120px; height: 120px; border-radius: 8px; border: 2px dashed #cbd5e1; overflow: hidden; display: flex; justify-content: center; align-items: center; background: white; flex-shrink: 0; }
.image-preview-box img { width: 100%; height: 100%; object-fit: cover; }
.empty-image { display: flex; flex-direction: column; align-items: center; color: #94a3b8; font-size: 12px; font-weight: 500; }
.empty-image .icon { font-size: 28px; margin-bottom: 5px; }
.flex-1 { flex: 1; }
textarea { resize: none; width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s; }
textarea:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
</style>