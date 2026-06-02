<template>
	<el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
		<el-form-item label="油脂名称" prop="oilChineseName">
			<el-input v-model="form.oilChineseName"></el-input>
		</el-form-item>
    <el-form-item label="英文名" prop="oilEnglishName">
      <el-input v-model="form.oilEnglishName" ></el-input>
    </el-form-item>
		<el-form-item label="NaOH皂化价" prop="sodiumHydroxideSaponificationValue" >
			<el-input v-model.number="form.sodiumHydroxideSaponificationValue" type="number" step="0.0001"></el-input>
		</el-form-item>
    <el-form-item label="INS" prop="insValue" >
      <el-input v-model.number="form.insValue" type="number" step="0.01"></el-input>
    </el-form-item>

		<el-form-item>
			<el-button type="primary" @click="addOrUpdateSoapToolsMethod()">保 存</el-button>
		</el-form-item>
	</el-form>
</template>

<script lang="ts" setup>
import {ElMessage, FormInstance, FormRules} from 'element-plus';
import {reactive, ref} from 'vue';
import {addOil, queryByPageOils} from "../api";

const props = defineProps({
	data: {
		type: Object,
		required: true
	},
	edit: {
		type: Boolean,
		required: false
	},
	update: {
		type: Function,
		required: true
	}
});

const defaultData = {
	id: '',
	oilChineseName: '',
  oilEnglishName: '',
  sodiumHydroxideSaponificationValue: 0,
  insValue: 0,
  createdBy: '',
  updatedBy: '',
  createdStr: '',
  modifiedStr: '',
};

const form = ref({ ...(props.edit ? props.data : defaultData) });


const rules: FormRules = {
	oilChineseName: [{ required: true, message: '油脂名称', trigger: 'blur' }],
  sodiumHydroxideSaponificationValue: [{ required: true, message: '皂化价(NaOH)', trigger: 'blur' }],
  insValue: [{ required: true, message: 'INS', trigger: 'blur' }],
};

const query = reactive({
  pageIndex: 1,
  pageSize: 10
});
const formRef = ref<FormInstance>();
const saveEdit = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.validate(valid => {
		if (!valid) return false;

		ElMessage.success('保存成功！');
	});
};

// 添加
function addOrUpdateSoapToolsMethod() {
  console.log("addOilsMethod");
  addOil(form.value).then(response => {
    // props.update(form.value);
    // 直接刷新页面
    location.reload();
    // const res =  queryByPageOils(query);
    ElMessage.success('保存成功！');
  });
}

</script>

<style>
.avatar-uploader .el-upload {
	border: 1px dashed var(--el-border-color);
	border-radius: 6px;
	cursor: pointer;

	position: relative;
	overflow: hidden;
	transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
	border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 178px;
	height: 178px;
	text-align: center;
}
</style>