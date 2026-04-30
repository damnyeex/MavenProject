<template>
    <div class="registration-form">
        <h3>Создать нового пользователя</h3>
        <form @submit.prevent="handleSubmit">
            <div class="form-group">
                <label>Логин</label>
                <BaseInput v-model="form.login" required />
            </div>
            <div class="form-group">
                <label>Пароль</label>
                <BaseInput type="password" v-model="form.password" required />
            </div>
            <div class="form-group">
                <label>Фамилия</label>
                <BaseInput v-model="form.lastname" required />
            </div>
            <div class="form-group">
                <label>Имя</label>
                <BaseInput v-model="form.firstname" required />
            </div>
            <div class="form-group">
                <label>Отчество</label>
                <BaseInput v-model="form.middlename" required />
            </div>
            <div class="form-group">
                <label>Роль</label>
                <select v-model="form.role" class="base-select">
                    <option value="USER">Пользователь</option>
                    <option value="ADMIN">Администратор</option>
                </select>
            </div>
            <div class="form-group">
                <label>Тенант (отдел)</label>
                <select v-model="form.tenantId" class="base-select">
                    <option
                        v-for="tenant in tenants"
                        :key="tenant.id"
                        :value="tenant.id"
                    >
                        {{ tenant.name }}
                    </option>
                </select>
            </div>
            <BaseButton type="submit" :disabled="isLoading">
                {{ isLoading ? "Создание..." : "Создать пользователя" }}
            </BaseButton>
            <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
            <p v-if="successMessage" class="success-message">
                {{ successMessage }}
            </p>
        </form>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseInput from "@/shared/ui/BaseInput.vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import { createUser, getAllTenants } from "../../api";

const emit = defineEmits(["user-created"]);

const form = ref({
    login: "",
    password: "",
    lastname: "",
    firstname: "",
    middlename: "",
    role: "USER",
    tenantId: "",
});

const tenants = ref([]);
const isLoading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

const loadTenants = async () => {
    try {
        const response = await getAllTenants();
        if (response.success && response.data) {
            tenants.value = response.data;
            if (tenants.value.length > 0) {
                form.value.tenantId = tenants.value[0].id;
            } else {
                errorMessage.value =
                    "Нет доступных отделов. Сначала создайте отдел.";
            }
        }
    } catch (error) {
        console.error("Failed to load tenants:", error);
        errorMessage.value = "Ошибка загрузки отделов";
    }
};

const handleSubmit = async () => {
    if (!form.value.tenantId) {
        errorMessage.value = "Пожалуйста, выберите отдел для пользователя";
        return;
    }

    isLoading.value = true;
    errorMessage.value = "";
    successMessage.value = "";

    try {
        const response = await createUser(form.value);
        if (response.success) {
            successMessage.value = `Пользователь ${form.value.login} успешно создан!`;
            form.value = {
                login: "",
                password: "",
                lastname: "",
                firstname: "",
                middlename: "",
                role: "USER",
                tenantId: tenants.value[0]?.id || "",
            };
            emit("user-created");
            setTimeout(() => {
                successMessage.value = "";
            }, 3000);
        } else {
            errorMessage.value =
                response.error || "Ошибка создания пользователя";
        }
    } catch (error) {
        errorMessage.value = error.response?.data?.error || "Ошибка сервера";
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    loadTenants();
});
</script>

<style scoped>
.registration-form {
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 50px;
}
.form-group {
    margin-bottom: 15px;
}
label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}
.base-select {
    width: 100%;
    padding: 8px;
    border: 1px solid #ced4da;
    border-radius: 4px;
}
.error-message {
    color: #dc3545;
    margin-top: 10px;
}
.success-message {
    color: #28a745;
    margin-top: 10px;
}
</style>
