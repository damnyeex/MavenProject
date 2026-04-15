<template>
    <div class="users-management">
        <div class="management-header">
            <h2>Управление пользователями</h2>
        </div>

        <!-- Таблица пользователей -->
        <div v-if="usersLoading" class="loading">Загрузка...</div>
        <div v-else class="users-table">
            <table>
                <thead>
                    <tr>
                        <th>Логин</th>
                        <th>Роль</th>
                        <th>Тенант</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in filteredUsers" :key="user.id">
                        <td>{{ user.login }}</td>
                        <td>{{ user.role }}</td>
                        <td>{{ getTenantName(user.tenantId) }}</td>

                        <td class="form-actions">
                            <BaseButton
                                variant="secondary"
                                size="small"
                                @click="openEditUser(user)"
                            >
                                подробнее
                            </BaseButton>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import { getAllUsers, getAllTenants, getOneUser } from "@/features/admin/api";

const emit = defineEmits(["back", "tenant-changed"]);

const users = ref([]);
const filteredUsers = ref([]);
const tenants = ref([]);
const usersLoading = ref(false);
const userLoading = ref(false);
const currentUserId = ref(null);

const loadUsers = async () => {
    usersLoading.value = true;
    try {
        const response = await getAllUsers();
        if (response.success) {
            users.value = response.data;
            applyFilter();
        }
    } catch (error) {
        console.error("Failed to load users:", error);
    } finally {
        usersLoading.value = false;
    }
};

const loadUser = async () => {
    userLoading.value = true;
    try {
        const response = await getOneUser();
        if (response.success) {
            users.value = response.data;
            applyFilter();
        }
    } catch (error) {
        console.error("Failed to load users:", error);
    } finally {
        userLoading.value = false;
    }
};

const loadTenants = async () => {
    try {
        const response = await getAllTenants();
        if (response.success) {
            tenants.value = response.data;
        }
    } catch (error) {
        console.error("Failed to load tenants:", error);
    }
};

const getTenantName = (tenantId) => {
    if (!tenantId) return "—";
    const tenant = tenants.value.find((t) => t.id === tenantId);
    return tenant ? tenant.name : "—";
};

const getCurrentUserId = async () => {
    try {
        const response = await getOneUser();
        if (response.data.success) {
            currentUserId.value = response.data.data.id;
        }
    } catch (error) {
        console.error("Failed to get current user:", error);
    }
};

onMounted(() => {
    loadUsers();
    loadUser();
    loadTenants();
    getCurrentUserId();
});
</script>
