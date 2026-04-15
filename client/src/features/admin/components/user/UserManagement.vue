<template>
    <div class="users-management">
        <div class="management-header">
            <h2>Управление пользователями</h2>
            <BaseButton variant="secondary" @click="$emit('back')">
                ← Назад к админ-панели
            </BaseButton>
        </div>

        <!-- Фильтр по отделам -->
        <div class="filter-section">
            <label>Фильтр по отделу:</label>
            <select
                v-model="selectedTenantFilter"
                @change="applyFilter"
                class="filter-select"
            >
                <option :value="null">Все отделы</option>
                <option
                    v-for="tenant in tenants"
                    :key="tenant.id"
                    :value="tenant.id"
                >
                    {{ tenant.name }}
                </option>
            </select>
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
                                Изменить
                            </BaseButton>
                            <BaseButton
                                variant="danger"
                                size="small"
                                @click="deleteUserHandler(user.id)"
                                :disabled="user.id === currentUserId"
                            >
                                Удалить
                            </BaseButton>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <UserUpdateForm
            v-if="editingUser"
            :userId="editingUserId"
            :currentUserData="editingUserData"
            @user-updated="onUserUpdated"
            @tenant-changed="refreshTenants"
            @cancel="cancelEditUser"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import UserUpdateForm from "./UserUpdateForm.vue";
import { getAllUsers, getAllTenants, deleteUser } from "@/features/admin/api";
import apiClient from "@/shared/api/ApiClient";

const emit = defineEmits(["back", "tenant-changed"]);

const users = ref([]);
const filteredUsers = ref([]);
const tenants = ref([]);
const usersLoading = ref(false);
const currentUserId = ref(null);
const selectedTenantFilter = ref(null);

const editingUser = ref(false);
const editingUserId = ref("");
const editingUserData = ref({});

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

const applyFilter = () => {
    if (!selectedTenantFilter.value) {
        filteredUsers.value = users.value;
    } else {
        filteredUsers.value = users.value.filter(
            (user) => user.tenantId === selectedTenantFilter.value,
        );
    }
};

const deleteUserHandler = async (userId) => {
    if (!confirm("Вы уверены, что хотите удалить этого пользователя?")) return;

    try {
        const response = await deleteUser(userId);
        if (response.success) {
            await loadUsers();
            emit("tenant-changed");
        }
    } catch (error) {
        console.error("Failed to delete user:", error);
        alert("Ошибка при удалении пользователя");
    }
};

const openEditUser = (user) => {
    editingUser.value = true;
    editingUserId.value = user.id;
    editingUserData.value = { ...user };
};

const cancelEditUser = () => {
    editingUser.value = false;
    editingUserId.value = "";
    editingUserData.value = {};
};

const onUserUpdated = async () => {
    editingUser.value = false;
    editingUserId.value = "";
    editingUserData.value = {};
    await loadUsers();
    emit("tenant-changed");
};

const getCurrentUserId = async () => {
    try {
        const response = await apiClient.get("/me");
        if (response.data.success) {
            currentUserId.value = response.data.data.id;
        }
    } catch (error) {
        console.error("Failed to get current user:", error);
    }
};

const refreshTenants = () => {
    loadTenants();
};

onMounted(() => {
    loadUsers();
    loadTenants();
    getCurrentUserId();
});
</script>

<style scoped>
.users-management {
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
.filter-section {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
}
.filter-select {
    padding: 8px;
    border: 1px solid #ced4da;
    border-radius: 4px;
    min-width: 200px;
}
.users-table {
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
.form-actions {
    display: flex;
    gap: 10px;
}
</style>
