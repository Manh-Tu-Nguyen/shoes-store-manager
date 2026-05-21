import axiosInstance from './axios';

const orderApi = {
  // Bỏ chữ /api ở đầu các đường dẫn này
  createOrder: (payload) => axiosInstance.post('/public/orders', payload), 
  createOrderSecure: (payload) => axiosInstance.post('/customer/orders', payload),
  getAllOrders: () => axiosInstance.get('/public/orders'),
  getOrderDetails: (orderId) => axiosInstance.get(`/public/orders/${orderId}/details`),
  updateOrderStatus: (orderId, newStatus) => axiosInstance.patch(`/public/orders/${orderId}/status?newStatus=${newStatus}`),
  
  // SỬA CÁC ĐƯỜNG DẪN POS NÀY:
  createDraftOrder: () => axiosInstance.post('/pos/orders/draft'),
  addOrUpdatePosItem: (orderId, payload) => axiosInstance.patch(`/pos/orders/${orderId}/items`, payload),
  posCheckout: (orderId, payload) => axiosInstance.post(`/pos/orders/${orderId}/checkout`, payload),
  getPosDraftOrders: () => axiosInstance.get('/pos/orders/drafts'), 
};

export default orderApi;