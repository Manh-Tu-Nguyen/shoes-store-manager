import axiosInstance from './axios';

const productDetailApi = {
    getByProductId: (productId) => axiosInstance.get(`/product-details/product/${productId}`),
    create: (data) => axiosInstance.post('/product-details', data),
    update: (id, data) => axiosInstance.put(`/product-details/${id}`, data),
    delete: (id) => axiosInstance.delete(`/product-details/${id}`)
};

export default productDetailApi;