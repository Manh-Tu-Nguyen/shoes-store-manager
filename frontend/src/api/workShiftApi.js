import axiosInstance from './axios';

const workShiftApi = {
  getAll: () => axiosInstance.get('/work-shifts'),
  getById: (id) => axiosInstance.get(`/work-shifts/${id}`),
};

export default workShiftApi;