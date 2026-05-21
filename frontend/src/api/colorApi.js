import axiosInstance from './axios';

const colorApi = {
    getAll: () => axiosInstance.get('/colors'),
  getById: (id) => axiosInstance.get(`/colors/${id}`),
  create: (payload) => axiosInstance.post('/colors', payload),
  update: (id, payload) => axiosInstance.put(`/colors/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/colors/${id}`),
};

export default colorApi;