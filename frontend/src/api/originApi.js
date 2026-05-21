import axiosInstance from './axios';

const originApi = {
    getAll: () => axiosInstance.get('/origins'),
  getById: (id) => axiosInstance.get(`/origins/${id}`),
  create: (payload) => axiosInstance.post('/origins', payload),
  update: (id, payload) => axiosInstance.put(`/origins/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/origins/${id}`),
};

export default originApi;