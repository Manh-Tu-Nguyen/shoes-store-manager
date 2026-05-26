<template>
  <div class="home-page container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-6 text-center">Sản phẩm nổi bật</h2>
    
    <div v-if="productStore.loading" class="text-center py-10">Đang tải dữ liệu...</div>
    
    <div v-else>
      <div class="product-grid">
        <div 
          v-for="p in paginatedProducts" 
          :key="p.id" 
          class="product-card" 
          @click="goToDetail(p.id)"
        >
          <img :src="p.image || 'https://placehold.co/300x300'" alt="Ảnh sản phẩm" class="w-full object-cover aspect-square rounded-t-md" />
          <div class="p-3">
            <h3 class="font-bold text-gray-800 truncate">{{ p.name }}</h3>
            <p class="text-red-600 font-semibold mt-1">Liên hệ</p> 
          </div>
        </div>
      </div>

      <div class="pagination-container" v-if="totalPages > 1">
        <button 
          :disabled="currentPage === 1" 
          @click="changePage(currentPage - 1)"
          class="page-btn"
        >
          &laquo; Trước
        </button>
        
        <button 
          v-for="page in totalPages" 
          :key="page" 
          :class="['page-btn', { 'active': currentPage === page }]"
          @click="changePage(page)"
        >
          {{ page }}
        </button>

        <button 
          :disabled="currentPage === totalPages" 
          @click="changePage(currentPage + 1)"
          class="page-btn"
        >
          Sau &raquo;
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useProductStore } from '@/store/productStore';

const router = useRouter();
const productStore = useProductStore();

// --- LOGIC PHÂN TRANG (CLIENT-SIDE) ---
const currentPage = ref(1);
const itemsPerPage = 12; // 12 sản phẩm / trang

// Tổng số trang = Làm tròn lên (Tổng số SP đang hoạt động / 12)
const totalPages = computed(() => {
  return Math.ceil((productStore.activeProducts?.length || 0) / itemsPerPage);
});

// Cắt mảng để lấy đúng 12 sản phẩm của trang hiện tại
const paginatedProducts = computed(() => {
  const products = productStore.activeProducts || [];
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return products.slice(start, end);
});

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    window.scrollTo({ top: 0, behavior: 'smooth' }); // Tự động cuộn lên đầu khi chuyển trang
  }
};

onMounted(() => {
  if (productStore.products.length === 0) {
    productStore.fetchProducts();
  }
});

const goToDetail = (productId) => {
  router.push(`/product/${productId}`);
};

</script>

<style scoped>
/* CSS GRID: Cốt lõi để tạo 4 cột */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* Chia đúng 4 cột đều nhau */
  gap: 24px; /* Khoảng cách giữa các cột/hàng */
  margin-bottom: 40px;
}

.product-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  background: white;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

/* CSS CHO NÚT PHÂN TRANG */
.pagination-container {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}
.page-btn {
  padding: 8px 16px;
  border: 1px solid #cbd5e1;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: 0.2s;
}
.page-btn:hover:not(:disabled) {
  background: #f1f5f9;
}
.page-btn.active {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive: Thu nhỏ cột khi màn hình nhỏ */
@media (max-width: 1024px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .product-grid { grid-template-columns: repeat(1, 1fr); }
}
</style>