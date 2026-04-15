<template>
    <button :type="type" class="base-button" :class="`base-button--${variant}`">
        <slot />
    </button>
</template>

<script setup>
defineProps({
    type: { type: String, default: "button" },
    variant: { type: String, default: "primary" },
});
</script>

<style scoped>
.base-button {
    padding: 8px 16px;
    border-radius: 4px;
    border: none;
    cursor: pointer;
    font-size: 14px;
}
.base-button--primary {
    background-color: #42b983;
    color: white;
}
.base-button--secondary {
    background-color: #6c757d;
    color: white;
}
.base-button--danger {
    background-color: #dc3545;
    color: white;
}
</style>
