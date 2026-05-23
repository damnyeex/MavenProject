<template>
    <component
        :is="iconComponent"
        :size="size"
        :color="color"
        class="icon"
        :class="className"
    />
</template>

<script setup>
import { computed } from "vue";
import * as Icons from "lucide-vue-next";

const props = defineProps({
    name: {
        type: String,
        required: true,
    },
    size: {
        type: [Number, String],
        default: 20,
    },
    color: {
        type: String,
        default: "currentColor",
    },
    class: {
        type: String,
        default: "",
    },
});

const className = computed(() => props.class);
const iconComponent = computed(() => Icons[props.name]);
</script>

<style scoped>
.icon {
    display: inline-block;
    vertical-align: middle;
}
</style>
