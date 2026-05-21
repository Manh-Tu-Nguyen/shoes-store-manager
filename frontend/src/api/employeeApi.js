import axiosInstance from './axios';

const employeeApi = {
  getAll: () => axiosInstance.get('/admin/employees'),
  getById: (id) => axiosInstance.get(`/admin/employees/${id}`),
  create: (payload) => axiosInstance.post('/admin/employees', payload),
  update: (id, payload) => axiosInstance.put(`/admin/employees/${id}`, payload),
  delete: (id) => axiosInstance.delete(`/admin/employees/${id}`),
  getRoles: () => axiosInstance.get('/admin/roles'),
  getWorkShifts: () => axiosInstance.get('/api/work-shifts'),
};

export default employeeApi;