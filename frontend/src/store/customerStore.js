import { defineStore } from 'pinia';
import customerApi from '@/api/customerApi';
import addressApi from '@/api/addressApi';

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    customers: [],
    addresses: [], // State lưu danh sách địa chỉ của khách hàng đang chọn
    loading: false,
  }),

  actions: {
    // --- QUẢN LÝ KHÁCH HÀNG ---
    async fetchCustomers() {
      this.loading = true;
      try {
        const res = await customerApi.getAll();
        this.customers = res?.data?.data || res?.data || [];
      } finally { this.loading = false; }
    },

    async createCustomer(data) { await customerApi.create(data); },
    async updateCustomer(id, data) { await customerApi.update(id, data); },
    async deleteCustomer(id) { await customerApi.delete(id); },

    // --- QUẢN LÝ ĐỊA CHỈ (ADDRESS) ---
    async fetchAddressesByCustomerId(customerId) {
      this.loading = true;
      try {
        const res = await addressApi.getByCustomerId(customerId);
        this.addresses = res?.data?.data || res?.data || [];
      } catch (error) {
        console.error("Lỗi tải địa chỉ:", error);
        this.addresses = [];
      } finally { this.loading = false; }
    },

    async createAddress(formData) {
      try {
        await addressApi.create(formData);
      } catch (error) { throw error; }
    },

    async updateAddress(id, formData) {
      try {
        await addressApi.update(id, formData);
      } catch (error) { throw error; }
    },

    async deleteAddress(id) {
      try {
        await addressApi.delete(id);
      } catch (error) { throw error; }
    }
  }
});