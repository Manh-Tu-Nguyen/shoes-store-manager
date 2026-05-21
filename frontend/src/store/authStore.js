import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  // 1. STATE: Nơi chứa dữ liệu
  state: () => ({
    token: localStorage.getItem('token') || null,
    role: localStorage.getItem('role') || null,
    fullName: localStorage.getItem('fullName') || null,
  }),

  // 2. GETTERS: Lấy trạng thái phái sinh
  getters: {
    isLoggedIn: (state) => !!state.token, // Trả về true nếu có token
    isAdmin: (state) => state.role === 'ROLE_ADMIN',
  },

  // 3. ACTIONS: Các hàm thay đổi dữ liệu
  actions: {
    // Hàm gọi khi đăng nhập thành công
    loginAction(userData) {
      this.token = userData.token;
      this.role = userData.role;
      this.fullName = userData.fullName;
      this.email = userData.email; // Hứng thêm email
      localStorage.setItem('token', userData.token);
      localStorage.setItem('role', userData.role);
      localStorage.setItem('fullName', userData.fullName);
      localStorage.setItem('email', userData.email); // Lưu vào két sắt
    },

    // Hàm gọi khi bấm nút Đăng xuất
    logoutAction() {
      localStorage.removeItem('token');
    localStorage.removeItem('role');
    
    // Reset state
    this.token = null;
    this.user = null;
  }
}
});