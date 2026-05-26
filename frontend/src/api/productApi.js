import axiosInstance from './axios';

const productApi = {
  getAllPublic: () => axiosInstance.get('/public/products'), // Gọi Controller công khai
  getAll: () => axiosInstance.get('/products'),             // Gọi Controller Admin
  create: (data) => axiosInstance.post('/products', data),
  update: (id, data) => axiosInstance.put(`/products/${id}`, data),
  delete: (id) => axiosInstance.delete(`/products/${id}`),
};

export default productApi;