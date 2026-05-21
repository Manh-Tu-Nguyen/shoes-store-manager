import axiosInstance from './axios';

const addressApi = {
  getByCustomerId: (customerId) => axiosInstance.get(`/addresses/customer/${customerId}`),
  create: (payload) => axiosInstance.post('/addresses', payload),
  update: (id, payload) => axiosInstance.put(`/addresses/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/addresses/${id}`),
};

export default addressApi;