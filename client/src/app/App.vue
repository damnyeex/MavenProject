<template>
    <div class="app">
        <Header v-if="isAuthenticated" />
        <main class="app-main">
            <router-view />
        </main>
    </div>
</template>
<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import Header from "../widgets/Header.vue";

const route = useRoute();
// Показываем шапку не на странице логина
const isAuthenticated = computed(() => route.path !== "/login");
</script>
