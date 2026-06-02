<template>
  <div>
    <div class="container">
      <div class="search-box">
        <el-button type="primary" :icon="Refresh" @click="getData">刷新数据</el-button>
      </div>

      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column>
        <el-table-column label="物料名称" min-width="140">
          <template #default="scope">{{ scope.row.consumableName || '物料ID:' + scope.row.consumableId }}</template>
        </el-table-column>
        <el-table-column label="品牌" width="100">
          <template #default="scope">{{ scope.row.consumableBrand || '-' }}</template>
        </el-table-column>
        <el-table-column label="生产批次" min-width="150">
          <template #default="scope">
            <span v-if="scope.row.productionName">{{ scope.row.productionName }}</span>
            <span v-else style="color: #999;">批次号: {{ scope.row.productionBatchNumber || scope.row.soapId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="消耗用量" width="120" align="center">
          <template #default="scope">{{ scope.row.quantityUsed }}g</template>
        </el-table-column>
        <el-table-column label="单价" width="100" align="center">
          <template #default="scope">{{ scope.row.unitPrice }} 元</template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="center">
          <template #default="scope">{{ scope.row.subtotal != null ? scope.row.subtotal.toFixed(2) + ' 元' : '-' }}</template>
        </el-table-column>
        <el-table-column label="使用时间" width="170" align="center">
          <template #default="scope">{{ formatDateTime(scope.row.created) }}</template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="query.pageIndex"
            :page-size="query.pageSize"
            :total="pageTotal"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="consumablesUsage">
import { ref, reactive } from 'vue';
import { Refresh } from '@element-plus/icons-vue';
import { queryConsumablesUsage } from '../api';

interface UsageItem {
  id: number;
  consumableName: string;
  consumableBrand: string;
  consumableId: number;
  soapId: number;
  productionName: string;
  productionBatchNumber: string;
  quantityUsed: number;
  unit: string;
  unitPrice: number;
  created: string;
}

const query = reactive({
  pageIndex: 1,
  pageSize: 10,
});
const tableData = ref<UsageItem[]>([]);
const pageTotal = ref(0);

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const h = String(d.getHours()).padStart(2, '0');
  const min = String(d.getMinutes()).padStart(2, '0');
  return `${y}-${m}-${day} ${h}:${min}`;
};

const getData = async () => {
  const res = await queryConsumablesUsage(query);
  tableData.value = res.data.content || [];
  pageTotal.value = res.data.totalElements || 0;
};
getData();

const handlePageChange = (val: number) => {
  query.pageIndex = val;
  getData();
};
</script>

<style scoped>
.search-box {
  margin-bottom: 20px;
}
</style>
