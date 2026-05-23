<template>
    <div class="tenant-page">
        <div class="page-header">
            <div class="header-actions">
                <BackButton @click="$emit('back')">
                    Назад к списку отделов
                </BackButton>
            </div>
        </div>

        <div v-if="tenantLoading" class="loading">
            <Icon name="Loader2" :size="18" class="spinner" />
        </div>

        <div v-else-if="error" class="error-message">
            {{ error }}
        </div>

        <div v-else-if="tenantData" class="tenant-content">
            <div class="tenant-info-header">
                <h2>{{ tenantData.name }}</h2>
                <p class="tenant-stats">
                    <Icon name="Users" :size="18" />
                    Всего сотрудников: {{ tenantData.userCount || 0 }}
                </p>
            </div>

            <h3 class="users-title">Сотрудники отдела</h3>

            <div v-if="usersLoading" class="loading">
                <Icon name="Loader2" :size="18" class="spinner" />
            </div>
            <div v-else-if="users.length === 0" class="empty-state">
                В этом отделе пока нет сотрудников
            </div>
            <ul v-else class="users-list">
                <li v-for="user in users" :key="user.id" class="user-card">
                    <div class="user-info">
                        <div class="user-name">
                            {{ user.lastname }} {{ user.firstname }}
                            {{ user.middlename }}
                        </div>
                    </div>
                    <div class="user-actions">
                        <BaseButton
                            variant="secondary"
                            size="small"
                            @click="openProfile(user)"
                            class="details-button"
                        >
                            <Icon name="Eye" :size="18" />
                            <span>Подробнее</span>
                        </BaseButton>
                    </div>
                </li>
            </ul>
        </div>

        <Modal v-model="showProfileModal" title="Профиль сотрудника">
            <Profile :userId="selectedUserId" />
        </Modal>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import Icon from "@/shared/ui/Icon/Icon.vue";
import BackButton from "@/shared/ui/Button/BackButton.vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import Modal from "@/shared/ui/Modal/Modal.vue";
import Profile from "@/features/profile/Profile.vue";
import { getOneTenant } from "@/features/admin/api";

const props = defineProps({
    tenantId: { type: String, required: true },
});

const emit = defineEmits(["back"]);

const tenantData = ref(null);
const users = ref([]);
const tenantLoading = ref(false);
const usersLoading = ref(false);
const showProfileModal = ref(false);
const selectedUserId = ref(null);
const error = ref(null);

const loadTenant = async () => {
    if (!props.tenantId) {
        error.value = "ID отдела не указан";
        return;
    }

    tenantLoading.value = true;
    error.value = null;
    tenantData.value = null;

    try {
        const response = await getOneTenant(props.tenantId);

        if (response.success && response.data) {
            tenantData.value = response.data;
            users.value = response.data.users || [];
        } else {
            error.value = response.error || "Отдел не найден";
            console.error("Failed to load tenant:", response.error);
        }
    } catch (err) {
        console.error("Error loading tenant:", err);
        error.value = "Ошибка загрузки данных отдела";
    } finally {
        tenantLoading.value = false;
    }
};

const openProfile = (user) => {
    selectedUserId.value = user.id;
    showProfileModal.value = true;
};

onMounted(() => {
    loadTenant();
});
</script>

<style scoped>
.tenant-page {
    padding: 20px;
}
.page-header {
    display: flex;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e9ecef;
}
.header-actions {
    display: flex;
    justify-content: flex-start;
}
.tenant-content {
    margin-top: 20px;
}
.tenant-info-header {
    margin-bottom: 30px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}
.tenant-info-header h2 {
    margin: 0 0 8px 0;
    color: #333;
}
.tenant-description {
    color: #6c757d;
    margin: 8px 0;
}
.tenant-stats {
    margin: 8px 0 0 0;
    font-weight: bold;
    color: #42b983;
}
.users-title {
    margin: 20px 0 16px 0;
    padding-bottom: 8px;
    border-bottom: 1px solid #e9ecef;
}
.users-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.user-card {
    background: white;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    padding: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: box-shadow 0.2s;
}
.user-card:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.user-name {
    font-weight: bold;
    font-size: 1.1rem;
    margin-bottom: 6px;
}
.details-button {
    display: inline-flex;
    align-items: center;
    gap: 6px;
}
.empty-state {
    text-align: center;
    padding: 40px;
    color: #6c757d;
    background: #f8f9fa;
    border-radius: 8px;
}
.loading {
    text-align: center;
    padding: 40px;
}
.error-message {
    text-align: center;
    padding: 40px;
    color: #dc3545;
    background: #f8f9fa;
    border-radius: 8px;
}
</style>
