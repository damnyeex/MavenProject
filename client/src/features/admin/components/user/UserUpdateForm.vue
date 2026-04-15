<template>
    <div class="update-user-form">
        <h3>Редактирование пользователя</h3>
        <form @submit.prevent="handleUpdateSubmit">
            <div class="form-group">
                <label>Логин</label>
                <BaseInput v-model="form.login" required />
            </div>
            <div class="form-group">
                <label>Новый пароль</label>
                <BaseInput type="password" v-model="form.password" />
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
            <div class="form-actions">
                <BaseButton type="submit" :disabled="isLoading">
                    {{ isLoading ? "Сохранение..." : "Сохранить изменения" }}
                </BaseButton>
                <BaseButton variant="secondary" @click="handleCancel">
                    Отмена
                </BaseButton>
            </div>
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
import { updateUser, getAllTenants } from "../../api";

const props = defineProps({
    userId: { type: String, required: true },
    currentUserData: { type: Object, required: true },
    onUserUpdated: { type: Function, default: null },
    onCancel: { type: Function, default: null },
});

const emit = defineEmits(["user-updated", "cancel"]);

const form = ref({
    login: "",
    password: "",
    role: "",
    tenantId: "",
});

const tenants = ref([]);
const isLoading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

const loadTenants = async () => {
    try {
        const response = await getAllTenants();
        if (response.success && response.data && response.data.length > 0) {
            tenants.value = response.data;
            if (tenants.value.length === 0) {
                errorMessage.value =
                    "Нет доступных отделов. Сначала создайте отдел.";
            }
        } else {
            errorMessage.value = "Не удалось загрузить список отделов";
        }
    } catch (error) {
        console.error("Failed to load tenants:", error);
        errorMessage.value = "Ошибка загрузки отделов";
    }
};

const loadUserData = () => {
    form.value = {
        login: props.currentUserData.login || "",
        password: "",
        role: props.currentUserData.role || "USER",
        tenantId: props.currentUserData.tenantId || null,
    };
};

const handleUpdateSubmit = async () => {
    if (!form.value.tenantId) {
        errorMessage.value = "Пожалуйста, выберите отдел для пользователя";
        return;
    }

    isLoading.value = true;
    errorMessage.value = "";
    successMessage.value = "";

    try {
        const updateData = {
            login: form.value.login,
            role: form.value.role,
            tenantId: form.value.tenantId,
        };

        if (form.value.password && form.value.password.trim() !== "") {
            updateData.password = form.value.password;
        }

        const response = await updateUser(props.userId, updateData);
        if (response.success) {
            successMessage.value = `Пользователь ${form.value.login} успешно обновлён!`;
            setTimeout(() => {
                if (props.onUserUpdated) props.onUserUpdated();
                emit("user-updated");
            }, 1500);
        } else {
            errorMessage.value =
                response.error || "Ошибка обновления пользователя";
        }
    } catch (error) {
        errorMessage.value = error.response?.data?.error || "Ошибка сервера";
    } finally {
        isLoading.value = false;
    }
};

const handleCancel = () => {
    if (props.onCancel) props.onCancel();
    emit("cancel");
};

onMounted(() => {
    loadTenants();
    loadUserData();
});
</script>

<style scoped>
.update-user-form {
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 30px;
    border-left: 4px solid #42b983;
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
.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 15px;
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
