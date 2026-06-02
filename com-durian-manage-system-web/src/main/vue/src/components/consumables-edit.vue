<template>
	<el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
		<el-form-item label="物料名称" prop="name">
			<el-autocomplete v-model="form.name" :fetch-suggestions="queryOilSuggestions" placeholder="输入名称，自动联想配方中的油脂" @select="onOilSuggestionSelect" style="width:100%"/>
		</el-form-item>
    <el-form-item label="品牌">
      <el-input v-model="form.brand" ></el-input>
    </el-form-item>
    <el-form-item label="采购渠道">
      <el-input v-model="form.purchaseChannel"></el-input>
    </el-form-item>
    <el-form-item label="采购时间" prop="purchaseTime">
      <el-date-picker
          v-model="form.purchaseTime"
          type="date"
          placeholder="选择日期">
      </el-date-picker>
    </el-form-item>
    <el-form-item label="数量/单位" prop="quantity">
      <div style="display: flex; gap: 8px;">
        <el-input v-model.number="form.quantity" style="width: 150px;" placeholder="数量" @blur="calculateTotalPrice"/>
        <el-select v-model="form.unit" style="width: 140px;" placeholder="单位">
          <el-option
            v-for="item in consumableUnits"
            :key="item.code"
            :label="item.desc"
            :value="item.code">
          </el-option>
        </el-select>
      </div>
    </el-form-item>
    <el-form-item label="物料类型" prop="consumableType">
      <el-select v-model="form.consumableType" placeholder="请选择物料类型">
        <el-option
          v-for="item in consumableTypes"
          :key="item.code"
          :label="item.desc"
          :value="item.code">
        </el-option>
      </el-select>
    </el-form-item>
      <el-form-item label="总价" prop="totalPrice">
      <el-input v-model.number="form.totalPrice" @blur="calculateTotalPrice"></el-input>
    </el-form-item>
    <el-form-item label="单价" prop="unitPrice" >
      <el-input v-model.number="form.unitPrice"  @blur="calculateTotalPrice"></el-input>
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <el-select v-model="form.status" placeholder="请选择状态">
        <el-option label="可用" :value="1"></el-option>
        <el-option label="已用完" :value="2"></el-option>
        <el-option label="已过期" :value="3"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="过期日期" prop="expiryDate">
      <el-date-picker
          v-model="form.expiryDate"
          type="date"
          placeholder="选择日期">
      </el-date-picker>
    </el-form-item>

			
		<el-form-item>
			<el-button type="primary" @click="addOrUpdateSoapConsumablesMethod()">保 存</el-button>
		</el-form-item>
	</el-form>
</template>

<script lang="ts" setup>
import {ElMessage, FormRules} from 'element-plus';
import {ref, onMounted} from 'vue';
import {addSoapConsumables, getConsumableTypes, getConsumableUnits, queryByPageOils} from "../api";

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

// 物料类型选项
const consumableTypes = ref([]);
const consumableUnits = ref([]);

// 获取物料类型选项
const loadConsumableTypes = async () => {
  try {
    const response = await getConsumableTypes();
    consumableTypes.value = response.data.data || [];
  } catch (error) {
    ElMessage.error('获取物料类型失败');
    console.error(error);
  }
};

const loadConsumableUnits = async () => {
  try {
    const response = await getConsumableUnits();
    consumableUnits.value = response.data.data || [];
  } catch (error) {
    ElMessage.error('获取物料单位失败');
    console.error(error);
  }
};

// 油脂名称联想（从配方灵感中搜索）
const queryOilSuggestions = (queryString: string, cb: Function) => {
  if (!queryString || queryString.trim().length < 1) {
    cb([]);
    return;
  }
  queryByPageOils({ name: queryString.trim(), pageIndex: 1, pageSize: 10 }).then(res => {
    const oils = (res.data?.content || []).map((o: any) => ({ value: o.oilChineseName }));
    cb(oils);
  }).catch(() => cb([]));
};

// 选中联想项时自动填充皂化值等信息
const onOilSuggestionSelect = (item: any) => {
  // 可扩展：选中后自动填充其他字段
};

const defaultData = {
	id: '',
	name: '',
  brand: '',
  quantity: 1,
  unit: 'g',
  unitPrice: 0,
  totalPrice: 0,
  status: 1,
  consumableType: null,
  expiryDate: null,
  purchaseChannel: '',
  purchaseTime: new Date(),
  createdBy: '',
  updatedBy: '',
  createdStr: '',
  modifiedStr: '',
};

const form = ref({ ...(props.edit ? props.data : defaultData) });


const rules: FormRules = {
	name: [{ required: true, message: '物料名称', trigger: 'blur' }],
  brand: [],
  purchaseChannel: [],
  purchaseTime: [{ required: true, message: '采购时间', trigger: 'blur' }],
  unit: [{ required: true, message: '单位', trigger: 'change' }],
  quantity: [{ required: true, message: '数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '单价', trigger: 'blur' }],
  status: [{ required: true, message: '状态', trigger: 'blur' }],
  consumableType: [{ required: true, message: '物料类型', trigger: 'blur' }],
  totalPrice: [{ required: true, message: '总价', trigger: 'blur' }]
};

// 更新总价
function calculateTotalPrice() {
  form.value.unitPrice = form.value.totalPrice / form.value.quantity
}

// 添加
function addOrUpdateSoapConsumablesMethod() {
  console.log("addSoapConsumablesMethod");
  addSoapConsumables(form.value).then(response => {
    // props.update(form.value);
    // 直接刷新页面
    location.reload();
    // const res =  queryByPageSoapConsumables(query);
    ElMessage.success('保存成功！');
  });
}

// 组件挂载时加载物料类型
onMounted(() => {
  loadConsumableTypes();
  loadConsumableUnits();
});
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