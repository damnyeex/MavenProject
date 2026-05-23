<template>
    <div class="users-page">
        <div class="page-header">
            <h2>Сотрудники</h2>
        </div>

        <div v-if="usersLoading" class="loading">
            <Icon name="Loader2" :size="18" class="spinner" />
        </div>
        <div v-else-if="users.length === 0" class="empty-state">
            Нет пользователей в вашем отделе
        </div>
        <ul v-else class="users-list">
            <li v-for="user in users" :key="user.id" class="user-card">
                <div class="user-info">
                    <div class="user-name">
                        {{ user.lastname }} {{ user.firstname }}
                        {{ user.middlename }}
                    </div>
                    <div class="user-meta">
                        <Icon name="AtSign" :size="16" />
                        <span>{{ user.login }}</span>
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

        <Modal v-model="showProfileModal" title="Профиль сотрудника">
            <Profile :userId="selectedUserId" />
        </Modal>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import Icon from "@/shared/ui/Icon/Icon.vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import Modal from "@/shared/ui/Modal/Modal.vue";
import Profile from "@/features/profile/Profile.vue";
import { getAllUsers, getAllTenants } from "@/features/admin/api";
import apiClient from "@/shared/api/ApiClient";

const users = ref([]);
const usersLoading = ref(false);
const showProfileModal = ref(false);
const selectedUserId = ref(null);

const loadUsers = async () => {
    usersLoading.value = true;
    try {
        const response = await getAllUsers();
        if (response.success) {
            users.value = response.data;
        }
    } catch (error) {
        console.error("Failed to load users:", error);
    } finally {
        usersLoading.value = false;
    }
};

const openProfile = (user) => {
    selectedUserId.value = user.id;
    showProfileModal.value = true;
};

onMounted(() => {
    loadUsers();
});
</script>

<style scoped>
.users-page {
    padding: 20px;
}
.page-header {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e9ecef;
    display: flex;
    gap: 12px;
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
    cursor: pointer;
}
.user-card:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.user-name {
    font-weight: bold;
    font-size: 1.1rem;
    margin-bottom: 6px;
}
.user-login,
.user-tenant {
    font-size: 0.9rem;
    color: #6c757d;
    margin-top: 4px;
}
.user-meta {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 0.85rem;
    color: #6c757d;
}
.details-button {
    display: inline-flex;
    align-items: center;
    gap: 6px;
}
.empty-state {
    text-align: center;
    padding: 60px;
    color: #6c757d;
    background: #f8f9fa;
    border-radius: 12px;
}
.empty-state svg {
    margin-bottom: 16px;
    opacity: 0.5;
}
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 60px;
    color: #6c757d;
}
.spinner {
    animation: spin 1s linear infinite;
}
@keyframes spin {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}
</style>
