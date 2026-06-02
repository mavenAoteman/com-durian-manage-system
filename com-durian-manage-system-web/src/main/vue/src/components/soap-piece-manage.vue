<template>
  <div>
    <div v-if="loading" style="text-align: center; padding: 40px;">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>加载皂块数据...</p>
    </div>
    <div v-else>
      <!-- 批次信息 + 成本核算 -->
      <el-alert type="info" :closable="false" style="margin-bottom:8px;">
        <template #title>
          {{ production.name }} | 批次: {{ production.batchNumber }} | 成熟重量 {{ production.actualWeight || '-' }}g
        </template>
      </el-alert>
      <el-alert type="warning" :closable="false" style="margin-bottom:16px;">
        <template #title>
          总成本 <b>{{ prodDetail?.totalCost || '0' }} 元</b>
          &nbsp;|&nbsp;
          切块 {{ prodDetail?.cutWeight || '-' }}g → 成熟 {{ prodDetail?.actualWeight || '-' }}g
          &nbsp;|&nbsp;
          损耗 {{ weightLoss.toFixed(0) }}g ({{ lossPercent.toFixed(1) }}%)
          &nbsp;|&nbsp;
          成本单价 <b>{{ costPerGram.toFixed(3) }} 元/g</b>
        </template>
      </el-alert>

      <!-- 皂块列表 -->
      <div style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
        <span style="font-weight: bold; font-size: 15px;">皂块分装列表（{{ form.pieces.length }} 块）</span>
        <div style="display: flex; gap: 8px;">
          <el-button type="warning" size="small" @click="openBatchEdit" :disabled="form.pieces.length === 0">批量修改</el-button>
          <el-button type="success" size="small" @click="addPiece">+ 手动添加</el-button>
        </div>
      </div>

      <el-table :data="form.pieces" border max-height="400">
        <el-table-column label="序号" width="50" align="center">
          <template #default="scope">{{ scope.$index + 1 }}</template>
        </el-table-column>
        <el-table-column label="皂块名称" min-width="120">
          <template #default="scope">
            <el-input v-model="scope.row.pieceName" placeholder="名称"/>
          </template>
        </el-table-column>
        <el-table-column label="批次号" width="170">
          <template #default="scope">
            <span style="font-family: monospace; font-size: 12px;">{{ scope.row.pieceBatchNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column label="重量(g)" width="95">
          <template #default="scope">
            <el-input v-model.number="scope.row.weight" type="number" placeholder="g"/>
          </template>
        </el-table-column>
        <el-table-column label="成本价" width="85" align="center">
          <template #default="scope">
            <span style="color:#e6a23c;">{{ (scope.row.weight * costPerGram).toFixed(2) }} 元</span>
          </template>
        </el-table-column>
        <el-table-column label="售价(元)" width="105">
          <template #default="scope">
            <el-input v-model.number="scope.row.price" type="number" placeholder="售价"/>
          </template>
        </el-table-column>
        <el-table-column label="利润率" width="75" align="center">
          <template #default="scope">
            <span v-if="scope.row.price && scope.row.weight" :style="{ color: profitMargin(scope.row) >= 0 ? '#67c23a' : '#f56c6c' }">
              {{ profitMargin(scope.row).toFixed(0) }}%
            </span>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-select v-model="scope.row.status" size="small">
              <el-option label="在售" :value="1"/>
              <el-option label="已售" :value="2"/>
              <el-option label="赠送" :value="3"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="55" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Delete" circle @click="removePiece(scope.$index)"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 重量与售价统计 -->
      <div style="margin-top: 12px; display: flex; align-items: center; gap: 24px; flex-wrap: wrap;">
        <div>
          <span style="color:#606266;">皂块总重量：</span>
          <b>{{ totalPieceWeight.toFixed(1) }} g</b>
        </div>
        <div>
          <span style="color:#606266;">总成本：</span>
          <b style="color:#e6a23c;">{{ (totalPieceWeight * costPerGram).toFixed(2) }} 元</b>
        </div>
        <div>
          <span style="color:#606266;">总售价：</span>
          <b style="color:#409EFF;">{{ totalPrice.toFixed(2) }} 元</b>
        </div>
        <div>
          <span style="color:#606266;">总利润：</span>
          <b :style="{ color: totalPrice - totalPieceWeight * costPerGram >= 0 ? '#67c23a' : '#f56c6c' }">
            {{ (totalPrice - totalPieceWeight * costPerGram).toFixed(2) }} 元
          </b>
        </div>
      </div>

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
        <template #footer>
          <el-button @click="batchVisible = false">取消</el-button>
          <el-button type="primary" @click="applyBatchEdit">应用到全部 {{ form.pieces.length }} 块</el-button>
        </template>
      </el-dialog>
    </div>

    <div style="margin-top: 24px; text-align: right;">
      <el-button type="warning" @click="submitPieces" :loading="submitting" :disabled="form.pieces.length === 0">保存修改</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete, Loading } from '@element-plus/icons-vue';
import { getSoapPieces, updateSoapPiecesBatch, getSoapProductionDetail } from '../api';

const props = defineProps({
  production: { type: Object, required: true },
  update: { type: Function, required: true },
});
const emit = defineEmits(['cancel']);

const loading = ref(true);
const submitting = ref(false);
const prodDetail = ref<any>(null);

const form = reactive<{ pieces: any[] }>({ pieces: [] });

// ---- 成本计算 ----
const costPerGram = computed(() => {
  const total = prodDetail.value?.totalCost || 0;
  const weight = prodDetail.value?.cutWeight || prodDetail.value?.actualWeight || 1;
  return weight > 0 ? total / weight : 0;
});

const weightLoss = computed(() => {
  return (prodDetail.value?.cutWeight || 0) - (prodDetail.value?.actualWeight || 0);
});

const lossPercent = computed(() => {
  const cut = prodDetail.value?.cutWeight || 1;
  return cut > 0 ? weightLoss.value / cut * 100 : 0;
});

const totalPieceWeight = computed(() => {
  return form.pieces.reduce((s: number, p: any) => s + (p.weight || 0), 0);
});

const totalPrice = computed(() => {
  return form.pieces.reduce((s: number, p: any) => s + (p.price || 0), 0);
});

const profitMargin = (piece: any) => {
  const cost = (piece.weight || 0) * costPerGram.value;
  return cost > 0 ? ((piece.price || 0) - cost) / (piece.price || 0) * 100 : 0;
};

// ---- 加载 ----
onMounted(async () => {
  try {
    const [detailRes, piecesRes] = await Promise.all([
      getSoapProductionDetail(props.production.id),
      getSoapPieces(props.production.id),
    ]);
    prodDetail.value = detailRes.data;
    form.pieces = piecesRes.data || [];
  } catch (e) {
    console.error(e);
    ElMessage.error('加载皂块数据失败');
  } finally {
    loading.value = false;
  }
});

// ---- 操作 ----
const addPiece = () => {
  const prefix = props.production.batchNumber || 'SOAP';
  const next = form.pieces.length + 1;
  form.pieces.push({ pieceName: '', pieceBatchNumber: prefix + '-P' + String(next).padStart(2, '0'), weight: null, price: null, status: 1 });
};

const removePiece = (i: number) => form.pieces.splice(i, 1);

// 批量修改
const batchVisible = ref(false);
const batchForm = reactive({ pieceName: '', weight: null as number | null, price: null as number | null, status: null as number | null });

const openBatchEdit = () => {
  batchForm.pieceName = ''; batchForm.weight = null; batchForm.price = null; batchForm.status = null;
  batchVisible.value = true;
};

const applyBatchEdit = () => {
  let c = 0;
  form.pieces.forEach((p: any) => {
    if (batchForm.pieceName) { p.pieceName = batchForm.pieceName; c++; }
    if (batchForm.weight != null) { p.weight = batchForm.weight; c++; }
    if (batchForm.price != null) { p.price = batchForm.price; c++; }
    if (batchForm.status != null) { p.status = batchForm.status; c++; }
  });
  batchVisible.value = false;
  ElMessage.success(c > 0 ? `已修改 ${form.pieces.length} 块` : '未填写修改项');
};

// 保存
const submitPieces = async () => {
  for (let i = 0; i < form.pieces.length; i++) {
    if (!form.pieces[i].weight || form.pieces[i].weight <= 0) { ElMessage.warning(`第${i+1}块皂请输入有效重量`); return; }
  }
  submitting.value = true;
  try {
    await updateSoapPiecesBatch(props.production.id, form.pieces);
    ElMessage.success('皂块信息已更新！');
    props.update();
  } catch (e: any) {
    ElMessage.error(e?.displayMessage || e?.response?.data?.message || e?.message || '操作失败');
  } finally { submitting.value = false; }
};
</script>
