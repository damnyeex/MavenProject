<template>
    <button class="back-button" @click="$emit('click')">
        <Icon name="ArrowLeft" :size="24" />
        <slot />
    </button>
</template>

<script setup>
import Icon from "@/shared/ui/Icon/Icon.vue";

defineEmits(["click"]);
</script>

<style scoped>
.back-button {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: none;
    border: none;
    color: #42b983;
    cursor: pointer;
    font-size: 0.9rem;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    transition: background-color 0.2s;
}
.back-button:hover {
    background-color: rgba(66, 185, 131, 0.1);
}
</style>
