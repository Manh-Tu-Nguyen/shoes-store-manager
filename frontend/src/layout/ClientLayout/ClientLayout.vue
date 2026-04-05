<template>
  <div class="client-layout">
    <header class="client-header">
      <div class="logo" @click="goHome">👟 SHOES STORE</div>
      
      <button @click="fakeLogin" class="btn-login">
        Đăng nhập Admin
      </button>
    </header>

    <main class="client-main">
      <router-view />
    </main>

    <footer class="client-footer">
      <p>&copy; 2026 Shoes Store. Hệ thống đang trong giai đoạn phát triển.</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

// Điều hướng về trang chủ khách hàng
const goHome = () => {
  router.push('/');
};

// Điều hướng đi tắt sang trang Admin
const fakeLogin = () => {
  // Thực tế sau này đoạn này sẽ gọi: authStore.login(username, password)
  // Hiện tại ta chỉ cần nhảy thẳng sang Dashboard
  router.push('/admin');
};
</script>

<style scoped>
.client-layout { display: flex; flex-direction: column; min-height: 100vh; background-color: #f8fafc; }
.client-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 40px; background-color: #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.logo { font-size: 24px; font-weight: bold; color: #1e293b; cursor: pointer; }
.btn-login { background-color: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
.btn-login:hover { background-color: #1d4ed8; }
.client-main { flex: 1; padding: 40px; }
.client-footer { background-color: #1e293b; color: #94a3b8; text-align: center; padding: 20px; font-size: 14px; }
</style>