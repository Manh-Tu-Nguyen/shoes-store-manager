<template>
  <div>
    <h1>Dashboard Admin</h1>
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
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('fullName');
  router.push('/login');
  
  console.log("Đã đăng xuất an toàn.");
};
</script>