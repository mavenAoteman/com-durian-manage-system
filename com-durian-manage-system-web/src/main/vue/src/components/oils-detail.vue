<template>
	<el-descriptions title="" :column="2" border>
		<el-descriptions-item>
			<template #label> 油脂ID </template>
			{{ data.id }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 油脂中文名 </template>
			{{ data.oilChineseName }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 油脂英文名 </template>
			{{ data.oilEnglishName }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 皂化价(NaOH) </template>
			{{ data.sodiumHydroxideSaponificationValue }}
		</el-descriptions-item>
		<el-descriptions-item >

			<template #label> INS </template>
			{{ data.insValue }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 创建人 </template>
			{{ data.creator }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 修改人 </template>
			{{ data.modifier }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 创建时间 </template>
			{{ data.createdStr }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 修改时间 </template>
			{{ data.modifiedStr }}
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