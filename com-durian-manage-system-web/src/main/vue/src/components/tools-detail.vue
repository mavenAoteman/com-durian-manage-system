<template>
	<el-descriptions title="" :column="2" border>
		<el-descriptions-item>
			<template #label> 工具ID </template>
			{{ data.id }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 工具名称 </template>
			{{ data.name }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 工具品牌 </template>
			{{ data.brand }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 工具数量 </template>
			{{ data.quantity }}
		</el-descriptions-item>
		<el-descriptions-item >

			<template #label> 单价 </template>
			{{ data.unitPrice }}元
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 总价 </template>
			{{ data.totalPrice }}元
		</el-descriptions-item>

	</el-descriptions>
</template>

<script lang="ts" setup>
const props = defineProps({
	data: {
		type: Object,
		required: true
	}
});
</script>
