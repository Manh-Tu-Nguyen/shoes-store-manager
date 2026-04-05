import { defineStore } from 'pinia';
import voucherApi from '@/api/voucher/voucherApi';

export const useVoucherStore = defineStore('voucher', {
  state: () => ({
    vouchers: [],
    isLoading: false,
  }),
  actions: {
    async fetchVouchers() {
      this.isLoading = true;
      try {
        const res = await voucherApi.getAll();
        if (res.success) this.vouchers = res.data;
      } finally {
        this.isLoading = false;
      }
    }
  }
});