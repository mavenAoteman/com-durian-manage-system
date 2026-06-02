<template>
  <div style="min-height: 520px;">
    <!-- 步骤条 -->
    <el-steps :active="step" align-center finish-status="success" style="margin-bottom: 24px;">
      <el-step title="基本信息"/>
      <el-step title="油相配方"/>
      <el-step title="碱液与水相"/>
      <el-step title="添加物"/>
      <el-step title="预览确认"/>
    </el-steps>

    <!-- Step 1: 基本信息 -->
    <el-form v-show="step === 0" ref="formRef1" :model="form" :rules="rules1" label-width="120px">
      <el-form-item label="皂的名称" prop="name">
        <el-input v-model="form.name" placeholder="如：薰衣草舒缓皂"/>
      </el-form-item>
      <el-form-item label="生产日期" prop="productionDate">
        <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%;"/>
      </el-form-item>
      <el-form-item label="预计成熟日期" prop="maturationDate">
        <el-date-picker v-model="form.maturationDate" type="date" value-format="YYYY-MM-DD" placeholder="预计晾皂成熟日期（默认45天后）" style="width: 100%;"/>
      </el-form-item>
      <el-form-item label="备注" prop="notes">
        <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="配方、工艺等备注信息"/>
      </el-form-item>
    </el-form>

    <!-- Step 2: 油相配方 -->
    <div v-show="step === 1">
      <!-- 油相总重量 -->
      <div style="margin-bottom:12px; display:flex; align-items:center; gap:12px;">
        <span style="font-weight:bold;white-space:nowrap;">油相总重量</span>
        <el-input-number v-model="form.totalOilTarget" :min="0" :step="10" style="width:150px;" @change="onTotalOilChange"/>
        <span>g</span>
        <el-tag :type="remainingOilWeight >= 0 ? 'success' : 'danger'" size="large" style="margin-left:12px;">
          {{ remainingOilWeight >= 0 ? '剩余可配' : '超出' }} {{ Math.abs(remainingOilWeight).toFixed(0) }} g
        </el-tag>
        <span style="color:#909399;font-size:13px;">已配 {{ totalUsedWeight.toFixed(0) }} / {{ form.totalOilTarget || 0 }} g</span>
      </div>

      <el-row :gutter="20">
        <!-- 左侧：油脂选择区 -->
        <el-col :span="15">
          <div class="section-title"><b>选择油脂（从物料库存）</b></div>
          <el-table :data="oilPhase" border max-height="320" style="width:100%;">
            <el-table-column label="油脂名称" min-width="130">
              <template #default="scope">
                <el-select v-model="scope.row.consumableId" placeholder="选择物料中的油脂" filterable @change="(v) => onOilChange(scope.row, v)" style="width:100%">
                  <el-option v-for="o in oilInventory" :key="o.id" :label="o.brand ? o.name + ' (' + o.brand + ')' : o.name" :value="o.id"/>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="库存" width="90" align="center">
              <template #default="scope">{{ scope.row.stockQty || '-' }}{{ scope.row.stockUnit || '' }}</template>
            </el-table-column>
            <el-table-column label="皂化值" width="65" align="center">
              <template #default="scope">{{ scope.row.sapNaOH || '-' }}</template>
            </el-table-column>
            <el-table-column label="重量(g)" width="95" align="center">
              <template #default="scope">
                <el-input v-model.number="scope.row.weight" type="number" placeholder="g" size="small" @input="onOilWeightChange(scope.row)"/>
              </template>
            </el-table-column>
            <el-table-column label="占比(%)" width="95" align="center">
              <template #default="scope">
                <el-input v-model.number="scope.row.percent" type="number" placeholder="%" size="small" @input="onOilPercentChange(scope.row)">
                </el-input>
              </template>
            </el-table-column>
            <el-table-column label="需NaOH" width="75" align="center">
              <template #default="scope">{{ calcOilNaOH(scope.row).toFixed(1) }}g</template>
            </el-table-column>
            <el-table-column label="操作" width="55" align="center">
              <template #default="scope">
                <el-button type="danger" size="small" :icon="Delete" circle @click="removeOil(scope.$index)"/>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" size="small" style="margin-top:8px;" @click="addOilRow">+ 添加油脂</el-button>
          <span v-if="oilPhase.length === 0" style="margin-left:12px;color:#999;">请添加至少一种油脂开始配方</span>
        </el-col>

        <!-- 右侧：配方摘要 -->
        <el-col :span="9">
          <el-card shadow="hover">
            <template #header><b>配方实时计算</b></template>
            <div class="stat-row">油相总目标 <b class="val">{{ form.totalOilTarget || 0 }} g</b></div>
            <div class="stat-row">已配重量 <b class="val">{{ totalUsedWeight.toFixed(0) }} g</b></div>
            <div class="stat-row" :style="{ color: remainingOilWeight >= 0 ? '#67c23a' : '#f56c6c' }">
              剩余可配 <b class="val">{{ remainingOilWeight.toFixed(0) }} g</b>
            </div>
            <div class="stat-row">油脂种类 <b class="val">{{ oilPhase.filter(o => o.consumableId).length }} 种</b></div>
            <el-divider style="margin:10px 0"/>
            <div class="stat-row">理论NaOH需求 <b class="val" style="color:#e6a23c;">{{ theoreticalNaOH.toFixed(1) }} g</b></div>
            <div style="font-size:12px;color:#909399;margin-top:4px;">
              计算公式：Σ(油脂重量(g) × NaOH皂化值)
            </div>
            <div class="stat-row" style="margin-top:6px;">油脂INS值 <b class="val">{{ avgInsValue.toFixed(0) }}</b></div>
            <div style="font-size:12px;color:#909399;">
              INS {{ avgInsValue < 120 ? '偏低，皂偏软' : avgInsValue > 170 ? '偏高，皂偏硬' : '适中，理想范围120-170' }}
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Step 3: 碱液与水相 -->
    <div v-show="step === 2">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><b>碱液配置</b></template>
            <el-form label-width="130px">
              <el-form-item label="理论NaOH">
                <el-input :model-value="theoreticalNaOH.toFixed(1)" disabled><template #append>g</template></el-input>
              </el-form-item>
              <el-form-item label="超脂率 (SF)">
                <el-input v-model.number="form.superfat" type="number" @input="()=>{}"><template #append>%</template></el-input>
                <div style="font-size:12px;color:#909399;">超脂增加滋润度，减少碱量，常见 5%-8%</div>
              </el-form-item>
              <el-form-item label="NaOH 纯度">
                <el-input v-model.number="form.naohPurity" type="number" @input="()=>{}"><template #append>%</template></el-input>
                <div style="font-size:12px;color:#909399;">市售NaOH通常 95%-99%</div>
              </el-form-item>
              <el-form-item label="实际NaOH用量">
                <el-input :model-value="actualNaOH.toFixed(1)" disabled><template #append>g</template></el-input>
                <div style="font-size:12px;color:#e6a23c;">
                  公式：理论NaOH × (1 - 超脂率%) ÷ 纯度%
                </div>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><b>水相配置</b></template>
            <!-- 水碱比 + 水量目标 -->
            <div style="display:flex;align-items:center;gap:8px;margin-bottom:6px;">
              <span style="width:80px;color:#606266;font-size:14px;">水碱比</span>
              <el-input v-model.number="form.waterRatio" type="number" :step="0.1" size="small" style="width:130px;">
                <template #append>: 1</template>
              </el-input>
              <span style="font-size:12px;color:#909399;">常见 2.0 - 3.0</span>
            </div>
            <div style="display:flex;align-items:center;gap:8px;margin-bottom:4px;">
              <span style="width:80px;color:#606266;font-size:14px;">水量目标</span>
              <el-input :model-value="waterAmountTarget" type="number" :min="0" size="small" style="width:130px;" @input="onWaterTargetInput">
                <template #append>g</template>
              </el-input>
              <el-tag :type="remainingWater >= 0 ? 'success' : 'danger'" size="small">
                {{ remainingWater >= 0 ? '剩余' : '超出' }} {{ Math.abs(remainingWater).toFixed(0) }}g
              </el-tag>
              <el-link v-if="waterAmountUserModified" type="primary" :underline="false" style="font-size:12px;" @click="resetWaterTarget">恢复默认</el-link>
            </div>
            <div style="font-size:12px;color:#909399;margin:0 0 10px 88px;">
              默认 = 实际NaOH × 水碱比 = {{ defaultWaterAmount.toFixed(1) }}g（皂师可按需调整）
            </div>

            <!-- 水相组成：可混搭多种（如 300g 水 + 200g 牛奶） -->
            <el-table :data="waterPhase" border size="small" style="width:100%;">
              <el-table-column label="水相物料" min-width="160">
                <template #default="scope">
                  <el-select :model-value="scope.row.name" filterable allow-create default-first-option clearable size="small" placeholder="选择或输入" style="width:100%" @change="(v) => onWaterRowChange(scope.row, v)">
                    <el-option-group v-if="waterPhaseList.length" label="物料库存">
                      <el-option v-for="w in waterPhaseList" :key="'inv-'+w.id" :label="w.name + ' (库存' + w.quantity + w.unit + ')'" :value="w.name"/>
                    </el-option-group>
                    <el-option-group label="常用水相">
                      <el-option v-for="name in extraWaterPhaseOptions" :key="'com-'+name" :label="name" :value="name"/>
                    </el-option-group>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="库存" width="80" align="center">
                <template #default="scope">
                  <span v-if="scope.row.fromInventory">{{ scope.row.stockQty }}{{ scope.row.stockUnit }}</span>
                  <span v-else style="color:#909399;">-</span>
                </template>
              </el-table-column>
              <el-table-column label="用量(g)" width="100" align="center">
                <template #default="scope">
                  <el-input v-model.number="scope.row.weight" type="number" :min="0" size="small"/>
                </template>
              </el-table-column>
              <el-table-column label="检查" width="70" align="center">
                <template #default="scope">
                  <span v-if="isRowInsufficient(scope.row)" style="color:#f56c6c;font-size:12px;">✗不足</span>
                  <span v-else-if="scope.row.fromInventory && scope.row.weight > 0" style="color:#67c23a;font-size:12px;">✓充足</span>
                  <span v-else-if="scope.row.weight > 0" style="color:#909399;font-size:12px;">非库存</span>
                  <span v-else style="color:#909399;font-size:12px;">-</span>
                </template>
              </el-table-column>
              <el-table-column width="50" align="center">
                <template #default="scope">
                  <el-button type="danger" size="small" :icon="Delete" circle @click="removeWaterRow(scope.$index)"/>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" size="small" style="margin-top:8px;" @click="addWaterRow">+ 添加水相</el-button>
            <span v-if="waterPhase.length === 0" style="margin-left:10px;color:#909399;font-size:12px;">至少添加一种水相（可混搭，如 300g 水 + 200g 牛奶）</span>

            <el-divider style="margin:12px 0"/>

            <div style="font-size:14px;">
              <span style="color:#606266;">入模总重量估算：</span>
              <b style="font-size:18px;color:#67c23a;">{{ estimatedTotalWeight.toFixed(0) }} g</b>
              <span style="font-size:12px;color:#909399;margin-left:8px;">油脂 + NaOH + 实际水量({{ waterAmount.toFixed(0) }}g)</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Step 4: 添加物 -->
    <div v-show="step === 3">
      <div class="section-title"><b>精油与添加物（可选）</b></div>
      <el-table :data="additives" border max-height="320" style="width:100%">
        <el-table-column label="物料名称" min-width="180">
          <template #default="scope">
            <el-select v-model="scope.row.consumableId" placeholder="选择添加物" filterable @change="(v) => onAdditiveChange(scope.row, v)" style="width:100%">
              <el-option v-for="c in additiveList" :key="c.id" :label="(c.brand ? c.name + ' (' + c.brand + ')' : c.name)" :value="c.id"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="120" align="center">
          <template #default="scope">{{ scope.row.stockQuantity || '-' }} {{ scope.row.unit }} <span v-if="scope.row.stockGrams" style="color:#909399;font-size:12px;">(≈{{ scope.row.stockGrams }}g)</span></template>
        </el-table-column>
        <el-table-column label="用量(g)" width="130" align="center">
          <template #default="scope">
            <el-input v-model.number="scope.row.quantityUsed" type="number" placeholder="g"/>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.unitPrice || '-' }} 元<span style="font-size:11px;color:#909399;">/{{ scope.row.origUnit }}</span></span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="55" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" :icon="Delete" circle @click="additives.splice(scope.$index,1)"/>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" size="small" style="margin-top:8px" @click="additives.push({consumableId:null,quantityUsed:null,origUnit:'',unitPrice:null,stockQuantity:null,unitFactor:1,stockGrams:null})">
        + 添加物料
      </el-button>
    </div>

    <!-- Step 5: 预览确认 -->
    <div v-show="step === 4">
      <el-row :gutter="20">
        <el-col :span="14">
          <el-card shadow="hover">
            <template #header><b>{{ form.name || '皂配方' }} - 配方总览</b></template>
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="皂名称">{{ form.name }}</el-descriptions-item>
              <el-descriptions-item label="生产日期">{{ form.productionDate }}</el-descriptions-item>
            </el-descriptions>

            <div style="margin-top:12px;"><b>油相配方</b></div>
            <el-table :data="oilPhase.filter(o=>o.consumableId)" border size="small" style="margin-top:4px;">
              <el-table-column label="油脂" prop="oilName"/>
              <el-table-column label="重量(g)" width="90" align="center" prop="weight"/>
              <el-table-column label="占比" width="60" align="center">
                <template #default="scope">{{ scope.row.percent || '-' }}%</template>
              </el-table-column>
              <el-table-column label="需NaOH(g)" width="95" align="center">
                <template #default="scope">{{ calcOilNaOH(scope.row).toFixed(1) }}</template>
              </el-table-column>
            </el-table>
            <div style="margin-top:4px;font-weight:bold;">油脂总重: {{ totalUsedWeight.toFixed(0) }} g</div>

            <div style="margin-top:12px;"><b>碱液与水相</b></div>
            <el-descriptions :column="2" border size="small" style="margin-top:4px;">
              <el-descriptions-item label="理论NaOH">{{ theoreticalNaOH.toFixed(1) }} g</el-descriptions-item>
              <el-descriptions-item label="超脂率">{{ form.superfat }}%</el-descriptions-item>
              <el-descriptions-item label="实际NaOH" :span="1">
                <b style="color:#e6a23c;">{{ actualNaOH.toFixed(1) }} g</b>
              </el-descriptions-item>
              <el-descriptions-item label="纯度">{{ form.naohPurity }}%</el-descriptions-item>
              <el-descriptions-item label="水相" :span="2">
                <template v-if="activeWaterPhase.length">
                  <span v-for="(w, i) in activeWaterPhase" :key="i">
                    {{ w.name || '未命名' }} {{ w.weight.toFixed(0) }}g<span v-if="i < activeWaterPhase.length - 1"> + </span>
                  </span>
                  <span style="color:#909399;">（合计 {{ waterAmount.toFixed(0) }}g，水碱比 {{ form.waterRatio }}:1）</span>
                </template>
                <span v-else style="color:#909399;">未配置</span>
              </el-descriptions-item>
              <el-descriptions-item label="入模总重">{{ estimatedTotalWeight.toFixed(0) }} g</el-descriptions-item>
              <el-descriptions-item label="INS值">{{ avgInsValue.toFixed(0) }}</el-descriptions-item>
            </el-descriptions>

            <div v-if="additives.filter(a=>a.consumableId).length" style="margin-top:12px;">
              <b>添加物</b>
              <el-table :data="additives.filter(a=>a.consumableId)" border size="small" style="margin-top:4px;">
                <el-table-column label="名称" prop="consumableName"/>
                <el-table-column label="用量" width="120" align="center">
                  <template #default="scope">{{ scope.row.quantityUsed }}g</template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>
        <el-col :span="10">
          <el-card shadow="hover">
            <template #header><b>成本核算</b></template>
            <div class="stat-row" v-for="(u,i) in costBreakdown" :key="i">
              {{ u.name }} <span style="color:#909399;">{{ u.amount }}</span>
              <b style="float:right;color:#e6a23c;">{{ u.cost.toFixed(2) }} 元</b>
            </div>
            <el-divider/>
            <div class="stat-row" style="font-size:16px;">
              物料总成本 <b style="float:right;color:#e6a23c;">{{ totalCost.toFixed(2) }} 元</b>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 底部按钮 -->
    <div style="margin-top: 24px; text-align: right;">
      <el-button v-if="step > 0" @click="step--">上一步</el-button>
      <el-tooltip v-if="step < 4" :disabled="!(step === 2 && waterStockInsufficient)" content="水相库存不足，请调小水量或更换水相后再继续" placement="top">
        <span>
          <el-button type="primary" :disabled="step === 2 && waterStockInsufficient" @click="nextStep">下一步</el-button>
        </span>
      </el-tooltip>
      <el-button v-if="step === 4" type="warning" size="large" @click="submit" :loading="submitting">确认生产</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { getAvailableConsumables, addSoapProduction, queryAllOils } from '../api';

const props = defineProps({
  update: { type: Function, required: true },
});
const emit = defineEmits(['cancel']);

// ---- 单位换算：统一转为克(g) ----
const unitToGram = (quantity: number, unit: string): number => {
  if (!unit || !quantity) return quantity || 0;
  const u = (unit || '').toLowerCase().trim();
  const ratios: Record<string, number> = {
    g: 1, ml: 1,          // 水和油近似 1ml≈1g
    kg: 1000, l: 1000,
    mg: 0.001, ul: 0.001,
    ug: 0.000001, ng: 0.000000001,
    cl: 10, dl: 100,
    oz: 28.35, lb: 453.59,
    tsp: 5, tbsp: 15, cup: 240, drop: 0.05,
  };
  return quantity * (ratios[u] || 1);
};

const step = ref(0);
const submitting = ref(false);

// ---- 基本表单 ----
const form = reactive({
  name: '',
  productionDate: new Date().toISOString().slice(0, 10),
  maturationDate: (() => { const d = new Date(); d.setDate(d.getDate() + 45); return d.toISOString().slice(0, 10); })(),
  notes: '',
  totalOilTarget: 500 as number | null,
  superfat: 5,
  naohPurity: 99,
  waterRatio: 2.5,
});

// 常用水相预设（即使物料库存里没有也始终可选）
const commonWaterPhases = ['纯净水', '蒸馏水', '牛奶', '母乳', '茶水', '咖啡', '花水', '椰子水'];

const rules1 = {
  name: [{ required: true, message: '请输入皂的名称', trigger: 'blur' }],
  productionDate: [{ required: true, message: '请选择生产日期', trigger: 'change' }],
};

// ---- 油相 ----
const oilRefTable = ref<any[]>([]);
const oilInventory = ref<any[]>([]);
const oilPhase = ref<any[]>([]);

// 已使用的总重量（所有油脂 weight 之和）
const totalUsedWeight = computed(() => oilPhase.value.reduce((s, o) => s + (o.weight || 0), 0));

// 剩余可配重量
const remainingOilWeight = computed(() => (form.totalOilTarget || 0) - totalUsedWeight.value);

// 用 totalUsedWeight 做皂化计算
const theoreticalNaOH = computed(() => oilPhase.value.reduce((s, o) => s + calcOilNaOH(o), 0));
const calcOilNaOH = (oil: any) => (oil.weight || 0) * (oil.sapNaOH || 0);

const avgInsValue = computed(() => {
  const oils = oilPhase.value.filter(o => o.consumableId && o.weight > 0);
  if (!oils.length) return 0;
  const w = oils.reduce((s, o) => s + (o.weight || 0), 0);
  return w > 0 ? oils.reduce((s, o) => s + (o.insValue || 0) * (o.weight || 0), 0) / w : 0;
});

const isVolumeUnit = (u: string) => ['ml','l','ul','cl','dl','tsp','tbsp','cup','drop'].includes((u||'').toLowerCase());

// ---- 重量 / 占比 互通逻辑 ----
// 用户改重量 → 自动算占比
const onOilWeightChange = (row: any) => {
  if (form.totalOilTarget && form.totalOilTarget > 0) {
    row.percent = parseFloat(((row.weight || 0) / form.totalOilTarget * 100).toFixed(1));
  }
};

// 用户改占比 → 自动算重量
const onOilPercentChange = (row: any) => {
  if (form.totalOilTarget && form.totalOilTarget > 0) {
    row.weight = parseFloat(((row.percent || 0) / 100 * form.totalOilTarget).toFixed(1));
  }
};

// 总目标变化 → 按现有占比重新算重量
const onTotalOilChange = () => {
  oilPhase.value.forEach(o => {
    if (o.percent && o.percent > 0 && form.totalOilTarget && form.totalOilTarget > 0) {
      o.weight = parseFloat((o.percent / 100 * form.totalOilTarget).toFixed(1));
    }
  });
};

// 选择油脂
const onOilChange = (row: any, consumableId: number) => {
  const inv = oilInventory.value.find((i: any) => i.id === consumableId);
  if (inv) {
    row.consumableId = inv.id;
    row.oilName = inv.name;
    row.stockQty = inv.quantity;
    row.stockUnit = inv.unit;
    // 归一化单价为克单价: rawPrice ÷ gramsPerUnit × 油脂密度(0.92)
    let gramsPerUnit = unitToGram(1, inv.unit);
    if (isVolumeUnit(inv.unit)) gramsPerUnit *= 0.92;
    row.unitPrice = gramsPerUnit > 0 ? (inv.unitPrice || 0) / gramsPerUnit : (inv.unitPrice || 0);
    row.origUnit = inv.unit;
    const ref = oilRefTable.value.find((r: any) =>
      r.oilChineseName === inv.name || inv.name.includes(r.oilChineseName) || r.oilChineseName.includes(inv.name)
    );
    if (ref) {
      row.sapNaOH = ref.sodiumHydroxideSaponificationValue;
      row.sapKOH = ref.potassiumHydroxideSaponificationValue;
      row.insValue = ref.insValue;
    } else {
      row.sapNaOH = null; row.sapKOH = null; row.insValue = null;
    }
  }
};

const addOilRow = () => oilPhase.value.push({ consumableId: null, oilName: '', sapNaOH: null, sapKOH: null, insValue: null, weight: null, percent: null, stockQty: null, stockUnit: '', unitPrice: null, origUnit: '' });
const removeOil = (i: number) => oilPhase.value.splice(i, 1);

// ---- 碱液计算 ----
const actualNaOH = computed(() => {
  const purity = (form.naohPurity || 99) / 100;
  const sf = (form.superfat || 0) / 100;
  const raw = theoreticalNaOH.value * (1 - sf) / purity;
  return Math.max(0, raw);
});

// ---- 水相（可混搭多种）----
// 水相库存物料列表（type=4 水相，或 type=0 其它）
const waterPhaseList = ref<any[]>([]);

// 常用水相中、库存里没有的，作为下拉补充项
const extraWaterPhaseOptions = computed(() =>
  commonWaterPhases.filter(n => !waterPhaseList.value.some((w: any) => w.name === n))
);

const toGramsUF = (qty: number, unit: string): number => {
  if (!unit || !qty) return qty || 0;
  const u = (unit || '').toLowerCase().trim();
  const r: Record<string, number> = { g:1, ml:1, kg:1000, l:1000, mg:0.001, ul:0.001, oz:28.35, lb:453.59, tsp:5, tbsp:15, cup:240, drop:0.05 };
  return qty * (r[u] || 1);
};

// 水量目标：默认 = 实际NaOH × 水碱比，用户可手动覆盖；调整后停止跟随公式直到「恢复默认」
const waterAmountTarget = ref(0);
const waterAmountUserModified = ref(false);
const defaultWaterAmount = computed(() => actualNaOH.value * (form.waterRatio || 2.5));

watch(
  defaultWaterAmount,
  (v) => {
    if (!waterAmountUserModified.value) waterAmountTarget.value = Math.round(v * 10) / 10;
  },
  { immediate: true }
);

const onWaterTargetInput = (val: any) => {
  const n = parseFloat(val);
  if (Number.isFinite(n) && n >= 0) {
    waterAmountTarget.value = n;
    waterAmountUserModified.value = true;
  }
};

const resetWaterTarget = () => {
  waterAmountUserModified.value = false;
  waterAmountTarget.value = Math.round(defaultWaterAmount.value * 10) / 10;
};

// 水相组成：每行 { consumableId, name, weight, stockQty, stockUnit, unitPrice, origUnit, fromInventory }
const waterPhase = ref<any[]>([]);

// 新增一行：第一行默认填「纯净水」+ 目标水量；后续行 name 空、weight 填剩余
const addWaterRow = () => {
  const used = waterPhase.value.reduce((s, w) => s + (Number(w.weight) || 0), 0);
  const remaining = Math.max(0, waterAmountTarget.value - used);
  const isFirst = waterPhase.value.length === 0;
  const row: any = {
    consumableId: null,
    name: isFirst ? '纯净水' : '',
    weight: Math.round(remaining * 10) / 10,
    stockQty: null,
    stockUnit: '',
    unitPrice: null,
    origUnit: '',
    fromInventory: false,
  };
  waterPhase.value.push(row);
};

const removeWaterRow = (i: number) => waterPhase.value.splice(i, 1);

// 选择水相物料时填充库存/单价
const onWaterRowChange = (row: any, val: string) => {
  row.name = val || '';
  const matched = waterPhaseList.value.find((w: any) => w.name === val);
  if (matched) {
    row.consumableId = matched.id;
    row.stockQty = matched.quantity;
    row.stockUnit = matched.unit;
    row.origUnit = matched.unit;
    row.unitPrice = matched.unitPrice || 0;
    row.fromInventory = true;
  } else {
    row.consumableId = null;
    row.stockQty = null;
    row.stockUnit = '';
    row.origUnit = '';
    row.unitPrice = null;
    row.fromInventory = false;
  }
};

// 单行库存不足？（仅对库存物料生效）
const isRowInsufficient = (row: any) => {
  if (!row.fromInventory || !(Number(row.weight) > 0)) return false;
  return Number(row.weight) > toGramsUF(row.stockQty || 0, row.stockUnit);
};

// 实际总水量 = 所有行用量之和（用于入模总重、成本、提交）
const waterAmount = computed(() => waterPhase.value.reduce((s, w) => s + (Number(w.weight) || 0), 0));

// 离目标水量还差多少（正=剩余、负=超出）
const remainingWater = computed(() => waterAmountTarget.value - waterAmount.value);

// 只要任意一行库存不足，禁用「下一步」
const waterStockInsufficient = computed(() => waterPhase.value.some(isRowInsufficient));

// 有效水相行（重量 > 0）：用于预览、提交、成本核算
const activeWaterPhase = computed(() => waterPhase.value.filter(w => Number(w.weight) > 0));

const estimatedTotalWeight = computed(() => totalUsedWeight.value + actualNaOH.value + waterAmount.value);

// 进入第 3 步时，若水相为空，自动加一行
watch(step, (s) => {
  if (s === 2 && waterPhase.value.length === 0) addWaterRow();
});

// ---- 添加物 ----
const additiveList = ref<any[]>([]);
const additives = ref<any[]>([]);

const onAdditiveChange = (row: any, id: number) => {
  const c = additiveList.value.find((i: any) => i.id === id);
  if (c) {
    row.consumableName = c.name;
    row.origUnit = c.unit;
    const gpu = unitToGram(1, c.unit);
    row.unitPrice = gpu > 0 ? (c.unitPrice || 0) / gpu : (c.unitPrice || 0); // 归一化为克单价
    row.stockQuantity = c.quantity;
    row.unitFactor = unitToGram(1, c.unit);
    row.stockGrams = c.weightInGrams;
  }
};

// ---- 成本 ----
const costBreakdown = computed(() => {
  const items: any[] = [];
  // 油脂成本（unitPrice 已是克单价）
  oilPhase.value.filter(o => o.consumableId).forEach(o => {
    const g = o.weight || 0;
    items.push({ name: '油脂: ' + o.oilName, amount: g + 'g', cost: g * (o.unitPrice || 0) });
  });
  // NaOH
  items.push({ name: 'NaOH(碱)', amount: actualNaOH.value.toFixed(1) + 'g', cost: 0 });
  // 水相：按每行单独算成本（非库存物料如「纯净水」按 0 元）
  activeWaterPhase.value.forEach(w => {
    let cost = 0;
    if (w.fromInventory && w.unitPrice != null) {
      const gramsPerUnit = Math.max(1, unitToGram(1, w.origUnit || 'ml'));
      cost = (Number(w.weight) || 0) * ((w.unitPrice || 0) / gramsPerUnit);
    }
    items.push({ name: '水相: ' + (w.name || '未命名'), amount: Number(w.weight).toFixed(0) + 'g', cost });
  });
  // 添加物（unitPrice 已是克单价）
  additives.value.filter(a => a.consumableId).forEach(a => {
    const g = a.quantityUsed || 0;
    items.push({ name: a.consumableName || '添加物', amount: g + 'g', cost: g * (a.unitPrice || 0) });
  });
  return items;
});

const totalCost = computed(() => costBreakdown.value.reduce((s, i) => s + i.cost, 0));

// ---- 加载数据 ----
onMounted(async () => {
  addOilRow();
  try {
    const [oilRes, consRes] = await Promise.all([queryAllOils(), getAvailableConsumables()]);
    oilRefTable.value = oilRes.data || [];
    const allCons = consRes.data.data || [];
    // 物料库存中的油脂（type=1）
    oilInventory.value = allCons.filter((c: any) => c.consumableType === 1);
    // 水相（type=4）和 其它（type=0）
    waterPhaseList.value = allCons.filter((c: any) => c.consumableType === 4 || c.consumableType === 0);
    // 添加物（精油 type=2，添加物 type=3）
    additiveList.value = allCons.filter((c: any) => c.consumableType === 2 || c.consumableType === 3);
  } catch (e) { ElMessage.error('加载数据失败'); }
});

// ---- 步骤验证 ----
const nextStep = () => {
  if (step.value === 0) {
    if (!form.name) { ElMessage.warning('请输入皂的名称'); return; }
  }
  if (step.value === 1) {
    const validOils = oilPhase.value.filter(o => o.consumableId);
    if (!validOils.length) { ElMessage.warning('请至少选择一种油脂'); return; }
    for (let i = 0; i < oilPhase.value.length; i++) {
      if (!oilPhase.value[i].consumableId) continue;
      if (!oilPhase.value[i].weight || oilPhase.value[i].weight <= 0) {
        ElMessage.warning(`请为「${oilPhase.value[i].oilName}」输入有效重量`); return;
      }
    }
  }
  if (step.value === 2) {
    if (activeWaterPhase.value.length === 0) {
      ElMessage.warning('请至少添加一种水相并填写用量');
      return;
    }
    if (waterStockInsufficient.value) {
      const bad = waterPhase.value.find(isRowInsufficient);
      const stockG = toGramsUF(bad?.stockQty || 0, bad?.stockUnit).toFixed(0);
      ElMessage.warning(`水相「${bad?.name || ''}」库存不足（用量 ${Number(bad?.weight).toFixed(0)}g，库存 ${stockG}g），请调小用量或更换`);
      return;
    }
  }
  step.value++;
};

// ---- 提交 ----
const submit = async () => {
  submitting.value = true;
  try {
    // 油脂消耗：直接从物料库存选中，consumableId 已确定
    const oilUsages: any[] = [];
    oilPhase.value.filter(o => o.consumableId && o.weight > 0).forEach(o => {
      oilUsages.push({
        consumableId: o.consumableId,
        quantityUsed: o.weight,
        unitPrice: o.unitPrice || 0,
        unit: 'g',
      });
    });

    // 添加物
    const addUsages = additives.value.filter(a => a.consumableId).map(a => ({
      consumableId: a.consumableId,
      quantityUsed: a.quantityUsed || 0, // 用户输入的是克重
      unitPrice: a.unitPrice || 0,
      unit: 'g',
    }));

    // 水相：每行若来自物料库存（consumableId 非空）就加入扣减；非库存（如「纯净水」）跳过
    const waterUsages: any[] = activeWaterPhase.value
      .filter(w => w.consumableId != null)
      .map(w => ({
        consumableId: w.consumableId,
        quantityUsed: Number(w.weight) || 0,
        unitPrice: w.unitPrice || 0,
        unit: 'g',
      }));

    // 合并所有有效 usage（必须 consumableId 不为空）
    const validUsages = [...oilUsages, ...addUsages, ...waterUsages].filter(u => u.consumableId != null);

    const payload = {
      name: form.name,
      productionDate: form.productionDate,
      totalWeight: estimatedTotalWeight.value,
      maturationDate: form.maturationDate,
      notes: form.notes,
      usages: validUsages,
    };

    await addSoapProduction(payload);
    ElMessage.success('生产批次创建成功！');
    props.update();
  } catch (e: any) {
    ElMessage.error(e?.displayMessage || e?.response?.data?.message || e?.message || '创建失败');
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.section-title { margin-bottom: 8px; font-size: 14px; }
.stat-row { padding: 4px 0; font-size: 14px; display: flex; justify-content: space-between; align-items: center; }
.stat-row .val { font-size: 16px; }
</style>
