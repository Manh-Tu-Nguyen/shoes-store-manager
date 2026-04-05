import axiosInstance from './axios';

const originApi = {
    getAll: () => axiosInstance.get('/Origins'),
    getById: (id) => axiosInstance.get(`/Origins/${id}`),
    create: (data) => axiosInstance.post('/Origins', data),
    update: (id, data) => axiosInstance.put(`/Origins/${id}`, data),
    delete: (id) => axiosInstance.delete(`/Origins/${id}`)
};

export default originApi;