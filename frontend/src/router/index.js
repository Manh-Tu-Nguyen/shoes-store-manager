import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  // ==========================================
  // 1. NHÁNH KHÁCH HÀNG (STOREFRONT)
  // ==========================================
  {
    path: '/',
    component: () => import('@/layout/ClientLayout/ClientLayout.vue'),
    children: [
      {
        path: '', 
        name: 'Home',
        component: () => import('@/page/client/Home.vue'),
        meta: { title: 'Trang chủ - Shoes Store' }
      },
      {
        path: 'product/:id', 
        name: 'ClientProductDetail',
        component: () => import('@/page/client/ClientProductDetail.vue'),
        meta: { title: 'Chi tiết sản phẩm' }
      },
      {
        path: 'checkout', 
        name: 'Checkout',
        component: () => import('@/page/client/Checkout.vue'),
        meta: { title: 'Thanh toán đơn hàng' }
      }
    ]
  },

  // ==========================================
  // 2. NHÁNH QUẢN TRỊ (ADMIN)
  // ==========================================
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Thống kê doanh thu' }
      },
      {
        path: 'products', 
        name: 'Product',
        component: () => import('@/page/admin/Product.vue'),
        meta: { title: 'Quản lý sản phẩm' }
      },
      {
        path: 'products/:id/details', 
        name: 'ProductDetail',
        component: () => import('@/page/admin/ProductDetail.vue'),
        meta: { title: 'Quản lý biến thể sản phẩm' }
      },
      {
        path: 'brands',
        name: 'Brand',
        component: () => import('@/page/admin/Brand.vue'),
        meta: { title: 'Quản lý thương hiệu' }
      },
      {
        path: 'sizes',
        name: 'Size',
        component: () => import('@/page/admin/Size.vue'),
        meta: { title: 'Quản lý kích cỡ' }
      },
      {
        path: 'colors',
        name: 'Color',
        component: () => import('@/page/admin/Color.vue'),
        meta: { title: 'Quản lý màu sắc' }
      },
      {
        path: 'origins',
        name: 'Origin',
        component: () => import('@/page/admin/Origin.vue'),
        meta: { title: 'Quản lý xuất xứ' }
      },
      {
        path: 'categories',
        name: 'Category',
        component: () => import('@/page/admin/Dashboard.vue'), 
        meta: { title: 'Quản lý danh mục' }
      },
      {
        path: 'employees',
        name: 'Employee',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Quản lý nhân viên' }
      },
      {
        path: 'customers',
        name: 'Customer',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Quản lý khách hàng' }
      },
      {
        path: 'vouchers',
        name: 'Voucher',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Quản lý Voucher' }
      },
      {
        path: 'invoices',
        name: 'Invoice',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Quản lý hóa đơn' }
      },
      {
        path: 'work-shifts',
        name: 'WorkShift',
        component: () => import('@/page/admin/Dashboard.vue'),
        meta: { title: 'Quản lý ca làm' }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.afterEach((to) => {
  document.title = to.meta.title || 'Shoes Store Manager';
});

export default router;