<template>
	<el-descriptions title="" :column="2" border>
		<el-descriptions-item>
			<template #label> 物料名称 </template>
			{{ data.name }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 物料品牌 </template>
			{{ data.brand }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 采购渠道 </template>
			{{ data.purchaseChannel }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 采购时间 </template>
			{{ formatDate(data.purchaseTime) }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 物料数量 </template>
			{{ data.quantity }} {{ data.unit }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 物料类型 </template>
			{{ getConsumableTypeText(data.consumableType) }}
		</el-descriptions-item>
		<el-descriptions-item >
			<template #label> 单价 </template>
			{{ data.unitPrice }}元
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 总价 </template>
			{{ data.totalPrice }}元
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 状态 </template>
			{{ getStatusText(data.status) }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 过期日期 </template>
			{{ data.expiryDate }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 修改人 </template>
			{{ data.updatedBy }}
		</el-descriptions-item>
		<el-descriptions-item>
			<template #label> 修改时间 </template>
			{{ data.modifiedStr }}
		</el-descriptions-item>

	</el-descriptions>
</template>

<script lang="ts" setup>
// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return dateString;
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

const props = defineProps({
	data: {
		type: Object,
		required: true
	}
});

// 物料类型映射
const getConsumableTypeText = (type) => {
	const typeMap = {
		0: '其它',
		1: '油脂',
		2: '精油',
		3: '添加物',
		4: '水相'
	};
	return typeMap[type] || '其它';
};

// 状态映射
const getStatusText = (status) => {
	const statusMap = {
		0: '已删除',
		1: '可用',
		2: '已用完',
		3: '已过期'
	};
	return statusMap[status] || '未知';
};
</script>