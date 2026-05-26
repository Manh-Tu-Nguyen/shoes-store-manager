import axiosInstance from './axios';

const orderApi = {
  // 1. PHÂN HỆ POS: QUẢN LÝ HÓA ĐƠN NHÁP TẠI QUẦY
  getPosDraftOrders: () => axiosInstance.get('/pos/orders/drafts'), 
  createDraftOrder: (payload) => axiosInstance.post('/pos/orders/draft', payload),
  deleteDraftOrder: (orderId) => axiosInstance.delete(`/pos/orders/draft/${orderId}`),
  
  // 2. PHÂN HỆ POS: THAO TÁC TRÊN GIỎ HÀNG CHI TIẾT
  getOrderDetails: (orderId) => axiosInstance.get(`/public/orders/${orderId}/details`),
  addOrUpdatePosItem: (orderId, payload) => axiosInstance.patch(`/pos/orders/${orderId}/items`, payload),
  removePosItem: (orderId, detailId) => axiosInstance.delete(`/pos/orders/${orderId}/items/${detailId}`),
  
  // 3. PHÂN HỆ POS: KẾT THÚC GIAO DỊCH
  posCheckout: (orderId, payload) => axiosInstance.post(`/pos/orders/${orderId}/checkout`, payload),
  
  // 4. PHÂN HỆ ONLINE TRỰC TUYẾN
  createOrder: (payload) => axiosInstance.post('/public/orders', payload), 
  createOrderSecure: (payload) => axiosInstance.post('/customer/orders', payload),
  getAllOrders: () => axiosInstance.get('/public/orders'),
  updateOrderStatus: (orderId, newStatus) => axiosInstance.patch(`/public/orders/${orderId}/status?newStatus=${newStatus}`),

  posCheckout: (orderId, payload) => axiosInstance.post(`/pos/orders/${orderId}/checkout`, payload),

  // BỔ SUNG THÊM HÀM GỌI VNPAY
  createVnPayUrl: (amount, orderCode) => {
    return axiosInstance.get(`/payment/vnpay/create-url?amount=${amount}&orderCode=${orderCode}`);
  },
};

export default orderApi;