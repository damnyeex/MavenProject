<template>
    <!-- Форма редактирования -->
    <div class="edit-tenant-form">
        <h3>Редактирование отдела</h3>
        <form @submit.prevent="handleUpdateTenant">
            <div class="form-group">
                <label>Название</label>
                <BaseInput v-model="form.name" required />
            </div>
            <div class="form-group">
                <label>Описание</label>
                <BaseInput v-model="form.description" />
            </div>
            <div class="form-actions">
                <BaseButton type="submit" :disabled="isUpdatingTenant">
                    {{ isUpdatingTenant ? "Сохранение..." : "Сохранить" }}
                </BaseButton>
                <BaseButton variant="secondary" @click="cancelEditTenant">
                    Отмена
                </BaseButton>
            </div>
        </form>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseInput from "@/shared/ui/BaseInput.vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import { updateTenant } from "../../api";

const props = defineProps({
    tenantId: { type: String, required: true },
    currentTenantData: { type: Object, required: true },
    onTenantUpdated: { type: Function, default: null },
    onCancel: { type: Function, default: null },
});

const emit = defineEmits(["cancel", "tenant-updated"]);

const isUpdatingTenant = ref(false);

const form = ref({
    name: "",
    description: "",
});

const loadTenantData = () => {
    form.value = {
        id: props.currentTenantData.id,
        name: props.currentTenantData.name || "",
        description: props.currentTenantData.description || "",
    };
};

const cancelEditTenant = () => {
    if (props.onCancel) props.onCancel();
};

const handleUpdateTenant = async () => {
    if (!form.value.name.trim()) return;

    isUpdatingTenant.value = true;
    try {
        const response = await updateTenant(form.value.id, {
            name: form.value.name,
            description: form.value.description,
        });
        if (response.success) {
            if (props.onTenantUpdated) {
                props.onTenantUpdated();
            }
            emit("tenant-updated");
        }
    } catch (error) {
        console.error("Failed to update tenant:", error);
        alert("Ошибка при обновлении отдела");
    } finally {
        isUpdatingTenant.value = false;
    }
};

onMounted(() => {
    loadTenantData();
});
</script>

<style scoped>
.loading {
    text-align: center;
    padding: 20px;
}
.edit-tenant-form {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
    margin-top: 20px;
    border-left: 4px solid #42b983;
}
.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 15px;
}
</style>
