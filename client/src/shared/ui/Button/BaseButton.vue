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
    transition: all 0.2s;
}

.base-button--primary {
    background-color: #39ab78;
    color: white;
}
.base-button--primary:hover {
    background-color: #42b983;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.base-button--heading {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    color: white;
    background: transparent;
    cursor: pointer;
    margin-left: auto;
    font-size: 1rem;
    text-decoration: none;
    font-weight: 500;
    padding: 0.5rem 0;
    border-bottom: 2px solid transparent;
    transition: border-color 0.2s;
}
.base-button--heading:hover {
    border-bottom-color: rgba(255, 255, 255, 0.5);
}
.base-button--heading-active {
    border-bottom-color: white;
}

.base-button--secondary {
    background-color: #6c757d;
    color: white;
}
.base-button--secondary:hover {
    background-color: #7e8387;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}
.base-button--danger {
    background-color: #dc3545;
    color: white;
}
.base-button--danger:hover {
    background-color: #ec4959;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}
</style>
