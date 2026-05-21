<script setup>
import homeHeader from '../../components/common/HomeHeader.vue';
import footerApp from '../../components/common/HomeFooter.vue';
import { h } from 'vue';
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <homeHeader/>
    <main class="flex-grow">
      <router-view />
    </main>
    <footerApp/>
  </div>
</template>