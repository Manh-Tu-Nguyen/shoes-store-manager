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
          <td><img :src="p.image || 'https://via.placeholder.com/50'" class="product-thumb" /></td>
          <td><span class="badge-code">{{ p.code }}</span></td>
          <td class="font-bold">{{ p.name }}</td>
          <td>{{ p.brand?.name }}</td>
          <td>{{ p.category?.name }}</td>
          <td>{{ p.origin?.name }}</td>
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
              @delete="confirmDelete(p.id)" 
            />
          </td>
        </tr>
      </template>
    </DataTable>

    <!-- MODAL FORM THÊM/SỬA SẢN PHẨM -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-row">
            <div class="form-group" v-if="isEditMode">
              <label>Mã Sản Phẩm</label>
              <input :value="formData.code" type="text" disabled />
            </div>
            <div class="form-group" :class="{ 'col-span-2': !isEditMode }">
              <label>Tên sản phẩm <span class="text-danger">*</span></label>
              <input v-model="formData.name" type="text" placeholder="VD: Nike Air Zoom 40" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Thương hiệu <span class="text-danger">*</span></label>
              <select v-model="formData.brandId" required>
                <option value="" disabled>-- Chọn thương hiệu --</option>
                <option v-for="b in productStore.brands" :key="b.id" :value="b.id">{{ b.name }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Danh mục <span class="text-danger">*</span></label>
              <select v-model="formData.categoryId" required>
                <option value="" disabled>-- Chọn danh mục --</option>
                <option v-for="c in productStore.categories" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Xuất xứ <span class="text-danger">*</span></label>
              <select v-model="formData.originId" required>
                <option value="" disabled>-- Chọn xuất xứ --</option>
                <option v-for="o in productStore.origins" :key="o.id" :value="o.id">{{ o.name }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Trạng thái</label>
              <select v-model="formData.status">
                <option :value="true">Đang kinh doanh</option>
                <option :value="false">Ngừng kinh doanh</option>
              </select>
            </div>
          </div>

          <!-- KHU VỰC NHẬP VÀ XEM TRƯỚC ẢNH -->
          <div class="image-input-container">
            <!-- Khung xem trước ảnh -->
            <div class="image-preview-box">
              <!-- Nếu có link thì hiện ảnh. Lỗi link thì tự đổi sang ảnh Placeholder -->
              <img 
                v-if="formData.image" 
                :src="formData.image" 
                alt="Preview" 
                @error="e => e.target.src = 'https://via.placeholder.com/120?text=Lỗi+Link'" 
              />
              <!-- Nếu chưa nhập link thì hiện cái khung trống -->
              <div v-else class="empty-image">
                <span class="icon">📸</span>
                <span>Chưa có ảnh</span>
              </div>
            </div>

            <!-- Ô nhập Link (Dùng textarea vì link ảnh mạng thường rất dài) -->
            <div class="form-group flex-1">
              <label>Link ảnh đại diện (URL)</label>
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

const initialForm = { brandId: '', categoryId: '', originId: '', name: '', code: '', image: '', status: true };
const formData = reactive({ ...initialForm });

onMounted(async () => {
  await Promise.all([
    productStore.fetchAllAttributes(),
    productStore.fetchProducts()
  ]);
});

// Nút Xem chi tiết -> Nhảy sang trang SKU
const goToVariants = (productId) => {
  router.push(`/admin/products/${productId}/variants`);
};

const openAddModal = () => {
  isEditMode.value = false;
  currentId.value = null;
  Object.assign(formData, initialForm);
  isModalOpen.value = true;
};

const editProduct = (product) => {
  isEditMode.value = true;
  currentId.value = product.id;
  Object.assign(formData, { 
    ...product,
    brandId: product.brand?.id || '',
    categoryId: product.category?.id || '',
    originId: product.origin?.id || ''
  });
  isModalOpen.value = true;
};

const closeModal = () => isModalOpen.value = false;

const handleSubmit = async () => {
  try {
    if (isEditMode.value) {
      await productStore.updateProduct(currentId.value, formData);
      Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã cập nhật sản phẩm.', timer: 1500, showConfirmButton: false });
    } else {
      await productStore.createProduct(formData);
      Swal.fire({ icon: 'success', title: 'Thành công!', text: 'Đã tạo sản phẩm mới.', timer: 1500, showConfirmButton: false });
    }
    await productStore.fetchProducts();
    closeModal();
  } catch (error) {
    Swal.fire({ icon: 'error', title: 'Lỗi!', text: error.response?.data?.message || 'Thao tác thất bại.' });
  }
};

const confirmDelete = (id) => {
  Swal.fire({
    title: 'Xóa mềm sản phẩm?',
    text: "Sản phẩm sẽ bị chuyển sang trạng thái Ngừng kinh doanh!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await productStore.deleteProduct(id);
        Swal.fire('Thành công!', 'Đã vô hiệu hóa sản phẩm.', 'success');
        await productStore.fetchProducts(); // Cập nhật lại bảng
      } catch(err) {
        Swal.fire('Lỗi!', err.response?.data?.message || 'Không thể xóa lúc này.', 'error');
      }
    }
  })
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