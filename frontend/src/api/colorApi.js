import axiosInstance from './axios';

const colorApi = {
    getAll: () => axiosInstance.get('/Colors'),
    getById: (id) => axiosInstance.get(`/Colors/${id}`),
    create: (data) => axiosInstance.post('/Colors', data),
    update: (id, data) => axiosInstance.put(`/Colors/${id}`, data),
    delete: (id) => axiosInstance.delete(`/Colors/${id}`)
};

export default colorApi;