<template>
	<el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
		<el-form-item label="工具名称" prop="name">
			<el-input v-model="form.name"></el-input>
		</el-form-item>
    <el-form-item label="品牌" prop="brand">
      <el-input v-model="form.brand" ></el-input>
    </el-form-item>
		<el-form-item label="数量" prop="quantity" >
			<el-input v-model.number="form.quantity" @blur="calculateTotalPrice"></el-input>
		</el-form-item>
    <el-form-item label="单价" prop="unitPrice" >
      <el-input v-model.number="form.unitPrice"  @blur="calculateTotalPrice"></el-input>
    </el-form-item>
    <el-form-item label="总价" prop="totalPrice">
      <el-input v-model.number="form.totalPrice" readonly></el-input>
    </el-form-item>

		<el-form-item>
			<el-button type="primary" @click="addOrUpdateSoapToolsMethod()">保 存</el-button>
		</el-form-item>
	</el-form>
</template>

<script lang="ts" setup>
import {ElMessage, FormInstance, FormRules, UploadProps} from 'element-plus';
import {reactive, ref} from 'vue';
import {addSoapTools, queryByPageSoapTools} from "../api";

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
	name: '',
  brand: '',
  quantity: 1,
  unitPrice: 0,
  totalPrice: 0,
  createdBy: '',
  updatedBy: '',
  createdStr: '',
  modifiedStr: '',
};

const form = ref({ ...(props.edit ? props.data : defaultData) });


const rules: FormRules = {
	name: [{ required: true, message: '工具名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '单价', trigger: 'blur' }],
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

const handleAvatarSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
	form.value.thumb = URL.createObjectURL(uploadFile.raw!);
};

const beforeAvatarUpload: UploadProps['beforeUpload'] = rawFile => {
	if (rawFile.type !== 'image/jpeg') {
		ElMessage.error('Avatar picture must be JPG format!');
		return false;
	} else if (rawFile.size / 1024 / 1024 > 2) {
		ElMessage.error('Avatar picture size can not exceed 2MB!');
		return false;
	}
	return true;
};
// 更新总价
function calculateTotalPrice() {
  form.value.totalPrice = form.value.quantity * form.value.unitPrice;
}

// 添加
function addOrUpdateSoapToolsMethod() {
  console.log("addSoapToolsMethod");
  addSoapTools(form.value).then(response => {
    // props.update(form.value);
    // 直接刷新页面
    location.reload();
    // const res =  queryByPageSoapTools(query);
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
