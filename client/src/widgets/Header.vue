<template>
    <header class="app-header">
        <nav>
            <router-link to="/">Главная</router-link>
            <router-link to="/users">Пользователи</router-link>
            <router-link to="/tenants">Отделы</router-link>
            <router-link v-if="isAdmin" to="/admin">Админ-панель</router-link>
            <button @click="logout" class="logout-btn">Выйти</button>
        </nav>
    </header>
</template>

<script setup>
import { useRouter } from "vue-router";
import { computed } from "vue";

const router = useRouter();

const isAdmin = computed(() => localStorage.getItem("userRole") === "ADMIN");

const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    router.push("/login");
};
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
