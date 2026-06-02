<template>
  <div>
    <div v-if="loading" style="text-align: center; padding: 40px;">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    <div v-else-if="!production" style="text-align: center; padding: 40px; color: #999;">数据加载失败</div>
    <div v-else>
      <!-- 基本信息 -->
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="批次号">{{ production.batchNumber }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ production.name }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{ formatDate(production.productionDate) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(production.status)">{{ getStatusLabel(production.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入模总重量">{{ production.totalWeight ? production.totalWeight + ' g' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="切块后重量">{{ production.cutWeight ? production.cutWeight + ' g' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="成熟后重量">{{ production.actualWeight ? production.actualWeight + ' g' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="预计成熟日期">{{ formatDate(production.maturationDate) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际成熟日期">{{ formatDate(production.actualMaturationDate) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="总成本" :span="2">
          <span style="color: #e6a23c; font-weight: bold; font-size: 16px;">{{ production.totalCost || 0 }} 元</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ production.notes || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ production.createdBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(production.created) }}</el-descriptions-item>
      </el-descriptions>

      <!-- 物料使用明细 -->
      <div style="margin-top: 20px;">
        <h4 style="margin-bottom: 10px;">物料使用明细</h4>
        <el-table :data="production.usages || []" border>
          <el-table-column label="物料名称" min-width="140">
            <template #default="scope">{{ scope.row.consumableName || '物料#' + scope.row.consumableId }}</template>
          </el-table-column>
          <el-table-column label="品牌" width="100">
            <template #default="scope">{{ scope.row.consumableBrand || '-' }}</template>
          </el-table-column>
          <el-table-column label="用量" width="120">
            <template #default="scope">{{ scope.row.quantityUsed }}g</template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="scope">{{ scope.row.unitPrice }} 元</template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="scope">{{ scope.row.subtotal != null ? scope.row.subtotal.toFixed(2) + ' 元' : '-' }}</template>
          </el-table-column>
        </el-table>
        <div v-if="!production.usages || production.usages.length === 0" style="text-align: center; padding: 20px; color: #999;">暂无物料使用记录</div>
      </div>

      <!-- 皂块列表 -->
      <div style="margin-top: 20px;">
        <h4 style="margin-bottom: 10px;">皂块分装</h4>
        <el-table :data="production.pieces || []" border>
          <el-table-column label="序号" width="55" align="center">
            <template #default="scope">{{ scope.$index + 1 }}</template>
          </el-table-column>
          <el-table-column prop="pieceName" label="皂块名称"/>
          <el-table-column prop="pieceBatchNumber" label="唯一批次号" width="200"/>
          <el-table-column prop="weight" label="重量(g)" width="100"/>
          <el-table-column label="售价" width="100">
            <template #default="scope">{{ scope.row.price != null ? scope.row.price + ' 元' : '未定价' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'info' : 'warning'" size="small">
                {{ scope.row.status === 1 ? '在售' : scope.row.status === 2 ? '已售' : '赠送' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!production.pieces || production.pieces.length === 0" style="text-align: center; padding: 20px; color: #999;">尚未切块分装</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { getSoapProductionDetail } from '../api';

const props = defineProps({
  data: { type: Object, required: true },
});

const emit = defineEmits(['refresh']);

const loading = ref(false);
const production = ref<any>(null);

const getStatusLabel = (status: number) => {
  const map: Record<number, string> = { 1: '生产中', 2: '已脱模', 3: '已切块', 4: '晾晒中', 5: '已成熟' };
  return map[status] || '未知';
};

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 1: '', 2: 'info', 3: 'warning', 4: 'warning', 5: 'success' };
  return map[status] || '';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};

const loadDetail = async () => {
  if (!props.data || !props.data.id) return;
  loading.value = true;
  try {
    const res = await getSoapProductionDetail(props.data.id);
    production.value = res.data;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

// 每次对话框打开时重新加载
watch(() => props.data, loadDetail, { immediate: true });
</script>
