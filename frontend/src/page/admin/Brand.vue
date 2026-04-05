<script setup>
import { ref, computed, onMounted } from 'vue';
import { useProductStore } from '@/store/productStore';

const productStore = useProductStore();
const searchQuery = ref('');
const isModalOpen = ref(false);
const editingId = ref(null);
const formData = ref({ name: '' });

onMounted(() => {
  productStore.fetchAllAttributes();
});

// Lưu ý: Đổi Brands -> brands (theo state trong store)
const filteredBrands = computed(() => {
  return (productStore.brands || []).filter(b => 
    b.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const openModal = (item = null) => {
  if (item) {
    editingId.value = item.id;
    formData.value = { name: item.name };
  } else {
    editingId.value = null;
    formData.value = { name: '' };
  }
  isModalOpen.value = true;
};

const saveData = async () => {
  if (!formData.value.name) return alert("Vui lòng nhập tên!");
  
  let res;
  if (editingId.value) {
    res = await productStore.updateBrand(editingId.value, formData.value);
  } else {
    res = await productStore.addBrand(formData.value);
  }

  if (res.success) {
    alert(res.message || "Thao tác thành công!");
    isModalOpen.value = false;
  }
};

const confirmDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa thương hiệu này?")) {
    const res = await productStore.removeBrand(id);
    if (res.success) {
      alert("Đã xóa thành công!");
    }
  }
};
</script>