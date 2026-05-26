import { defineStore } from 'pinia';
import cartApi from '@/api/cartApi';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],         
    selectedItemIds: [],   
    loading: false
  }),

  getters: {
    totalItemsCount: (state) => state.cartItems.length,
    selectedCount: (state) => state.cartItems.filter(item => state.selectedItemIds.includes(item.id)).length,
    totalBasePrice: (state) => {
      return state.cartItems
        .filter(item => state.selectedItemIds.includes(item.id))
        .reduce((sum, item) => sum + ((item.productDetail?.price || 0) * item.quantity), 0);
    },
    isSelectAll: (state) => state.cartItems.length > 0 && state.selectedItemIds.length === state.cartItems.length
  },

  actions: {
    /**
     * TẢI GIỎ HÀNG (Dành cho thành viên đã login)
     */
    async fetchCart() {
      this.loading = true;
      try {
        const response = await cartApi.getCartByCustomerId();
        const resData = response?.data?.data || response?.data || response;
        this.cartItems = Array.isArray(resData) ? resData : [];
      } catch (error) {
        console.error("Lỗi khi tải giỏ hàng từ Server:", error);
        this.cartItems = [];
      } {
        this.loading = false;
      }
    },

    /**
     * THÊM VÀO GIỎ HÀNG: SỬA LỖI CHÍ MẠNG KHI ĐĂNG NHẬP
     */
    async addToCart(productDetail, quantity = 1, isLoggedIn = false) {
      if (!isLoggedIn) {
        // --- LUỒNG 1: KHÁCH CHƯA ĐĂNG NHẬP (LƯU LOCALSTORAGE) ---
        const existingItem = this.cartItems.find(item => item.productDetailId === productDetail.id);
        if (existingItem) {
          existingItem.quantity += quantity;
        } else {
          this.cartItems.push({
            id: Date.now(), // Tạo ID tạm thời để làm key render loop
            productDetailId: productDetail.id,
            quantity: quantity,
            productDetail: productDetail
          });
        }
        this.saveCartToLocalStorage();
      } else {
        // --- LUỒNG 2: KHÁCH ĐÃ ĐĂNG NHẬP (LƯU VÀO DATABASE SQL SERVER) ---
        this.loading = true;
        try {
          const payload = {
            productDetailId: productDetail.id,
            quantity: quantity
          };
          
          // 1. Bắn lệnh ghi trực tiếp xuống cơ sở dữ liệu qua API
          await cartApi.addToCart(payload);
          
          // 2. Ép hệ thống gọi lại hàm fetchCart để cập nhật mảng dữ liệu chuẩn từ DB về RAM
          await this.fetchCart();
          
        } catch (error) {
          console.error("Lỗi hạ tầng khi thêm sản phẩm vào DB Giỏ hàng:", error);
          // Ném lỗi ra ngoài để hàm xử lý sự kiện tại Component (ProductDetail.vue) biết đường chặn Toast thành công
          throw error; 
        } finally {
          this.loading = false;
        }
      }
    },

    loadCartFromLocalStorage() {
      const localData = localStorage.getItem('guest_cart');
      this.cartItems = localData ? JSON.parse(localData) : [];
      this.selectedItemIds = [];
    },

    saveCartToLocalStorage() {
      localStorage.setItem('guest_cart', JSON.stringify(this.cartItems));
    },

    toggleSelectAll(checked) {
      this.selectedItemIds = checked ? this.cartItems.map(item => item.id) : [];
    },

    updateQuantity(itemId, delta, isLoggedIn = false) {
      const item = this.cartItems.find(i => i.id === itemId);
      if (item) {
        const newQty = item.quantity + delta;
        if (newQty >= 1) {
          item.quantity = newQty;
          if (!isLoggedIn) this.saveCartToLocalStorage();
        }
      }
    },

    removeItem(itemId, isLoggedIn = false) {
      this.cartItems = this.cartItems.filter(item => item.id !== itemId);
      this.selectedItemIds = this.selectedItemIds.filter(id => id !== itemId);
      if (!isLoggedIn) this.saveCartToLocalStorage();
    },

    clearCart(isLoggedIn = false) {
      this.cartItems = [];
      this.selectedItemIds = [];
      if (!isLoggedIn) localStorage.removeItem('guest_cart');
    }
  }
});