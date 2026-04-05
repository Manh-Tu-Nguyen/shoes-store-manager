<template>
  <div class="home-page">
    <div class="product-grid">
      <div v-for="p in paginatedProducts" :key="p.id" class="product-card" @click="goToDetail(p.id)">
        <div class="card-img-wrapper">
          <img :src="p.image || 'https://via.placeholder.com/300'" alt="Shoe" />
        </div>
        <div class="card-info">
          <span class="brand-tag">{{ p.brandName }}</span>
          <h3 class="product-name">{{ p.name }}</h3>
          <div class="price-range">Giá từ 2.000.000 ₫</div>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="currentPage === 1" @click="currentPage--" class="page-btn">PREV</button>
      <div class="page-numbers">
        <button 
          v-for="page in totalPages" 
          :key="page" 
          @click="currentPage = page"
          :class="{ active: currentPage === page }"
        >
          {{ page }}
        </button>
      </div>
      <button :disabled="currentPage === totalPages" @click="currentPage++" class="page-btn">NEXT</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useProductStore } from '@/store/productStore';
import { useRouter } from 'vue-router';

const productStore = useProductStore();
const router = useRouter();

// THIẾT LẬP KIẾN TRÚC PHÂN TRANG
const currentPage = ref(1);
const itemsPerPage = 16; // 16 sản phẩm mỗi trang theo yêu cầu

onMounted(async () => {
  await productStore.fetchProducts();
});

const totalPages = computed(() => {
  const activeProducts = productStore.products.filter(p => p.status === true);
  return Math.ceil(activeProducts.length / itemsPerPage);
});

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return productStore.products.filter(p => p.status === true).slice(start, end);
});

const goToDetail = (id) => {
  router.push(`/product/${id}`);
};
</script>

<style scoped>
/* GRID 4 CỘT CHUẨN */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* Ép hiển thị đúng 4 cột */
  gap: 20px;
  margin-bottom: 50px;
}

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .product-grid { grid-template-columns: 1fr; } }

.pagination { display: flex; justify-content: center; align-items: center; gap: 15px; }
.page-numbers button {
  width: 40px; height: 40px; border: 1px solid #e2e8f0; background: white;
  border-radius: 4px; cursor: pointer; transition: all 0.3s;
}
.page-numbers button.active { background: #1e293b; color: white; border-color: #1e293b; }
</style>