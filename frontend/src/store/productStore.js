import { defineStore } from 'pinia';
import productApi from '@/api/productApi';
import productDetailApi from '@/api/productDetailApi';
import brandApi from '@/api/brandApi';
import categoryApi from '@/api/categoryApi';
import colorApi from '@/api/colorApi';
import sizeApi from '@/api/sizeApi';
import originApi from '@/api/originApi';

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    productDetails: [], 
    brands: [],
    categories: [],
    colors: [],
    sizes: [],
    origins: [],
    isLoading: false,
    error: null
  }),

  actions: {
    // ==========================================
    // MODULE 1: QUẢN LÝ SẢN PHẨM CHÍNH (PRODUCT)
    // ==========================================
    async fetchProducts() {
      this.isLoading = true;
      try {
        const res = await productApi.getAll();
        const actualData = res.data?.data ? res.data.data : res.data;
        if (actualData) {
          this.products = actualData;
        }
      } catch (err) {
        console.error("Fetch Products Error:", err);
      } finally {
        this.isLoading = false;
      }
    },

    async createProduct(productData) {
      this.isLoading = true;
      try {
        const res = await productApi.create(productData);
        if (res.data && res.data.success) {
          await this.fetchProducts();
          return res.data;
        }
      } catch (err) {
        console.error("Lỗi khi thêm sản phẩm:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    async updateProduct(id, productData) {
      this.isLoading = true;
      try {
        const res = await productApi.update(id, productData);
        if (res.data && res.data.success) {
          await this.fetchProducts();
          return res.data;
        }
      } catch (err) {
        console.error("Lỗi khi cập nhật sản phẩm:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    async deleteProduct(id) {
      this.isLoading = true;
      try {
        const res = await productApi.delete(id);
        if (res.data && res.data.success) {
          await this.fetchProducts();
          return res.data;
        }
      } catch (err) {
        console.error("Lỗi khi xóa sản phẩm:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    // ==========================================
    // MODULE 2: QUẢN LÝ THUỘC TÍNH (ATTRIBUTES)
    // ==========================================
    async fetchAllAttributes() {
      this.isLoading = true;
      try {
        const [b, c, cl, s, o] = await Promise.all([
          brandApi.getAll(),
          categoryApi.getAll(),
          colorApi.getAll(),
          sizeApi.getAll(),
          originApi.getAll(),
        ]);

        const extractList = (res) => {
          if (res?.data?.data && Array.isArray(res.data.data)) return res.data.data;
          if (res?.data && Array.isArray(res.data)) return res.data;
          return [];
        };

        this.brands = extractList(b);
        this.categories = extractList(c);
        this.colors = extractList(cl);
        this.sizes = extractList(s);
        this.origins = extractList(o);
        
      } catch (err) {
        console.error("Store Attribute Error:", err);
      } finally {
        this.isLoading = false;
      }
    },

    // ==========================================
    // MODULE 3: QUẢN LÝ BIẾN THỂ (PRODUCT DETAIL)
    // ==========================================
    async fetchProductVariants(productId) {
      this.isLoading = true;
      try {
        const res = await productDetailApi.getByProductId(productId);
        // Lấy dữ liệu an toàn tương tự như product
        const actualData = res.data?.data ? res.data.data : res.data;
        if (actualData) {
          this.productDetails = actualData;
        }
      } catch (err) {
        console.error("Fetch Variants Error:", err);
      } finally {
        this.isLoading = false;
      }
    },

    // 7. Thêm biến thể mới
    async createProductDetail(data) {
      this.isLoading = true;
      try {
        const res = await productDetailApi.create(data);
        return res.data;
      } catch (err) {
        console.error("Lỗi thêm biến thể:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    // 8. Cập nhật biến thể
    async updateProductDetail(id, data) {
      this.isLoading = true;
      try {
        const res = await productDetailApi.update(id, data);
        return res.data;
      } catch (err) {
        console.error("Lỗi sửa biến thể:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    },

    // 9. Xóa biến thể
    async deleteProductDetail(id) {
      this.isLoading = true;
      try {
        const res = await productDetailApi.delete(id);
        return res.data;
      } catch (err) {
        console.error("Lỗi xóa biến thể:", err);
        throw err;
      } finally {
        this.isLoading = false;
      }
    }
  }
});