<template>
  <div class="product-detail-page container mx-auto p-4">
    <button @click="goBack" class="back-btn mb-6">&larr; Tiếp tục mua sắm</button>

    <div v-if="productStore.loading" class="text-center py-10">Đang tải chi tiết sản phẩm...</div>

    <div v-else class="detail-grid">
      <div class="image-gallery">
        <img :src="currentImage" alt="Ảnh sản phẩm" class="main-image" />
      </div>

      <div class="product-info">
        <h1 class="product-title">{{ productName }}</h1>
        
        <div class="price-section">
          <span class="price" v-if="selectedVariant">
            {{ selectedVariant.price.toLocaleString('vi-VN') }} ₫
          </span>
          <span class="price" v-else>Vui lòng chọn phân loại</span>
        </div>

        <div class="divider"></div>

        <div class="variants-section">
          <h3 class="section-label">Chọn phân loại hàng:</h3>
          <div class="variant-buttons">
            <button 
              v-for="variant in productStore.productDetails" 
              :key="variant.id"
              :class="['variant-btn', { 
                'active': selectedVariant?.id === variant.id,
                'out-of-stock': variant.quantity === 0 
              }]"
              @click="selectVariant(variant)"
              :disabled="variant.quantity === 0"
            >
              {{ variant.color?.name }} - Size {{ variant.size?.name }}
            </button>
          </div>
        </div>

        <div class="quantity-section" v-if="selectedVariant && availableQty > 0">
          <h3 class="section-label">Số lượng:</h3>
          <div class="quantity-wrapper">
            <div class="quantity-control">
              <button @click="decreaseQty" :disabled="selectedQty <= 1">-</button>
              <input type="number" v-model.number="selectedQty" @blur="validateQty" />
              <button @click="increaseQty" :disabled="selectedQty >= Math.min(20, availableQty)">+</button>
            </div>
            <span class="stock-text" v-if="availableQty <= 20">Còn {{ availableQty }} sản phẩm</span>
          </div>
        </div>

        <div class="stock-out-alert" v-if="selectedVariant && availableQty <= 0">
          <span class="text-danger font-bold">Sản phẩm này tạm thời hết hàng.</span>
        </div>

        <div class="action-buttons">
          <button class="btn-add-cart" :disabled="!selectedVariant || availableQty <= 0" @click="addToCart">
            THÊM VÀO GIỎ HÀNG
          </button>
          <button class="btn-buy-now" :disabled="!selectedVariant || availableQty <= 0" @click="buyNow">
            MUA NGAY
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';
// --- IMPORT CÁC THÀNH PHẦN QUAN TRỌNG ---
import { useProductStore } from '@/store/productStore';
import { useOrderStore } from '@/store/orderStore';
import productDetailApi from '@/api/productDetailApi'; 
import { useCartStore } from '@/store/cartStore';
import { useAuthStore } from '@/store/authStore';// SỬA LỖI: Đã import API

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();
const orderStore = useOrderStore();
const cartStore = useCartStore();
const authStore = useAuthStore();
const productId = route.params.id;
const selectedVariant = ref(null);
const selectedQty = ref(1);
const productName = ref("Giày Thể Thao Cao Cấp"); // Bạn có thể lấy từ store nếu có

const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 1500 });

onMounted(async () => {
  try {
    productStore.loading = true;
    const res = await productDetailApi.getPublicDetailsByProductId(productId);
    productStore.productDetails = res?.data?.data || res?.data || res || [];
  } catch (err) {
    console.error(err);
    Toast.fire({ icon: 'error', title: 'Không tải được chi tiết' });
  } finally {
    productStore.loading = false;
  }
});

const availableQty = computed(() => {
  if (!selectedVariant.value) return 0;
  return (selectedVariant.value.quantity || 0) - (selectedVariant.value.pendingQuantity || 0);
});

const selectVariant = (variant) => {
  selectedVariant.value = variant;
  selectedQty.value = 1;
};

const currentImage = computed(() => selectedVariant.value?.image || 'https://placehold.co/600x600');

const increaseQty = () => {
  const maxAllowed = Math.min(20, availableQty.value);
  if (selectedQty.value < maxAllowed) selectedQty.value++;
};

const decreaseQty = () => {
  if (selectedQty.value > 1) selectedQty.value--;
};

const validateQty = () => {
  let val = selectedQty.value;
  const maxAllowed = Math.min(20, availableQty.value);
  if (isNaN(val) || val < 1) val = 1;
  if (val > maxAllowed) val = maxAllowed;
  selectedQty.value = val;
};

const goBack = () => router.push('/');

const addToCart = async () => {
  if (!selectedVariant.value || availableQty.value <= 0) return;

  // Gọi hàm addToCart tập trung từ Store (đã được kiến trúc rẽ nhánh Đăng nhập/Ẩn danh)
  await cartStore.addToCart(selectedVariant.value, selectedQty.value, authStore.isLoggedIn);

  Toast.fire({ icon: 'success', title: `Đã thêm ${selectedQty.value} sản phẩm vào giỏ!` });
};

const buyNow = () => {
  if (!selectedVariant.value || availableQty.value <= 0) return;
  const checkoutItem = {
    productDetailId: selectedVariant.value.id,
    name: `${productName.value} - ${selectedVariant.value.color?.name} - Size ${selectedVariant.value.size?.name}`,
    image: currentImage.value,
    price: selectedVariant.value.price,
    quantity: selectedQty.value
  };
  orderStore.setCheckoutItems([checkoutItem]);
  router.push('/checkout');
};
</script>

<style scoped>
/* CSS ĐÃ ĐƯỢC GỘP CHUẨN */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-top: 20px;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.image-gallery { display: flex; justify-content: center; align-items: flex-start; }
.main-image {
  width: 100%; max-width: 500px; border-radius: 8px;
  object-fit: cover; aspect-ratio: 1/1; border: 1px solid #f1f5f9;
}

.product-info { display: flex; flex-direction: column; }
.product-title { font-size: 28px; font-weight: 700; color: #1e293b; margin-bottom: 15px; }
.price-section { font-size: 32px; font-weight: bold; color: #ef4444; margin-bottom: 20px; }
.divider { height: 1px; background: #e2e8f0; margin: 20px 0; }

.section-label { font-size: 16px; font-weight: 600; margin-bottom: 10px; color: #475569; }
.variant-buttons { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 20px; }
.variant-btn {
  padding: 10px 20px; border: 2px solid #cbd5e1; background: white;
  border-radius: 6px; cursor: pointer; font-weight: 500; transition: all 0.2s;
}
.variant-btn:hover:not(.out-of-stock) { border-color: #3b82f6; color: #3b82f6; }
.variant-btn.active { border-color: #1e293b; background: #1e293b; color: white; }
.variant-btn.out-of-stock {
  background: #f1f5f9; color: #94a3b8; border-color: #e2e8f0;
  cursor: not-allowed; text-decoration: line-through;
}

/* BỘ ĐẾM SỐ LƯỢNG */
.quantity-wrapper { display: flex; align-items: center; gap: 15px; margin-bottom: 25px; }
.quantity-control { display: flex; border: 1px solid #cbd5e1; border-radius: 6px; overflow: hidden; width: 120px; }
.quantity-control button {
  background: #f8fafc; border: none; width: 35px; height: 35px;
  font-size: 18px; cursor: pointer; color: #475569; transition: 0.2s;
}
.quantity-control button:hover:not(:disabled) { background: #e2e8f0; }
.quantity-control button:disabled { opacity: 0.4; cursor: not-allowed; }
.quantity-control input {
  width: 50px; border: none; text-align: center; border-left: 1px solid #cbd5e1;
  border-right: 1px solid #cbd5e1; font-weight: 600; color: #1e293b; outline: none;
  -moz-appearance: textfield;
}
.quantity-control input::-webkit-outer-spin-button,
.quantity-control input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }

.stock-text { font-size: 14px; color: #64748b; }

/* THÔNG BÁO ZALO */
.zalo-wholesale-alert {
  display: flex; align-items: center; gap: 10px; background: #e0f2fe; border: 1px solid #bae6fd;
  padding: 12px 15px; border-radius: 8px; margin-bottom: 25px;
}
.zalo-icon { width: 30px; height: 30px; object-fit: contain; }
.zalo-wholesale-alert span { font-size: 14px; color: #0369a1; line-height: 1.4; }
.zalo-wholesale-alert a { color: #0284c7; font-weight: bold; text-decoration: underline; }

/* NÚT HÀNH ĐỘNG */
.action-buttons { display: flex; gap: 15px; }
.btn-add-cart {
  flex: 1; padding: 16px; background: #eff6ff; color: #2563eb;
  border: 2px solid #3b82f6; border-radius: 8px; font-weight: bold;
  cursor: pointer; transition: 0.2s;
}
.btn-add-cart:hover:not(:disabled) { background: #dbeafe; }
.btn-buy-now {
  flex: 1; padding: 16px; background: #ef4444; color: white;
  border: none; border-radius: 8px; font-weight: bold;
  cursor: pointer; transition: 0.2s;
}
.btn-buy-now:hover:not(:disabled) { background: #dc2626; }
button:disabled { opacity: 0.5; cursor: not-allowed; }

@media (max-width: 768px) {
  .detail-grid { grid-template-columns: 1fr; gap: 20px; }
}
</style>