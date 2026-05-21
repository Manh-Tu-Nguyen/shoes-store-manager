import axiosInstance from './axios';

const customerApi = {
  getAll: () => axiosInstance.get('/customers'),
  getById: (id) => axiosInstance.get(`/customers/${id}`),
  create: (payload) => axiosInstance.post('/customers', payload),
  update: (id, payload) => axiosInstance.put(`/customers/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/customers/${id}`),
};

export default customerApi;