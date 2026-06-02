<template>
  <div>
    <el-steps :active="step" align-center finish-status="success" style="margin-bottom:20px;">
      <el-step title="选择皂块"/>
      <el-step title="内包装"/>
      <el-step title="赠品（可选）"/>
      <el-step title="外包装"/>
    </el-steps>

    <!-- Step 1: 选择皂块 -->
    <div v-show="step===0">
      <div class="section-title"><b>选择要打包的皂块（已成熟+在售）</b></div>
      <div style="margin-bottom:8px;display:flex;align-items:center;gap:8px;">
        <span style="color:#606266;font-size:13px;">按生产批次筛选</span>
        <el-select v-model="pieceBatchFilter" placeholder="全部批次" clearable size="small" style="width:300px;">
          <el-option v-for="b in availablePieceBatches" :key="b.key" :label="b.label" :value="b.key"/>
        </el-select>
        <el-button v-if="pieceBatchFilter" type="primary" size="small" link @click="selectAllInPieceBatch">选中本批次全部</el-button>
      </div>
      <el-table :data="filteredPieces" border max-height="360" @selection-change="onSelect" ref="pieceTable">
        <el-table-column type="selection" width="50"/>
        <el-table-column prop="productionName" label="所属生产批次" min-width="140">
          <template #default="scope">
            <span>{{ scope.row.productionName || '-' }}</span>
            <span v-if="scope.row.productionBatchNumber" style="color:#909399;font-size:12px;"> ({{ scope.row.productionBatchNumber }})</span>
          </template>
        </el-table-column>
        <el-table-column prop="pieceBatchNumber" label="皂块批次号" width="170"/>
        <el-table-column prop="pieceName" label="名称" min-width="100"/>
        <el-table-column prop="weight" label="重量(g)" width="90" align="center"/>
        <el-table-column prop="price" label="售价" width="80" align="center">
          <template #default="scope">{{ scope.row.price ? scope.row.price + ' 元' : '-' }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!allPieces.length" style="text-align:center;padding:30px;color:#999;">暂无可打包的皂块</div>
      <div v-else-if="!filteredPieces.length" style="text-align:center;padding:20px;color:#999;">该批次下没有可选皂块</div>
    </div>

    <!-- Step 2: 内包装（包装纸+干燥剂+真空袋） -->
    <div v-show="step===1">
      <div class="section-title">
        <b>内包装物料（包装纸、干燥剂、真空袋等）</b>
        <span style="margin-left:8px;color:#909399;font-size:12px;">仅显示「包装材料」类型物料</span>
      </div>
      <el-table :data="innerMaterials" border max-height="300">
        <el-table-column label="物料" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择包装材料" filterable @change="(v)=>onMatChange(scope.row,v)" style="width:100%">
              <el-option v-for="c in packagingConsumables" :key="c.id" :label="c.name+' ('+c.brand+')'" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty||'-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="120" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="90" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="55" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Delete" circle @click="innerMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" size="small" style="margin-top:8px" @click="addInner">+ 添加内包装物料</el-button>
      <span v-if="!packagingConsumables.length" style="margin-left:10px;color:#f56c6c;font-size:12px;">
        物料库还没有「包装材料」，请先到「物料管理」添加
      </span>
    </div>

    <!-- Step 3: 赠品（可选） -->
    <div v-show="step===2">
      <div class="section-title">
        <b>赠品（非必填）</b>
        <span style="margin-left:8px;color:#909399;font-size:12px;">如自家小皂、泡泡网、明信片等；选填以核算实际成本</span>
      </div>
      <el-table :data="giftMaterials" border max-height="300">
        <el-table-column label="赠品" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择赠品" filterable @change="(v)=>onMatChange(scope.row,v)" style="width:100%">
              <el-option v-for="c in giftConsumables" :key="c.id" :label="c.brand ? c.name+' ('+c.brand+')' : c.name" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty||'-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="120" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="90" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="55" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Delete" circle @click="giftMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:8px;display:flex;gap:8px;align-items:center;">
        <el-button type="primary" size="small" @click="addGift">+ 选择已有赠品</el-button>
        <el-button type="success" size="small" @click="openQuickAddGift">+ 新增赠品到物料库</el-button>
        <span style="color:#909399;font-size:12px;">没找到要送的东西？点右边按钮快速添加到物料库</span>
      </div>
    </div>

    <!-- Step 4: 外包装（蜂窝纸+纸盒+绳子等） -->
    <div v-show="step===3">
      <div class="section-title">
        <b>外包装物料（蜂窝纸、纸盒、绳子等快递材料）</b>
        <span style="margin-left:8px;color:#909399;font-size:12px;">仅显示「包装材料」类型物料</span>
      </div>
      <el-table :data="outerMaterials" border max-height="300">
        <el-table-column label="物料" min-width="160">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择包装材料" filterable @change="(v)=>onMatChange(scope.row,v)" style="width:100%">
              <el-option v-for="c in packagingConsumables" :key="c.id" :label="c.name+' ('+c.brand+')'" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" align="center">
          <template #default="scope">{{ scope.row.stockQty||'-' }}{{ unitDesc(scope.row.stockUnit) }}</template>
        </el-table-column>
        <el-table-column label="用量" width="120" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" :placeholder="unitDesc(scope.row.unit)"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="90" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.unitPrice" type="number" placeholder="元"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="55" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Delete" circle @click="outerMaterials.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" size="small" style="margin-top:8px" @click="addOuter">+ 添加外包装物料</el-button>

      <div style="margin-top:16px;text-align:right;">
        <span style="color:#606266;">内包装 {{ innerCost.toFixed(2) }} 元 + 赠品 {{ giftCost.toFixed(2) }} 元 + 外包装 {{ outerCost.toFixed(2) }} 元 =</span>
        <b style="color:#e6a23c;font-size:16px;margin-left:6px;">打包总成本 {{ totalMatCost.toFixed(2) }} 元</b>
      </div>
    </div>

    <div style="margin-top:20px;text-align:right;">
      <el-button v-if="step>0" @click="step--">上一步</el-button>
      <el-button v-if="step<3" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="step===3" type="warning" @click="submit" :loading="submitting">确认打包</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </div>

    <!-- 快速新增赠品对话框 -->
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
        <el-button type="primary" @click="submitQuickAdd" :loading="quickAddLoading">添加并选用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { getAvailablePieces, getAvailableConsumables, createPackage, addSoapConsumables, getConsumableUnits } from '../api';

const props = defineProps({ update: { type: Function, required: true } });
const emit = defineEmits(['cancel']);

const step = ref(0);
const submitting = ref(false);
const allPieces = ref<any[]>([]);
const selectedPieces = ref<any[]>([]);
const pieceBatchFilter = ref<string>('');
const pieceTable = ref<any>(null);

const availablePieceBatches = computed(() => {
  const seen = new Map<string, string>();
  allPieces.value.forEach((p: any) => {
    const key = p.productionBatchNumber || '';
    if (!seen.has(key)) {
      const label = (p.productionName || '未命名') + (key ? ` (${key})` : '');
      seen.set(key, label);
    }
  });
  return Array.from(seen.entries()).map(([key, label]) => ({ key, label }));
});

const filteredPieces = computed(() =>
  pieceBatchFilter.value
    ? allPieces.value.filter((p: any) => (p.productionBatchNumber || '') === pieceBatchFilter.value)
    : allPieces.value
);

const selectAllInPieceBatch = () => {
  if (pieceTable.value) {
    filteredPieces.value.forEach((row: any) => pieceTable.value.toggleRowSelection(row, true));
  }
};
const allConsumables = ref<any[]>([]);
const innerMaterials = ref<any[]>([]);
const outerMaterials = ref<any[]>([]);
const giftMaterials = ref<any[]>([]);
const consumableUnits = ref<any[]>([]); // 用于「新增赠品到物料库」单位下拉，与物料管理共用同一枚举
const unitDescMap = computed(() => {
  const m = new Map<string, string>();
  consumableUnits.value.forEach((u: any) => m.set(u.code, u.desc));
  return m;
});
const unitDesc = (code: string) => unitDescMap.value.get(code) || code || '';

// 仅「包装材料」(type=5) 用于内/外包装
const packagingConsumables = computed(() =>
  allConsumables.value.filter((c: any) => c.consumableType === 5)
);
// 仅「赠品」(type=6) 用于赠品步骤
const giftConsumables = computed(() =>
  allConsumables.value.filter((c: any) => c.consumableType === 6)
);

const rowCost = (m: any) => (m.quantityUsed || 0) * (m.unitPrice || 0);
const innerCost = computed(() => innerMaterials.value.reduce((s, m) => s + rowCost(m), 0));
const outerCost = computed(() => outerMaterials.value.reduce((s, m) => s + rowCost(m), 0));
const giftCost = computed(() => giftMaterials.value.reduce((s, m) => s + rowCost(m), 0));
const totalMatCost = computed(() => innerCost.value + outerCost.value + giftCost.value);

const loadConsumables = async () => {
  const cRes = await getAvailableConsumables();
  allConsumables.value = cRes.data.data || [];
};

onMounted(async () => {
  try {
    const [pRes, , uRes] = await Promise.all([
      getAvailablePieces(),
      loadConsumables(),
      getConsumableUnits(),
    ]);
    allPieces.value = pRes.data || [];
    consumableUnits.value = uRes.data?.data || [];
  } catch (e) {
    ElMessage.error('加载数据失败');
  }
});

const onSelect = (rows: any[]) => { selectedPieces.value = rows; };

const onMatChange = (row: any, id: number) => {
  const c = allConsumables.value.find((i: any) => i.id === id);
  if (c) {
    row.consumableName = c.name;
    row.unit = c.unit;
    row.unitPrice = c.unitPrice;
    row.stockQty = c.quantity;
    row.stockUnit = c.unit;
    row.unitDesc = c.unit; // 存 code，后续渲染时 lookup
    if (row.quantityUsed == null) {
      row.quantityUsed = 1;
    }
  }
};

const emptyRow = () => ({ consumableId: null, quantityUsed: null, unitPrice: null, unit: '', stockQty: null, stockUnit: '', unitDesc: '' });
const addInner = () => innerMaterials.value.push(emptyRow());
const addOuter = () => outerMaterials.value.push(emptyRow());
const addGift = () => giftMaterials.value.push(emptyRow());

// ---- 快速新增赠品 ----
const quickAddVisible = ref(false);
const quickAddLoading = ref(false);
const quickGiftForm = reactive({ name: '', unit: '个', unitPrice: null as number | null, quantity: null as number | null, brand: '' });

const openQuickAddGift = () => {
  quickGiftForm.name = '';
  quickGiftForm.unit = 'ge'; // 默认「个」(对应枚举 code=ge)
  quickGiftForm.unitPrice = null;
  quickGiftForm.quantity = null;
  quickGiftForm.brand = '';
  quickAddVisible.value = true;
};

const submitQuickAdd = async () => {
  if (!quickGiftForm.name) { ElMessage.warning('请填写赠品名称'); return; }
  if (!quickGiftForm.unit) { ElMessage.warning('请选择单位'); return; }
  quickAddLoading.value = true;
  try {
    const unitPrice = quickGiftForm.unitPrice || 0;
    const quantity = quickGiftForm.quantity || 0;
    const payload = {
      name: quickGiftForm.name,
      brand: quickGiftForm.brand || '',
      unit: quickGiftForm.unit, // 枚举 code，如 'ge' / 'zhang' / 'g'
      unitPrice,
      quantity,
      totalPrice: unitPrice * quantity, // 数据库 NOT NULL，按 单价×数量 自动算
      consumableType: 6, // 赠品
      status: 1,
    };
    await addSoapConsumables(payload);
    await loadConsumables();
    // 找到刚加的（按名字+类型匹配），自动加一行选中
    const justAdded = giftConsumables.value.find((c: any) => c.name === quickGiftForm.name);
    if (justAdded) {
      const row = emptyRow();
      row.consumableId = justAdded.id;
      onMatChange(row, justAdded.id);
      giftMaterials.value.push(row);
    }
    quickAddVisible.value = false;
    ElMessage.success('赠品已添加到物料库');
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || e?.message || '添加失败');
  } finally {
    quickAddLoading.value = false;
  }
};

const nextStep = () => {
  if (step.value === 0 && !selectedPieces.value.length) { ElMessage.warning('请至少选择一个皂块'); return; }
  // 赠品步骤可跳过，不强校验
  step.value++;
};

const matToPayload = (m: any) => ({
  consumableId: m.consumableId,
  quantityUsed: m.quantityUsed,
  unitPrice: m.unitPrice,
  unit: m.unit,
});

const submit = async () => {
  if (!selectedPieces.value.length) { ElMessage.warning('请选择皂块'); return; }
  submitting.value = true;
  try {
    const payload = {
      pieceIds: selectedPieces.value.map((p: any) => p.id),
      innerMaterials: innerMaterials.value.filter((m: any) => m.consumableId).map(matToPayload),
      outerMaterials: outerMaterials.value.filter((m: any) => m.consumableId).map(matToPayload),
      giftMaterials: giftMaterials.value.filter((m: any) => m.consumableId).map(matToPayload),
    };
    await createPackage(payload);
    ElMessage.success('打包批次创建成功！物料已扣减。');
    props.update();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || e?.message || '创建失败');
  } finally { submitting.value = false; }
};
</script>

<style scoped>
.section-title { margin-bottom: 8px; font-size: 14px; }
</style>
