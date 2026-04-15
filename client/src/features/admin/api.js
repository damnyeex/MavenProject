import apiClient from "@/shared/api/apiClient";

// Получить всех пользователей
export const getAllUsers = async () => {
    const response = await apiClient.get("/users");
    return response.data;
};

// Получить пользователя
export const getOneUser = async () => {
    const response = await apiClient.get("/users");
    return response.data;
};

// Получить все тенанты
export const getAllTenants = async () => {
    const response = await apiClient.get("/tenants");
    return response.data;
};

// Получить тенант
export const getOneTenant = async () => {
    const response = await apiClient.get("/tenants");
    return response.data;
};

// Cоздание пользователя
export const createUser = async (userData) => {
    const response = await apiClient.post("/auth/register", userData);
    return response.data;
};

// Обновить пользователя
export const updateUser = async (userId, userData) => {
    const response = await apiClient.put(`/users/${userId}`, userData);
    return response.data;
};

// Удалить пользователя
export const deleteUser = async (userId) => {
    const response = await apiClient.delete(`/users/${userId}`);
    return response.data;
};

// Создать тенант
export const createTenant = async (tenantData) => {
    const response = await apiClient.post("/tenants", tenantData);
    return response.data;
};

// Обновить тенант
export const updateTenant = async (tenantId, tenantData) => {
    const response = await apiClient.put(`/tenants/${tenantId}`, tenantData);
    return response.data;
};

// Удалить тенант
export const deleteTenant = async (tenantId) => {
    const response = await apiClient.delete(`/tenants/${tenantId}`);
    return response.data;
};
