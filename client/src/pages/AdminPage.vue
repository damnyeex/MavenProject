<template>
    <div class="admin-page">
        <!-- Главное меню админ-панели -->
        <div v-if="currentView === 'menu'" class="admin-menu">
            <h1>Административная панель</h1>

            <UserRegistrationForm @user-created="onUserCreated" />

            <div class="menu-buttons">
                <BaseButton variant="primary" @click="currentView = 'users'">
                    Управление пользователями
                </BaseButton>
                <BaseButton variant="primary" @click="currentView = 'tenants'">
                    Управление отделами
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
import BaseButton from "@/shared/ui/BaseButton.vue";
import UserRegistrationForm from "@/features/admin/components/user/UserRegistrationForm.vue";
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

const onUserCreated = () => {
    if (currentView.value === "users") {
    }
};

loadTenants();
</script>

<style scoped>
.admin-page {
    padding: 20px;
}
.admin-menu {
    max-width: 600px;
    margin: 0 auto;
    text-align: center;
}
.admin-menu h1 {
    margin-bottom: 30px;
    color: #333;
}
.menu-buttons {
    display: flex;
    flex-direction: column;
    gap: 15px;
    margin-top: 30px;
}
.menu-buttons .base-button {
    padding: 15px;
    font-size: 18px;
}
</style>
