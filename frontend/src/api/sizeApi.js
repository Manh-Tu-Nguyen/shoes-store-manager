import axiosInstance from './axios';

const sizeApi = {
    getAll: () => axiosInstance.get('/sizes'),
  getById: (id) => axiosInstance.get(`/sizes/${id}`),
  create: (payload) => axiosInstance.post('/sizes', payload),
  update: (id, payload) => axiosInstance.put(`/sizes/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/sizes/${id}`),
};

export default sizeApi;