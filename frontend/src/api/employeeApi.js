import axiosInstance from './axios';

const employeeApi = {
  getAll: () => axiosInstance.get('/employees'),
  getById: (id) => axiosInstance.get(`/employees/${id}`),
  create: (data) => axiosInstance.post('/employees', data),
  update: (id, data) => axiosInstance.put(`/employees/${id}`, data),
  delete: (id) => axiosInstance.delete(`/employees/${id}`),
};

export default employeeApi;