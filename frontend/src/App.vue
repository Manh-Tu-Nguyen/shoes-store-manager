<template>
  <router-view />
</template>

<script setup>

</script>

<style>

:root {
  --primary-color: #2c3e50;
  --secondary-color: #34495e;
  --success-color: #27ae60;
  --danger-color: #e74c3c;
  --bg-light: #f8f9fa;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--bg-light);
  color: #333;
}

* {
  box-sizing: border-box;
}

::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: #f1f1f1;
}
::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>