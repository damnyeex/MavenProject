<template>
    <div class="tenants-management">
        <div class="management-header">
            <h2>Управление отделами (тенантами)</h2>
            <BaseButton variant="secondary" @click="$emit('back')">
                ← Назад к админ-панели
            </BaseButton>
        </div>

        <!-- Форма создания -->
        <div class="create-tenant-form">
            <h3>Создать новый отдел</h3>
            <form @submit.prevent="handleCreateTenant">
                <div class="form-row">
                    <div class="form-group">
                        <label>Название</label>
                        <BaseInput v-model="newTenant.name" required />
                    </div>
                    <div class="form-group">
                        <label>Описание</label>
                        <BaseInput v-model="newTenant.description" />
                    </div>
                    <div class="form-group">
                        <BaseButton type="submit" :disabled="isCreatingTenant">
                            {{
                                isCreatingTenant
                                    ? "Создание..."
                                    : "Создать отдел"
                            }}
                        </BaseButton>
                    </div>
                </div>
            </form>
        </div>

        <!-- Таблица тенантов -->
        <div v-if="tenantsLoading" class="loading">Загрузка...</div>
        <div v-else class="tenants-table">
            <table>
                <thead>
                    <tr>
                        <th>Название</th>
                        <th>Описание</th>
                        <th>Пользователей</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="tenant in tenants" :key="tenant.id">
                        <td>{{ tenant.name }}</td>
                        <td>{{ tenant.description || "—" }}</td>
                        <td>{{ tenant.userCount || 0 }}</td>

                        <td class="form-actions">
                            <BaseButton
                                variant="secondary"
                                size="small"
                                @click="openEditTenant(tenant)"
                            >
                                Изменить
                            </BaseButton>
                            <BaseButton
                                variant="danger"
                                size="small"
                                @click="deleteTenantHandler(tenant.id)"
                                :disabled="tenant.userCount > 0"
                                :title="
                                    tenant.userCount > 0
                                        ? 'Нельзя удалить отдел с пользователями'
                                        : ''
                                "
                            >
                                Удалить
                            </BaseButton>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <TenantUpdateForm
            v-if="editingTenant"
            :tenantId="editingTenantId"
            :currentTenantData="editingTenantData"
            @tenant-updated="onTenantUpdated"
            @cancel="cancelEditTenant"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseInput from "@/shared/ui/BaseInput.vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import { getAllTenants, createTenant, deleteTenant } from "../../api";
import TenantUpdateForm from "./TenantUpdateForm.vue";

const emit = defineEmits(["back", "tenant-changed"]);

const tenants = ref([]);
const tenantsLoading = ref(false);
const isCreatingTenant = ref(false);
const editingTenant = ref(false);
const editingTenantId = ref("");
const editingTenantData = ref({});

const newTenant = ref({
    name: "",
    description: "",
});

const loadTenants = async () => {
    tenantsLoading.value = true;
    try {
        const response = await getAllTenants();
        if (response.success) {
            tenants.value = response.data;
        }
    } catch (error) {
        console.error("Failed to load tenants:", error);
    } finally {
        tenantsLoading.value = false;
    }
};

const handleCreateTenant = async () => {
    if (!newTenant.value.name.trim()) return;

    isCreatingTenant.value = true;
    try {
        const response = await createTenant({
            name: newTenant.value.name,
            description: newTenant.value.description,
        });
        if (response.success) {
            newTenant.value = { name: "", description: "" };
            await loadTenants();
            emit("tenant-changed");
        }
    } catch (error) {
        console.error("Failed to create tenant:", error);
        alert("Ошибка при создании отдела");
    } finally {
        isCreatingTenant.value = false;
    }
};

const openEditTenant = (tenant) => {
    editingTenant.value = true;
    editingTenantId.value = tenant.id;
    editingTenantData.value = { ...tenant };
};

const cancelEditTenant = () => {
    editingTenant.value = false;
    editingTenantId.value = "";
    editingTenantData.value = {};
};

const onTenantUpdated = async () => {
    editingTenant.value = false;
    editingTenantId.value = "";
    editingTenantData.value = {};
    await loadTenants();
    emit("tenant-changed");
};

const deleteTenantHandler = async (tenantId) => {
    const tenant = tenants.value.find((t) => t.id === tenantId);
    if (tenant.userCount > 0) {
        alert(
            "Нельзя удалить отдел, в котором есть пользователи. Сначала переместите или удалите пользователей.",
        );
        return;
    }

    if (!confirm("Вы уверены, что хотите удалить этот отдел?")) return;

    try {
        const response = await deleteTenant(tenantId);
        if (response.success) {
            await loadTenants();
            emit("tenant-changed");
        }
    } catch (error) {
        console.error("Failed to delete tenant:", error);
        alert("Ошибка при удалении отдела");
    }
};

onMounted(() => {
    loadTenants();
});
</script>

<style scoped>
.tenants-management {
    padding: 20px;
}
.management-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e9ecef;
}
.create-tenant-form {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}
.form-row {
    display: flex;
    gap: 15px;
    align-items: flex-end;
}
.form-row .form-group {
    flex: 1;
    margin-bottom: 0;
}
.form-group {
    margin-bottom: 15px;
}
label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}
.tenants-table {
    overflow-x: auto;
    margin-bottom: 30px;
}
table {
    width: 100%;
    border-collapse: collapse;
}
th,
td {
    padding: 10px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}
th {
    background-color: #f2f2f2;
}
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
}
</style>
