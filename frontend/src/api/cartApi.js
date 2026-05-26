import axiosInstance from './axios';

const cartApi = {

  getCartByCustomerId: () => {
    return axiosInstance.get('/cart/my-cart');
  },

  addToCart: (payload) => {
    return axiosInstance.post('/cart/add', payload);
  }
};

export default cartApi;