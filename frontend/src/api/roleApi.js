import axiosInstance from './axios';

const roleApi = {
  getAll: () => axiosInstance.get('/roles'),
  getById: (id) => axiosInstance.get(`/roles/${id}`),
  create: (data) => axiosInstance.post('/roles', data),
  update: (id, data) => axiosInstance.put(`/roles/${id}`, data),
  delete: (id) => axiosInstance.delete(`/roles/${id}`),
};

export default roleApi;