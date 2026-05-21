<template>
  <div class="staff-layout">
    <header class="staff-header">
      <h2>Điểm Bán Hàng</h2>
    </header>
    <main class="staff-main">
      <slot />
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.staff-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.staff-header {
  background-color: #343a40;
  color: white;
  padding: 1rem;
}

.staff-main {
  flex: 1;
  padding: 1rem;
}
</style>