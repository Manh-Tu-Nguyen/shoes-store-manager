import axiosInstance from './axios';

const productDetailApi = {
  // === API BẢO MẬT (Dành cho Admin) ===
  getAllDetails: () => axiosInstance.get('/product-details'),
  getDetailById: (id) => axiosInstance.get(`/product-details/${id}`),
  getDetailsByAdmin: (productId) => axiosInstance.get(`/product-details/product/${productId}`),
  createDetail: (payload) => axiosInstance.post('/product-details', payload),
  updateDetail: (id, payload) => axiosInstance.put(`/product-details/${id}`, payload),
  deleteDetail: (id) => axiosInstance.delete(`/product-details/${id}`),

  // === API CÔNG KHAI (Dành cho Client trang chủ) ===
  getPublicDetailsByProductId: (productId) => axiosInstance.get(`/public/product-details/product/${productId}`)
};

export default productDetailApi;