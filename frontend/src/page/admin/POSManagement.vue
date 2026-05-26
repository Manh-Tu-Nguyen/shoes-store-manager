<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-title">
        <h2>Bán hàng tại quầy</h2>
        <p class="text-muted">Quản lý hóa đơn trực tiếp tại quầy POS</p>
      </div>
      <div class="header-actions">
        <button class="btn-primary" @click="handleCreateDraft" :disabled="isProcessing || orders.length >= 10">
          <span class="icon">+</span> Tạo đơn hàng ({{ orders.length }}/10)
        </button>
      </div>
    </div>

    <div v-if="orders.length === 0" class="pos-empty-system">
      <div class="empty-state-box">
        <Icon icon="mdi:cart-off" width="64" class="text-gray-300" />
        <h3>Chưa có đơn hàng nào</h3>
        <button class="btn-primary mt-4" @click="handleCreateDraft" :disabled="isProcessing">+ Tạo đơn hàng mới</button>
      </div>
    </div>

    <div v-else class="pos-active-layout">
      <div class="tabs-container">
        <div v-for="(order, index) in orders" :key="order.id" 
             :class="['order-tab', { 'active': selectedOrder?.id === order.id }]" 
             @click="selectOrder(order)">
          <span>Đơn {{ index + 1 }} - {{ order.code }}</span>
          <button class="btn-close-tab" @click.stop="confirmCancelOrder(order)">×</button>
        </div>
      </div>

      <div class="pos-main-content">
        <div class="left-panel">
          <div class="panel-inner-header">
            <h3 class="font-bold text-gray-800">SẢN PHẨM</h3>
            <button class="btn-primary py-1.5 text-sm" @click="openProductModal">+ Chọn sản phẩm</button>
          </div>
          <div v-if="orderDetails.length === 0" class="products-empty-state"><p class="text-gray-500 font-medium">Chưa có sản phẩm nào</p></div>
          <DataTable v-else :isEmpty="orderDetails.length === 0" :colCount="5">
            <template #header><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th><th>Xóa</th></template>
            <template #body>
              <tr v-for="item in orderDetails" :key="item.id">
                <td>
                  <div class="product-cell">
                    <img :src="item.productDetail?.image || 'https://placehold.co/40'" class="product-thumb" />
                    <div>
                      <div class="font-bold text-slate-800">{{ item.productDetail?.name || '---' }}</div>
                      <div class="text-xs text-gray-400">Mã: {{ item.productDetail?.code }}</div>
                    </div>
                  </div>
                </td>
                <td>{{ item.price?.toLocaleString('vi-VN') }} ₫</td>
                <td>
                  <div class="qty-control">
                    <button @click="updateCartQuantity(item, -1)" :disabled="isProcessing">-</button>
                    <input type="number" :value="item.quantity" @change="handleManualQuantityChange(item, $event)" />
                    <button @click="updateCartQuantity(item, 1)" :disabled="isProcessing">+</button>
                  </div>
                </td>
                <td class="font-bold text-blue-600">{{ item.totalPrice?.toLocaleString('vi-VN') }} ₫</td>
                <td><button class="text-red-500" @click="removeProduct(item)">🗑</button></td>
              </tr>
            </template>
          </DataTable>
        </div>

        <div class="right-panel">
          <div class="info-block">
            <h4 class="block-title">THÔNG TIN KHÁCH HÀNG</h4>
            <button class="btn-secondary w-full mb-3" @click="openCustomerModal">Chọn khách hàng</button>
            <input type="text" placeholder="Tên khách hàng" v-model="customerInfo.name" class="pos-input mb-2" />
            <input type="text" placeholder="Số điện thoại" v-model="customerInfo.phone" class="pos-input" />
          </div>
          <div class="info-block">
            <h4 class="block-title">VOUCHER</h4>
            <select v-model="selectedVoucherId" class="pos-input">
              <option :value="null">-- Không áp dụng voucher --</option>
              <option v-for="v in activeVouchers" :key="v.id" :value="v.id">{{ v.name }} (-{{ v.value }}{{ v.type ? '%' : 'đ' }})</option>
            </select>
          </div>
          <div class="info-block payment-summary flex-1">
  <h4 class="block-title">THÔNG TIN GIAO DỊCH</h4>
  
  <div class="transaction-section mb-4">
    <label class="font-bold text-sm text-gray-700 mb-2 block">Hình thức nhận hàng:</label>
    <div class="flex gap-2">
      <button :class="['toggle-btn', deliveryType === 'DIRECT' ? 'active' : '']" @click="deliveryType = 'DIRECT'">Tại quầy</button>
      <button :class="['toggle-btn', deliveryType === 'SHIPPING' ? 'active' : '']" @click="deliveryType = 'SHIPPING'">Giao hàng</button>
    </div>
    
    <div v-if="deliveryType === 'SHIPPING'" class="mt-3 slide-down">
      <input type="text" v-model="shippingInfo.address" class="pos-input mb-2" placeholder="Địa chỉ giao hàng chi tiết..." />
      <div class="flex justify-between items-center text-sm">
        <span class="text-gray-600">Phí vận chuyển:</span>
        <span class="font-semibold text-blue-600">+ {{ shippingInfo.fee.toLocaleString('vi-VN') }} ₫</span>
      </div>
    </div>
  </div>

  <div class="transaction-section mb-4 border-t pt-3">
    <label class="font-bold text-sm text-gray-700 mb-2 block">Thanh toán qua:</label>
    <div class="flex gap-2 mb-3">
      <button :class="['toggle-btn', paymentMethod === 'CASH' ? 'active' : '']" @click="paymentMethod = 'CASH'">Tiền mặt</button>
      <button :class="['toggle-btn', paymentMethod === 'VNPAY' ? 'active' : '']" @click="paymentMethod = 'VNPAY'">VNPay (QR)</button>
    </div>

    <div v-if="paymentMethod === 'CASH'" class="slide-down bg-gray-50 p-3 rounded border">
      <div class="flex justify-between items-center mb-2">
        <span class="text-sm">Tiền khách đưa:</span>
        <input type="number" v-model.number="cashInfo.amountTendered" class="pos-input w-32 text-right font-bold text-green-600" />
      </div>
      <div class="flex justify-between items-center">
        <span class="text-sm">Tiền thối lại:</span>
        <span :class="['font-bold', changeAmount > 0 ? 'text-red-500' : 'text-gray-400']">
          {{ changeAmount.toLocaleString('vi-VN') }} ₫
        </span>
      </div>
    </div>
  </div>

  <div class="summary-row font-bold text-lg border-t pt-3 mt-auto">
    <span>Tổng phải thu:</span>
    <span class="text-blue-700 text-xl">{{ finalAmount.toLocaleString('vi-VN') }} ₫</span>
  </div>
  
  <button class="btn-primary w-full py-3 mt-4 justify-center text-base uppercase" 
          @click="processPayment" 
          :disabled="isProcessing || !isPaymentValid">
    {{ paymentMethod === 'VNPAY' ? 'TẠO MÃ QR VNPAY (F2)' : 'HOÀN TẤT ĐƠN HÀNG (F2)' }}
  </button>
</div>
        </div>
      </div>
    </div>

    <div v-if="isProductModalOpen" class="modal-overlay" @click.self="closeProductModal">
      <div class="modal-content" style="width: 800px;">
        <div class="modal-header"><h3>Chọn sản phẩm</h3><button @click="closeProductModal" class="close-btn">&times;</button></div>
        <div class="modal-body">
            <input v-model="productSearchQuery" class="pos-input w-full mb-3" placeholder="Tìm sản phẩm..." />
            <div style="max-height: 400px; overflow-y: auto;">
             <table class="product-table w-full"><tr v-for="sku in filteredProductDetails" :key="sku.id">
                <td><img :src="sku.image" class="product-thumb" /></td>
                <td>{{ sku.name }} ({{ sku.code }})</td>
                <td><button class="btn-primary" @click="handleSelectSku(sku)">Thêm</button></td>
             </tr></table>
            </div>
        </div>
      </div>
    </div>

    <div v-if="isCustomerModalOpen" class="modal-overlay" @click.self="closeCustomerModal">
      <div class="modal-content" style="width: 950px;">
        <div class="modal-header"><h3>Chọn khách hàng</h3><button @click="closeCustomerModal" class="close-btn">&times;</button></div>
        <div class="modal-body">
          <input v-model="customerSearchQuery" class="pos-input w-full mb-3" placeholder="Tìm tên/SĐT..." />
          <div style="max-height: 500px; overflow-y: auto;">
            <table class="product-table w-full">
              <thead><tr><th>Ảnh</th><th>Mã KH</th><th>Họ tên</th><th>Liên hệ</th><th>Chọn</th></tr></thead>
              <tbody>
                <tr v-for="c in filteredCustomers" :key="c.id">
                  <td><img :src="c.image || 'https://placehold.co/40'" class="product-thumb" /></td>
                  <td>{{ c.code }}</td>
                  <td>{{ c.lastName }} {{ c.firstName }}</td>
                  <td>{{ c.phoneNumber }}</td>
                  <td><button class="btn-primary" @click="handleSelectCustomer(c)">Chọn</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    <div v-if="isVnPayModalOpen" class="modal-overlay" @click.self="isVnPayModalOpen = false">
  <div class="modal-content" style="width: 400px; text-align: center; padding: 30px;">
    <h3 class="font-bold text-xl mb-2 text-blue-800">Quét mã VNPay</h3>
    <p class="text-gray-500 text-sm mb-4">Sử dụng App ngân hàng để thanh toán</p>
    
    <div class="flex justify-center mb-4">
      <img :src="vnPayQrUrl" alt="VNPay QR" class="border p-2 rounded shadow-sm" />
    </div>
    
    <div class="font-bold text-2xl text-red-500 mb-6">
      {{ finalAmount.toLocaleString('vi-VN') }} ₫
    </div>
    
    <div class="flex gap-3">
      <button class="btn-secondary flex-1" @click="isVnPayModalOpen = false">Hủy</button>
      <button class="btn-primary flex-1 justify-center" @click="submitFinalOrder('VNPAY')">Xác nhận đã thanh toán</button>
    </div>
  </div>
</div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import Swal from 'sweetalert2';
import orderApi from '@/api/orderApi';
import productDetailApi from '@/api/productDetailApi';
import voucherApi from '@/api/voucherApi';
import { useCustomerStore } from '@/store/customerStore';
import DataTable from '@/components/common/DataTable.vue';
import { Icon } from '@iconify/vue';
const deliveryType = ref('DIRECT'); // 'DIRECT' (Tại quầy) hoặc 'SHIPPING' (Giao hàng)
const paymentMethod = ref('CASH');
// --- STATE ---
const orders = ref([]);
const selectedOrder = ref(null);
const orderDetails = ref([]);
const isProcessing = ref(false);
const customerStore = useCustomerStore();
const vouchers = ref([]);
const selectedVoucherId = ref(null);

const isProductModalOpen = ref(false);
const isCustomerModalOpen = ref(false);
const productSearchQuery = ref('');
const customerSearchQuery = ref('');
const allProductDetails = ref([]);
const customerInfo = reactive({ name: '', phone: '' });

const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 1500, timerProgressBar: true });
const shippingInfo = reactive({
  address: '',
  fee: 30000 // Tạm hardcode 30k, có thể tích hợp API GHTK/GHN sau
});

// Thông tin tiền mặt (Chỉ hiển thị khi paymentMethod === 'CASH')
const cashInfo = reactive({
  amountTendered: 0 // Tiền khách đưa
});

// Trạng thái QR Code VNPay (Giả lập)
const isVnPayModalOpen = ref(false);
const vnPayQrUrl = ref('');
// --- LIFECYCLE ---
onMounted(async () => {
  await loadDraftOrders();
  await customerStore.fetchCustomers();
  try {
    const res = await voucherApi.getAll();
    vouchers.value = res?.data || [];
  } catch (err) { console.error(err); }
});

// --- COMPUTED ---
const activeVouchers = computed(() => {
  const now = new Date();
  return vouchers.value.filter(v => v.status === true && v.quantity > 0 && new Date(v.startDate) <= now && new Date(v.endDate) >= now);
});

const filteredCustomers = computed(() => {
  const q = customerSearchQuery.value.toLowerCase();
  return customerStore.customers.filter(c => c.firstName?.toLowerCase().includes(q) || c.lastName?.toLowerCase().includes(q) || c.phoneNumber?.includes(q));
});

const filteredProductDetails = computed(() => {
  const q = productSearchQuery.value.toLowerCase();
  return allProductDetails.value.filter(i => i.name?.toLowerCase().includes(q) || i.code?.toLowerCase().includes(q));
});

// --- ACTIONS ---
const loadDraftOrders = async () => {
  const res = await orderApi.getPosDraftOrders();
  orders.value = Array.isArray(res) ? res : (res?.data?.data || res?.data || []);
  if (orders.value.length > 0 && !selectedOrder.value) await selectOrder(orders.value[0]);
  else if (orders.value.length === 0) selectedOrder.value = null;
};

const selectOrder = async (order) => {
  selectedOrder.value = order;
  customerInfo.name = order.customerName || '';
  customerInfo.phone = order.customerPhone || '';
  selectedVoucherId.value = null;
  const res = await orderApi.getOrderDetails(order.id);
  orderDetails.value = Array.isArray(res) ? res : (res?.data?.data || res?.data || []);
};

const handleCreateDraft = async () => {
  isProcessing.value = true;
  await orderApi.createDraftOrder({ orderType: 'POS' });
  await loadDraftOrders();
  await selectOrder(orders.value[orders.value.length - 1]);
  isProcessing.value = false;
  Toast.fire({ icon: 'success', title: 'Đã tạo hóa đơn mới' });
};

const confirmCancelOrder = (order) => {
  Swal.fire({ title: 'Hủy đơn?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#d33' }).then(async (result) => {
    if (result.isConfirmed) {
      await orderApi.deleteDraftOrder(order.id);
      selectedOrder.value = null;
      await loadDraftOrders();
      Toast.fire({ icon: 'success', title: 'Đã hủy đơn' });
    }
  });
};

const removeProduct = (item) => {
  Swal.fire({ title: 'Xóa sản phẩm?', showCancelButton: true, confirmButtonColor: '#d33' }).then(async (res) => {
    if (res.isConfirmed) {
      await orderApi.removePosItem(selectedOrder.value.id, item.id);
      await selectOrder(selectedOrder.value);
      Toast.fire({ icon: 'success', title: 'Đã xóa sản phẩm' });
    }
  });
};

const processPayment = async () => {
  const confirm = await Swal.fire({ 
    title: paymentMethod.value === 'VNPAY' ? 'Tạo mã QR thanh toán?' : 'Hoàn tất đơn hàng?', 
    icon: 'info', 
    showCancelButton: true 
  });
  
  if (confirm.isConfirmed) {
    await submitFinalOrder(paymentMethod.value);
  }
};
const submitFinalOrder = async (method) => {
  try {
    isProcessing.value = true;
    const payload = { 
      customerName: customerInfo.name, 
      note: `Thanh toán ${method} - Giao hàng: ${deliveryType.value}`, 
      voucherId: selectedVoucherId.value,
      paymentMethod: method,
      deliveryType: deliveryType.value,
      shippingAddress: deliveryType.value === 'SHIPPING' ? shippingInfo.address : null,
      amountTendered: method === 'CASH' ? cashInfo.amountTendered : finalAmount.value,
      changeAmount: method === 'CASH' ? changeAmount.value : 0
    };

    // 1. Lưu đơn hàng và trạng thái Payment xuống DB (CASH thì xong luôn, VNPAY thì PENDING)
    const response = await orderApi.posCheckout(selectedOrder.value.id, payload);
const savedOrder = response;

    // 2. RẼ NHÁNH: Nếu là VNPAY, tạo QR thật
    if (method === 'VNPAY') {
       // Gọi Backend lấy link VNPAY xịn
       const vnpayRes = await orderApi.createVnPayUrl(savedOrder.finalAmount, savedOrder.code);
       console.log("ĐỒNG BÀO CHÚ Ý - DỮ LIỆU VNPAY TRẢ VỀ LÀ:", vnpayRes);
       const realPaymentUrl = vnpayRes.paymentUrl;

       // Biến link VNPAY thành hình ảnh QR để khách lấy điện thoại quét
       vnPayQrUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(vnpayRes.paymentUrl)}`;
       isVnPayModalOpen.value = true;
       
    } else {
       // Tiền mặt thì báo thành công luôn
       Toast.fire({ icon: 'success', title: 'Thanh toán tiền mặt thành công' });
       selectedOrder.value = null;
       cashInfo.amountTendered = 0;
       shippingInfo.address = '';
       await loadDraftOrders();
    }
    
  } catch (e) { 
    console.error(e);
    Swal.fire('Lỗi', 'Giao dịch thất bại, vui lòng thử lại', 'error'); 
  } finally { 
    isProcessing.value = false; 
  }
};
const openProductModal = async () => {
  const res = await productDetailApi.getAllDetails();
  allProductDetails.value = Array.isArray(res) ? res : (res?.data?.data || res?.data || []);
  isProductModalOpen.value = true;
};
const closeProductModal = () => isProductModalOpen.value = false;

const handleSelectSku = async (sku) => {
  await orderApi.addOrUpdatePosItem(selectedOrder.value.id, { productDetailId: sku.id, quantity: 1 });
  await selectOrder(selectedOrder.value);
  closeProductModal();
  Toast.fire({ icon: 'success', title: 'Đã thêm sản phẩm' });
};

const openCustomerModal = () => isCustomerModalOpen.value = true;
const closeCustomerModal = () => isCustomerModalOpen.value = false;

const handleSelectCustomer = (c) => {
  customerInfo.name = `${c.lastName} ${c.firstName}`;
  customerInfo.phone = c.phoneNumber;
  closeCustomerModal();
  Toast.fire({ icon: 'success', title: 'Đã chọn khách hàng' });
};

const handleManualQuantityChange = async (item, event) => {
  const val = parseInt(event.target.value);
  if (val <= 0) removeProduct(item);
  else {
    try {
      await orderApi.addOrUpdatePosItem(selectedOrder.value.id, { productDetailId: item.productDetailId, quantity: val });
      await selectOrder(selectedOrder.value);
      Toast.fire({ icon: 'success', title: 'Đã cập nhật' });
    } catch (e) {
      event.target.value = item.quantity;
      Swal.fire('Lỗi', 'Không đủ tồn kho', 'error');
    }
  }
};

const updateCartQuantity = async (item, change) => {
  const newQty = item.quantity + change;
  if (newQty <= 0) removeProduct(item);
  else {
    await orderApi.addOrUpdatePosItem(selectedOrder.value.id, { productDetailId: item.productDetailId, quantity: newQty });
    await selectOrder(selectedOrder.value);
    Toast.fire({ icon: 'success', title: 'Đã cập nhật' });
  }
};
// Tính lại finalAmount (Bổ sung cờ kiểm tra Delivery Type)
const finalAmount = computed(() => {
  const subtotal = orderDetails.value.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const selectedVoucher = vouchers.value.find(v => v.id === selectedVoucherId.value);
  
  let discount = 0;
  if (selectedVoucher) {
    discount = selectedVoucher.type ? (subtotal * selectedVoucher.value) / 100 : selectedVoucher.value;
  }
  
  let total = Math.max(subtotal - discount, 0);
  
  // NẾU GIAO HÀNG: Cộng thêm phí ship
  if (deliveryType.value === 'SHIPPING') {
    total += shippingInfo.fee;
  }
  
  return total;
});

// Tính toán tiền thối lại (Change Amount)
const changeAmount = computed(() => {
  if (paymentMethod.value !== 'CASH' || !cashInfo.amountTendered) return 0;
  return Math.max(cashInfo.amountTendered - finalAmount.value, 0);
});

// Kiểm tra điều kiện có cho phép bấm nút Thanh toán hay không
const isPaymentValid = computed(() => {
  if (orderDetails.value.length === 0) return false;
  
  if (deliveryType.value === 'SHIPPING' && !shippingInfo.address) return false; // Giao hàng phải có địa chỉ
  
  if (paymentMethod.value === 'CASH') {
    return cashInfo.amountTendered >= finalAmount.value; // Tiền khách đưa phải >= Tổng tiền
  }
  
  return true; // VNPay thì luôn cho phép tạo mã QR
});
</script>

<style scoped>
/* GIAO DIỆN CHUNG & LAYOUT */
.management-page { padding: 24px; background: #ffffff; min-height: 100vh; font-family: inherit; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; font-weight: 700; }
.text-muted { color: #64748b; font-size: 14px; margin-top: 4px; }

/* BUTTONS */
.btn-primary { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500; display: flex; align-items: center; gap: 8px; transition: background 0.2s; }
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { background: #94a3b8; cursor: not-allowed; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500; }
.btn-secondary:hover:not(:disabled) { background: #cbd5e1; }
.btn-outline-primary { background: #ffffff; color: #2563eb; border: 1px solid #2563eb; padding: 8px 20px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.btn-outline-primary:hover { background: #eff6ff; }

/* EMPTY STATES */
.pos-empty-system { display: flex; justify-content: center; align-items: center; height: 60vh; background: #ffffff; border: 2px dashed #e2e8f0; border-radius: 12px; }
.empty-state-box { text-align: center; max-width: 400px; display: flex; flex-direction: column; align-items: center; }
.empty-state-box h3 { font-size: 18px; color: #1e293b; font-weight: 700; margin: 16px 0 8px; }
.products-empty-state { flex: 1; display: flex; flex-direction: column; justify-content: center; align-items: center; padding: 40px; text-align: center; }
.box-illustration { margin-bottom: 16px; color: #cbd5e1; }

/* TABS */
.pos-active-layout { display: flex; flex-direction: column; gap: 20px; }
.tabs-container { display: flex; gap: 6px; border-bottom: 2px solid #e2e8f0; padding-bottom: 1px; overflow-x: auto; }
.order-tab { display: flex; align-items: center; gap: 10px; padding: 10px 18px; background: #f1f5f9; color: #64748b; border-radius: 8px 8px 0 0; font-weight: 500; cursor: pointer; border: 1px solid #e2e8f0; border-bottom: none; white-space: nowrap; }
.order-tab.active { background: #ffffff; color: #2563eb; font-weight: 700; border-color: #cbd5e1; border-top: 3px solid #2563eb; border-bottom: 2px solid #ffffff; margin-bottom: -2px; }
.btn-close-tab { background: none; border: none; color: #94a3b8; font-size: 16px; cursor: pointer; padding: 0 2px; }
.btn-close-tab:hover { color: #ef4444; }

/* MAIN CONTENT */
.pos-main-content { display: grid; grid-template-columns: 1fr 380px; gap: 24px; align-items: start; }
.left-panel { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; display: flex; flex-direction: column; min-height: 450px; }
.panel-inner-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e2e8f0; }
.panel-inner-header h3 { font-size: 16px; margin: 0; }

.table-wrapper { padding: 12px; }
.product-cell { display: flex; gap: 12px; align-items: center; }
.product-thumb { width: 40px; height: 40px; object-fit: cover; border-radius: 6px; border: 1px solid #e2e8f0; }

/* QUANTITY CONTROLS (ĐÃ MỞ KHÓA HOÀN TOÀN CONSOLE NHẬP LIỆU) */
.qty-control { display: inline-flex; align-items: center; border: 1px solid #cbd5e1; border-radius: 6px; overflow: hidden; background: #f8fafc; }
.qty-control button { background: transparent; border: none; padding: 6px 12px; font-size: 16px; cursor: pointer; font-weight: bold; color: #475569; transition: background 0.2s; }
.qty-control button:hover:not(:disabled) { background: #e2e8f0; color: #2563eb; }
.qty-control button:disabled { opacity: 0.5; cursor: not-allowed; }
.qty-control input { width: 50px; text-align: center; border: none; border-left: 1px solid #cbd5e1; border-right: 1px solid #cbd5e1; font-weight: 600; font-size: 14px; background: #ffffff; padding: 6px 0; }
.qty-control input::-webkit-outer-spin-button,
.qty-control input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }

/* RIGHT PANEL */
.right-panel { display: flex; flex-direction: column; gap: 20px; min-height: 450px; }
.info-block { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px; display: flex; flex-direction: column; }
.block-title { font-size: 13px; font-weight: 700; color: #475569; margin: 0 0 16px 0; text-align: center; border-bottom: 1px solid #f1f5f9; padding-bottom: 8px; letter-spacing: 0.5px; }
.input-grid { display: grid; grid-template-columns: 1fr; gap: 12px; }
.pos-input { padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 14px; width: 100%; }
.payment-summary { background: #ffffff; }
.summary-row { display: flex; justify-content: space-between; align-items: center; }
.border-dashed { border-top-style: dashed !important; }
.w-full { width: 100%; }
.mt-4 { margin-top: 16px; }
.mb-3 { margin-bottom: 12px; }

/* MODAL STYLES */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(4px); display: flex; justify-content: center; align-items: center; z-index: 9999; }
.modal-content { background: #ffffff; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); border: 1px solid #e2e8f0; animation: modalScaleUp 0.15s ease-out; }
@keyframes modalScaleUp { from { opacity: 0; transform: scale(0.96) translateY(10px); } to { opacity: 1; transform: scale(1) translateY(0); } }
.modal-header { padding: 16px 24px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; background: #f8fafc; border-top-left-radius: 12px; border-top-right-radius: 12px; }
.modal-header h3 { margin: 0; font-size: 18px; color: #1e293b; font-weight: 700; }
.close-btn { background: none; border: none; font-size: 24px; color: #94a3b8; cursor: pointer; line-height: 1; transition: color 0.2s; }
.close-btn:hover { color: #ef4444; }
.modal-body { padding: 24px; }
.modal-footer { padding: 16px 24px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; background: #f8fafc; border-bottom-left-radius: 12px; border-bottom-right-radius: 12px; }
.product-table th { padding: 12px; font-weight: 600; font-size: 13px; color: #475569; }
.product-table td { border-bottom: 1px solid #f1f5f9; }
.badge-code { background: #eff6ff; color: #1d4ed8; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.product-table { border-collapse: collapse; }
.product-table th, .product-table td { padding: 10px; border-bottom: 1px solid #eee; text-align: left; }
.status-dot { height: 8px; width: 8px; border-radius: 50%; display: inline-block; }
.status-dot.active { background-color: #22c55e; }
.status-dot.inactive { background-color: #94a3b8; }

.toggle-btn { flex: 1; padding: 8px 0; background: #f1f5f9; border: 1px solid #cbd5e1; border-radius: 4px; font-weight: 600; color: #64748b; cursor: pointer; transition: all 0.2s; }
.toggle-btn.active { background: #eff6ff; border-color: #3b82f6; color: #1d4ed8; box-shadow: inset 0 0 0 1px #3b82f6; }
.slide-down { animation: slideDown 0.2s ease-out forwards; overflow: hidden; }
@keyframes slideDown { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }
</style>