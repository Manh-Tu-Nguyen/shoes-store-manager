<template>
  <div class="actions-cell">
    <!-- Nút xem chi tiết chung -->
    <button v-if="showDetail" @click="$emit('detail')" class="btn-icon btn-detail" title="Xem chi tiết">👁️</button>
    
    <!-- Nút chuyển sang list biến thể SKU -->
    <button v-if="showVariants" @click="$emit('variants')" class="btn-icon btn-variants" title="Xem biến thể (SKU)">📦</button>
    
    <!-- Nút chuyển sang list hóa đơn -->
    <button v-if="showInvoices" @click="$emit('invoices')" class="btn-icon btn-invoices" title="Xem hóa đơn">🧾</button>
    
    <!-- Nút Sửa -->
    <button v-if="showEdit" @click="$emit('edit')" class="btn-icon btn-edit" title="Sửa thông tin">✏️</button>
    
    <!-- Nút Xóa (Sẽ gọi popup confirm ở component cha) -->
    <button v-if="showDelete" @click="$emit('delete')" class="btn-icon btn-delete" title="Xóa/Khóa">🗑️</button>
  </div>
</template>

<script setup>
defineProps({
  showDetail: { type: Boolean, default: false },
  showVariants: { type: Boolean, default: false },
  showInvoices: { type: Boolean, default: false },
  showEdit: { type: Boolean, default: true },
  showDelete: { type: Boolean, default: true },
});
defineEmits(['detail', 'variants', 'invoices', 'edit', 'delete']);
</script>

<style scoped>
.actions-cell { display: flex; justify-content: center; gap: 8px; }
.btn-icon {
  width: 32px; height: 32px; border-radius: 6px; border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s; background: #f1f5f9;
}
.btn-detail:hover { background: #e0f2fe; color: #0369a1; transform: translateY(-2px); }
.btn-variants:hover { background: #f3e8ff; color: #7e22ce; transform: translateY(-2px); }
.btn-invoices:hover { background: #dcfce7; color: #15803d; transform: translateY(-2px); }
.btn-edit:hover { background: #fef3c7; color: #b45309; transform: translateY(-2px); }
.btn-delete:hover { background: #fee2e2; color: #b91c1c; transform: translateY(-2px); }
</style>