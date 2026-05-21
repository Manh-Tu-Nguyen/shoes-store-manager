<template>
  <div>
    <h1>Point of Sale</h1>
    <p>Xin chào, {{ fullName }}!</p>
    <button class="btn btn-outline-danger btn-sm ms-3" @click="handleLogout">
        <i class="fas fa-sign-out-alt"></i> Đăng xuất
     </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const fullName = ref(localStorage.getItem('fullName'));
import { useRouter } from 'vue-router';

const router = useRouter();

const handleLogout = () => {
  // 1. Xóa sạch "giấy thông hành" trong ví
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('fullName');

  // 2. (Tùy chọn) Nếu dùng Pinia/Vuex, hãy gọi action reset state ở đây
  
  // 3. Đuổi người dùng về trang Login
  router.push('/login');
  
  // 4. Thông báo nhẹ nhàng
  console.log("Đã đăng xuất an toàn.");
};
</script>
