<template>
  <div class="login-page">
    <div class="login-box">
      <h3 class="text-center mb-4">HypeWay</h3>

      <div v-if="errorMessage" class="alert alert-danger text-center p-2 mb-3" style="font-size: 14px;">
        {{ errorMessage }}
      </div>

      <div class="mb-3">
        <input
          v-model="email"
          type="email"
          class="form-control"
          placeholder="Email đăng nhập"
          @keyup.enter="handleLogin"
        />
      </div>

      <div class="mb-3">
        <input
          type="password"
          v-model="password"
          class="form-control"
          placeholder="Mật khẩu"
          @keyup.enter="handleLogin"
        />
      </div>

      <button 
        class="btn btn-primary w-100" 
        @click="handleLogin" 
        :disabled="isLoading"
      >
        <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        {{ isLoading ? 'Đang xử lý...' : 'Đăng nhập' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/authStore';
import axiosInstance from '@/api/axios'; 

const router = useRouter();
const authStore = useAuthStore(); 

// Khai báo state
const email = ref('admin@gmail.com');
const password = ref('123456');
const errorMessage = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  errorMessage.value = '';

  if (!email.value || !password.value) {
    errorMessage.value = 'Vui lòng nhập đầy đủ email và mật khẩu!';
    return;
  }

  isLoading.value = true;

  try {
    const response = await axiosInstance.post('/auth/login', {
      email: email.value.trim(), 
      password: password.value
    });

    // 1. LINH HOẠT BÓC VỎ JSON (Hỗ trợ cả API có bọc Map "data" và không bọc)
    const authData = response.data ? response.data : response;

    // 2. KIỂM TRA TOKEN TRÊN DỮ LIỆU ĐÃ BÓC VỎ
    if (authData && authData.token) {
      
      // Lưu thẳng dữ liệu thực chất vào Store
      authStore.loginAction(authData);
      localStorage.setItem('token', authData.token);
localStorage.setItem('user', JSON.stringify(authData));
      // Lấy quyền từ payload trả về
      const userRole = authData.role || authData.roleName; 

      // 3. RẼ NHÁNH ĐIỀU HƯỚNG QUYỀN
      if (userRole === 'ROLE_ADMIN' || userRole === 'ADMIN') {
        router.push('/admin/dashboard'); 
        
      } else if (userRole === 'ROLE_STAFF' || userRole === 'STAFF') {
        router.push('/staff/pos'); 
        
      } else if (userRole === 'ROLE_CUSTOMER' || userRole === 'CUSTOMER') {
        router.push('/'); 
        
      } else {
        router.push('/unauthorized'); 
      }
    } else {
       errorMessage.value = 'Dữ liệu xác thực không hợp lệ (Không tìm thấy Token)!';
    }
  } catch (error) {
    if (error.response) {
      if (error.response.status === 401) {
        errorMessage.value = 'Tài khoản hoặc mật khẩu không chính xác!';
      } else if (error.response.status === 403) {
        errorMessage.value = 'Tài khoản của bạn đã bị khóa!';
      } else {
        errorMessage.value = 'Lỗi hệ thống! Vui lòng thử lại sau.';
      }
    } else {
      errorMessage.value = 'Không thể kết nối đến máy chủ Backend.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #4e73df, #1cc88a); 
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  width: 350px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}
</style>