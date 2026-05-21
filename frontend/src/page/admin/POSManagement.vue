<template>
  <div class="pos-layout">
    <div class="search-section">
  <div class="search-container">
    <input 
      v-model="searchQuery" 
      placeholder="Nhập mã đơn hoặc tên khách..." 
      class="search-input"
    />
    <hr>
      <button class="btn-add-order" @click="handleCreateDraft">
      + Thêm đơn mới
    </button>
  </div>
</div>
    <div class="main-body">
      <div class="order-table-panel">
  <table class="data-table">
    <thead>
      <tr>
        <th>Mã đơn</th>
        <th>Nhân viên tạo</th> <th>SL</th>            <th>Tổng tiền</th>
        <th>Khách phải trả</th>
        <th>Trạng thái</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="order in filteredOrders" :key="order.id" 
          @click="selectOrder(order)" 
          :class="{'selected-row': selectedOrder?.id === order.id}">
        <td class="font-bold">{{ order.code }}</td>
        
        <td>{{ order.employeeName || 'Chưa rõ' }}</td>
        
        <td class="text-center font-bold">{{ order.totalQuantity || 0 }}</td>
        
        <td class="text-gray-500 line-through text-sm" v-if="order.totalMoney !== order.finalAmount">
          {{ order.totalMoney?.toLocaleString('vi-VN') }} ₫
        </td>
        <td v-else>{{ order.totalMoney?.toLocaleString('vi-VN') }} ₫</td>
        
        <td class="font-bold text-red-600">{{ order.finalAmount?.toLocaleString('vi-VN') }} ₫</td>
        
        <td><span :class="'status-' + order.status">{{ getStatusText(order.status) }}</span></td>
      </tr>
    </tbody>
  </table>
</div>
      <div class="payment-panel">
        <div v-if="selectedOrder" class="payment-content">
          <h3 class="panel-title">Chi tiết: {{ selectedOrder.code }}</h3>
          <div class="customer-info-box">
            <p><strong>Khách hàng:</strong> {{ selectedOrder.customerName || 'Vãng lai' }}</p>
          </div>
          <div class="summary-box">
            <div class="row"><span>Tổng tiền:</span> <span>{{ selectedOrder.totalMoney?.toLocaleString('vi-VN') }} ₫</span></div>
            <div class="row total"><span>Phải trả:</span> <span>{{ selectedOrder.finalAmount?.toLocaleString('vi-VN') }} ₫</span></div>
            <button class="btn-checkout" @click="processPayment" :disabled="isProcessing">CHỐT ĐƠN (F2)</button>
          </div>
        </div>
        <div v-else class="empty-state">Chọn một đơn hàng từ bảng để xem thông tin...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import orderApi from '@/api/orderApi';
import Swal from 'sweetalert2';

const searchQuery = ref('');
const orders = ref([]);
const selectedOrder = ref(null);
const isProcessing = ref(false);
const orderDetails = ref([]);
// Logic tìm kiếm realtime (Google-like)
const filteredOrders = computed(() => {
  if (!searchQuery.value) return orders.value;
  
  const q = searchQuery.value.toLowerCase();
  return orders.value.filter(o => {
    // Ép kiểu về chuỗi rỗng '' nếu trường đó bị null trước khi gọi toLowerCase()
    const code = o.code?.toLowerCase() || '';
    const customer = o.customerName?.toLowerCase() || '';
    
    return code.includes(q) || customer.includes(q);
  });
});

onMounted(async () => await loadOrders());

const loadOrders = async () => {
  try {
    const res = await orderApi.getPosDraftOrders(); 
    orders.value = Array.isArray(res) ? res : (res?.data || []);
    
  } catch (err) { 
    console.error("Lỗi tải hóa đơn POS nháp:", err); 
  }
};

const selectOrder = async (order) => { 
  selectedOrder.value = order; 
  try {
    // Gọi API lấy danh sách chi tiết (Bạn đã định nghĩa trong orderApi.js)
    const res = await orderApi.getOrderDetails(order.id);
    // Bóc vỏ dữ liệu an toàn
    orderDetails.value = Array.isArray(res) ? res : (res?.data?.data || res?.data || []);
  } catch (err) {
    console.error("Lỗi tải chi tiết sản phẩm:", err);
  }
};

const getStatusText = (status) => {
  const map = { 0: 'Nháp', 1: 'Chờ xác nhận', 2: 'Hoàn thành' };
  return map[status] || 'Khác';
};

const processPayment = async () => {
  isProcessing.value = true;
  try {
    await orderApi.posCheckout(selectedOrder.value.id, { 
        orderId: selectedOrder.value.id,
        amountTendered: selectedOrder.value.finalAmount 
    });
    Swal.fire('Thành công', 'Đơn hàng đã chốt!', 'success');
    await loadOrders();
    selectedOrder.value = null;
  } catch (err) { Swal.fire('Lỗi', 'Thất bại', 'error'); }
  finally { isProcessing.value = false; }
};
const handleCreateDraft = async () => {
  try {
    isProcessing.value = true;
    // Gọi API tạo đơn nháp
    await orderApi.createDraftOrder(); 
    // Load lại danh sách để thấy đơn mới hiện trên bảng
    await loadOrders();
    Swal.fire('Thành công', 'Đã tạo hóa đơn nháp mới', 'success');
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tạo hóa đơn mới', 'error');
  } finally {
    isProcessing.value = false;
  }
};

</script>

<style scoped>
.pos-layout { display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
.search-section { padding: 15px; border-bottom: 2px solid #e2e8f0; }
.search-input { width: 100%; padding: 12px; border: 1px solid #cbd5e1; border-radius: 8px; }

.main-body { display: grid; grid-template-columns: 1fr 350px; flex-grow: 1; gap: 20px; padding: 20px; overflow: hidden; }
.order-table-panel { overflow-y: auto; background: white; border: 1px solid #e2e8f0; border-radius: 8px; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { position: sticky; top: 0; background: #f8fafc; padding: 12px; border-bottom: 2px solid #e2e8f0; }
.data-table td { padding: 12px; border-bottom: 1px solid #f1f5f9; cursor: pointer; }
.selected-row { background-color: #e0f2fe; }

.payment-panel { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; }
.btn-checkout { width: 100%; padding: 15px; background: #22c55e; color: white; border: none; border-radius: 6px; font-weight: bold; cursor: pointer; }
.search-container { 
  display: flex; 
  gap: 10px; 
  align-items: center; 
}
.btn-add-order { 
  padding: 12px 20px; 
  background: #3b82f6; 
  color: white; 
  border: none; 
  border-radius: 8px; 
  font-weight: bold; 
  cursor: pointer;
  white-space: nowrap;
}
.btn-add-order:hover { background: #2563eb; }
</style>