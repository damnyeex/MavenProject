<template>
    <header class="app-header">
        <nav>
            <router-link
                v-if="userTenantId && userTenantName"
                :to="{ name: 'tenant-detail', params: { id: userTenantId } }"
                class="tenant-link"
            >
                {{ userTenantName }}
            </router-link>

            <div v-else-if="tenantsLoading" class="loading"></div>

            <router-link v-if="isUser" to="/users">Пользователи</router-link>
            <router-link v-if="isAdmin" to="/tenants">Отделы</router-link>
            <router-link v-if="isAdmin" to="/admin">Админ-панель</router-link>
            <button @click="logout" class="logout-btn">Выйти</button>
            <BaseButton variant="secondary" size="small" @click="openMyProfile">
                Мой профиль
            </BaseButton>
        </nav>
        <Modal v-model="showProfileModal" title="Мой профиль">
            <Profile :userId="currentUserId" />
        </Modal>
    </header>
</template>

<script setup>
import { useRouter } from "vue-router";
import { computed, ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import Modal from "@/shared/ui/Modal/Modal.vue";
import Profile from "@/features/profile/Profile.vue";
import { getCurrentUserId, getCurrentUserTenantId } from "@/shared/utils/token";
import { getOneTenant } from "@/features/admin/api";

import { useTenants } from "@/shared/composables/useTenants";
const {} = useTenants();

const router = useRouter();
const showProfileModal = ref(false);
const currentUserId = ref(null);
const userTenantId = ref(null);
const userTenantName = ref("");
const error = ref(null);
const tenantsLoading = ref(false);
const isAdmin = computed(() => localStorage.getItem("userRole") === "ADMIN");
const isUser = computed(() => localStorage.getItem("userRole") === "USER");

const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    router.push("/login");
};

const openMyProfile = () => {
    if (currentUserId.value) {
        showProfileModal.value = true;
    } else {
        console.error("User ID not available");
    }
};

const loadUserTenant = async () => {
    const tenantId = getCurrentUserTenantId();

    tenantsLoading.value = true;
    userTenantId.value = tenantId;

    try {
        const response = await getOneTenant(tenantId);
        if (response.success && response.data) {
            userTenantName.value = response.data.name;
        } else {
            console.error("Failed to load user's tenant:", response.error);
        }
    } catch (err) {
        console.error("Error loading user's tenant:", err);
    } finally {
        tenantsLoading.value = false;
    }
};

onMounted(async () => {
    currentUserId.value = getCurrentUserId();
    await loadUserTenant();
});
</script>

<style scoped>
.app-header {
    background-color: #42b983;
    color: white;
    padding: 1rem;
}
nav {
    display: flex;
    gap: 1rem;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
}
a {
    color: white;
    text-decoration: none;
    font-weight: bold;
}
.logout-btn {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    margin-left: auto;
    font-size: 1rem;
}
</style>
