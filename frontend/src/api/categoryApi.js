import axiosInstance from './axios';

const categoryApi = {
    getAll: () => axiosInstance.get('/categories'),
  getById: (id) => axiosInstance.get(`/categories/${id}`),
  create: (payload) => axiosInstance.post('/categories', payload),
  update: (id, payload) => axiosInstance.put(`/categories/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/categories/${id}`),
};

export default categoryApi;