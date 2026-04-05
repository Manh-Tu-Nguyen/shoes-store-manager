<template>
  <div class="product-detail-page">
    <button @click="router.push('/')" class="btn-back">⬅ Tiếp tục mua sắm</button>

    <div class="detail-container shadow-sm">
      <div class="image-section">
        <img :src="currentImage" alt="Product Image" class="main-image"/>
      </div>

      <div class="info-section">
        <span class="badge-brand">HÀNG CHÍNH HÃNG</span>
        <h1 class="title">Giày Thể Thao Cao Cấp #{{ productId }}</h1>
        
        <div class="price-box">
          <span class="price-text">{{ displayPrice }}</span>
          <span class="stock-text" v-if="selectedVariant">Kho: {{ selectedVariant.quantity }}</span>
        </div>

        <div class="divider"></div>

        <div class="variant-group">
          <h3>Màu sắc</h3>
          <div class="options-container">
            <button 
              v-for="c in uniqueColors" :key="c.id"
              :class="['option-btn', { active: selectedColor === c.id }]"
              @click="selectColor(c.id)"
            >
              {{ c.name }}
            </button>
          </div>
        </div>

        <div class="variant-group">
          <h3>Kích cỡ (Size)</h3>
          <div class="options-container">
            <button 
              v-for="s in uniqueSizes" :key="s.id"
              :class="['option-btn', { active: selectedSize === s.id }]"
              @click="selectSize(s.id)"
            >
              {{ s.name }}
            </button>
          </div>
        </div>

        <div class="variant-group" v-if="selectedVariant">
          <h3>Số lượng</h3>
          <div class="quantity-selector">
            <button @click="changeQty(-1)" :disabled="quantity <= 1" class="qty-btn">-</button>
            <input type="number" v-model.number="quantity" readonly class="qty-input" />
            <button @click="changeQty(1)" :disabled="quantity >= selectedVariant.quantity" class="qty-btn">+</button>
            <span class="stock-hint">(Còn lại: {{ selectedVariant.quantity }})</span>
          </div>
        </div>

        <div v-if="!selectedVariant && selectedColor && selectedSize" class="alert-error">
          Phiên bản này hiện không có sẵn.
        </div>

        <div class="buy-action">
          <button class="btn-buy-now" :disabled="!selectedVariant" @click="handleBuyNow">
            MUA NGAY
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'; // Thêm watch
import { useRoute, useRouter } from 'vue-router';
import { useProductStore } from '@/store/productStore';

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();

const productId = route.params.id;
const currentImage = ref('https://via.placeholder.com/500');

const selectedColor = ref(null);
const selectedSize = ref(null);
const quantity = ref(1); // Biến số lượng

onMounted(async () => {
  await productStore.fetchProductVariants(productId);
  if(productStore.productDetails.length > 0 && productStore.productDetails[0].image) {
    currentImage.value = productStore.productDetails[0].image;
  }
});

// Reset số lượng khi khách hàng đổi biến thể
watch([selectedColor, selectedSize], () => {
  quantity.value = 1;
});

const uniqueColors = computed(() => {
  const map = new Map();
  productStore.productDetails.forEach(pd => {
    if (!map.has(pd.idColor)) map.set(pd.idColor, { id: pd.idColor, name: pd.colorName });
  });
  return Array.from(map.values());
});

const uniqueSizes = computed(() => {
  const map = new Map();
  productStore.productDetails.forEach(pd => {
    if (!map.has(pd.idSize)) map.set(pd.idSize, { id: pd.idSize, name: pd.sizeName });
  });
  return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name));
});

const selectedVariant = computed(() => {
  if (!selectedColor.value || !selectedSize.value) return null;
  return productStore.productDetails.find(
    pd => pd.idColor === selectedColor.value && pd.idSize === selectedSize.value
  );
});

const displayPrice = computed(() => {
  if (selectedVariant.value) return `${selectedVariant.value.price.toLocaleString('vi-VN')} ₫`;
  return "Vui lòng chọn Màu và Size";
});

const selectColor = (id) => { selectedColor.value = id; };
const selectSize = (id) => { selectedSize.value = id; };

const changeQty = (val) => {
  const newQty = quantity.value + val;
  if (newQty >= 1 && newQty <= selectedVariant.value.quantity) {
    quantity.value = newQty;
  }
};

const handleBuyNow = () => {
  if (selectedVariant.value) {
    router.push({ 
      path: '/checkout', 
      query: { 
        skuId: selectedVariant.value.id,
        qty: quantity.value // Gửi số lượng sang trang Checkout
      } 
    });
  }
};
</script>

<style scoped>
/* Giữ nguyên Style cũ và thêm style cho Quantity */
.quantity-selector { display: flex; align-items: center; gap: 10px; margin-top: 5px; }
.qty-btn { width: 35px; height: 35px; border: 1px solid #cbd5e1; background: white; border-radius: 4px; cursor: pointer; font-weight: bold; }
.qty-btn:disabled { opacity: 0.5; }
.qty-input { width: 50px; height: 35px; text-align: center; border: 1px solid #cbd5e1; border-radius: 4px; font-weight: bold; }
.stock-hint { font-size: 13px; color: #64748b; font-style: italic; }

/* CSS cho Option Btn Active từ câu hỏi của bạn */
.option-btn.active { 
  border: 2px solid #ea580c; 
  color: #ea580c; 
  background: #fff7ed; 
}
/* ... Các style khác giữ nguyên ... */
</style>