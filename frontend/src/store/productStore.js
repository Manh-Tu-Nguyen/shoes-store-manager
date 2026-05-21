import { defineStore } from 'pinia';
import productApi from '@/api/productApi';
import productDetailApi from '@/api/productDetailApi';
import axiosInstance from '@/api/axios';

export const useProductStore = defineStore('product', {
  state: () => ({
    // STATE CỦA SẢN PHẨM CHA
    products: [],
    
    // STATE CỦA BIẾN THỂ (SKU) - BẮT BUỘC PHẢI CÓ ĐỂ KHÔNG BỊ SẬP TRANG
    productDetails: [], 
    
    // DỮ LIỆU CÁC DROP-DOWN TÙY CHỌN
    brands: [], 
    categories: [], 
    origins: [],
    colors: [], 
    sizes: [],
    
    loading: false
  }),
  getters: {
    activeProducts: (state) => {
      return state.products.filter(p => p.status === true);
    }
  },
  actions: {
    // 1. Tải toàn bộ thuộc tính dùng chung
    async fetchAllAttributes() {
      try {
        const [brandRes, catRes, originRes, colorRes, sizeRes] = await Promise.all([
          axiosInstance.get('/brands'),
          axiosInstance.get('/categories'),
          axiosInstance.get('/origins'),
          axiosInstance.get('/colors'),
          axiosInstance.get('/sizes')
        ]);
        
        // CÁCH GÁN BẤT TỬ: Tự động dò tìm đúng mảng dữ liệu
        this.brands = brandRes?.data?.data || brandRes?.data || [];
        this.categories = catRes?.data?.data || catRes?.data || [];
        this.origins = originRes?.data?.data || originRes?.data || [];
        this.colors = colorRes?.data?.data || colorRes?.data || [];
        this.sizes = sizeRes?.data?.data || sizeRes?.data || [];
      } catch (error) {
        console.error("Lỗi khi tải thuộc tính:", error);
      }
    },

    // ==========================================
    // MODULE 1: SẢN PHẨM CHA (PRODUCT)
    // ==========================================
    async fetchProducts() {
      this.loading = true;
      try {
        // Thay vì gọi getAllProducts, hãy gọi getPublicProducts
        const response = await productApi.getPublicProducts();
        this.products = response?.data?.data || response?.data || []; 
      } catch (error) {
        console.error("Lỗi tải danh sách sản phẩm:", error);
      } finally {
        this.loading = false;
      }
    },

    async createProduct(formData) {
      await productApi.createProduct(formData);
    },

    async updateProduct(id, formData) {
      await productApi.updateProduct(id, formData);
    },

    async deleteProduct(id) {
      await productApi.deleteProduct(id);
    },

    // ==========================================
    // MODULE 2: BIẾN THỂ SẢN PHẨM (PRODUCT DETAIL/SKU)
    // ==========================================
    async fetchProductVariants(productId) {
      this.loading = true;
      try {
        // GỌI API PUBLIC thay vì API của Admin
        const response = await productDetailApi.getPublicDetailsByProductId(productId);
        this.productDetails = response?.data?.data || response?.data || [];
      } catch (error) {
        console.error("Lỗi khi tải danh sách biến thể:", error);
      } finally {
        this.loading = false;
      }
    },

    async createProductDetail(formData) {
      try {
        const response = await productDetailApi.createDetail(formData);
        return response.data;
      } catch (error) {
        throw error;
      }
    },

    async updateProductDetail(id, formData) {
      try {
        const response = await productDetailApi.updateDetail(id, formData);
        return response.data;
      } catch (error) {
        throw error;
      }
    },

    async deleteProductDetail(id) {
      try {
        const response = await productDetailApi.deleteDetail(id);
        return response.data;
      } catch (error) {
        throw error;
      }
    }
  }
});