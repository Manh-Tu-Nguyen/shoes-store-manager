import { defineStore } from 'pinia';
import employeeApi from '@/api/auth/employeeApi';
import customerApi from '@/api/auth/customerApi';
import roleApi from '@/api/auth/roleApi';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    employees: [],
    customers: [],
    roles: [],
    isLoading: false,
  }),
  actions: {
    // --- Employee Actions ---
    async fetchEmployees() {
      this.isLoading = true;
      try {
        const res = await employeeApi.getAll();
        if (res.success) this.employees = res.data;
      } finally { this.isLoading = false; }
    },
    async removeEmployee(id) {
      const res = await employeeApi.delete(id);
      if (res.success) this.employees = this.employees.filter(e => e.id !== id);
    },

    // --- Customer Actions ---
    async fetchCustomers() {
      this.isLoading = true;
      try {
        const res = await customerApi.getAll();
        if (res.success) this.customers = res.data;
      } finally { this.isLoading = false; }
    },

    // --- Role Actions ---
    async fetchRoles() {
      this.isLoading = true;
      try {
        const res = await roleApi.getAll();
        if (res.success) this.roles = res.data;
      } finally { this.isLoading = false; }
    }
  }
});