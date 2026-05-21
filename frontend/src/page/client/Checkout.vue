<template>
  <div class="checkout-page container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-8">Thanh Toán Đơn Hàng</h1>

    <div class="checkout-section items-section mb-8">
      <h2 class="section-title">Sản phẩm đã chọn</h2>
      <div v-if="orderStore.checkoutItems.length === 0" class="empty-msg">
        Chưa có sản phẩm nào để thanh toán.
      </div>
      <table v-else class="w-full text-left">
        <thead>
          <tr class="border-b">
            <th class="py-2">Sản phẩm</th>
            <th>Đơn giá</th>
            <th>Số lượng</th>
            <th class="text-right">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in orderStore.checkoutItems" :key="index" class="border-b">
            <td class="py-4 flex items-center gap-4">
              <img :src="item.image || 'https://placehold.co/50x50'" class="w-12 h-12 object-cover rounded" />
              <span>{{ item.name }}</span>
            </td>
            <td>{{ item.price.toLocaleString('vi-VN') }} ₫</td>
            <td>{{ item.quantity }}</td>
            <td class="text-right font-bold">{{ (item.price * item.quantity).toLocaleString('vi-VN') }} ₫</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="grid-bottom">
      
      <div class="checkout-section form-section">
        <div class="flex justify-between items-center border-b-2 border-slate-100 pb-2 mb-5">
          <h2 class="section-title !border-0 !pb-0 !mb-0">Thông tin giao hàng</h2>
          
          <button 
            v-if="authStore.isLoggedIn" 
            @click="openAddressModal"
            class="text-sm bg-blue-50 text-blue-600 px-3 py-1.5 rounded hover:bg-blue-100 transition-colors font-semibold"
          >
            + Chọn địa chỉ đã lưu
          </button>
        </div>

        <div class="form-group">
          <label>Họ và tên người nhận (*)</label>
          <input type="text" v-model="orderStore.shippingInfo.consigneeName" placeholder="Nhập họ tên đầy đủ" required />
        </div>
        <div class="form-group">
          <label>Số điện thoại (*)</label>
          <input type="tel" v-model="orderStore.shippingInfo.consigneePhone" placeholder="Nhập số điện thoại" required />
        </div>
        <div class="form-group">
          <label>Địa chỉ nhận hàng chi tiết (*)</label>
          <textarea v-model="orderStore.shippingInfo.consigneeAddress" rows="3" placeholder="Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Tỉnh/TP" required></textarea>
        </div>
      </div>

      <div class="checkout-section summary-section">
        <h2 class="section-title">Tóm tắt đơn hàng</h2>
        
        <div class="summary-row">
          <span>Tổng tiền hàng ({{ orderStore.totalQuantity }} SP)</span>
          <span>{{ orderStore.totalMoney.toLocaleString('vi-VN') }} ₫</span>
        </div>
        <div class="summary-row">
          <span>Phí vận chuyển</span>
          <span>{{ orderStore.shippingFee.toLocaleString('vi-VN') }} ₫</span>
        </div>
        <div class="summary-row total-row">
          <span>Khách phải trả</span>
          <span class="final-price">{{ orderStore.finalAmount.toLocaleString('vi-VN') }} ₫</span>
        </div>

        <div class="divider"></div>

        <h3 class="font-bold mb-3">Phương thức thanh toán</h3>
        <div class="payment-methods">
          <label class="radio-label">
            <input type="radio" value="COD" v-model="orderStore.paymentMethod" />
            <span>Thanh toán khi nhận hàng (COD)</span>
          </label>
          <label class="radio-label">
            <input type="radio" value="VNPAY" v-model="orderStore.paymentMethod" />
            <span>Thanh toán trực tuyến (VNPay)</span>
          </label>
        </div>

        <button 
          class="btn-checkout" 
          @click="handleCheckout"
          :disabled="orderStore.loading || orderStore.checkoutItems.length === 0"
        >
          {{ orderStore.loading ? 'Đang xử lý...' : 'ĐẶT HÀNG' }}
        </button>
      </div>

    </div>

    <div v-if="isAddressModalOpen" class="modal-overlay" @click.self="isAddressModalOpen = false">
      <div class="modal-content">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-bold">Sổ địa chỉ của bạn</h3>
          <button @click="isAddressModalOpen = false" class="text-gray-500 hover:text-red-500 font-bold text-xl">&times;</button>
        </div>

        <div v-if="isLoadingAddresses" class="text-center py-4 text-gray-500">Đang tải địa chỉ...</div>
        
        <div v-else-if="addressList.length === 0" class="text-center py-4 text-gray-500">
          Bạn chưa lưu địa chỉ nào.
        </div>

        <div v-else class="address-list">
          <div 
            v-for="addr in addressList" 
            :key="addr.id" 
            class="address-card"
            @click="selectAddress(addr)"
          >
            <div class="font-bold text-gray-800">{{ addr.consigneeName || addr.receiverName || 'Chưa có tên' }}</div>
            <div class="text-sm text-gray-600 my-1">{{ addr.consigneePhone || addr.receiverPhone || 'Chưa có SĐT' }}</div>
            <div class="text-sm text-gray-500 line-clamp-2">
              {{ addr.street || addr.addressDetail }}, {{ addr.ward }}, {{ addr.district }}, {{ addr.city || addr.province }}
            </div>
            <span v-if="addr.isDefault" class="default-badge">Mặc định</span>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useOrderStore } from '@/store/orderStore';
import { useAuthStore } from '@/store/authStore'; 
import orderApi from '@/api/orderApi';
import addressApi from '@/api/addressApi'; 
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';

const orderStore = useOrderStore();
const authStore = useAuthStore(); 
const router = useRouter();

// --- STATE QUẢN LÝ POPUP ĐỊA CHỈ ---
const isAddressModalOpen = ref(false);
const isLoadingAddresses = ref(false);
const addressList = ref([]);

// --- KHỞI TẠO VÀ KIỂM TRA LUỒNG ---
onMounted(async () => {
  // 1. Chốt chặn giỏ hàng trống
  if (orderStore.checkoutItems.length === 0) {
    Swal.fire('Opps!', 'Không có sản phẩm nào để thanh toán!', 'warning');
    router.push('/'); 
    return;
  }

  // 2. Tự động điền dữ liệu nếu khách đã đăng nhập
  if (authStore.isLoggedIn && authStore.user) {
    orderStore.shippingInfo.consigneeName = authStore.user.fullName || '';
    orderStore.shippingInfo.consigneePhone = authStore.user.phone || '';
  }
});

// --- LOGIC TẢI VÀ CHỌN ĐỊA CHỈ TỪ SỔ ---
const openAddressModal = async () => {
  isAddressModalOpen.value = true;
  if (addressList.value.length > 0) return; 

  try {
    isLoadingAddresses.value = true;
    
    // TÌM ID TỪ NHIỀU NGUỒN: Store -> User Object -> LocalStorage
    let customerId = authStore.id || authStore.user?.id; 
    
    // Nếu Store bị trống (do f5 trang), tìm cứu viện trong LocalStorage
    if (!customerId) {
        const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
        customerId = storedUser.id;
    }
    
    if (!customerId) {
      Swal.fire('Lỗi', 'Không tìm thấy định danh khách hàng (ID). Hãy thử đăng xuất và đăng nhập lại.', 'error');
      return;
    }

    const res = await addressApi.getByCustomerId(customerId);
    addressList.value = res.data?.data || res.data || res || []; 
  } catch (error) {
    console.error("Lỗi tải sổ địa chỉ:", error);
    Swal.fire('Lỗi', 'Không thể tải danh sách địa chỉ.', 'error');
  } finally {
    isLoadingAddresses.value = false;
  }
};

const selectAddress = (addr) => {
  // Bơm dữ liệu ngược lại vào Form (Data Hydration)
  orderStore.shippingInfo.consigneeName = addr.consigneeName || addr.receiverName || '';
  orderStore.shippingInfo.consigneePhone = addr.consigneePhone || addr.receiverPhone || '';
  orderStore.shippingInfo.consigneeAddress = `${addr.street || addr.addressDetail}, ${addr.ward}, ${addr.district}, ${addr.city || addr.province}`;
  
  isAddressModalOpen.value = false; 
};

// --- LOGIC XỬ LÝ THANH TOÁN CHÍNH ---
const handleCheckout = async () => {
  // Validate cơ bản
  if (!orderStore.shippingInfo.consigneeName || !orderStore.shippingInfo.consigneePhone || !orderStore.shippingInfo.consigneeAddress) {
    Swal.fire('Thiếu thông tin', 'Vui lòng điền đầy đủ thông tin giao hàng!', 'error');
    return;
  }

  // Đóng gói Payload với các lớp bọc lót an toàn
 const payload = {
  order: {
    customerName: authStore.isLoggedIn 
        ? (authStore.user?.fullName || authStore.fullName || orderStore.shippingInfo.consigneeName)
        : orderStore.shippingInfo.consigneeName, 
        
    customerPhone: authStore.isLoggedIn 
        ? (authStore.user?.phone || authStore.phone || orderStore.shippingInfo.consigneePhone)
        : orderStore.shippingInfo.consigneePhone,
    
    consigneeName: orderStore.shippingInfo.consigneeName,
    consigneePhone: orderStore.shippingInfo.consigneePhone,
    consigneeAddress: orderStore.shippingInfo.consigneeAddress,
    
    shippingFee: orderStore.shippingFee || 0,
    totalMoney: orderStore.totalMoney || 0,
    totalQuantity: orderStore.totalQuantity || 0,
    status: 1,
    orderType: 'ONLINE' // <--- BỔ SUNG DÒNG NÀY
  },
  details: orderStore.checkoutItems.map(item => ({
    productDetailId: item.productDetailId || item.id, 
    quantity: item.quantity,
    price: item.price || 0
  }))
};

  try {
    orderStore.loading = true;
    let res;

    // Rẽ nhánh API thông minh dựa trên trạng thái đăng nhập
    if (authStore.isLoggedIn) {
      res = await orderApi.createOrderSecure(payload); 
    } else {
      res = await orderApi.createOrder(payload); 
    }
    
    // Quét mã code hóa đơn trả về
    const orderCode = res?.data?.data?.code || res?.data?.code || 'ORD_UNKNOWN';
    
    if (orderStore.paymentMethod === 'VNPAY') {
      Swal.fire('Thành công', `Tạo đơn thành công (Mã: ${orderCode}). Đang chuyển hướng VNPay...`, 'success');
      // TODO: Tích hợp logic Redirect VNPay
    } else {
      Swal.fire('Thành công!', `Mã đơn hàng của bạn là: ${orderCode}`, 'success');
      orderStore.setCheckoutItems([]); 
      router.push('/');
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || "Đã xảy ra lỗi khi tạo đơn hàng. Vui lòng thử lại.";
    Swal.fire('Thất bại!', errorMsg, 'error');
  } finally {
    orderStore.loading = false;
  }
};
</script>

<style scoped>
.checkout-section {
  background: #ffffff;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 1px solid #e2e8f0;
}
.section-title { font-size: 20px; font-weight: 700; margin-bottom: 20px; border-bottom: 2px solid #f1f5f9; padding-bottom: 10px; }

.grid-bottom {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.form-group { margin-bottom: 15px; }
.form-group label { display: block; font-weight: 600; margin-bottom: 5px; color: #475569; }
.form-group input, .form-group textarea {
  width: 100%; padding: 12px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none; transition: 0.2s;
}
.form-group input:focus, .form-group textarea:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }

.summary-row { display: flex; justify-content: space-between; margin-bottom: 15px; color: #475569; }
.total-row { font-size: 18px; font-weight: bold; color: #0f172a; margin-top: 10px; }
.final-price { color: #ef4444; font-size: 24px; }
.divider { height: 1px; background: #e2e8f0; margin: 20px 0; }

.payment-methods { display: flex; flex-direction: column; gap: 10px; margin-bottom: 30px; }
.radio-label { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 10px; border: 1px solid #e2e8f0; border-radius: 6px; transition: 0.2s; }
.radio-label:hover { background: #f8fafc; border-color: #cbd5e1; }

.btn-checkout {
  width: 100%; padding: 16px; background: #ef4444; color: white; font-size: 18px; font-weight: bold; border: none; border-radius: 8px; cursor: pointer; transition: 0.2s;
}
.btn-checkout:hover:not(:disabled) { background: #dc2626; }
.btn-checkout:disabled { background: #94a3b8; cursor: not-allowed; }

@media (max-width: 768px) {
  .grid-bottom { grid-template-columns: 1fr; } 
}

/* Modal CSS */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(15, 23, 42, 0.6);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
  backdrop-filter: blur(2px);
}

.modal-content {
  background: white;
  width: 90%; max-width: 500px;
  max-height: 80vh;
  border-radius: 12px;
  padding: 24px;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.address-list {
  display: flex; flex-direction: column; gap: 12px;
}

.address-card {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.address-card:hover {
  border-color: #3b82f6;
  background: #f8fafc;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.default-badge {
  position: absolute;
  top: 12px; right: 12px;
  background: #ef4444; color: white;
  font-size: 10px; font-weight: bold;
  padding: 2px 8px; border-radius: 12px;
}
</style>