<template>
  <aside class="sidebar">
    <div class="sidebar-logo">
      <span class="logo-text">SHOES STORE</span>
      <span class="logo-sub">ADM</span>
    </div>
    
    <nav class="sidebar-nav">
      <router-link to="/admin/dashboard" class="nav-item">
        <span class="icon">📊</span> Thống kê
      </router-link>
      <router-link to="/admin/invoices" class="nav-item">
        <span class="icon">📜</span> Hóa đơn
      </router-link>

      <div class="nav-group">
        <div class="nav-item dropdown-toggle" @click="toggleMenu('product')">
          <span><span class="icon">👟</span> Quản lý Sản phẩm</span>
          <span class="arrow" :class="{ open: openMenus.product }">▼</span>
        </div>
        <transition name="slide">
          <div v-show="openMenus.product" class="sub-nav">
            <router-link to="/admin/products" class="sub-item">Sản phẩm</router-link>
            <router-link to="/admin/brands" class="sub-item">Thương hiệu</router-link>
            <router-link to="/admin/categories" class="sub-item">Danh mục</router-link>
            <router-link to="/admin/sizes" class="sub-item">Kích cỡ</router-link>
            <router-link to="/admin/colors" class="sub-item">Màu sắc</router-link>
            <router-link to="/admin/origins" class="sub-item">Xuất xứ</router-link>
          </div>
        </transition>
      </div>

      <div class="nav-group">
        <div class="nav-item dropdown-toggle" @click="toggleMenu('employee')">
          <span><span class="icon">👥</span> Quản lý Nhân sự</span>
          <span class="arrow" :class="{ open: openMenus.employee }">▼</span>
        </div>
        <transition name="slide">
          <div v-show="openMenus.employee" class="sub-nav">
            <router-link to="/admin/employees" class="sub-item">Nhân viên</router-link>
            <router-link to="/admin/work-shifts" class="sub-item">Ca làm việc</router-link>
          </div>
        </transition>
      </div>

      <router-link to="/admin/customers" class="nav-item">
        <span class="icon">👤</span> Khách hàng
      </router-link>
      <router-link to="/admin/vouchers" class="nav-item">
        <span class="icon">🎟️</span> Voucher
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { reactive } from 'vue';

const openMenus = reactive({
  product: false,
  employee: false
});

const toggleMenu = (menu) => {
  openMenus[menu] = !openMenus[menu];
};
</script>

<style scoped>
.sidebar {
  width: 260px;
  background: #1e293b; /* Màu tối hiện đại hơn */
  color: #cbd5e1;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.sidebar-logo {
  padding: 25px 20px;
  text-align: center;
  border-bottom: 1px solid #334155;
}
.logo-text { color: #38bdf8; font-weight: 800; font-size: 1.1rem; }
.logo-sub { color: white; margin-left: 5px; }

.sidebar-nav { padding: 15px 0; flex: 1; overflow-y: auto; }

.nav-item {
  padding: 12px 20px;
  color: #94a3b8;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.icon { margin-right: 12px; font-size: 1.1rem; }

.nav-item:hover, .router-link-active {
  background: #334155;
  color: #f8fafc;
}

.sub-nav { background: #0f172a; padding: 5px 0; }

.sub-item {
  padding: 10px 52px;
  color: #64748b;
  text-decoration: none;
  display: block;
  font-size: 0.85rem;
}
.sub-item:hover, .router-link-active { color: #38bdf8; }

/* Slide Animation */
.slide-enter-active, .slide-leave-active { transition: max-height 0.3s ease-out; max-height: 400px; overflow: hidden; }
.slide-enter-from, .slide-leave-to { max-height: 0; }

.arrow { font-size: 0.6rem; transition: transform 0.3s; }
.arrow.open { transform: rotate(180deg); }
</style>