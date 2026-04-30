import { createRouter, createWebHistory } from "vue-router";

import UsersPage from "../pages/UsersPage.vue";
import LoginPage from "../pages/LoginPage.vue";
import AdminPage from "../pages/AdminPage.vue";

const routes = [
    {
        path: "/login",
        component: LoginPage,
        meta: { requiresGuest: true },
    },
    {
        path: "/admin",
        component: AdminPage,
        meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
        path: "/users",
        component: UsersPage,
        meta: { requiresAuth: true, requiresUser: true },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// --- Защита маршрутов (Guard) ---
router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem("token");
    const userRole = localStorage.getItem("userRole");

    if (to.meta.requiresAuth && !isAuthenticated) {
        next("/login");
        return;
    }

    if (to.meta.requiresAdmin && userRole !== "ADMIN") {
        next("/");
        return;
    }

    if (to.meta.requiresUser && userRole !== "USER") {
        next("/");
        return;
    }

    if (to.meta.requiresGuest && isAuthenticated) {
        next("/");
        return;
    }

    next();
});

export default router;
