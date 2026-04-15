import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginRequest } from "./api";

export function useLogin() {
    const login = ref("");
    const password = ref("");
    const isLoading = ref(false);
    const errorMessage = ref("");
    const router = useRouter();

    const handleSubmit = async () => {
        isLoading.value = true;
        errorMessage.value = "";
        try {
            const response = await loginRequest(login.value, password.value);
            if (response.success && response.token) {
                localStorage.setItem("token", response.token);
                localStorage.setItem("userRole", response.role);
                await router.push("/");
            } else {
                errorMessage.value = "Ошибка авторизации";
            }
        } catch (error) {
            console.error(error);
            errorMessage.value =
                error.response?.data?.error || "Ошибка соединения с сервером";
        } finally {
            isLoading.value = false;
        }
    };

    return { login, password, isLoading, errorMessage, handleSubmit };
}
