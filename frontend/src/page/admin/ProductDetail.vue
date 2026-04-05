<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Biến thể Sản phẩm (SKU)</h2>
        <p class="text-muted">Đang xem biến thể của Sản phẩm ID: {{ productId }}</p>
      </div>
      <div class="header-actions">
        <button @click="goBack" class="btn-secondary mr-2">⬅ Quay lại</button>
        <button @click="openAddModal" class="btn-add">
          <span class="icon">+</span> Thêm biến thể
        </button>
      </div>
    </div>

    <div class="table-container shadow-sm">
      <table class="data-table">
        <thead>
          <tr>
            <th>Hình ảnh</th>
            <th>Mã SKU</th>
            <th>Màu sắc</th>
            <th>Kích cỡ</th>
            <th>Giá bán (VNĐ)</th>
            <th>Tồn kho</th>
            <th>Trạng thái</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="productStore.productDetails.length === 0">
            <td colspan="8" class="text-center">Chưa có biến thể nào cho sản phẩm này.</td>
          </tr>
          <tr v-for="pd in productStore.productDetails" :key="pd.id">
            <td>
              <img :src="pd.image || 'https://via.placeholder.com/50'" class="product-thumb" />
            </td>
            <td><span class="badge-code">{{ pd.code }}</span></td>
            
            <td class="font-bold">{{ pd.colorName }}</td>
            <td class="font-bold">{{ pd.sizeName }}</td>
            
            <td class="text-price">{{ pd.price?.toLocaleString('vi-VN') }} ₫</td>
            <td>{{ pd.quantity }}</td>
            <td>
              <span :class="['status-dot', pd.status ? 'active' : 'inactive']"></span>
              {{ pd.status ? 'Đang bán' : 'Ngừng bán' }}
            </td>
            <td class="actions-cell">
              <button @click="editVariant(pd)" class="btn-icon btn-edit" title="Sửa thông tin">✏️</button>
              <button @click="confirmDelete(pd.id)" class="btn-icon btn-delete" title="Xóa biến thể">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label>Mã SKU <span class="text-danger">*</span></label>
              <input v-model="formData.code" type="text" placeholder="VD: SKU-RED-40" required :disabled="isEditMode" />
            </div>
            <div class="form-group">
              <label>Tên biến thể</label>
              <input v-model="formData.name" type="text" placeholder="Tên hiển thị phụ (Tùy chọn)" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Màu sắc <span class="text-danger">*</span></label>
              <select v-model="formData.idColor" required>
                <option value="" disabled>-- Chọn màu --</option>
                <option v-for="c in productStore.colors" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Kích cỡ <span class="text-danger">*</span></label>
              <select v-model="formData.idSize" required>
                <option value="" disabled>-- Chọn size --</option>
                <option v-for="s in productStore.sizes" :key="s.id" :value="s.id">{{ s.name }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Giá bán <span class="text-danger">*</span></label>
              <input v-model="formData.price" type="number" min="0" required />
            </div>
            <div class="form-group">
              <label>Số lượng <span class="text-danger">*</span></label>
              <input v-model="formData.quantity" type="number" min="0" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Trạng thái</label>
              <select v-model="formData.status">
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>
            <div class="form-group">
              <label>Link hình ảnh riêng (URL)</label>
              <input v-model="formData.image" type="text" placeholder="https://..." />
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy bỏ</button>
            <button type="submit" class="btn-primary">
              {{ isEditMode ? 'Lưu thay đổi' : 'Tạo biến thể' }}
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
import { useProductStore } from '@/store/productStore';

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();

// Lấy ID sản phẩm cha từ URL
const productId = route.params.id;

// State Modal
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = {
  idProduct: Number(productId), 
  idColor: '',
  idSize: '',
  code: '',
  name: '',
  price: 0,
  quantity: 0,
  image: '',
  status: true
};
const formData = reactive({ ...initialForm });

onMounted(async () => {
  // fetchAllAttributes vẫn giữ lại để lấy dữ liệu đổ vào thẻ <select> trong Pop-up
  await Promise.all([
    productStore.fetchAllAttributes(),
    productStore.fetchProductVariants(productId)
  ]);
});

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
  Object.assign(formData, { 
    idProduct: variant.idProduct,
    idColor: variant.idColor,
    idSize: variant.idSize,
    code: variant.code,
    name: variant.name,
    price: variant.price,
    quantity: variant.quantity,
    image: variant.image,
    status: variant.status
  });
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleSubmit = async () => {
  try {
    if (isEditMode.value) {
      await productStore.updateProductDetail(currentId.value, formData);
      alert("Cập nhật biến thể thành công!");
    } else {
      await productStore.createProductDetail(formData);
      alert("Thêm biến thể mới thành công!");
    }
    // Tự động load lại bảng sau khi thao tác
    await productStore.fetchProductVariants(productId);
    closeModal();
  } catch (error) {
    alert("Thao tác thất bại! Vui lòng kiểm tra lại dữ liệu.");
    console.error(error);
  }
};

const confirmDelete = async (id) => {
  if (confirm("Xóa biến thể này? Khách hàng sẽ không thể mua màu/size này nữa.")) {
    try {
       await productStore.deleteProductDetail(id);
       alert("Đã cập nhật trạng thái biến thể!");
       // Tự động load lại bảng sau khi xóa
       await productStore.fetchProductVariants(productId);
    } catch(err) {
       alert("Lỗi khi thao tác!");
       console.error(err);
    }
  }
};
</script>

<style scoped>
.mr-2 { margin-right: 10px; }
.header-actions { display: flex; align-items: center; }
.text-price { color: #b91c1c; font-weight: bold; }

.product-thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }
.badge-code { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.font-bold { font-weight: 600; color: #1e293b; }
.text-danger { color: red; }

.actions-cell { display: flex; justify-content: center; gap: 8px; }
.btn-icon {
  width: 32px; height: 32px; border-radius: 6px; border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s; background: #f1f5f9;
}

.btn-detail:hover { background: #e0f2fe; color: #0369a1; transform: translateY(-2px); }
.btn-edit:hover { background: #fef3c7; color: #b45309; transform: translateY(-2px); }
.btn-delete:hover { background: #fee2e2; color: #b91c1c; transform: translateY(-2px); }

.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.status-dot.active { background-color: #22c55e; box-shadow: 0 0 8px #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }

.table-container { background: white; border-radius: 12px; overflow: hidden; margin-top: 20px; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { background: #f8fafc; padding: 15px; text-align: left; font-size: 13px; color: #64748b; text-transform: uppercase; }
.data-table td { padding: 15px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }

/* Modal Styles */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(2px);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.modal-content {
  background: white; width: 550px; border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease-out;
}
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }

.modal-header { padding: 15px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; background-color: #f8fafc; }
.modal-header h3 { margin: 0; font-size: 18px; color: #1e293b; }
.close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #94a3b8; transition: color 0.2s;}
.close-btn:hover { color: #ef4444; }

.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 13px; color: #475569;}
.form-group input, .form-group select {
  width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-family: inherit; font-size: 14px; transition: border-color 0.2s;
}
.form-group input:focus, .form-group select:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }
.form-group input:disabled { background-color: #f1f5f9; cursor: not-allowed; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }

.modal-footer {
  padding: 15px 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; background: #f8fafc;
}
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-primary:hover { background: #1d4ed8; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.btn-secondary:hover { background: #cbd5e1; }
</style>