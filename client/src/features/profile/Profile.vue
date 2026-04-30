<template>
    <div class="profile-view">
        <div v-if="loading" class="loading">Загрузка...</div>
        <div v-else-if="userData" class="profile-content">
            <div class="profile-field">
                <label>Фамилия:</label>
                <span>{{ userData.lastname || "—" }}</span>
            </div>
            <div class="profile-field">
                <label>Имя:</label>
                <span>{{ userData.firstname || "—" }}</span>
            </div>
            <div class="profile-field">
                <label>Отчество:</label>
                <span>{{ userData.middlename || "—" }}</span>
            </div>
            <div class="profile-field">
                <label>Логин:</label>
                <span>{{ userData.login || "—" }}</span>
            </div>
            <div class="profile-field">
                <label>Отдел:</label>
                <span>{{ tenantName }}</span>
            </div>
        </div>
        <div v-else-if="error" class="error-message">
            {{ error }}
        </div>
        <div v-else class="error-message">
            Не удалось загрузить данные пользователя
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { getOneUser } from "@/features/admin/api";
import { useTenants } from "@/shared/composables/useTenants";

const { getTenantName, loadTenants } = useTenants();

const props = defineProps({
    userId: { type: String, required: true },
});

const userData = ref(null);
const loading = ref(false);
const error = ref(null);
const tenantName = ref("");

const loadUser = async () => {
    if (!props.userId) {
        error.value = "ID пользователя не указан";
        return;
    }

    loading.value = true;
    error.value = null;
    userData.value = null;
    tenantName.value = "";

    try {
        const response = await getOneUser(props.userId);

        if (response.success && response.data) {
            userData.value = response.data;
            if (userData.value.tenantId) {
                await loadTenants();
                tenantName.value = getTenantName(userData.value.tenantId);
            }
        } else {
            error.value = response.error || "Пользователь не найден";
            console.error("Failed to load user:", response.error);
        }
    } catch (err) {
        console.error("Error loading user:", err);
        error.value = "Ошибка загрузки данных пользователя";
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    loadUser();
    loadTenants();
});
</script>

<style scoped>
.profile-view {
    padding: 0;
}
.profile-field {
    margin-bottom: 15px;
    display: flex;
    align-items: baseline;
}
.profile-field label {
    width: 100px;
    font-weight: bold;
    color: #495057;
}
.profile-field span {
    flex: 1;
    color: #212529;
}
.loading,
.error-message {
    text-align: center;
    padding: 20px;
}
.error-message {
    color: #dc3545;
}
</style>
