<template>
    <div class="login-page">
        <div class="login-card">
            <h1>Вход в систему</h1>
            <form @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label for="login">Логин</label>
                    <BaseInput id="login" v-model="login" placeholder="admin" />
                </div>
                <div class="form-group">
                    <label for="password">Пароль</label>
                    <BaseInput
                        id="password"
                        type="password"
                        v-model="password"
                    />
                </div>
                <BaseButton type="submit" :disabled="isLoading">
                    {{ isLoading ? "Вход..." : "Войти" }}
                </BaseButton>
                <p v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                </p>
            </form>
        </div>
    </div>
</template>

<script setup>
import { useLogin } from "../features/auth/login/index";
import BaseInput from "../shared/ui/BaseInput.vue";
import BaseButton from "../shared/ui/BaseButton.vue";

const { login, password, isLoading, errorMessage, handleSubmit } = useLogin();
</script>

<style scoped>
.login-page {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}
.login-card {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
}
.form-group {
    margin-bottom: 1rem;
}
label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: bold;
}
.error-message {
    color: #dc3545;
    margin-top: 1rem;
    text-align: center;
}
</style>
