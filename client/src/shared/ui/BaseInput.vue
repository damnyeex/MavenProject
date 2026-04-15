<template>
    <input
        :type="type"
        :value="modelValue"
        @input="onInput"
        class="base-input"
        :placeholder="placeholder"
    />
</template>

<script setup>
defineProps({
    type: { type: String, default: "text" },
    modelValue: { type: [String, Number], default: "" },
    placeholder: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue"]);
const onInput = (e) => emit("update:modelValue", e.target.value);
</script>

<style scoped>
.base-input {
    padding: 8px;
    border: 1px solid #ced4da;
    border-radius: 4px;
    width: 100%;
    box-sizing: border-box;
}
</style>
