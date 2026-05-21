import axiosInstance from './axios';

const brandApi = {
    getAll: () => axiosInstance.get('/brands'),
  getById: (id) => axiosInstance.get(`/brands/${id}`),
  create: (payload) => axiosInstance.post('/brands', payload),
  update: (id, payload) => axiosInstance.put(`/brands/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/brands/${id}`),
};

export default brandApi;