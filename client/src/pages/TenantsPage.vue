<template>
    <div class="tanants-page">
        <div v-if="currentView === 'tenants'">
            <div class="page-header">
                <h2>Отделы</h2>
            </div>

            <div v-if="tenantsLoading" class="loading">Загрузка...</div>
            <div
                v-else-if="!tenants || tenants.length === 0"
                class="empty-state"
            >
                Нет доступных отделов
            </div>
            <ul v-else class="tenants-list">
                <li
                    v-for="tenant in tenants"
                    :key="tenant.id"
                    class="tenant-card"
                >
                    <div class="tenant-info">
                        <div class="tenant-name">
                            {{ tenant.name }}
                        </div>
                    </div>
                    <div class="tenant-actions">
                        <BaseButton
                            variant="secondary"
                            size="small"
                            @click="
                                (openTenant(tenant), (currentView = 'detail'))
                            "
                        >
                            Подробнее
                        </BaseButton>
                    </div>
                </li>
            </ul>
        </div>

        <TenantDetail
            v-else-if="currentView === 'detail'"
            :tenantId="selectedTenantId"
            @back="currentView = 'tenants'"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import BaseButton from "@/shared/ui/BaseButton.vue";
import { useTenants } from "@/shared/composables/useTenants";
import TenantDetail from "@/pages/TenantDetail/TenantDetail.vue";

const { loadTenants, tenants } = useTenants();
const currentView = ref("tenants");
const tenantsLoading = ref(false);
const selectedTenantId = ref(null);

const openTenant = (tenant) => {
    selectedTenantId.value = tenant.id;
};

onMounted(async () => {
    tenantsLoading.value = true;
    await loadTenants();
    tenantsLoading.value = false;
});
</script>

<style scoped>
.tanants-page {
    padding: 20px;
}
.page-header {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e9ecef;
}
.tenants-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.tenant-card {
    background: white;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    padding: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: box-shadow 0.2s;
    cursor: pointer;
}
.tenant-card:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.tenant-info {
    flex: 1;
}
.tenant-name {
    font-weight: bold;
    font-size: 1.1rem;
    margin-bottom: 6px;
}
.empty-state {
    text-align: center;
    padding: 40px;
    color: #6c757d;
    background: #f8f9fa;
    border-radius: 8px;
}
.loading {
    text-align: center;
    padding: 40px;
}
</style>
