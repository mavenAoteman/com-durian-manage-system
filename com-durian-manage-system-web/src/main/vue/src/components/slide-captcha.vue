<template>
    <div class="slide-captcha" :class="{ verified, dragging }">
        <div class="track">
            <div class="track-fill" :style="{ width: progressPx + 'px' }"></div>
            <div class="track-text" v-if="!verified">{{ tipText }}</div>
            <div class="track-text track-text-success" v-else>
                <el-icon><Check /></el-icon>&nbsp;验证通过
            </div>
            <div
                class="slider"
                :style="{ transform: `translateX(${progressPx}px)` }"
                @mousedown="onStart"
                @touchstart="onStart"
            >
                <el-icon v-if="!verified"><DArrowRight /></el-icon>
                <el-icon v-else><Check /></el-icon>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { DArrowRight, Check } from '@element-plus/icons-vue';

const props = defineProps<{ modelValue: boolean }>();
const emit = defineEmits<{
    (e: 'update:modelValue', val: boolean): void;
    (e: 'verify'): void;
}>();

const TRACK_WIDTH = 290; // 与 CSS 中 .track 宽度一致
const SLIDER_WIDTH = 40;
const TOLERANCE_PX = 8;

const verified = ref(props.modelValue);
const dragging = ref(false);
const progressPx = ref(0);
const startX = ref(0);
const startProgress = ref(0);

const tipText = computed(() => (dragging.value ? '继续向右拖动' : '请按住滑块拖动至右侧'));

const onStart = (e: MouseEvent | TouchEvent) => {
    if (verified.value) return;
    dragging.value = true;
    startProgress.value = progressPx.value;
    if (e instanceof TouchEvent) {
        startX.value = e.touches[0].clientX;
    } else {
        startX.value = e.clientX;
    }
    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseup', onEnd);
    document.addEventListener('touchmove', onMove, { passive: false });
    document.addEventListener('touchend', onEnd);
    e.preventDefault();
};

const onMove = (e: MouseEvent | TouchEvent) => {
    if (!dragging.value) return;
    let clientX: number;
    if (e instanceof TouchEvent) {
        clientX = e.touches[0].clientX;
        e.preventDefault();
    } else {
        clientX = e.clientX;
    }
    const delta = clientX - startX.value;
    let next = startProgress.value + delta;
    const max = TRACK_WIDTH - SLIDER_WIDTH;
    if (next < 0) next = 0;
    if (next > max) next = max;
    progressPx.value = next;
};

const onEnd = () => {
    if (!dragging.value) return;
    dragging.value = false;
    document.removeEventListener('mousemove', onMove);
    document.removeEventListener('mouseup', onEnd);
    document.removeEventListener('touchmove', onMove);
    document.removeEventListener('touchend', onEnd);
    const max = TRACK_WIDTH - SLIDER_WIDTH;
    if (progressPx.value >= max - TOLERANCE_PX) {
        progressPx.value = max;
        verified.value = true;
        emit('update:modelValue', true);
        emit('verify');
    } else {
        // 没拖到，回弹
        progressPx.value = 0;
    }
};

const reset = () => {
    verified.value = false;
    progressPx.value = 0;
    emit('update:modelValue', false);
};

defineExpose({ reset });

onBeforeUnmount(() => {
    document.removeEventListener('mousemove', onMove);
    document.removeEventListener('mouseup', onEnd);
    document.removeEventListener('touchmove', onMove);
    document.removeEventListener('touchend', onEnd);
});

onMounted(() => {
    verified.value = props.modelValue;
});
</script>

<style scoped>
.slide-captcha {
    user-select: none;
    width: 100%;
}
.track {
    position: relative;
    width: 290px;
    height: 40px;
    background: #f0f2f5;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}
.track-fill {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    background: linear-gradient(90deg, #b3e5fc, #4fc3f7);
    transition: width 0.05s ease-out;
}
.slide-captcha.verified .track-fill {
    background: linear-gradient(90deg, #c8e6c9, #66bb6a);
}
.track-text {
    position: absolute;
    width: 100%;
    height: 100%;
    line-height: 40px;
    text-align: center;
    color: #909399;
    font-size: 13px;
    pointer-events: none;
}
.track-text-success {
    color: #fff;
    font-weight: 600;
}
.slider {
    position: absolute;
    left: 0;
    top: 0;
    width: 40px;
    height: 40px;
    background: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
    cursor: grab;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #409eff;
    transition: transform 0.05s ease-out;
}
.slide-captcha.dragging .slider {
    cursor: grabbing;
    box-shadow: 0 0 6px rgba(64, 158, 255, 0.4);
}
.slide-captcha.verified .slider {
    color: #fff;
    background: #67c23a;
    border-color: #67c23a;
    cursor: default;
}
</style>
