// src/store/orderStore.js
import { defineStore } from 'pinia';
import orderApi from '@/api/orderApi';

export const useOrderStore = defineStore('order', {
  state: () => ({
    // Vì chưa có Giỏ hàng, ta giả lập 1 mảng chứa các món khách vừa bấm "Mua ngay"
    checkoutItems: [], 
    
    // Form thông tin khách hàng
    shippingInfo: {
      consigneeName: '',
      consigneePhone: '',
      consigneeAddress: ''
    },
    
    paymentMethod: 'COD', // Mặc định là Thanh toán khi nhận hàng
    loading: false,
  }),

  getters: {
    totalQuantity: (state) => state.checkoutItems.reduce((sum, item) => sum + item.quantity, 0),
    totalMoney: (state) => state.checkoutItems.reduce((sum, item) => sum + (item.price * item.quantity), 0),
    shippingFee: () => 30000, // Hardcode 30k phí ship tạm thời
    finalAmount() {
      return this.totalMoney + this.shippingFee;
    }
  },

  actions: {
    // Hàm này dùng để trang ProductDetail đẩy dữ liệu sang trước khi chuyển trang
    setCheckoutItems(items) {
      this.checkoutItems = items;
    },

    async submitOrder() {
      this.loading = true;
      try {
        // ĐÓNG GÓI PAYLOAD CHUẨN XÁC VỚI WRAPPER "CreateOrderRequest" CỦA JAVA
        const payload = {
          order: {
            consigneeName: this.shippingInfo.consigneeName,
            consigneePhone: this.shippingInfo.consigneePhone,
            consigneeAddress: this.shippingInfo.consigneeAddress,
            totalMoney: this.totalMoney,
            totalQuantity: this.totalQuantity,
            shippingFee: this.shippingFee,
            finalAmount: this.finalAmount,
            status: 1 // 1: Chờ xác nhận (Đơn Online)
          },
          details: this.checkoutItems.map(item => ({
            productDetailId: item.productDetailId,
            price: item.price, // Gửi lên để qua vòng Validate @NotNull của DTO, backend sẽ tự lấy giá chuẩn từ DB đè lên để bảo mật
            quantity: item.quantity
          }))
        };

        const response = await orderApi.createOrder(payload);
        return response.data; // Trả data về cho Vue xử lý chuyển hướng
      } catch (error) {
        console.error("Lỗi khi tạo đơn hàng:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});