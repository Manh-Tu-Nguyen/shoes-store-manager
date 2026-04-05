import axiosInstance from './axios';

const brandApi = {
    getAll: () => axiosInstance.get('/brands'),
    getById: (id) => axiosInstance.get(`/brands/${id}`),
    create: (data) => axiosInstance.post('/brands', data),
    update: (id, data) => axiosInstance.put(`/brands/${id}`, data),
    delete: (id) => axiosInstance.delete(`/brands/${id}`)
};

export default brandApi;