<template>
  <div class="management-page">
    
    <div v-if="!selectedOrder">
      <div class="page-header">
        <div class="header-title">
          <h2>Quản lý hóa đơn bán hàng</h2>
          <p class="text-muted">Danh sách đơn hàng online toàn hệ thống</p>
        </div>
      </div>

      <DataTable :isEmpty="orders.length === 0" :colCount="6">
        <template #header>
          <th>Mã đơn hàng</th>
          <th>Người nhận</th>
          <th>Số điện thoại</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
          <th class="text-center">Hành động</th>
        </template>

        <template #body>
          <tr v-for="order in orders" :key="order.id">
            <td><span class="badge-code">{{ order.code }}</span></td>
            <td class="font-bold">{{ order.consigneeName }}</td>
            <td>{{ order.consigneePhone }}</td>
            <td class="text-danger font-bold">{{ order.finalAmount?.toLocaleString('vi-VN') }} ₫</td>
            <td>
              <span :class="['status-badge', getStatusClass(order.status)]">
                {{ getStatusName(order.status) }}
              </span>
            </td>
            <td class="text-center">
              <button @click="viewOrderDetails(order)" class="btn-view">👁 Xem chi tiết</button>
            </td>
          </tr>
        </template>
      </DataTable>
    </div>

    <div v-else class="order-detail-view">
      <div class="page-header">
        <div class="header-title">
          <h2>Chi tiết đơn hàng: <span class="badge-code text-xl">{{ selectedOrder.code }}</span></h2>
          <p class="text-muted">Mã định danh hệ thống: #{{ selectedOrder.id }}</p>
        </div>
        <div class="header-actions">
          <button @click="closeDetail" class="btn-secondary">⬅ Quay lại danh sách</button>
        </div>
      </div>

      <div class="stepper-container">
        <div v-if="selectedOrder.status !== 0" class="stepper">
          <div :class="['step', { active: selectedOrder.status >= 1 }]">
            <div class="circle">1</div>
            <span>Chờ xác nhận</span>
          </div>
          <div :class="['line', { fill: selectedOrder.status >= 2 }]"></div>
          <div :class="['step', { active: selectedOrder.status >= 2 }]">
            <div class="circle">2</div>
            <span>Chuẩn bị hàng</span>
          </div>
          <div :class="['line', { fill: selectedOrder.status >= 3 }]"></div>
          <div :class="['step', { active: selectedOrder.status >= 3 }]">
            <div class="circle">3</div>
            <span>Đang giao hàng</span>
          </div>
          <div :class="['line', { fill: selectedOrder.status >= 4 }]"></div>
          <div :class="['step', { active: selectedOrder.status >= 4 }]">
            <div class="circle">4</div>
            <span>Hoàn thành</span>
          </div>
        </div>

        <div v-else class="stepper cancelled">
          <div class="step active">
            <div class="circle">1</div>
            <span>Khởi tạo đơn</span>
          </div>
          <div class="line fill-danger"></div>
          <div class="step active danger">
            <div class="circle">✕</div>
            <span>Đơn hàng đã hủy</span>
          </div>
        </div>
      </div>

      <div class="status-action-bar">
        <div class="action-buttons-left">
          <button 
            v-if="selectedOrder.status === 1 || selectedOrder.status === 2" 
            @click="handleCancelOrder" 
            class="btn-danger-outline"
            :disabled="isProcessing"
          >
            ✕ Hủy đơn hàng
          </button>
        </div>
        
        <div class="action-buttons-right">
          <button 
            v-if="selectedOrder.status !== 0 && selectedOrder.status !== 4"
            @click="handleNextStatus" 
            class="btn-success-action"
            :disabled="isProcessing"
          >
            {{ getNextActionText(selectedOrder.status) }} ➔
          </button>
        </div>
      </div>

      <div class="info-grid mb-6">
        <div class="info-card">
          <h3>Thông tin khách mua</h3>
          <p><strong>Tên khách hàng:</strong> {{ selectedOrder.customerName || 'Khách vãng lai' }}</p>
          <p><strong>Số điện thoại:</strong> {{ selectedOrder.customerPhone || '---' }}</p>
          <p><strong>Ghi chú đơn:</strong> {{ selectedOrder.note || 'Không có ghi chú' }}</p>
        </div>
        <div class="info-card">
          <h3>Địa chỉ nhận hàng</h3>
          <p><strong>Người nhận:</strong> {{ selectedOrder.consigneeName }}</p>
          <p><strong>Số điện thoại:</strong> {{ selectedOrder.consigneePhone }}</p>
          <p><strong>Địa chỉ chi tiết:</strong> {{ selectedOrder.consigneeAddress }}</p>
        </div>
        <div class="info-card">
  <h3>Nhân viên xử lý</h3>
  <p><strong>Người xác nhận:</strong> {{ selectedOrder.employeeId ? 'Nhân viên ID: ' + selectedOrder.employeeId : 'Chưa có nhân viên nào' }}</p>
</div>
      </div>

      <h3 class="font-bold text-lg mb-3">Danh sách sản phẩm mua</h3>
      <DataTable :isEmpty="orderDetails.length === 0" :colCount="4">
        <template #header>
          <th>Sản phẩm</th>
          <th>Đơn giá mua</th>
          <th>Số lượng</th>
          <th class="text-right">Thành tiền</th>
        </template>
        <template #body>
          <tr v-for="detail in orderDetails" :key="detail.id">
            <td class="flex items-center gap-3 py-2">
              <img :src="detail.productDetail?.image || 'https://placehold.co/50x50'" class="product-thumb" />
              <div>
                <div class="font-bold">{{ detail.productDetail?.name || 'Sản phẩm không tên' }}</div>
                <span class="text-xs text-muted">
                  Màu: {{ detail.productDetail?.color?.name }} | Size: {{ detail.productDetail?.size?.name }}
                </span>
              </div>
            </td>
            <td>{{ detail.price?.toLocaleString('vi-VN') }} ₫</td>
            <td>{{ detail.quantity }}</td>
            <td class="text-right font-bold text-danger">
              {{ (detail.price * detail.quantity).toLocaleString('vi-VN') }} ₫
            </td>
          </tr>
        </template>
      </DataTable>

      <div class="financial-summary">
        <div class="summary-line"><span>Tổng tiền hàng:</span> <span>{{ selectedOrder.totalMoney?.toLocaleString('vi-VN') }} ₫</span></div>
        <div class="summary-line"><span>Phí vận chuyển:</span> <span>+ {{ selectedOrder.shippingFee?.toLocaleString('vi-VN') }} ₫</span></div>
        <div class="summary-line"><span>Voucher giảm giá:</span> <span>- {{ selectedOrder.voucherDiscountValue?.toLocaleString('vi-VN') }} ₫</span></div>
        <div class="divider"></div>
        <div class="summary-line final"><span>Khách phải trả:</span> <span class="text-danger">{{ selectedOrder.finalAmount?.toLocaleString('vi-VN') }} ₫</span></div>
      </div>
    </div>

  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import DataTable from '@/components/common/DataTable.vue';
import orderApi from '@/api/orderApi';
import Swal from 'sweetalert2';

const orders = ref([]);
const selectedOrder = ref(null);
const orderDetails = ref([]);
const isProcessing = ref(false); // Chốt chặn chống Double Click

const loadOrders = async () => {
  try {
    const res = await orderApi.getAllOrders();
    orders.value = res.data || res;
  } catch (e) {
    Swal.fire('Lỗi!', 'Không thể tải danh sách hóa đơn', 'error');
  }
};

onMounted(loadOrders);

const viewOrderDetails = async (order) => {
  try {
    const res = await orderApi.getOrderDetails(order.id);
    orderDetails.value = res.data || res;
    selectedOrder.value = order;
  } catch (e) {
    Swal.fire('Lỗi!', 'Không thể lấy dữ liệu sản phẩm chi tiết', 'error');
  }
};

const closeDetail = () => {
  const index = orders.value.findIndex(o => o.id === selectedOrder.value.id);
  if (index !== -1) {
    orders.value[index] = { ...selectedOrder.value };
  }
  selectedOrder.value = null;
  selectedOrder.value = null;
  loadOrders(); // Tải lại danh sách để đồng bộ trạng thái mới ra ngoài bảng chính
};

// Tự động dịch chuyển trạng thái theo luồng tịnh tiến (currentStatus + 1)
const handleNextStatus = async () => {
  const nextStatus = selectedOrder.value.status + 1;
  
  const confirm = await Swal.fire({
    title: 'Xác nhận chuyển trạng thái?',
    text: `Hệ thống sẽ chuyển đơn hàng này sang luồng: "${getStatusName(nextStatus)}"`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#22c55e'
  });

  if (confirm.isConfirmed) {
    executeStatusUpdate(nextStatus);
  }
};

// Nghiệp vụ ép trạng thái về 0 (Hủy đơn)
const handleCancelOrder = async () => {
  const confirm = await Swal.fire({
    title: 'Bạn chắc chắn muốn HỦY đơn hàng?',
    text: 'Hành động này sẽ hoàn trả lại toàn bộ tồn kho cho các sản phẩm trong đơn!',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#ef4444'
  });

  if (confirm.isConfirmed) {
    executeStatusUpdate(0);
  }
};

// Hàm lõi thực thi gọi API
const executeStatusUpdate = async (statusTarget) => {
  isProcessing.value = true;
  try {
    const res = await orderApi.updateOrderStatus(selectedOrder.value.id, statusTarget);
    const updatedData = res.data || res;
    
    // Cập nhật nóng dữ liệu thực thể trên RAM giao diện để thanh Stepper nhảy ngay lập tức
    selectedOrder.value.status = statusTarget;
    
    Swal.fire({ icon: 'success', title: 'Cập nhật trạng thái thành công!', timer: 1200, showConfirmButton: false });
  } catch (error) {
    const errorMsg = error.response?.data?.message || 'Vui lòng kiểm tra lại quy tắc chuyển đổi trạng thái!';
    Swal.fire('Thất bại!', errorMsg, 'error');
  } finally {
    isProcessing.value = false;
  }
};

// Bộ từ điển dynamic text cho nút hành động tiếp theo
const getNextActionText = (status) => {
  const actions = {
    1: 'Xác nhận đơn & Chuẩn bị hàng',
    2: 'Bàn giao đơn vị vận chuyển (Giao hàng)',
    3: 'Xác nhận hoàn thành đơn hàng'
  };
  return actions[status] || 'Tiếp tục';
};

const getStatusName = (status) => {
  const map = { 0: 'Đã hủy', 1: 'Chờ xác nhận', 2: 'Đang chuẩn bị hàng', 3: 'Đang giao hàng', 4: 'Hoàn thành' };
  return map[status] || 'Không xác định';
};

const getStatusClass = (status) => {
  const map = { 0: 'status-cancelled', 1: 'status-pending', 2: 'status-shipping', 3: 'status-shipping', 4: 'status-success' };
  return map[status] || 'status-unknown';
};
</script>

<style scoped>
/* --- CSS DÀNH CHO THANH STEPPER TRẠNG THÁI (CHUẨN E-COMMERCE APP) --- */
.stepper-container { background: white; padding: 30px; border-radius: 8px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
.stepper { display: flex; align-items: center; justify-content: space-between; width: 100%; }
.step { display: flex; flex-direction: column; align-items: center; position: relative; flex: 1; color: #94a3b8; transition: 0.3s; }
.step .circle { width: 36px; height: 36px; border-radius: 50%; background: #e2e8f0; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 14px; margin-bottom: 8px; transition: 0.3s; color: #64748b; }
.step span { font-size: 13px; font-weight: 500; }
.line { flex: 1; height: 4px; background: #e2e8f0; transition: 0.3s; transform: translateY(-12px); }

/* Khi trạng thái kích hoạt (Active) */
.step.active { color: #2563eb; }
.step.active .circle { background: #2563eb; color: white; box-shadow: 0 0 12px rgba(37, 99, 235, 0.4); }
.line.fill { background: #2563eb; }

/* Luồng hiển thị khi Đơn hàng bị Hủy */
.stepper.cancelled .step.active.danger { color: #ef4444; }
.stepper.cancelled .step.active.danger .circle { background: #ef4444; color: white; box-shadow: 0 0 12px rgba(239, 68, 68, 0.4); }
.line.fill-danger { background: #ef4444; }

/* --- CSS BỘ NÚT ĐIỀU KHIỂN HÀNH ĐỘNG --- */
.status-action-bar { display: flex; justify-content: space-between; align-items: center; background: #f8fafc; padding: 15px 20px; border-radius: 8px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
.btn-success-action { background: #22c55e; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 14px; transition: 0.2s; box-shadow: 0 4px 6px -1px rgba(34, 197, 94, 0.2); }
.btn-success-action:hover { background: #16a34a; }
.btn-success-action:disabled { background: #cbd5e1; cursor: not-allowed; box-shadow: none; }
.btn-danger-outline { background: transparent; color: #ef4444; border: 1px solid #ef4444; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 14px; transition: 0.2s; }
.btn-danger-outline:hover { background: #fee2e2; }
.btn-danger-outline:disabled { color: #cbd5e1; border-color: #cbd5e1; cursor: not-allowed; background: transparent; }

/* Các thuộc tính cũ giữ nguyên */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;}
.header-title h2 { margin: 0; font-size: 24px; color: #1e293b; }
.text-muted { color: #64748b; margin-top: 5px; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500;}
.product-thumb { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #eee; }
.badge-code { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.font-bold { font-weight: 600; color: #1e293b; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.info-card { background: #f8fafc; padding: 20px; border-radius: 8px; border: 1px solid #e2e8f0; }
.info-card h3 { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 12px; border-bottom: 1px solid #cbd5e1; padding-bottom: 6px; }
.info-card p { font-size: 14px; margin-bottom: 8px; color: #475569; }
.financial-summary { width: 350px; margin-left: auto; margin-top: 25px; background: #f8fafc; padding: 15px; border-radius: 8px; border: 1px solid #e2e8f0; }
.summary-line { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; color: #475569; }
.summary-line.final { font-size: 18px; font-weight: bold; color: #0f172a; margin-top: 10px; }
.divider { height: 1px; background: #cbd5e1; margin: 10px 0; }
</style>