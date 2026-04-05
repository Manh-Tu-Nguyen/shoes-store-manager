import axiosInstance from './axios';

const sizeApi = {
    getAll: () => axiosInstance.get('/Sizes'),
    getById: (id) => axiosInstance.get(`/Sizes/${id}`),
    create: (data) => axiosInstance.post('/Sizes', data),
    update: (id, data) => axiosInstance.put(`/Sizes/${id}`, data),
    delete: (id) => axiosInstance.delete(`/Sizes/${id}`)
};

export default sizeApi;