<template>
  <footer class="bg-gray-950 text-gray-300 pt-16 pb-8">
    <div class="container mx-auto px-6 max-w-7xl">
      <div class="flex flex-col lg:flex-row justify-between gap-12 border-b border-gray-800 pb-12 mb-8">
        <div class="space-y-6 lg:w-1/4">
          <!-- Logo -->
          <a href="/" class="flex items-center gap-2 text-3xl font-bold group">
            <span class="text-red-600 hover:drop-shadow-[0_0_12px_rgba(255,255,255,1)] transition">
              HighSport
            </span>
          </a>

          <!-- Social Icons -->
          <ul class="flex gap-4">
            <li v-for="(icon, i) in socialIcons" :key="i">
              <a href="#"
                 class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-800 hover:bg-rose-500 transition">
                <Icon :icon="icon" class="text-xl" />
              </a>
            </li>
          </ul>
        </div>

        <!-- Links -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 flex-1">
          <!-- Contact -->
          <ul>
            <p class="text-lg font-semibold text-white mb-4">Liên hệ</p>
            <li class="flex items-start gap-3 mb-3">
              <Icon icon="mdi:map-marker" class="mt-1" />
              <span>Số 10, Cổ Nhuế, Bắc Từ Liêm, Hà Nội</span>
            </li>
            <li class="flex items-center gap-3 mb-3">
              <Icon icon="mdi:phone" />
              <a href="tel:+557343673257" class="hover:text-rose-400">0763805857</a>
            </li>
            <li class="flex items-center gap-3">
              <Icon icon="mdi:email" />
              <a href="mailto:hightsport@help.com" class="hover:text-rose-400">hightsport@help.com</a>
            </li>
          </ul>

          <!-- Account Links -->
          <ul>
            <p class="text-lg font-semibold text-white mb-4">Tài khoản của tôi</p>
            <li v-for="(item, i) in accountLinks" :key="i" class="mb-2">
              <a href="#" class="flex items-center gap-2 hover:text-rose-400 transition">
                <Icon icon="mdi:chevron-right" />
                <span>{{ item }}</span>
              </a>
            </li>
          </ul>

          <!-- Opening Hours -->
          <div>
            <p class="text-lg font-semibold text-white mb-4">Giờ mở cửa</p>
            <table class="w-full text-sm">
              <tbody>
                <tr v-for="(time, day) in openingHours" :key="day">
                  <th class="font-normal text-left pr-3">{{ day }}:</th>
                  <td>{{ time }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div class="text-center text-gray-500 text-sm">
      &copy; 2025 <a href="#" class="text-rose-400 hover:underline">HighSport</a>. Tất cả các quyền được bảo lưu.
    </div>
  </footer>
</template>

<script setup>
import { Icon } from '@iconify/vue'

const socialIcons = ['mdi:facebook','mdi:instagram','mdi:twitter']
const accountLinks = ['Tài khoản của tôi','Xem giỏ hàng','Danh sách yêu thích','So sánh sản phẩm','Sản phẩm mới']
const openingHours = {'Thứ 2 - Thứ 7':'8:00 - 22:00','Chủ nhật':'Nghỉ'}
</script>
