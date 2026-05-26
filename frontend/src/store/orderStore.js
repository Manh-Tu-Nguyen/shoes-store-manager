import { defineStore } from 'pinia';
import orderApi from '@/api/orderApi';
// BỔ SUNG CHÍ MẠNG: Import authStore để phục vụ cơ chế rẽ nhánh tạo đơn
import { useAuthStore } from '@/store/authStore'; 

export const useOrderStore = defineStore('order', {
  state: () => ({
    // Chứa các món hàng được chọn từ giỏ hàng hoặc từ nút "Mua ngay" truyền sang
    checkoutItems: [], 
    
    // Form thông tin khách nhận hàng
    shippingInfo: {
      consigneeName: '',
      consigneePhone: '',
      consigneeAddress: ''
    },
    
    paymentMethod: 'COD', // Mặc định: Thanh toán khi nhận hàng
    loading: false,
  }),

  getters: {
    totalQuantity: (state) => state.checkoutItems.reduce((sum, item) => sum + item.quantity, 0),
    totalMoney: (state) => state.checkoutItems.reduce((sum, item) => sum + (item.price * item.quantity), 0),
    shippingFee: () => 30000, // Tạm thời hardcode phí vận chuyển 30k
    finalAmount() {
      return this.totalMoney + this.shippingFee;
    }
  },

  actions: {
    /**
     * Hàm nhận dữ liệu bàn giao từ trang Giỏ hàng hoặc Chi tiết sản phẩm
     */
    setCheckoutItems(items) {
      this.checkoutItems = items;
    },

    /**
     * Logic tạo đơn hàng tổng hợp: Tự động phân luồng Đăng nhập / Ẩn danh
     */
    async submitOrder() {
      this.loading = true;
      const authStore = useAuthStore(); // Khởi tạo instance an toàn sau khi đã import

      try {
        // Đóng gói Payload khớp 100% với cấu trúc class "CreateOrderRequest" của Spring Boot
        const payload = {
          order: {
            consigneeName: this.shippingInfo.consigneeName,
            consigneePhone: this.shippingInfo.consigneePhone,
            consigneeAddress: this.shippingInfo.consigneeAddress,
            totalMoney: this.totalMoney,
            totalQuantity: this.totalQuantity,
            shippingFee: this.shippingFee,
            finalAmount: this.finalAmount,
            status: 1 // 1: Trạng thái chờ xác nhận mặc định của đơn Online
          },
          details: this.checkoutItems.map(item => ({
            productDetailId: item.productDetailId,
            price: item.price, 
            quantity: item.quantity
          }))
        };

        let response;
        
        // KIẾN TRÚC RẼ NHÁNH ĐIỀU PHỐI (Payload Rerouting)
        if (authStore.isLoggedIn) {
          // Khách đã login: Đẩy vào API bảo mật, Spring Boot tự lấy ID qua JWT Token
          response = await orderApi.createOrderSecure(payload);
        } else {
          // Khách ẩn danh: Đẩy vào API Public, tạo hóa đơn không có ID khách hàng
          response = await orderApi.createOrder(payload);
        }

        return response.data || response; 
      } catch (error) {
        console.error("Lỗi hệ thống khi tạo đơn hàng:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});