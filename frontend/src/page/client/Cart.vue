<template>
  <div class="cart-page-container">
    <div class="breadcrumbs">
      <span>Trang chủ</span> <span class="divider">&gt;</span> <span class="active">Giỏ hàng</span>
    </div>

    <h1 class="page-title">Giỏ hàng của bạn</h1>

    <div v-if="loading" class="text-center py-10 text-gray-500">
      Đang cập nhật dữ liệu giỏ hàng...
    </div>

    <div v-else class="cart-layout-grid">
      <div class="left-panel">
        
        <div v-if="cartItems.length === 0" class="empty-cart-state">
          <div class="empty-icon">🛒</div>
          <p>Chưa có sản phẩm nào trong giỏ hàng của bạn.</p>
        </div>

        <div v-else class="cart-items-wrapper">
          <div class="cart-header-actions">
            <label class="checkbox-label">
              <input type="checkbox" v-model="isSelectAll" />
              <span class="checkbox-text">Chọn tất cả ({{ cartItems.length }} sản phẩm)</span>
            </label>
            <button class="btn-delete-all" @click="clearAllCart">Xóa tất cả</button>
          </div>

          <div class="cart-item-card" v-for="item in cartItems" :key="item.id">
            <div class="item-checkbox-section">
              <input type="checkbox" :value="item.id" v-model="selectedItemIds" />
            </div>
            
            <div class="item-image-section">
              <img :src="item.productDetail?.image || 'https://placehold.co/100x100'" alt="Product" />
            </div>

            <div class="item-info-section">
              <h3 class="product-name">{{ item.productDetail?.name || 'Sản phẩm không tên' }}</h3>
              <p class="product-attributes">
                {{ item.productDetail?.size?.name || 'Free Size' }} — {{ item.productDetail?.color?.name || 'Mặc định' }}
              </p>
            </div>

            <div class="item-price-section">
              {{ item.productDetail?.price?.toLocaleString('vi-VN') }} ₫
            </div>

            <div class="item-quantity-section">
              <div class="qty-counter">
                <button @click="updateQty(item, -1)" :disabled="item.quantity <= 1">-</button>
                <input type="number" v-model.number="item.quantity" @blur="validateInputQty(item)" />
                <button @click="updateQty(item, 1)">+</button>
              </div>
            </div>

            <div class="item-total-section">
              {{ (item.productDetail?.price * item.quantity).toLocaleString('vi-VN') }} ₫
            </div>

            <div class="item-remove-action">
              <button class="btn-remove-item" @click="removeItem(item.id)">🗑</button>
            </div>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="order-summary-card">
          <h2>Thông tin đơn hàng</h2>
          
          <div class="summary-row">
            <span class="label">Tổng tiền gốc ({{ selectedCount }} sản phẩm)</span>
            <span class="value">{{ totalBasePrice.toLocaleString('vi-VN') }} ₫</span>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-row total-row">
            <span class="label">Tạm tính</span>
            <span class="value-highlight">{{ totalBasePrice.toLocaleString('vi-VN') }} ₫</span>
          </div>

          <p class="shipping-note">Phí vận chuyển sẽ được tính ở trang thanh toán</p>

          <button class="btn-checkout" :disabled="selectedItemIds.length === 0" @click="handleCheckout">
            THANH TOÁN NGAY
          </button>

          <button class="btn-continue-shopping" @click="continueShopping">
            Tiếp tục mua sắm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useCartStore } from '@/store/cartStore';
import { useAuthStore } from '@/store/authStore';
import { useRouter } from 'vue-router';
import { useOrderStore } from '@/store/orderStore';

// 1. Khởi tạo kết nối các Store trung tâm
const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();
const orderStore = useOrderStore();
// 2. Kỹ thuật chuyển đổi cấu trúc phản xạ (Bảo toàn reactivity từ Pinia Store xuống Template)
const { cartItems, selectedItemIds, loading } = storeToRefs(cartStore);

// 3. Phân luồng nạp dữ liệu tại mốc mounted hệt như tư duy phòng vệ
onMounted(() => {
  if (authStore.isLoggedIn) {
    cartStore.fetchCart(); // XÓA BỎ tham số authStore.user.id thừa thãi đi
  } else {
    cartStore.loadCartFromLocalStorage();
  }
});

// 4. Đồng bộ hóa bộ chọn tất cả (Ủy quyền trực tiếp cho Action của Store)
const isSelectAll = computed({
  get: () => cartStore.isSelectAll,
  set: (value) => cartStore.toggleSelectAll(value)
});

// 5. Kết nối trực tiếp dữ liệu tính toán phái sinh từ Getters của Store
const selectedCount = computed(() => cartStore.selectedCount);
const totalBasePrice = computed(() => cartStore.totalBasePrice);

// 6. Định vị các hàm hành động tương tác (Đã dọn sạch trùng lặp)
const updateQty = (item, delta) => {
  cartStore.updateQuantity(item.id, delta, authStore.isLoggedIn);
};

const validateInputQty = (item) => {
  if (isNaN(item.quantity) || item.quantity < 1) {
    item.quantity = 1;
  }
  if (!authStore.isLoggedIn) {
    cartStore.saveCartToLocalStorage();
  }
};

const removeItem = (itemId) => {
  cartStore.removeItem(itemId, authStore.isLoggedIn);
};

const clearAllCart = () => {
  if (confirm("Bạn có chắc chắn muốn xóa toàn bộ sản phẩm khỏi giỏ hàng?")) {
    cartStore.clearCart(authStore.isLoggedIn);
  }
};

const handleCheckout = () => {
  // 1. Lọc ra danh sách các mặt hàng ĐANG ĐƯỢC CHECK CHỌN
  const selectedItems = cartStore.cartItems.filter(item => 
    cartStore.selectedItemIds.includes(item.id)
  );

  // 2. Chuyển đổi (Map) cấu trúc cho khớp với yêu cầu của orderStore
  const checkoutItems = selectedItems.map(item => ({
    productDetailId: item.productDetail.id,
    price: item.productDetail.price,
    quantity: item.quantity,
    // Truyền thêm tên và ảnh để trang Checkout còn hiển thị
    name: item.productDetail.name,
    image: item.productDetail.image,
    attributes: `${item.productDetail.size?.name} - ${item.productDetail.color?.name}`
  }));

  // 3. Đẩy dữ liệu vào OrderStore
  orderStore.setCheckoutItems(checkoutItems);

  // 4. Chuyển hướng sang màn hình thanh toán
  router.push('/checkout');
};
const continueShopping = () => {
  window.location.href = "/";
};

</script>

<style scoped>
.cart-page-container { max-width: 1200px; margin: 0 auto; padding: 20px; font-family: sans-serif; color: #111; }
.breadcrumbs { font-size: 14px; color: #888; margin-bottom: 20px; }
.breadcrumbs .divider { margin: 0 8px; }
.breadcrumbs .active { color: #111; font-weight: 500; }
.page-title { font-size: 32px; font-weight: bold; margin-bottom: 30px; }

.cart-layout-grid { display: grid; grid-template-columns: 1fr 380px; gap: 30px; align-items: start; }

.left-panel { min-height: 200px; }
.empty-cart-state { background: #fff; border-radius: 8px; padding: 60px; text-align: center; color: #666; border: 1px solid #f0f0f0; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

.cart-header-actions { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 16px 20px; border-radius: 8px; margin-bottom: 16px; border: 1px solid #f0f0f0; }
.checkbox-label { display: flex; align-items: center; cursor: pointer; gap: 10px; }
.checkbox-text { font-size: 15px; font-weight: 500; }
.btn-delete-all { background: none; border: none; color: #ef4444; font-weight: 600; cursor: pointer; font-size: 14px; }

.cart-item-card { display: flex; align-items: center; background: #fff; padding: 20px; border-radius: 8px; margin-bottom: 12px; border: 1px solid #f0f0f0; gap: 20px; }
.item-image-section img { width: 80px; height: 80px; object-fit: cover; border-radius: 6px; background: #f9f9f9; }
.item-info-section { flex: 1; }
.product-name { font-size: 16px; font-weight: 600; margin: 0 0 6px 0; color: #111; }
.product-attributes { font-size: 13px; color: #777; margin: 0; }
.item-price-section { font-weight: 600; font-size: 16px; width: 100px; text-align: right; }

.qty-counter { display: flex; border: 1px solid #ddd; border-radius: 4px; overflow: hidden; background: #fff; }
.qty-counter button { background: #fff; border: none; width: 32px; height: 32px; font-size: 18px; cursor: pointer; }
.qty-counter button:hover:not(:disabled) { background: #f5f5f5; }
.qty-counter button:disabled { opacity: 0.3; cursor: not-allowed; }
.qty-counter input { width: 40px; text-align: center; border: none; border-left: 1px solid #ddd; border-right: 1px solid #ddd; font-weight: 600; font-size: 14px; outline: none; }

.item-total-section { font-weight: bold; font-size: 16px; color: #ef4444; width: 110px; text-align: right; }
.btn-remove-item { background: none; border: none; color: #aaa; font-size: 18px; cursor: pointer; }
.btn-remove-item:hover { color: #ef4444; }

.order-summary-card { background: #fff; border-radius: 12px; padding: 24px; border: 1px solid #f0f0f0; box-shadow: 0 2px 8px rgba(0,0,0,0.02); }
.order-summary-card h2 { font-size: 18px; font-weight: bold; margin: 0 0 24px 0; color: #111; }
.summary-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; font-size: 14px; color: #555; }
.summary-row .value { font-weight: 600; color: #111; }
.summary-divider { height: 1px; background: #eee; margin: 16px 0; }
.total-row { margin-bottom: 8px; }
.total-row .label { font-size: 16px; font-weight: bold; color: #111; }
.total-row .value-highlight { font-size: 22px; font-weight: bold; color: #ef4444; }
.shipping-note { font-size: 12px; color: #888; font-style: italic; margin: 0 0 24px 0; text-align: right; }

.btn-checkout { width: 100%; padding: 14px; background: #e2e8f0; color: #475569; border: none; border-radius: 6px; font-weight: bold; font-size: 14px; cursor: pointer; transition: all 0.2s; margin-bottom: 12px; }
.btn-checkout:not(:disabled) { background: #111; color: #fff; }
.btn-checkout:hover:not(:disabled) { background: #222; }
.btn-checkout:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-continue-shopping { width: 100%; padding: 14px; background: #fff; color: #111; border: 1px solid #111; border-radius: 6px; font-weight: bold; font-size: 14px; cursor: pointer; }
.btn-continue-shopping:hover { background: #f9f9f9; }

@media (max-width: 992px) {
  .cart-layout-grid { grid-template-columns: 1fr; }
}
</style>

<style scoped>
/* --- THIẾT KẾ GRID LAYOUT TỔNG THỂ --- */
.cart-page-container { max-width: 1200px; margin: 0 auto; padding: 20px; font-family: sans-serif; color: #111; }
.breadcrumbs { font-size: 14px; color: #888; margin-bottom: 20px; }
.breadcrumbs .divider { margin: 0 8px; }
.breadcrumbs .active { color: #111; font-weight: 500; }
.page-title { font-size: 32px; font-weight: bold; margin-bottom: 30px; text-transform: none; }

.cart-layout-grid { display: grid; grid-template-columns: 1fr 380px; gap: 30px; align-items: start; }

/* --- PANEL TRÁI: SẢN PHẨM CHI TIẾT --- */
.empty-cart-state { background: #fff; border-radius: 8px; padding: 60px; text-align: center; color: #666; border: 1px solid #f0f0f0; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }

.cart-header-actions { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 16px 20px; border-radius: 8px; margin-bottom: 16px; border: 1px solid #f0f0f0; }
.checkbox-label { display: flex; align-items: center; cursor: pointer; gap: 10px; }
.checkbox-text { font-size: 15px; font-weight: 500; }
.btn-delete-all { background: none; border: none; color: #ef4444; font-weight: 600; cursor: pointer; font-size: 14px; }

.cart-item-card { display: flex; align-items: center; background: #fff; padding: 20px; border-radius: 8px; margin-bottom: 12px; border: 1px solid #f0f0f0; gap: 20px; }
.item-image-section img { width: 80px; height: 80px; object-fit: cover; border-radius: 6px; background: #f9f9f9; }
.item-info-section { flex: 1; }
.product-name { font-size: 16px; font-weight: 600; margin: 0 0 6px 0; color: #111; }
.product-attributes { font-size: 13px; color: #777; margin: 0; }
.item-price-section { font-weight: 600; font-size: 16px; width: 100px; text-align: right; }

/* Bộ đếm số lượng tinh chỉnh */
.qty-counter { display: flex; border: 1px solid #ddd; border-radius: 4px; overflow: hidden; background: #fff; }
.qty-counter button { background: #fff; border: none; width: 32px; height: 32px; font-size: 18px; cursor: pointer; transition: background 0.2s; }
.qty-counter button:hover:not(:disabled) { background: #f5f5f5; }
.qty-counter button:disabled { opacity: 0.3; cursor: not-allowed; }
.qty-counter input { width: 40px; text-align: center; border: none; border-left: 1px solid #ddd; border-right: 1px solid #ddd; font-weight: 600; font-size: 14px; outline: none; }
.qty-counter input::-webkit-outer-spin-button, .qty-counter input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }

.item-total-section { font-weight: bold; font-size: 16px; color: #ef4444; width: 110px; text-align: right; }
.btn-remove-item { background: none; border: none; color: #aaa; font-size: 18px; cursor: pointer; transition: color 0.2s; }
.btn-remove-item:hover { color: #ef4444; }

/* --- PANEL PHẢI: BẢNG TẠM TÍNH HÓA ĐƠN --- */
.order-summary-card { background: #fff; border-radius: 12px; padding: 24px; border: 1px solid #f0f0f0; box-shadow: 0 2px 8px rgba(0,0,0,0.02); }
.order-summary-card h2 { font-size: 18px; font-weight: bold; margin: 0 0 24px 0; color: #111; }
.summary-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; font-size: 14px; color: #555; }
.summary-row .value { font-weight: 600; color: #111; }
.summary-divider { height: 1px; background: #eee; margin: 16px 0; }
.total-row { margin-bottom: 8px; }
.total-row .label { font-size: 16px; font-weight: bold; color: #111; }
.total-row .value-highlight { font-size: 22px; font-weight: bold; color: #ef4444; }
.shipping-note { font-size: 12px; color: #888; font-style: italic; margin: 0 0 24px 0; text-align: right; }

.btn-checkout { w-full: 100%; width: 100%; padding: 14px; background: #e2e8f0; color: #475569; border: none; border-radius: 6px; font-weight: bold; font-size: 14px; cursor: pointer; transition: all 0.2s; margin-bottom: 12px; letter-spacing: 0.5px; }
.btn-checkout:not(:disabled) { background: #111; color: #fff; }
.btn-checkout:hover:not(:disabled) { background: #222; }
.btn-checkout:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-continue-shopping { width: 100%; padding: 14px; background: #fff; color: #111; border: 1px solid #111; border-radius: 6px; font-weight: bold; font-size: 14px; cursor: pointer; transition: all 0.2s; }
.btn-continue-shopping:hover { background: #f9f9f9; }

@media (max-width: 992px) {
  .cart-layout-grid { grid-template-columns: 1fr; }
}
</style>