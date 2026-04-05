import axiosInstance from './axios';

const categoryApi = {
    getAll: () => axiosInstance.get('/Categories'),
    getById: (id) => axiosInstance.get(`/Categories/${id}`),
    create: (data) => axiosInstance.post('/Categories', data),
    update: (id, data) => axiosInstance.put(`/Categories/${id}`, data),
    delete: (id) => axiosInstance.delete(`/Categories/${id}`)
};

export default categoryApi;