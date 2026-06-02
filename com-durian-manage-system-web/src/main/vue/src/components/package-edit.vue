<template>
  <div>
    <div v-if="loading" style="text-align:center;padding:40px;">
      <el-icon class="is-loading"><Loading/></el-icon>
    </div>
    <div v-else-if="!pkg" style="text-align:center;padding:40px;color:#999;">加载失败</div>
    <div v-else>
      <el-alert type="info" :closable="false" style="margin-bottom:12px;">
        <template #title>
          批次号 <b>{{ pkg.batchNumber }}</b>
          ｜ 状态 <el-tag size="small" :type="statusType(pkg.status)">{{ statusLabel(pkg.status) }}</el-tag>
          ｜ 实时总成本 <b style="color:#e6a23c;">{{ liveTotalCost.toFixed(2) }} 元</b>
        </template>
      </el-alert>

      <!-- 备注 -->
      <div style="margin-bottom:12px;display:flex;align-items:center;gap:8px;">
        <span style="color:#606266;font-size:14px;width:48px;">备注</span>
        <el-input v-model="form.notes" placeholder="可选" size="small" style="flex:1;"/>
      </div>

      <!-- 皂块 -->
      <div class="section-title">
        <b>打包皂块（{{ form.pieces.length }} 块）</b>
        <el-button type="primary" size="small" @click="openPiecePicker">+ 添加皂块</el-button>
        <el-button type="danger" size="small" :disabled="!form.pieces.length" @click="clearAllPieces">一键清空</el-button>
        <span class="hint">点 ✕ 单个移出；清空后保存，所有皂块回退到「在售」</span>
      </div>
      <el-table :data="form.pieces" border size="small" max-height="200">
        <el-table-column prop="pieceBatchNumber" label="批次号" width="170"/>
        <el-table-column prop="pieceName" label="名称" min-width="100"/>
        <el-table-column prop="pieceWeight" label="重量(g)" width="80" align="center"/>
        <el-table-column prop="productionName" label="所属批次" min-width="100"/>
        <el-table-column width="50" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Close" circle @click="form.pieces.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 内包装 -->
      <div class="section-title" style="margin-top:14px;">
        <b>内包装物料</b>
        <el-button type="primary" size="small" @click="addRow(form.innerMaterials)">+ 添加内包装</el-button>
      </div>
      <el-table :data="form.innerMaterials" border size="small">
        <el-table-column label="物料" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择包装材料" filterable size="small"
              style="width:100%" @change="(v: any) => onMatChange(scope.row, v)">
              <el-option v-for="c in packagingConsumables" :key="c.id" :label="c.brand ? c.name+' ('+c.brand+')' : c.name" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty || '-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="115" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" size="small" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="100" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" size="small" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column width="50" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Close" circle @click="form.innerMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 赠品 -->
      <div class="section-title" style="margin-top:14px;">
        <b>赠品</b>
        <el-button type="primary" size="small" @click="addRow(form.giftMaterials)">+ 添加赠品</el-button>
        <el-button type="success" size="small" @click="openQuickAddGift">+ 新增赠品到物料库</el-button>
      </div>
      <el-table :data="form.giftMaterials" border size="small">
        <el-table-column label="赠品" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择赠品" filterable size="small"
              style="width:100%" @change="(v: any) => onMatChange(scope.row, v)">
              <el-option v-for="c in giftConsumables" :key="c.id" :label="c.brand ? c.name+' ('+c.brand+')' : c.name" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty || '-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="115" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" size="small" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="100" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" size="small" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column width="50" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Close" circle @click="form.giftMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 外包装 -->
      <div class="section-title" style="margin-top:14px;">
        <b>外包装物料</b>
        <el-button type="primary" size="small" @click="addRow(form.outerMaterials)">+ 添加外包装</el-button>
      </div>
      <el-table :data="form.outerMaterials" border size="small">
        <el-table-column label="物料" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择包装材料" filterable size="small"
              style="width:100%" @change="(v: any) => onMatChange(scope.row, v)">
              <el-option v-for="c in packagingConsumables" :key="c.id" :label="c.brand ? c.name+' ('+c.brand+')' : c.name" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty || '-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="115" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" size="small" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="100" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" size="small" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column width="50" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Close" circle @click="form.outerMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 保存 -->
      <div style="margin-top:18px;text-align:right;">
        <span style="color:#606266;margin-right:12px;">
          内 {{ matSum(form.innerMaterials).toFixed(2) }} + 赠 {{ matSum(form.giftMaterials).toFixed(2) }} + 外 {{ matSum(form.outerMaterials).toFixed(2) }} =
          <b style="color:#e6a23c;font-size:15px;">{{ liveTotalCost.toFixed(2) }} 元</b>
        </span>
        <el-button @click="$emit('cancel')">取消</el-button>
        <el-button type="warning" :loading="saving" @click="save">保存修改</el-button>
      </div>
    </div>

    <!-- 添加皂块对话框 -->
    <el-dialog v-model="piecePickerVisible" title="选择要加入的皂块（已成熟+在售）" width="780px" append-to-body destroy-on-close>
      <div style="margin-bottom:8px;display:flex;align-items:center;gap:8px;">
        <span style="color:#606266;font-size:13px;">按生产批次筛选</span>
        <el-select v-model="pickerBatchFilter" placeholder="全部批次" clearable size="small" style="width:300px;">
          <el-option v-for="b in availableBatches" :key="b.key" :label="b.label" :value="b.key"/>
        </el-select>
        <el-button v-if="pickerBatchFilter" type="primary" size="small" link @click="selectAllInBatch">选中本批次全部</el-button>
      </div>
      <el-table :data="filteredAddablePieces" border size="small" max-height="380" @selection-change="onPickerSelect" ref="pickerTableRef">
        <el-table-column type="selection" width="50"/>
        <el-table-column prop="productionName" label="所属生产批次" min-width="140">
          <template #default="scope">
            <span>{{ scope.row.productionName || '-' }}</span>
            <span v-if="scope.row.productionBatchNumber" style="color:#909399;font-size:12px;"> ({{ scope.row.productionBatchNumber }})</span>
          </template>
        </el-table-column>
        <el-table-column prop="pieceBatchNumber" label="皂块批次号" width="170"/>
        <el-table-column prop="pieceName" label="名称" min-width="90"/>
        <el-table-column prop="weight" label="重量(g)" width="80" align="center"/>
        <el-table-column label="售价" width="80" align="center">
          <template #default="scope">{{ scope.row.price ? scope.row.price + ' 元' : '-' }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!filteredAddablePieces.length" style="text-align:center;padding:20px;color:#999;">
        {{ pickerBatchFilter ? '本批次没有可加入的皂块' : '没有可加入的皂块' }}
      </div>
      <template #footer>
        <el-button @click="piecePickerVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!pickerSelected.length" @click="confirmAddPieces">加入选中（{{ pickerSelected.length }}）</el-button>
      </template>
    </el-dialog>

    <!-- 新增赠品到物料库 -->
    <el-dialog v-model="quickAddVisible" title="新增赠品到物料库" width="460px" append-to-body>
      <el-form :model="quickGiftForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="quickGiftForm.name" placeholder="如：泡泡网、明信片、小样皂"/>
        </el-form-item>
        <el-form-item label="单位" required>
          <el-select v-model="quickGiftForm.unit" placeholder="请选择单位" style="width:100%">
            <el-option v-for="u in consumableUnits" :key="u.code" :label="u.desc" :value="u.code"/>
          </el-select>
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model.number="quickGiftForm.unitPrice" type="number" placeholder="元"/>
        </el-form-item>
        <el-form-item label="库存">
          <el-input v-model.number="quickGiftForm.quantity" type="number" placeholder="初始库存数量"/>
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="quickGiftForm.brand" placeholder="可选"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="quickAddVisible = false">取消</el-button>
        <el-button type="primary" :loading="quickAddLoading" @click="submitQuickAdd">添加并选用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Loading, Close } from '@element-plus/icons-vue';
import {
  getPackageDetail, updatePackage, getAvailablePieces, getAvailableConsumables,
  addSoapConsumables, getConsumableUnits,
} from '../api';

const props = defineProps({ id: { type: Number, required: true } });
const emit = defineEmits(['changed', 'cancel']);

const loading = ref(false);
const saving = ref(false);
const pkg = ref<any>(null);
const allConsumables = ref<any[]>([]);
const consumableUnits = ref<any[]>([]); // 单位下拉
const unitDescMap = computed(() => {
  const m = new Map<string, string>();
  consumableUnits.value.forEach((u: any) => m.set(u.code, u.desc));
  return m;
});
const unitDesc = (code: string) => unitDescMap.value.get(code) || code || '';

const form = reactive<{
  notes: string;
  pieces: any[];
  innerMaterials: any[];
  outerMaterials: any[];
  giftMaterials: any[];
}>({ notes: '', pieces: [], innerMaterials: [], outerMaterials: [], giftMaterials: [] });

const packagingConsumables = computed(() => allConsumables.value.filter((c: any) => c.consumableType === 5));
const giftConsumables = computed(() => allConsumables.value.filter((c: any) => c.consumableType === 6));

const matSum = (arr: any[]) => arr.reduce((s, m) => s + (Number(m.quantityUsed) || 0) * (Number(m.unitPrice) || 0), 0);
const liveTotalCost = computed(() => matSum(form.innerMaterials) + matSum(form.outerMaterials) + matSum(form.giftMaterials));

const statusLabel = (s: number) => ({ 1: '待内包装', 2: '待外包装', 3: '已完成' } as Record<number, string>)[s] || '未知';
const statusType = (s: number) => (s === 1 ? 'warning' : s === 2 ? '' : 'success');

const cloneMat = (m: any) => ({
  consumableId: m.consumableId ?? null,
  quantityUsed: m.quantityUsed ?? null,
  unitPrice: m.unitPrice ?? null,
  unit: m.unit ?? '',
  unitDesc: m.unit ?? '',
  stockQty: null as number | null,
  stockUnit: '',
  consumableName: m.consumableName,
});

const fillStock = (row: any) => {
  if (!row.consumableId) return;
  const c = allConsumables.value.find((i: any) => i.id === row.consumableId);
  if (c) { row.stockQty = c.quantity; row.stockUnit = c.unit; }
};

const load = async () => {
  if (!props.id) return;
  loading.value = true;
  try {
    const [pkgRes, consRes, unitRes] = await Promise.all([
      getPackageDetail(props.id),
      getAvailableConsumables(),
      getConsumableUnits(),
    ]);
    pkg.value = pkgRes.data;
    allConsumables.value = consRes.data.data || [];
    consumableUnits.value = unitRes.data?.data || [];

    if (pkg.value) {
      form.notes = pkg.value.notes || '';
      form.pieces = [...(pkg.value.pieces || [])];
      const allMats = pkg.value.materials || [];
      form.innerMaterials = allMats.filter((m: any) => m.materialType === 'inner').map(cloneMat);
      form.outerMaterials = allMats.filter((m: any) => m.materialType === 'outer').map(cloneMat);
      form.giftMaterials = allMats.filter((m: any) => m.materialType === 'gift').map(cloneMat);
      [...form.innerMaterials, ...form.outerMaterials, ...form.giftMaterials].forEach(fillStock);
    }
  } catch (e) { console.error(e); ElMessage.error('加载失败'); }
  finally { loading.value = false; }
};

watch(() => props.id, load, { immediate: true });

const onMatChange = (row: any, id: number) => {
  const c = allConsumables.value.find((i: any) => i.id === id);
  if (c) {
    row.consumableId = c.id;
    row.consumableName = c.name;
    row.unit = c.unit;
    row.unitPrice = c.unitPrice;
    row.stockQty = c.quantity;
    row.stockUnit = c.unit;
    row.unitDesc = c.unit;
    if (row.quantityUsed == null) {
      row.quantityUsed = 1;
    }
  }
};

const emptyRow = () => ({ consumableId: null, quantityUsed: null, unitPrice: null, unit: '', stockQty: null, stockUnit: '' });
const addRow = (arr: any[]) => arr.push(emptyRow());

// ---- 添加皂块 / 清空 ----
const piecePickerVisible = ref(false);
const availablePieces = ref<any[]>([]);
const pickerSelected = ref<any[]>([]);
const pickerBatchFilter = ref<string>(''); // 按生产批次号筛选
const pickerTableRef = ref<any>(null);
const onPickerSelect = (rows: any[]) => { pickerSelected.value = rows; };

const addablePieces = computed(() => {
  const inPkg = new Set(form.pieces.map((p: any) => p.pieceId));
  return availablePieces.value.filter((p: any) => !inPkg.has(p.id));
});

// 可选的生产批次列表（去重 + 按 batchNumber 排序）
const availableBatches = computed(() => {
  const seen = new Map<string, string>();
  addablePieces.value.forEach((p: any) => {
    const key = p.productionBatchNumber || '';
    if (!seen.has(key)) {
      const label = (p.productionName || '未命名') + (key ? ` (${key})` : '');
      seen.set(key, label);
    }
  });
  return Array.from(seen.entries()).map(([key, label]) => ({ key, label }));
});

const filteredAddablePieces = computed(() => {
  if (!pickerBatchFilter.value) return addablePieces.value;
  return addablePieces.value.filter((p: any) => (p.productionBatchNumber || '') === pickerBatchFilter.value);
});

const selectAllInBatch = () => {
  // 触发 el-table 的全选效果（仅当前筛选范围内）
  if (pickerTableRef.value) {
    filteredAddablePieces.value.forEach((row: any) => pickerTableRef.value.toggleRowSelection(row, true));
  }
};

const openPiecePicker = async () => {
  try {
    const r = await getAvailablePieces();
    availablePieces.value = r.data || [];
    pickerSelected.value = [];
    pickerBatchFilter.value = '';
    piecePickerVisible.value = true;
  } catch { ElMessage.error('加载可用皂块失败'); }
};

const confirmAddPieces = () => {
  pickerSelected.value.forEach((p: any) => {
    form.pieces.push({
      pieceId: p.id,
      pieceName: p.pieceName,
      pieceBatchNumber: p.pieceBatchNumber,
      pieceWeight: p.weight,
      piecePrice: p.price,
      productionName: p.productionName || '',
    });
  });
  piecePickerVisible.value = false;
  pickerSelected.value = [];
};

// 一键清空：移出所有皂块（保存后所有皂块回退在售）
const clearAllPieces = async () => {
  if (!form.pieces.length) return;
  try {
    await ElMessageBox.confirm(
      `确定要把所有 ${form.pieces.length} 个皂块从本批次移出吗？保存后这些皂块会回到「在售」状态。`,
      '一键清空确认',
      { confirmButtonText: '清空', cancelButtonText: '取消', type: 'warning' }
    );
    form.pieces = [];
    ElMessage.success('已清空（点「保存修改」才真正生效）');
  } catch { /* cancel */ }
};

// ---- 新增赠品到物料库 ----
const quickAddVisible = ref(false);
const quickAddLoading = ref(false);
const quickGiftForm = reactive({ name: '', unit: 'ge', unitPrice: null as number | null, quantity: null as number | null, brand: '' });

const openQuickAddGift = () => {
  quickGiftForm.name = '';
  quickGiftForm.unit = 'ge';
  quickGiftForm.unitPrice = null;
  quickGiftForm.quantity = null;
  quickGiftForm.brand = '';
  quickAddVisible.value = true;
};

const submitQuickAdd = async () => {
  if (!quickGiftForm.name) { ElMessage.warning('请填写名称'); return; }
  if (!quickGiftForm.unit) { ElMessage.warning('请选择单位'); return; }
  quickAddLoading.value = true;
  try {
    const unitPrice = quickGiftForm.unitPrice || 0;
    const quantity = quickGiftForm.quantity || 0;
    await addSoapConsumables({
      name: quickGiftForm.name,
      brand: quickGiftForm.brand || '',
      unit: quickGiftForm.unit,
      unitPrice,
      quantity,
      totalPrice: unitPrice * quantity, // 数据库 NOT NULL，自动算
      consumableType: 6,
      status: 1,
    });
    const cRes = await getAvailableConsumables();
    allConsumables.value = cRes.data.data || [];
    const justAdded = giftConsumables.value.find((c: any) => c.name === quickGiftForm.name);
    if (justAdded) {
      const row = emptyRow();
      onMatChange(row, justAdded.id);
      form.giftMaterials.push(row);
    }
    quickAddVisible.value = false;
    ElMessage.success('赠品已添加到物料库');
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || e?.message || '添加失败');
  } finally { quickAddLoading.value = false; }
};

// ---- 保存 ----
const matToPayload = (m: any) => ({
  consumableId: m.consumableId,
  quantityUsed: m.quantityUsed,
  unitPrice: m.unitPrice,
  unit: m.unit,
});

const save = async () => {
  const allMats = [...form.innerMaterials, ...form.outerMaterials, ...form.giftMaterials];
  for (const m of allMats) {
    if (!m.consumableId) {
      ElMessage.warning('有物料行未选择物料，请删除或选择物料后再保存');
      return;
    }
    if (!(Number(m.quantityUsed) > 0)) {
      ElMessage.warning(`「${m.consumableName || '某物料'}」用量需 > 0`);
      return;
    }
  }
  try {
    await ElMessageBox.confirm(
      '保存会全量替换打包内容：原物料库存先恢复、再按新列表扣减；皂块状态按差异调整。继续？',
      '确认修改',
      { confirmButtonText: '保存', cancelButtonText: '取消', type: 'warning' }
    );
  } catch { return; }

  saving.value = true;
  try {
    await updatePackage(props.id, {
      pieceIds: form.pieces.map((p: any) => p.pieceId),
      innerMaterials: form.innerMaterials.map(matToPayload),
      outerMaterials: form.outerMaterials.map(matToPayload),
      giftMaterials: form.giftMaterials.map(matToPayload),
      notes: form.notes,
    });
    ElMessage.success('打包记录已更新');
    emit('changed');
    await load();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || e?.message || '保存失败');
  } finally { saving.value = false; }
};
</script>

<style scoped>
.section-title { font-size: 14px; display: flex; align-items: center; gap: 6px; margin-bottom: 6px; }
.section-title .hint { color: #909399; font-size: 12px; }
</style>
