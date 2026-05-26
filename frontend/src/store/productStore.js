import { defineStore } from 'pinia';
import productApi from '@/api/productApi';
import productDetailApi from '@/api/productDetailApi';
import axiosInstance from '@/api/axios';

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],           // Dữ liệu thô từ API
    productDetails: [],     // Biến thể (SKU)
    brands: [], 
    categories: [], 
    origins: [],
    colors: [], 
    sizes: [],
    loading: false
  }),

  getters: {
    // Getter chỉ dùng để đọc, không gán trực tiếp dữ liệu vào đây
    activeProducts: (state) => {
      return state.products.filter(p => p.status === true);
    }
  },

  actions: {
    // 1. DỮ LIỆU DÙNG CHUNG
    async fetchAllAttributes() {
      try {
        const [brandRes, catRes, originRes, colorRes, sizeRes] = await Promise.all([
          axiosInstance.get('/brands'),
          axiosInstance.get('/categories'),
          axiosInstance.get('/origins'),
          axiosInstance.get('/colors'),
          axiosInstance.get('/sizes')
        ]);
        this.brands = brandRes?.data?.data || [];
        this.categories = catRes?.data?.data || [];
        this.origins = originRes?.data?.data || [];
        this.colors = colorRes?.data?.data || [];
        this.sizes = sizeRes?.data?.data || [];
      } catch (error) {
        console.error("Lỗi tải thuộc tính:", error);
      }
    },

    // 2. SẢN PHẨM (PUBLIC - Trang chủ)
    async fetchProducts() {
      this.loading = true;
      try {
        // GỌI HÀM PUBLIC (Không cần token)
        const res = await productApi.getAllPublic(); 
        this.products = res?.data?.data || res?.data || [];
      } catch (err) {
        console.error("Lỗi tải sản phẩm công khai:", err);
      } finally {
        this.loading = false;
      }
    },

    // 3. SẢN PHẨM (ADMIN - Trang quản trị)
    async fetchAdminProducts() {
      this.loading = true;
      try {
        const res = await productApi.getAll(); // Gọi API có Token
        this.products = res?.data?.data || res?.data || [];
      } catch (err) {
        console.error("Lỗi tải sản phẩm admin:", err);
      } finally {
        this.loading = false;
      }
    },

    // 4. BIẾN THỂ (SKU)
    async fetchProductVariants(productId) {
      this.loading = true;
      try {
        // Dùng hàm Admin
        const response = await productDetailApi.getDetailsByAdmin(productId);
        this.productDetails = response?.data?.data || response?.data || [];
      } catch (error) {
        console.error("Lỗi tải biến thể:", error);
        this.productDetails = [];
      } finally {
        this.loading = false;
      }
    },

    // 5. CRUD CƠ BẢN
    async createProduct(formData) {
      return await productApi.create(formData);
    },
    async updateProduct(id, formData) {
      return await productApi.update(id, formData);
    },
    async deleteProduct(id) {
      return await productApi.delete(id);
    },
    async createProductDetail(formData) {
      return await productDetailApi.createDetail(formData);
    },
    async updateProductDetail(id, formData) {
      return await productDetailApi.updateDetail(id, formData);
    },
    async deleteProductDetail(id) {
      return await productDetailApi.deleteDetail(id);
    }
  }
});