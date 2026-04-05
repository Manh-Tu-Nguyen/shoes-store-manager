import axiosInstance from './axios';

const customerApi = {
  getAll: () => axiosInstance.get('/customers'),
  getById: (id) => axiosInstance.get(`/customers/${id}`),
  create: (data) => axiosInstance.post('/customers', data),
  update: (id, data) => axiosInstance.put(`/customers/${id}`, data),
  delete: (id) => axiosInstance.delete(`/customers/${id}`),
};

export default customerApi;