import axiosInstance from './axios';

const addressApi = {
  // Lấy danh sách địa chỉ theo ID khách hàng
  getByCustomerId: (customerId) => axiosInstance.get(`/addresses/customer/${customerId}`),
  getById: (id) => axiosInstance.get(`/addresses/${id}`),
  create: (data) => axiosInstance.post('/addresses', data),
  update: (id, data) => axiosInstance.put(`/addresses/${id}`, data),
  delete: (id) => axiosInstance.delete(`/addresses/${id}`),
};

export default addressApi;