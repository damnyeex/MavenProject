import apiClient from "@/shared/api/apiClient";

export const loginRequest = async (login, password) => {
    const response = await apiClient.post("/auth/login", { login, password });
    return response.data;
};
