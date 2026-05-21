<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Icon } from '@iconify/vue';
import { useAuthStore } from '@/store/authStore'; // Bỏ chữ 's' ở đuôi 'stores'
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// --- STATE ---
const isProductOpen = ref(true); 

// --- COMPUTED ---
// Lấy tiêu đề trang hiện tại dựa trên name của route
const currentPageTitle = computed(() => {
    const name = route.name || '';
    return name.replace('admin-', '').replace('-', ' ');
});

// --- FUNCTIONS ---
const toggleProduct = () => {
    isProductOpen.value = !isProductOpen.value;
};

const handleLogout = () => {
  localStorage.clear(); 
  window.location.href = '/'; // F5 và ép trình duyệt tải lại hoàn toàn ở Trang chủ
};
</script>

<template>
    <div class="flex h-screen bg-gray-100 font-sans">
        
        <aside class="w-64 bg-slate-900 text-slate-300 flex flex-col transition-all duration-300 shadow-xl z-20">
            <div class="h-16 flex items-center px-6 border-b border-slate-700 bg-slate-950">
                <Icon icon="ri:admin-fill" class="text-pink-600 text-2xl mr-2" />
                <span class="text-lg font-bold text-white tracking-wide uppercase">HypeWay Admin</span>
            </div>

            <nav class="flex-1 p-4 space-y-2 overflow-y-auto scrollbar-hide">
                
                <router-link to="/admin/pos-management" 
    class="flex items-center gap-3 py-3 px-4 ..." 
    active-class="...">
    <Icon icon="mdi:point-of-sale" width="22" />
    <span>Bán hàng tại quầy</span>
</router-link>

                <div class="border-t border-slate-700 my-2 opacity-30"></div>

                <div>
                    <button @click="toggleProduct"
                        class="w-full flex items-center justify-between py-2.5 px-4 rounded-lg transition-colors hover:bg-slate-800 hover:text-white"
                        :class="{ 'text-white bg-slate-800': isProductOpen }">
                        <div class="flex items-center gap-3">
                            <Icon icon="icon-park-solid:sport" width="20" /> 
                            <span>Quản lý sản phẩm</span>
                        </div>
                        <Icon icon="mdi:chevron-down" width="20" class="transition-transform duration-200"
                            :class="{ 'rotate-180': isProductOpen }" />
                    </button>

                    <div v-show="isProductOpen" class="mt-1 space-y-1 bg-slate-950/50 rounded-lg overflow-hidden">
                        <router-link to="/admin/products"
                            class="flex items-center gap-3 py-2.5 pl-12 pr-4 text-sm hover:text-pink-400 hover:bg-slate-800 transition-colors"
                            active-class="text-pink-500 font-bold bg-slate-800 border-l-4 border-pink-500">
                            <Icon icon="mdi:shoe-sneaker" width="18" /> Sản phẩm
                        </router-link>

                        <router-link to="/admin/attributes"
                            class="flex items-center gap-3 py-2.5 pl-12 pr-4 text-sm hover:text-pink-400 hover:bg-slate-800 transition-colors"
                            active-class="text-pink-500 font-bold bg-slate-800 border-l-4 border-pink-500">
                            <Icon icon="mdi:format-list-bulleted-type" width="18" /> Thuộc tính
                        </router-link>
                    </div>
                </div>

                <router-link to="/admin/orders"
                    class="flex items-center gap-3 py-2.5 px-4 rounded-lg transition-colors hover:bg-slate-800 hover:text-white"
                    active-class="bg-pink-600 text-white shadow-md">
                    <Icon icon="icon-park-solid:bill" width="20" />
                    <span>Hóa đơn</span>
                </router-link>

                <router-link to="/admin/vouchers"
                    class="flex items-center gap-3 py-2.5 px-4 rounded-lg transition-colors hover:bg-slate-800 hover:text-white"
                    active-class="bg-pink-600 text-white shadow-md">
                    <Icon icon="mdi:ticket-percent-outline" width="20" />
                    <span>Voucher</span>
                </router-link>

                <div class="border-t border-slate-700 my-2 opacity-30"></div>
                <p class="px-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-2">Tài khoản & Phân quyền</p>

                <router-link to="/admin/employees"
                    class="flex items-center gap-3 py-2.5 px-4 rounded-lg transition-colors hover:bg-slate-800 hover:text-white"
                    active-class="bg-pink-600 text-white shadow-md">
                    <Icon icon="mdi:card-account-details-outline" width="20" />
                    <span>Nhân viên</span>
                </router-link>

                <router-link to="/admin/customers"
                    class="flex items-center gap-3 py-2.5 px-4 rounded-lg transition-colors hover:bg-slate-800 hover:text-white"
                    active-class="bg-pink-600 text-white shadow-md">
                    <Icon icon="mdi:account-group-outline" width="20" />
                    <span>Khách hàng</span>
                </router-link>

            </nav>

            <div class="p-4 border-t border-slate-700 bg-slate-950">
                <button @click="handleLogout" class="flex items-center gap-3 text-red-400 hover:text-white hover:bg-red-600/20 w-full transition-all px-4 py-3 rounded-lg font-bold">
                    <Icon icon="mdi:logout" width="22" /> 
                    <span>Đăng xuất</span>
                </button>
            </div>
        </aside>

        <div class="flex-1 flex flex-col overflow-hidden">
            
            <header class="h-16 bg-white shadow-sm border-b border-gray-200 flex justify-between items-center px-8 z-10">
                <div class="flex items-center gap-2">
                    <div class="w-1 h-6 bg-pink-600 rounded-full"></div>
                    <h2 class="text-xl font-bold text-gray-800 capitalize tracking-tight">
                        {{ currentPageTitle }}
                    </h2>
                </div>

                <div class="flex items-center gap-4">
                    <div class="text-right hidden sm:block">
                        <p class="text-sm font-black text-gray-800 uppercase leading-none">
                            {{ authStore.user?.name || 'Admin User' }}
                        </p>
                        <p class="text-[10px] text-gray-400 font-bold uppercase tracking-tighter mt-1">
                            {{ authStore.userRole }} hệ thống
                        </p>
                    </div>
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-slate-800 to-slate-900 text-white flex items-center justify-center font-black shadow-lg border-2 border-white ring-2 ring-pink-500/20">
                        {{ authStore.user?.name?.charAt(0) || 'A' }}
                    </div>
                </div>
            </header>

            <main class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50 p-6 scroll-smooth custom-scrollbar">
                <router-view v-slot="{ Component }">
                    <transition name="page-fade" mode="out-in">
                        <component :is="Component" />
                    </transition>
                </router-view>
            </main>
        </div>
    </div>
</template>

<style scoped>
/* Hiệu ứng chuyển trang mượt mà */
.page-fade-enter-active,
.page-fade-leave-active {
    transition: all 0.25s ease-out;
}

.page-fade-enter-from {
    opacity: 0;
    transform: translateX(10px);
}
.page-fade-leave-to {
    opacity: 0;
    transform: translateX(-10px);
}

/* Ẩn scrollbar sidebar nhưng vẫn cuộn được */
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;  
    scrollbar-width: none; 
}

/* Custom scrollbar cho nội dung chính */
.custom-scrollbar::-webkit-scrollbar {
    width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
    background: #f1f1f1;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
}
</style>