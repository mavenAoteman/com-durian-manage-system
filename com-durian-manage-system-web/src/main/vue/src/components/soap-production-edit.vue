<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="生产名称" prop="name">
      <el-input v-model="form.name"></el-input>
    </el-form-item>
    <el-form-item label="产量" prop="quantity">
      <el-input v-model.number="form.quantity"></el-input>
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <el-select v-model="form.status" placeholder="请选择状态">
        <el-option label="进行中" :value="1"></el-option>
        <el-option label="已完成" :value="2"></el-option>
        <el-option label="已取消" :value="3"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="关联物料">
      <el-select v-model="form.consumableId" placeholder="请选择物料">
        <el-option v-for="item in consumablesList" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="使用量" prop="quantityUsed">
      <el-input v-model.number="form.quantityUsed"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="save()">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { addSoapProduction } from '../api';
import { ElMessage } from 'element-plus';

const props = defineProps({
  data: {
    type: Object,
    required: true
  },
  edit: {
    type: Boolean,
    default: false
  },
  update: {
    type: Function,
    required: true
  }
});

const form = ref({ ...props.data });

const rules = reactive({
  name: [{ required: true, message: '请输入生产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入产量', trigger: 'blur' }],
  quantityUsed: [{ required: true, message: '请输入使用量', trigger: 'blur' }]
});

const consumablesList = ref([
  { id: 1, name: '橄榄油' },
  { id: 2, name: '椰子油' },
  { id: 3, name: '氢氧化钠' }
]);

const save = () => {
    addSoapProduction(form.value).then(res => {
      ElMessage.success('新增成功');
      props.update(form.value);
    });
};
</script>