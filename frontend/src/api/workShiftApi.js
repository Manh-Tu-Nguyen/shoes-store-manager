import axiosInstance from './axios';

const workShiftApi = {
  getAll: () => axiosInstance.get('/work-shifts'),
  getById: (id) => axiosInstance.get(`/work-shifts/${id}`),
  create: (data) => axiosInstance.post('/work-shifts', data),
  update: (id, data) => axiosInstance.put(`/work-shifts/${id}`, data),
  delete: (id) => axiosInstance.delete(`/work-shifts/${id}`),
};

export default workShiftApi;