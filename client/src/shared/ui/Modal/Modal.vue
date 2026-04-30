<template>
    <Teleport to="body">
        <div v-if="modelValue" class="modal-overlay" @click.self="close">
            <div class="modal-container">
                <div class="modal-header">
                    <h3>{{ title }}</h3>
                    <button class="modal-close" @click="close">&times;</button>
                </div>
                <div class="modal-body">
                    <slot />
                </div>
                <div class="modal-footer">
                    <slot name="footer" />
                </div>
            </div>
        </div>
    </Teleport>
</template>

<script setup>
const props = defineProps({
    modelValue: { type: Boolean, default: false },
    title: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue"]);

const close = () => {
    emit("update:modelValue", false);
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
.modal-container {
    background: white;
    border-radius: 8px;
    min-width: 400px;
    max-width: 600px;
    width: 90%;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}
.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #e9ecef;
}
.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #6c757d;
}
.modal-body {
    padding: 20px;
}
.modal-footer {
    padding: 12px 20px;
    border-top: 1px solid #e9ecef;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>
