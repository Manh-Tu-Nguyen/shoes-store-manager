import axiosInstance from './axios';

const roleApi = {
  getAll: () => axiosInstance.get('/roles'),
  getById: (id) => axiosInstance.get(`/roles/${id}`),
};

export default roleApi;