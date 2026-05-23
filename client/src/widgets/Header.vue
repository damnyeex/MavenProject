<template>
    <header class="app-header">
        <nav>
            <router-link
                v-if="userTenantId && userTenantName"
                :to="{ name: 'tenant-detail', params: { id: userTenantId } }"
                class="nav-link"
            >
                <Icon name="Building2" :size="18" />
                {{ userTenantName }}
            </router-link>

            <span v-else-if="tenantsLoading" class="loading">
                <Icon name="Loader2" :size="18" class="spinner" />
            </span>

            <router-link v-if="isUser" to="/users" class="nav-link"
                ><Icon name="Users" :size="18" /><span
                    >Пользователи</span
                ></router-link
            >
            <router-link v-if="isAdmin" to="/tenants" class="nav-link"
                ><Icon name="Briefcase" :size="18" />
                <span>Отделы</span></router-link
            >
            <router-link v-if="isAdmin" to="/admin" class="nav-link">
                <Icon name="Shield" :size="18" />
                <span>Админ-панель</span></router-link
            >
            <div class="nav-action">
                <BaseButton variant="heading" @click="logout">
                    <Icon name="LogOut" :size="24" />
                    <span>Выйти</span>
                </BaseButton>
                <BaseButton variant="heading" @click="openMyProfile">
                    <Icon name="UserCircle" :size="24" />
                    <span>Мой профиль</span>
                </BaseButton>
            </div>
        </nav>
        <Modal v-model="showProfileModal" title="Мой профиль">
            <Profile :userId="currentUserId" />
        </Modal>
    </header>
</template>

<script setup>
import { useRouter } from "vue-router";
import { computed, ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import Modal from "@/shared/ui/Modal/Modal.vue";
import Icon from "@/shared/ui/Icon/Icon.vue";
import Profile from "@/features/profile/Profile.vue";
import { getCurrentUserId, getCurrentUserTenantId } from "@/shared/utils/token";
import { getOneTenant } from "@/features/admin/api";

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
    padding: 0.75rem 1rem;
}
nav {
    display: flex;
    gap: 1.5rem;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
}
.nav-link {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    color: white;
    text-decoration: none;
    font-weight: 500;
    padding: 0.5rem 0;
    border-bottom: 2px solid transparent;
    transition: border-color 0.2s;
}
.nav-link:hover {
    border-bottom-color: rgba(255, 255, 255, 0.5);
}
.router-link-active {
    border-bottom-color: white;
}
.nav-action {
    display: flex;
    gap: 1.5rem;
    margin-left: auto;
}
.tenant-link {
    background-color: rgba(255, 255, 255, 0.2);
    padding: 0.5rem 1rem;
    border-radius: 20px;
    border-bottom: none;
}
.tenant-link:hover {
    background-color: rgba(255, 255, 255, 0.3);
    border-bottom: none;
}
.tenant-loading {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    color: rgba(255, 255, 255, 0.8);
    font-size: 0.9rem;
    padding: 0.5rem 1rem;
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
