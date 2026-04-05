<template>
  <div class="admin-layout">
    <AdminSidebar class="sidebar-fixed" />

    <div class="main-container">
      <header class="admin-header">
        <div class="breadcrumb">
          <span class="text-muted">Admin /</span> 
          <span class="text-active">{{ $route.meta.title || 'Dashboard' }}</span>
        </div>
        <div class="user-info">
          <i class="fas fa-user-circle"></i> Xin chào, **Nguyễn Mạnh Tú**
        </div>
      </header>

      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import AdminSidebar from './AdminSlidebar.vue';
</script>

<style scoped>
.admin-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.sidebar-fixed {
  flex-shrink: 0; /* Quan trọng: Ngăn Sidebar bị bóp méo */
  width: 260px;
}

.main-container {
  flex-grow: 1; /* Chiếm toàn bộ phần còn lại */
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background-color: #f4f7f6;
}

.admin-header {
  height: 64px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between; /* Đẩy 2 khối về 2 đầu cực */
  padding: 0 30px;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.breadcrumb {
  font-size: 14px;
  font-weight: 500;
}
.text-muted { color: #95a5a6; margin-right: 5px; }
.text-active { color: #2c3e50; }

.user-info {
  font-size: 14px;
  color: #34495e;
}

.admin-content {
  padding: 30px;
  flex: 1;
}

/* Transition */
.fade-enter-active, .fade-leave-active { transition: all 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px); }
</style>