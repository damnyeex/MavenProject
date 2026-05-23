<template>
    <div class="dashboard-page">
        <div class="dashboard-header"></div>

        <div v-if="userTenantId" class="dashboard-content">
            <div class="dashboard-info-header">
                <h2>{{ TenantDescription }}</h2>
            </div>
        </div>

        <div v-else-if="tenantLoading" class="loading">
            Загрузка данных отдела...
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/Button/BaseButton.vue";
import { getOneTenant } from "@/features/admin/api";
import { getCurrentUserTenantId } from "../shared/utils/token";

const userTenantId = ref(null);
const tenantLoading = ref(false);
const TenantDescription = ref("");

const loadTenantInfo = async () => {
    const tenantId = getCurrentUserTenantId();

    tenantLoading.value = true;
    userTenantId.value = tenantId;

    try {
        const response = await getOneTenant(tenantId);
        if (response.success && response.data) {
            TenantDescription.value = response.data.description;
        } else {
            console.error("Failed to load user's tenant:", response.error);
        }
    } catch (err) {
        console.error("Error loading user's tenant:", err);
    } finally {
        tenantLoading.value = false;
    }
};

onMounted(async () => {
    await loadTenantInfo();
});
</script>

<style scoped>
.dashboard-page {
    padding: 20px;
}
.dashboard-header {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e9ecef;
}

.dashboard-content {
    margin-top: 20px;
}
.dashboard-info-header {
    margin-bottom: 30px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
}
.dashboard-info-header h2 {
    margin: 0 0 8px 0;
    color: #333;
}
.dashboard-description {
    color: #6c757d;
    margin: 8px 0;
}
.dashboard-stats {
    margin: 8px 0 0 0;
    font-weight: bold;
    color: #42b983;
}
.loading {
    text-align: center;
    padding: 40px;
}
.error-message {
    text-align: center;
    padding: 40px;
    color: #dc3545;
    background: #f8f9fa;
    border-radius: 8px;
}
</style>
