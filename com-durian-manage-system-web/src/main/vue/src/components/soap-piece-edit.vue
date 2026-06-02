<template>
  <div>
    <!-- 批次信息 -->
    <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
      <template #title>
        生产批次：{{ production.name }} &nbsp;|&nbsp; 批次号：{{ production.batchNumber }}
        &nbsp;|&nbsp; 入模总重：{{ production.totalWeight || '-' }} g
      </template>
    </el-alert>

    <!-- 实际重量 -->
    <el-form :model="metaForm" label-width="160px" style="margin-bottom: 16px;">
      <el-form-item label="成熟后实际总重量(g)">
        <el-input v-model.number="metaForm.actualWeight" type="number" placeholder="请输入成熟后称重的实际重量" style="width: 250px;">
          <template #append>g</template>
        </el-input>
        <span style="margin-left: 12px; color: #909399; font-size: 13px;">晾皂后修边会有损耗，实际重量通常小于入模重量</span>
      </el-form-item>
    </el-form>

    <!-- 一键切块 -->
    <el-card shadow="hover" style="margin-bottom: 16px;">
      <template #header>
        <span style="font-weight: bold;">一键切块</span>
      </template>
      <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
        <span>切成</span>
        <el-input-number v-model="autoCutCount" :min="2" :max="50" style="width: 120px;"/>
        <span>块</span>
        <el-button type="primary" @click="autoCut" :disabled="!metaForm.actualWeight || metaForm.actualWeight <= 0">
          自动切块
        </el-button>
        <span style="color: #909399; font-size: 13px; margin-left: 8px;">
          每块约 {{ autoCutCount > 0 && metaForm.actualWeight ? (metaForm.actualWeight / autoCutCount).toFixed(1) : '-' }} g，
          自动生成唯一批次号
        </span>
      </div>
      <div style="margin-top: 8px; font-size: 12px; color: #909399;">
        一键切块会清除已有皂块并重新生成，批次号格式：{{ production.batchNumber }}-P01 ~ P{{ String(autoCutCount).padStart(2, '0') }}
      </div>
    </el-card>

    <!-- 皂块列表 -->
    <div style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
      <span style="font-weight: bold; font-size: 15px;">皂块分装列表（{{ form.pieces.length }} 块）</span>
      <div style="display: flex; gap: 8px;">
        <el-button type="warning" size="small" @click="openBatchEdit" :disabled="form.pieces.length === 0">批量修改</el-button>
        <el-button type="success" size="small" @click="addPiece">+ 手动添加</el-button>
      </div>
    </div>

    <el-table :data="form.pieces" border max-height="400">
      <el-table-column label="序号" width="55" align="center">
        <template #default="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="皂块名称" min-width="140">
        <template #default="scope">
          <el-input v-model="scope.row.pieceName" placeholder="如：薰衣草皂"/>
        </template>
      </el-table-column>
      <el-table-column label="唯一批次号" width="200">
        <template #default="scope">
          <span style="font-family: monospace; font-size: 13px;">{{ scope.row.pieceBatchNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="重量(g)" width="120">
        <template #default="scope">
          <el-input v-model.number="scope.row.weight" type="number" placeholder="重量"/>
        </template>
      </el-table-column>
      <el-table-column label="售价(元)" width="110">
        <template #default="scope">
          <el-input v-model.number="scope.row.price" type="number" placeholder="售价"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-select v-model="scope.row.status">
            <el-option label="在售" :value="1"/>
            <el-option label="已售" :value="2"/>
            <el-option label="赠送" :value="3"/>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="60" fixed="right">
        <template #default="scope">
          <el-button type="danger" size="small" :icon="Delete" circle @click="removePiece(scope.$index)"/>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量修改弹窗 -->
    <el-dialog v-model="batchVisible" title="批量修改皂块" width="480px" append-to-body>
      <el-form :model="batchForm" label-width="80px">
        <el-form-item label="皂块名称">
          <el-input v-model="batchForm.pieceName" placeholder="留空则不修改"/>
        </el-form-item>
        <el-form-item label="重量(g)">
          <el-input v-model.number="batchForm.weight" type="number" placeholder="留空则不修改"/>
        </el-form-item>
        <el-form-item label="售价(元)">
          <el-input v-model.number="batchForm.price" type="number" placeholder="留空则不修改"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="batchForm.status" placeholder="留空则不修改" clearable>
            <el-option label="在售" :value="1"/>
            <el-option label="已售" :value="2"/>
            <el-option label="赠送" :value="3"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div style="color: #909399; font-size: 13px; margin-top: 4px;">仅修改填写了的字段，留空字段保持不变。唯一批次号不可修改。</div>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="applyBatchEdit">应用到全部 {{ form.pieces.length }} 块</el-button>
      </template>
    </el-dialog>

    <!-- 重量统计 -->
    <div style="margin-top: 12px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 16px;">
        <div>
          <span style="color: #606266;">皂块总重量：</span>
          <span :style="{ color: totalPieceWeight > (metaForm.actualWeight || Infinity) ? '#f56c6c' : '#67c23a', fontWeight: 'bold', fontSize: '16px' }">
            {{ totalPieceWeight.toFixed(2) }} g
          </span>
        </div>
        <div v-if="metaForm.actualWeight">
          <span style="color: #606266;">实际总重量：</span>
          <span style="font-weight: bold; font-size: 16px;">{{ metaForm.actualWeight }} g</span>
        </div>
        <div v-if="metaForm.actualWeight">
          <span style="color: #606266;">修边损耗：</span>
          <span style="font-weight: bold; color: #e6a23c;">{{ Math.max(0, metaForm.actualWeight - totalPieceWeight).toFixed(2) }} g</span>
        </div>
      </div>
      <div v-if="metaForm.actualWeight">
        <el-tag v-if="totalPieceWeight <= metaForm.actualWeight" type="success">重量合理</el-tag>
        <el-tag v-else type="danger">超出实际重量 {{ (totalPieceWeight - metaForm.actualWeight).toFixed(2) }} g</el-tag>
      </div>
    </div>

    <div v-if="form.pieces.length === 0" style="text-align: center; padding: 30px; color: #999; background: #f5f7fa; border-radius: 4px;">
      请使用「一键切块」或「手动添加皂块」进行切块分装
    </div>

    <!-- 底部按钮 -->
    <div style="margin-top: 24px; text-align: right;">
      <el-button type="warning" @click="submitPieces" :loading="submitting" :disabled="form.pieces.length === 0">
        确认分装
      </el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { addSoapPieces, updateSoapProductionStatus } from '../api';

const props = defineProps({
  production: { type: Object, required: true },
  update: { type: Function, required: true },
});

const emit = defineEmits(['cancel']);

const submitting = ref(false);
const autoCutCount = ref(6);

const metaForm = reactive({
  actualWeight: props.production.totalWeight || null as number | null,
});

const form = reactive<{ pieces: any[] }>({
  pieces: [],
});

const totalPieceWeight = computed(() => {
  return form.pieces.reduce((sum, p) => sum + (p.weight || 0), 0);
});

// 一键切块
const autoCut = () => {
  if (!metaForm.actualWeight || metaForm.actualWeight <= 0) {
    ElMessage.warning('请先输入成熟后实际总重量');
    return;
  }
  const count = autoCutCount.value;
  const batchPrefix = props.production.batchNumber || 'SOAP';
  const pieceWeight = parseFloat((metaForm.actualWeight / count).toFixed(2));
  const pieces = [];
  for (let i = 1; i <= count; i++) {
    pieces.push({
      pieceName: props.production.name || '皂块',
      pieceBatchNumber: batchPrefix + '-P' + String(i).padStart(2, '0'),
      weight: pieceWeight,
      price: null,
      status: 1,
    });
  }
  form.pieces = pieces;
  ElMessage.success(`已自动生成 ${count} 块皂块，每块 ${pieceWeight} g`);
};

// 手动添加
const addPiece = () => {
  const batchPrefix = props.production.batchNumber || 'SOAP';
  const nextIndex = form.pieces.length + 1;
  form.pieces.push({
    pieceName: props.production.name || '',
    pieceBatchNumber: batchPrefix + '-P' + String(nextIndex).padStart(2, '0'),
    weight: null,
    price: null,
    status: 1,
  });
};

// 删除
const removePiece = (index: number) => {
  form.pieces.splice(index, 1);
};

// 批量修改
const batchVisible = ref(false);
const batchForm = reactive({
  pieceName: '',
  weight: null as number | null,
  price: null as number | null,
  status: null as number | null,
});

const openBatchEdit = () => {
  batchForm.pieceName = '';
  batchForm.weight = null;
  batchForm.price = null;
  batchForm.status = null;
  batchVisible.value = true;
};

const applyBatchEdit = () => {
  let changed = 0;
  form.pieces.forEach((p) => {
    if (batchForm.pieceName) { p.pieceName = batchForm.pieceName; changed++; }
    if (batchForm.weight != null) { p.weight = batchForm.weight; changed++; }
    if (batchForm.price != null) { p.price = batchForm.price; changed++; }
    if (batchForm.status != null) { p.status = batchForm.status; changed++; }
  });
  batchVisible.value = false;
  if (changed > 0) {
    ElMessage.success(`已批量修改 ${form.pieces.length} 块皂块`);
  } else {
    ElMessage.info('未填写任何修改项');
  }
};

// 提交
const submitPieces = async () => {
  // 校验
  for (let i = 0; i < form.pieces.length; i++) {
    const p = form.pieces[i];
    if (!p.weight || p.weight <= 0) {
      ElMessage.warning(`第${i + 1}块皂请输入有效重量`);
      return;
    }
  }

  // 校验总重量不超过实际重量
  if (metaForm.actualWeight && totalPieceWeight.value > metaForm.actualWeight) {
    await ElMessageBox.confirm(
      `皂块总重量(${totalPieceWeight.value.toFixed(2)}g)超过实际重量(${metaForm.actualWeight}g)，确定继续吗？`,
      '重量超出提示',
      { confirmButtonText: '继续', cancelButtonText: '取消', type: 'warning' }
    );
  }

  submitting.value = true;
  try {
    // 更新成熟后实际重量
    if (metaForm.actualWeight && metaForm.actualWeight > 0) {
      await updateSoapProductionStatus(props.production.id, 3, metaForm.actualWeight);
      // status 2→3 切块，weight 是切块后重量
    }
    // 添加皂块
    await addSoapPieces(props.production.id, form.pieces);
    ElMessage.success('切块分装成功！');
    props.update();
  } catch (e: any) {
    if (e !== 'cancel') {
      const msg = e?.displayMessage || e?.response?.data?.message || e?.message || '操作失败';
      ElMessage.error(msg);
    }
  } finally {
    submitting.value = false;
  }
};
</script>
