<template>
  <div class="checkout-page">
    <h1 class="checkout-title">Thanh toán an toàn</h1>
    
    <div class="checkout-grid">
      <div class="billing-section">
        <div class="card shadow-sm">
          <h2>1. Thông tin giao hàng</h2>
          <form class="checkout-form">
            <div class="form-group">
              <label>Họ và tên</label>
              <input v-model="customerInfo.name" type="text" placeholder="Nhập họ tên đầy đủ..." />
            </div>
            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="customerInfo.phone" type="tel" placeholder="Nhập số điện thoại..." />
            </div>
            <div class="form-group">
              <label>Địa chỉ nhận hàng</label>
              <textarea v-model="customerInfo.address" rows="3" placeholder="Địa chỉ chi tiết..."></textarea>
            </div>
          </form>
        </div>

        <div class="card shadow-sm">
          <h2>2. Hình thức thanh toán</h2>
          <div class="payment-methods">
            <label class="radio-label">
              <input type="radio" v-model="paymentMethod" value="COD" />
              <span>Thanh toán khi nhận hàng (COD)</span>
            </label>
            <label class="radio-label">
              <input type="radio" v-model="paymentMethod" value="VNPAY" />
              <span>Thanh toán qua VNPay</span>
            </label>
          </div>
        </div>
      </div>

      <div class="summary-section">
        <div class="card shadow-sm sticky-card">
          <h2>Tóm tắt đơn hàng</h2>
          
          <div class="order-item" v-if="skuInfo">
            <img :src="skuInfo.image || 'https://via.placeholder.com/60'" alt="Shoe" class="item-img" />
            <div class="item-info">
              <h4>{{ skuInfo.name || 'Giày thể thao' }}</h4>
              <p>Màu: {{ skuInfo.colorName }} | Size: {{ skuInfo.sizeName }}</p>
              <p class="item-price">
                {{ skuInfo.price?.toLocaleString('vi-VN') }} ₫ x {{ buyQty }}
              </p>
            </div>
          </div>

          <div class="summary-calc">
            <div class="calc-row">
              <span>Tạm tính</span>
              <span>{{ subTotal.toLocaleString('vi-VN') }} ₫</span>
            </div>
            <div class="calc-row">
              <span>Phí vận chuyển</span>
              <span>30.000 ₫</span>
            </div>
            <div class="calc-row total">
              <span>Tổng cộng</span>
              <span>{{ (subTotal + 30000).toLocaleString('vi-VN') }} ₫</span>
            </div>
          </div>

          <button class="btn-confirm" @click="confirmOrder">XÁC NHẬN ĐẶT HÀNG</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const skuId = route.query.skuId;
const buyQty = Number(route.query.qty) || 1; // Nhận số lượng từ URL

const skuInfo = ref(null);
const paymentMethod = ref('COD');
const customerInfo = ref({ name: '', phone: '', address: '' });

// Tính toán tiền hàng
const subTotal = computed(() => {
  if (!skuInfo.value) return 0;
  return skuInfo.value.price * buyQty;
});

onMounted(async () => {
  // Demo data: Trong thực tế sẽ gọi API lấy chi tiết biến thể theo skuId
  if (skuId) {
    skuInfo.value = {
      name: 'Giày Nike Air Zoom',
      colorName: 'Đen',
      sizeName: '41',
      price: 2500000,
      image: ''
    };
  }
});

const confirmOrder = () => {
  if (!customerInfo.value.name || !customerInfo.value.phone) {
    alert("Vui lòng nhập đầy đủ thông tin giao hàng!");
    return;
  }
  alert(`Đơn hàng của bạn đã được tiếp nhận!\nTổng tiền: ${(subTotal.value + 30000).toLocaleString('vi-VN')} ₫`);
  router.push('/');
};
</script>

<style scoped>
/* Giữ nguyên phần Style bạn đã gửi */
</style>