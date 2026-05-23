<template>
    <div class="login-page">
        <div class="login-card">
            <div class="login-header">
                <h1>Вход в систему</h1>
            </div>
            <form @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label class="form-label">
                        <Icon name="User" :size="16" />
                        <span>Логин</span>
                    </label>
                    <BaseInput id="login" v-model="login" placeholder="admin">
                        <template #prefix>
                            <Icon name="User" :size="16" class="input-icon" />
                        </template>
                    </BaseInput>
                </div>
                <div class="form-group">
                    <label class="form-label">
                        <Icon name="Lock" :size="16" />
                        <span>Пароль</span>
                    </label>
                    <BaseInput
                        id="password"
                        type="password"
                        v-model="password"
                        placeholder="••••••"
                    >
                        <template #prefix>
                            <Icon name="Lock" :size="16" class="input-icon" />
                        </template>
                    </BaseInput>
                </div>
                <BaseButton
                    type="submit"
                    :disabled="isLoading"
                    class="login-button"
                >
                    <Icon v-if="!isLoading" name="LogIn" :size="18" />
                    <Icon v-else name="Loader2" :size="18" class="spinner" />
                    <span>{{ isLoading ? "Вход..." : "Войти" }}</span>
                </BaseButton>
                <p v-if="errorMessage" class="error-message">
                    <Icon name="AlertCircle" :size="16" />
                    <span>{{ errorMessage }}</span>
                </p>
            </form>
        </div>
    </div>
</template>

<script setup>
import { useLogin } from "@/features/auth/login/index";
import BaseInput from "@/shared/ui/BaseInput.vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import Icon from "@/shared/ui/Icon/Icon.vue";

const { login, password, isLoading, errorMessage, handleSubmit } = useLogin();
</script>

<style scoped>
.login-page {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    border-inline: none;
}
.login-card {
    background: white;
    padding: 2rem;
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 420px;
    transition: transform 0.2s;
}
.login-header {
    text-align: center;
    margin-bottom: 2rem;
}
.login-icon {
    color: #42b983;
    margin-bottom: 0.5rem;
}
.login-header h1 {
    margin: 0;
    font-size: 1.5rem;
    color: #333;
}
.form-group {
    margin-bottom: 1.25rem;
}
.form-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
    font-weight: 600;
    color: #555;
    font-size: 0.9rem;
}
.login-button {
    width: 100%;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.75rem;
    font-size: 1rem;
}
.error-message {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    color: #dc3545;
    margin-top: 1rem;
    text-align: center;
    font-size: 0.9rem;
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
