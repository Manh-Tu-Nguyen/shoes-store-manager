import { defineStore } from 'pinia';
import employeeApi from '@/api/employeeApi';
import workShiftApi from '@/api/workShiftApi'; // Import thêm API ca làm việc
import roleApi from '@/api/roleApi';
export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: [],
    roles: [],
    workShifts: [], // Lưu trữ danh sách ca làm việc tập trung
    loading: false,
  }),

  actions: {
    async fetchInitData() {
      this.loading = true;
      try {
        const [empRes, roleRes, wsRes] = await Promise.all([
          employeeApi.getAll().catch(err => { console.error("SẬP API NV:", err); return { data: [] }; }),
          roleApi.getAll().catch(err => { console.error("SẬP API ROLE:", err); return { data: [] }; }), 
          
          workShiftApi.getAll().catch(err => { console.error("SẬP API CA LÀM:", err); return { data: [] }; })
        ]);

        this.employees = empRes?.data?.data || empRes?.data || [];
        this.roles = roleRes?.data?.data || roleRes?.data || [];
        this.workShifts = wsRes?.data?.data || wsRes?.data || [];
        
      } catch (e) { 
        console.error("Lỗi khởi tạo:", e); 
      } finally {
        this.loading = false;
      }
    },

    // ==========================================
    // PHÂN HỆ: NHÂN VIÊN (EMPLOYEE CRUD)
    // ==========================================
    async createEmployee(data) { await employeeApi.create(data); },
    async updateEmployee(id, data) { await employeeApi.update(id, data); },
    async deleteEmployee(id) { await employeeApi.delete(id); },

    // ==========================================
    // PHÂN HỆ: CA LÀM VIỆC (WORK SHIFT CRUD)
    // ==========================================
    async fetchWorkShifts() {
      this.loading = true;
      try {
        const res = await workShiftApi.getAll();
        this.workShifts = res?.data?.data || res?.data || [];
      } finally { 
        this.loading = false; 
      }
    },

    async createWorkShift(data) { 
      await workShiftApi.create(data); 
    },

    async updateWorkShift(id, data) { 
      await workShiftApi.update(id, data); 
    },

    async deleteWorkShift(id) { 
      await workShiftApi.delete(id); 
    }
  }
});