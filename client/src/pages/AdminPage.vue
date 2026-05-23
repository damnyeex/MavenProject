<template>
    <div class="admin-page">
        <!-- Главное меню админ-панели -->
        <div v-if="currentView === 'menu'" class="admin-menu">
            <div class="admin-header">
                <Icon name="ShieldCheck" :size="40" class="admin-icon" />
                <h1>Административная панель</h1>
                <p class="admin-subtitle">
                    Управление системой и пользователями
                </p>
            </div>

            <div class="menu-buttons">
                <BaseButton
                    variant="primary"
                    @click="currentView = 'users'"
                    class="menu-button"
                >
                    <Icon name="UsersRound" :size="20" />
                    <span>Управление пользователями</span>
                    <Icon name="ArrowRight" :size="18" class="arrow-icon" />
                </BaseButton>

                <BaseButton
                    variant="primary"
                    @click="currentView = 'tenants'"
                    class="menu-button"
                >
                    <Icon name="LayoutGrid" :size="20" />
                    <span>Управление отделами</span>
                    <Icon name="ArrowRight" :size="18" class="arrow-icon" />
                </BaseButton>
            </div>
        </div>

        <!-- Страница управления пользователями -->
        <UserManagement
            v-else-if="currentView === 'users'"
            @back="currentView = 'menu'"
            @tenant-changed="refreshTenants"
        />

        <!-- Страница управления тенантами -->
        <TenantsManagement
            v-else-if="currentView === 'tenants'"
            @back="currentView = 'menu'"
            @tenant-changed="refreshTenants"
        />
    </div>
</template>

<script setup>
import { ref } from "vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import Icon from "@/shared/ui/Icon/Icon.vue";
import UserManagement from "@/features/admin/components/user/UserManagement.vue";
import TenantsManagement from "@/features/admin/components/tenant/TenantsManagement.vue";
import { getAllTenants } from "@/features/admin/api";

const currentView = ref("menu");
const tenants = ref([]);

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

const refreshTenants = () => {
    loadTenants();
};

loadTenants();
</script>

<style scoped>
.admin-page {
    padding: 20px;
}
.admin-menu {
    max-width: 550px;
    margin: 40px auto;
    text-align: center;
}
.admin-header {
    margin-bottom: 40px;
}
.admin-icon {
    color: #42b983;
    margin-bottom: 16px;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}
.admin-menu h1 {
    margin: 0 0 8px 0;
    color: #2c3e50;
}
.admin-subtitle {
    color: #6c757d;
    margin: 0;
}
.menu-buttons {
    display: flex;
    flex-direction: column;
    gap: 16px;
}
.menu-button {
    display: inline-flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 16px 24px;
    font-size: 1rem;
    font-weight: 500;
}
.menu-button span {
    flex: 1;
    text-align: left;
    margin-left: 12px;
}
.arrow-icon {
    opacity: 0.7;
    transition: transform 0.2s;
}
.menu-button:hover .arrow-icon {
    transform: translateX(4px);
    opacity: 1;
}
</style>
