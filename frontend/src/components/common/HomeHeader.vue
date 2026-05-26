<template>
    <header class="sticky top-0 z-50 transition-all duration-300">
        <section :class="[
            'w-full py-4 transition-all duration-300',
            isScrolled ? 'bg-white/95 backdrop-blur-md shadow-md' : 'bg-gray-100'
        ]">

            <div class="flex flex-col md:flex-row items-center justify-between gap-3 md:gap-6 px-4 md:px-6">

                <div class="flex justify-between items-center w-full md:w-auto">
                    <router-link to="/" class="flex items-center hover:scale-105 transition-transform duration-300">
                        <span class="text-3xl md:text-5xl font-extrabold text-black">Hype</span>
                        <span class="text-3xl md:text-5xl font-extrabold text-red-600">Way</span>
                    </router-link>

                    <button class="md:hidden text-gray-700 hover:text-indigo-600" aria-label="Toggle mobile menu"
                        @click="isMobileMenuOpen = !isMobileMenuOpen">
                        <Icon icon="mdi:menu" class="w-6 h-6" />
                    </button>
                </div>

                <form class="w-full md:flex-1 max-w-xl" role="search">
                    <label class="relative w-full">
                        <input type="text" placeholder="Tìm kiếm sản phẩm...."
                            class="w-full px-4 py-3 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black">
                        <button type="submit"
                            class="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-black">
                            <Icon icon="mdi:magnify" class="w-5 h-5" />
                        </button>
                    </label>
                </form>

                <aside class="flex items-center justify-end space-x-4 w-full md:w-auto mt-2 md:mt-0">
                    
                    <router-link to="/cart" class="relative p-2 text-gray-700 hover:text-pink-600" aria-label="Cart">
    <Icon icon="mdi:cart-outline" class="w-6 h-6" />
    <span class="absolute -top-1 -right-1 bg-pink-600 text-white text-[10px] rounded-full h-4 w-4 flex items-center justify-center">
        0 
    </span>
</router-link>

                    <div class="relative group">
                        <router-link v-if="!authStore.isLoggedIn" to="/login" 
                            class="p-2 text-gray-700 hover:text-pink-600 flex items-center gap-1" title="Đăng nhập">
                            <Icon icon="mdi:account-outline" class="w-6 h-6" />
                        </router-link>

                        <div v-else class="flex items-center gap-2 cursor-pointer p-1 rounded-lg hover:bg-white transition group relative">
                            <div class="flex items-center gap-2">
                                <div class="w-8 h-8 rounded-full bg-black text-white flex items-center justify-center text-xs font-bold uppercase">
                                    {{ authStore.fullName?.charAt(0) || 'U' }}
                                </div>
                                <span class="hidden md:block text-sm font-bold text-gray-800">
                                    Hi, {{ authStore.fullName?.split(' ').pop() || 'User' }}
                                </span>
                            </div>

                            <div class="absolute right-0 top-full mt-2 w-56 bg-white rounded-xl shadow-xl border border-gray-100 py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 z-50">
                                <div class="px-4 py-2 border-b border-gray-50 mb-1">
                                    <p class="text-xs text-gray-400">Tài khoản</p>
                                    <p class="text-sm font-bold truncate">{{ authStore.fullName }}</p>
                                </div>
                                
                                <router-link v-if="authStore.isAdmin" to="/admin/dashboard" 
                                    class="flex items-center gap-2 px-4 py-2 text-sm text-pink-600 hover:bg-gray-50 font-bold">
                                    <Icon icon="mdi:shield-crown" /> Quản trị hệ thống
                                </router-link>
                                
                                <router-link v-if="authStore.role === 'ROLE_STAFF'" to="/staff/pos" 
                                    class="flex items-center gap-2 px-4 py-2 text-sm text-pink-600 hover:bg-gray-50 font-bold">
                                    <Icon icon="mdi:point-of-sale" /> Màn hình bán hàng
                                </router-link>

                                <router-link to="/profile" class="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                                    <Icon icon="mdi:account-circle" /> Hồ sơ cá nhân
                                </router-link>

                                <router-link to="/lookup" class="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                                    <Icon icon="mdi:package-variant-closed" /> Đơn hàng của tôi
                                </router-link>

                                <hr class="my-1 border-gray-50">
                                
                                <button @click="handleLogout" class="w-full flex items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-red-50 font-bold">
                                    <Icon icon="mdi:logout" /> Đăng xuất
                                </button>
                            </div>
                        </div>
                    </div>
                    
                </aside>
            </div>
        </section>
    </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
// Lưu ý: Đảm bảo đường dẫn này khớp với thư mục thực tế của bạn
import { useAuthStore } from '@/store/authStore' 

const router = useRouter()
const authStore = useAuthStore()

const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

const handleScroll = () => {
    isScrolled.value = window.scrollY > 10
}

const handleLogout = () => {
    if(confirm("Bạn có chắc chắn muốn đăng xuất?")) {
        authStore.logoutAction() // Gọi đúng hàm logoutAction() trong Pinia
        
        // F5 lại trang để xóa cache giao diện triệt để (Tùy chọn)
        window.location.reload(); 
    }
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
.group:hover .group-hover\:visible {
    visibility: visible;
}
</style>