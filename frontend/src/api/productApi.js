import axiosInstance from './axios';

const productApi = {
    getAll: () => axiosInstance.get('/products'),
    create: (data) => axiosInstance.post('/products', data),
    
    // BỔ SUNG HÀM UPDATE
    update: (id, data) => axiosInstance.put(`/products/${id}`, data),
    
    delete: (id) => axiosInstance.delete(`/products/${id}`)
};

export default productApi;