import path from 'node:path';
import { createRouter, createWebHistory } from 'vue-router';

// Lazy loading components để tối ưu hiệu năng
const POSManagement = () => import('@/page/admin/POSManagement.vue');

const routes = [
  // 1. PUBLIC
  {
    path: '/',
    component: () => import('@/layout/ClientLayout/ClientLayout.vue'),
    meta: { requiresAuth: false },
    children: [
      { path: '', name: 'client-home', component: () => import('@/page/client/Home.vue') },
      { path: 'product/:id', name: 'client-product-detail', component: () => import('@/page/client/ProductClientDetail.vue') },
      { path: 'cart', name: 'client-cart', component: () => import('@/page/client/Cart.vue') },
      { path: 'checkout', name: 'client-checkout', component: () => import('@/page/client/Checkout.vue') }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/page/login/Login.vue'), meta: { requiresAuth: false } },

  // 2. ADMIN
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout/AdminLayout.vue'),
    meta: { requiresAuth: true, requiredRole: 'ROLE_ADMIN' },
    redirect: '/admin/dashboard', 
    children: [
      { path: 'dashboard', name: 'admin-dashboard', component: () => import('@/page/admin/Dashboard.vue') },
      { path: 'pos-management', name: 'admin-pos-management', component: POSManagement, props: { userRole: 'ROLE_ADMIN' } },
      { path: 'products', name: 'admin-products', component: () => import('@/page/admin/Product.vue') },
      { path: 'products/:id/variants', component: () => import('@/page/admin/ProductDetail.vue') },
      { path: 'attributes', name: 'admin-attributes', component: () => import('@/page/admin/Attributes.vue') },
      { path: 'customers', name: 'admin-customers', component: () => import('@/page/admin/Customer.vue') },
      { path: 'customers/:id/addresses', name: 'admin-customer-addresses', component: () => import('@/page/admin/Address.vue') },
      { path: 'employees', name: 'admin-employees', component: () => import('@/page/admin/Employee.vue') },
      { path: 'work-shifts', name: 'admin-work-shifts', component: () => import('@/page/admin/WorkShift.vue') },
      { path: 'orders', name: 'admin-orders', component: () => import('@/page/admin/Order.vue') },
      { path: 'vouchers', name: 'admin-vouchers', component: () => import('@/page/admin/Voucher.vue') }
    ]
  },

  // 3. STAFF
  {
    path: '/staff',
    component: () => import('@/layout/StaffLayout/StaffLayout.vue'),
    meta: { requiresAuth: true, requiredRole: 'ROLE_STAFF' },
    redirect: '/staff/pos',
    children: [
      { path: 'pos', name: 'staff-pos', component: () => import('@/page/staff/POS.vue') },
      { path: 'pos-management', name: 'staff-pos-management', component: POSManagement, props: { userRole: 'ROLE_STAFF' } }
    ]
  },

  { path: '/:pathMatch(.*)*', redirect: '/' }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// ROUTER GUARD
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');

  if (to.meta.requiresAuth && !token) return next('/login');

  if (to.path === '/login' && token) {
    if (userRole === 'ROLE_ADMIN') return next('/admin/dashboard');
    if (userRole === 'ROLE_STAFF') return next('/staff/pos');
    return next('/');
  }

  const requiredRole = to.matched.find(record => record.meta.requiredRole)?.meta.requiredRole;
  if (requiredRole && requiredRole !== userRole) {
    alert('Bạn không đủ thẩm quyền!');
    return next('/'); 
  }

  next();
});

export default router;