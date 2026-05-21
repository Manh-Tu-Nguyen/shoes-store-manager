import axiosInstance from './axios';

const productApi = {
  getAllProducts: () => axiosInstance.get('/products'), 
  // API dành cho Khách hàng (Không cần Token)
  getPublicProducts: () => axiosInstance.get('/public/products'),
  getProductById: (id) => axiosInstance.get(`/products/${id}`),
  createProduct: (payload) => axiosInstance.post('/products', payload),
  updateProduct: (id, payload) => axiosInstance.put(`/products/${id}`, payload),
  deleteProduct: (id) => axiosInstance.delete(`/products/${id}`),
};

export default productApi;