<template>
  <div class="table-container shadow-sm">
    <table class="data-table">
      <thead>
        <tr>
          <!-- Slot để cha truyền Header vào -->
          <slot name="header"></slot>
        </tr>
      </thead>
      <tbody>
        <tr v-if="isEmpty">
          <td :colspan="colCount" class="text-center text-muted py-4">
            Không có dữ liệu.
          </td>
        </tr>
        <!-- Slot để cha truyền các thẻ <tr> dữ liệu vào -->
        <slot name="body"></slot>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps({
  isEmpty: { type: Boolean, default: false },
  colCount: { type: Number, default: 5 } // Số cột để gộp (colspan) khi bảng trống
});
</script>

<style scoped>
.table-container { background: white; border-radius: 12px; overflow: hidden; margin-top: 20px; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table :deep(th) { background: #f8fafc; padding: 15px; text-align: left; font-size: 13px; color: #64748b; text-transform: uppercase; }
.data-table :deep(td) { padding: 15px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.py-4 { padding-top: 1rem; padding-bottom: 1rem; }
.text-muted { color: #6c757d; }
</style>