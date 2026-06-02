<template>
  <div>
    <div v-if="loading" style="text-align:center;padding:40px;"><el-icon class="is-loading"><Loading/></el-icon></div>
    <div v-else-if="!pkg" style="text-align:center;padding:40px;color:#999;">加载失败</div>
    <div v-else>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="打包批次号">{{ pkg.batchNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="pkg.status===1?'warning':pkg.status===2?'':'success'">{{ {1:'待内包装',2:'待外包装',3:'已完成'}[pkg.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="打包总成本" :span="2">
          <b style="color:#e6a23c;">{{ pkg.totalMaterialCost||0 }} 元</b>
          <span style="color:#909399;font-size:12px;margin-left:8px;">含内包装、外包装、赠品</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ pkg.notes||'-' }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:16px;"><b>打包皂块</b></div>
      <el-table :data="pkg.pieces" border size="small">
        <el-table-column prop="pieceBatchNumber" label="批次号" width="170"/>
        <el-table-column prop="pieceName" label="名称"/>
        <el-table-column prop="pieceWeight" label="重量(g)" width="80" align="center"/>
        <el-table-column prop="productionName" label="所属生产批次" min-width="120"/>
      </el-table>

      <div style="margin-top:16px;" v-if="innerMat.length">
        <b>内包装物料</b>
        <el-table :data="innerMat" border size="small">
          <el-table-column prop="consumableName" label="物料"/>
          <el-table-column label="用量" width="120" align="center">
            <template #default="scope">{{ scope.row.quantityUsed }} {{ unitDesc(scope.row.unit) }}</template>
          </el-table-column>
          <el-table-column label="单价" width="80" align="center">
            <template #default="scope">{{ scope.row.unitPrice }} 元</template>
          </el-table-column>
          <el-table-column label="小计" width="100" align="center">
            <template #default="scope">{{ ((scope.row.quantityUsed||0)*(scope.row.unitPrice||0)).toFixed(2) }} 元</template>
          </el-table-column>
        </el-table>
      </div>

      <div style="margin-top:16px;" v-if="giftMat.length">
        <b>赠品</b>
        <el-table :data="giftMat" border size="small">
          <el-table-column prop="consumableName" label="赠品"/>
          <el-table-column label="用量" width="120" align="center">
            <template #default="scope">{{ scope.row.quantityUsed }} {{ unitDesc(scope.row.unit) }}</template>
          </el-table-column>
          <el-table-column label="单价" width="80" align="center">
            <template #default="scope">{{ scope.row.unitPrice }} 元</template>
          </el-table-column>
          <el-table-column label="小计" width="100" align="center">
            <template #default="scope">{{ ((scope.row.quantityUsed||0)*(scope.row.unitPrice||0)).toFixed(2) }} 元</template>
          </el-table-column>
        </el-table>
      </div>

      <div style="margin-top:16px;" v-if="outerMat.length">
        <b>外包装物料</b>
        <el-table :data="outerMat" border size="small">
          <el-table-column prop="consumableName" label="物料"/>
          <el-table-column label="用量" width="120" align="center">
            <template #default="scope">{{ scope.row.quantityUsed }} {{ unitDesc(scope.row.unit) }}</template>
          </el-table-column>
          <el-table-column label="单价" width="80" align="center">
            <template #default="scope">{{ scope.row.unitPrice }} 元</template>
          </el-table-column>
          <el-table-column label="小计" width="100" align="center">
            <template #default="scope">{{ ((scope.row.quantityUsed||0)*(scope.row.unitPrice||0)).toFixed(2) }} 元</template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { getPackageDetail, getConsumableUnits } from '../api';
const props = defineProps({ id: { type: Number, required: true } });
const loading = ref(false);
const pkg = ref<any>(null);
const consumableUnits = ref<any[]>([]);
const unitDescMap = computed(() => {
  const m = new Map<string, string>();
  consumableUnits.value.forEach((u: any) => m.set(u.code, u.desc));
  return m;
});
const unitDesc = (code: string) => unitDescMap.value.get(code) || code || '';
const innerMat = computed(() => (pkg.value?.materials||[]).filter((m:any)=>m.materialType==='inner'));
const outerMat = computed(() => (pkg.value?.materials||[]).filter((m:any)=>m.materialType==='outer'));
const giftMat = computed(() => (pkg.value?.materials||[]).filter((m:any)=>m.materialType==='gift'));
const load = async () => {
  if (!props.id) return;
  loading.value = true;
  try {
    const [r, uRes] = await Promise.all([getPackageDetail(props.id), getConsumableUnits()]);
    pkg.value = r.data;
    consumableUnits.value = uRes.data?.data || [];
  } catch(e){ console.error(e); }
  finally { loading.value = false; }
};
watch(()=>props.id, load, {immediate:true});
</script>
