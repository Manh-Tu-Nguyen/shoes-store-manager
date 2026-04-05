<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Quản lý Sản phẩm</h2>
        <p class="text-muted">Danh sách dòng sản phẩm chính (Parent Products)</p>
      </div>
      <button @click="openAddModal" class="btn-add">
        <span class="icon">+</span> Thêm sản phẩm mới
      </button>
    </div>

    <div class="filter-bar">
      <input v-model="searchQuery" placeholder="Tìm theo tên hoặc mã SP..." class="search-input" />
      <select v-model="filterBrand" class="filter-select">
        <option value="">Tất cả Thương hiệu</option>
        <option v-for="b in productStore.brands" :key="b.id" :value="b.id">{{ b.name }}</option>
      </select>
    </div>

    <div class="table-container shadow-sm">
      <table class="data-table">
        <thead>
          <tr>
            <th>Hình ảnh</th>
            <th>Mã SP</th>
            <th>Tên sản phẩm</th>
            <th>Thương hiệu</th>
            <th>Danh mục</th>
            <th>Xuất xứ</th>
            <th>Trạng thái</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in filteredProducts" :key="p.id">
            <td>
              <img :src="p.image || 'https://via.placeholder.com/50'" class="product-thumb" />
            </td>
            <td><span class="badge-code">{{ p.code }}</span></td>
            <td class="font-bold">{{ p.name }}</td>
            <td>{{ p.brandName }}</td>
            <td>{{ p.categoryName }}</td>
            <td>{{ p.originName }}</td>
            <td>
              <span :class="['status-dot', p.status ? 'active' : 'inactive']"></span>
              {{ p.status ? 'Đang bán' : 'Ngừng bán' }}
            </td>
            <td class="actions-cell">
              <button @click="viewDetail(p.id)" class="btn-icon btn-detail" title="Chi tiết biến thể">👁️</button>
              <button @click="editProduct(p)" class="btn-icon btn-edit" title="Sửa thông tin">✏️</button>
              <button @click="confirmDelete(p.id)" class="btn-icon btn-delete" title="Xóa sản phẩm">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditMode ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm mới' }}</h3>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body">
          <div class="form-group">
            <label>Mã sản phẩm <span class="text-danger">*</span></label>
            <input v-model="formData.code" type="text" placeholder="Nhập mã SP (VD: P001)..." required :disabled="isEditMode" />
          </div>

          <div class="form-group">
            <label>Tên sản phẩm <span class="text-danger">*</span></label>
            <input v-model="formData.name" type="text" placeholder="Nhập tên sản phẩm..." required />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Thương hiệu <span class="text-danger">*</span></label>
              <select v-model="formData.idBrand" required>
                <option value="" disabled>-- Chọn thương hiệu --</option>
                <option v-for="b in productStore.brands" :key="b.id" :value="b.id">{{ b.name }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>Danh mục <span class="text-danger">*</span></label>
              <select v-model="formData.idCategory" required>
                <option value="" disabled>-- Chọn danh mục --</option>
                <option v-for="c in productStore.categories" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Xuất xứ <span class="text-danger">*</span></label>
              <select v-model="formData.idOrigin" required>
                <option value="" disabled>-- Chọn xuất xứ --</option>
                <option v-for="o in productStore.origins" :key="o.id" :value="o.id">{{ o.name }}</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Trạng thái</label>
              <select v-model="formData.status">
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>Link hình ảnh (URL)</label>
            <input v-model="formData.image" type="text" placeholder="https://example.com/image.jpg" />
          </div>

          <div class="modal-footer">
            <button type="button" @click="closeModal" class="btn-secondary">Hủy bỏ</button>
            <button type="submit" class="btn-primary">
              {{ isEditMode ? 'Lưu thay đổi' : 'Tạo sản phẩm' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { useProductStore } from '@/store/productStore';
import { useRouter } from 'vue-router';

const productStore = useProductStore();
const router = useRouter();

const searchQuery = ref('');
const filterBrand = ref('');
const isModalOpen = ref(false);
const isEditMode = ref(false);
const currentId = ref(null);

const initialForm = {
  code: '',
  name: '',
  idBrand: '',
  idCategory: '',
  idOrigin: '',
  image: '',
  status: true
};
const formData = reactive({ ...initialForm });

onMounted(async () => {
  await Promise.all([
    productStore.fetchAllAttributes(), 
    productStore.fetchProducts()
  ]);
});

const filteredProducts = computed(() => {
  return productStore.products.filter(p => {
    const matchSearch = p.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                        p.code.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchBrand = filterBrand.value ? p.idBrand === Number(filterBrand.value) : true;
    return matchSearch && matchBrand;
  });
});

const viewDetail = (productId) => {
  router.push(`/admin/products/${productId}/details`);
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
    code: product.code,
    name: product.name,
    idBrand: product.idBrand,
    idCategory: product.idCategory,
    idOrigin: product.idOrigin,
    image: product.image,
    status: product.status
  });
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleSubmit = async () => {
  try {
    if (isEditMode.value) {
      await productStore.updateProduct(currentId.value, formData);
      alert("Cập nhật sản phẩm thành công!");
    } else {
      await productStore.createProduct(formData);
      alert("Thêm sản phẩm mới thành công!");
    }
    await productStore.fetchProducts();
    closeModal();
  } catch (error) {
    alert("Thao tác thất bại! Vui lòng kiểm tra lại dữ liệu.");
    console.error(error);
  }
};

const confirmDelete = async (id) => {
  if (confirm("Xóa sản phẩm này sẽ ảnh hưởng đến các biến thể. Bạn chắc chắn chứ?")) {
    try {
       await productStore.deleteProduct(id);
       alert("Đã chuyển trạng thái sản phẩm thành công!");
       await productStore.fetchProducts();
    } catch(err) {
       alert("Không thể xóa sản phẩm. Hãy thử lại!");
       console.error("Lỗi xóa SP", err);
    }
  }
};
</script>

<style scoped>
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