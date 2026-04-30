<template>
    <header class="app-header">
        <nav>
            <router-link to="/">Главная</router-link>
            <router-link v-if="isUser" to="/users">Пользователи</router-link>
            <router-link v-if="isUser" to="/tenants">Отделы</router-link>
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
import { getCurrentUserId } from "@/shared/utils/token";

const router = useRouter();
const showProfileModal = ref(false);
const currentUserId = ref(null);

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

onMounted(() => {
    currentUserId.value = getCurrentUserId();
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
