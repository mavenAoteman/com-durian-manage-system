<template>
  <div>
    <div class="container">
      <div class="search-box" style="display: flex; align-items: center; flex-wrap: wrap;">
        <el-input v-model="query.name" placeholder="生产名称" class="search-input mr10" clearable></el-input>
        <el-select v-model="query.status" placeholder="状态筛选" clearable class="search-input mr10">
          <el-option label="生产中" :value="1"></el-option>
          <el-option label="已脱模" :value="2"></el-option>
          <el-option label="已切块" :value="3"></el-option>
          <el-option label="晾晒中" :value="4"></el-option>
          <el-option label="已成熟" :value="5"></el-option>
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="warning" :icon="CirclePlusFilled" @click="openCreateDialog">生产皂皂咯</el-button>
        <span style="display: inline-flex; align-items: center; margin-left: auto; padding: 8px 18px; background: #fdf6ec; border: 1px solid #f5dab1; border-radius: 8px; font-size: 15px; white-space: nowrap;">
          总成本
          <b style="font-size: 20px; color: #e6a23c; margin: 0 12px 0 6px;">{{ summaryStats.totalCost.toFixed(2) }} 元</b>
          总售价
          <b style="font-size: 20px; color: #409EFF; margin: 0 12px 0 6px;">{{ summaryStats.totalSellingPrice.toFixed(2) }} 元</b>
          预计收益
          <b style="font-size: 20px; margin-left: 6px;" :style="{ color: summaryStats.estimatedProfit >= 0 ? '#67c23a' : '#f56c6c' }">{{ summaryStats.estimatedProfit.toFixed(2) }} 元</b>
        </span>
      </div>

      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="batchNumber" label="批次号" width="160" align="center"></el-table-column>
        <el-table-column prop="name" label="生产名称" align="center"></el-table-column>
        <el-table-column prop="productionDate" label="生产日期" width="120" align="center">
          <template #default="scope">{{ formatDate(scope.row.productionDate) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalWeight" label="总重量(g)" width="100" align="center">
          <template #default="scope">{{ scope.row.totalWeight || '-' }}</template>
        </el-table-column>
        <el-table-column prop="actualWeight" label="成熟后重量(g)" width="120" align="center">
          <template #default="scope">{{ scope.row.actualWeight || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalCost" label="总成本" width="110" align="center">
          <template #default="scope">{{ (scope.row.totalCost || 0) }} 元</template>
        </el-table-column>
        <el-table-column prop="totalPiecePrice" label="总售价" width="110" align="center">
          <template #default="scope">{{ (scope.row.totalPiecePrice || 0) }} 元</template>
        </el-table-column>
        <el-table-column label="毛利率" width="100" align="center">
          <template #default="scope">
            <span v-if="!scope.row.totalPiecePrice" style="color:#909399;">-</span>
            <span v-else :style="{ color: marginOf(scope.row) >= 0 ? '#67c23a' : '#f56c6c' }">
              {{ marginOf(scope.row).toFixed(1) }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="maturationDate" label="预计成熟日" width="120" align="center">
          <template #default="scope">{{ formatDate(scope.row.maturationDate) || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" width="100" align="center"></el-table-column>

        <el-table-column label="操作" width="400" align="center" fixed="right">
          <template #default="scope">
            <el-button type="warning" size="small" :icon="View" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 1" type="success" size="small" @click="handleChangeStatus(scope.row, 2)">脱模</el-button>
            <el-button v-if="scope.row.status === 2" type="primary" size="small" @click="openPieceDialog(scope.row)">切块分装</el-button>
            <el-button v-if="scope.row.status === 3" type="success" size="small" @click="handleChangeStatus(scope.row, 4)">开始晾晒</el-button>
            <el-button v-if="scope.row.status === 4" type="primary" size="small" @click="handleMature(scope.row)">标记成熟</el-button>
            <el-button v-if="scope.row.status === 5" type="primary" size="small" @click="openPieceManage(scope.row)">编辑皂块</el-button>
            <el-button v-if="scope.row.status === 5" type="warning" size="small" @click="$router.push('/packaging')">打包发货</el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
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

    <!-- 新增生产对话框（向导式） -->
    <el-dialog
        title="生产皂皂咯 - 皂化配方计算"
        v-model="createVisible"
        width="1150px"
        destroy-on-close
        :close-on-click-modal="false"
        @close="closeCreateDialog"
    >
      <SoapProductionCreate :update="onCreated" @cancel="closeCreateDialog"/>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="生产记录详情" v-model="detailVisible" width="750px" destroy-on-close>
      <SoapProductionDetail :data="rowData" @refresh="getData"/>
    </el-dialog>

    <!-- 切块分装对话框 -->
    <el-dialog
        title="切块分装"
        v-model="pieceVisible"
        width="900px"
        destroy-on-close
        :close-on-click-modal="false"
    >
      <SoapPieceEdit
          :production="rowData"
          :update="onPiecesAdded"
          @cancel="pieceVisible = false"
      />
    </el-dialog>

    <!-- 编辑皂块对话框（已切块后修改） -->
    <el-dialog
        title="编辑皂块信息"
        v-model="pieceManageVisible"
        width="900px"
        destroy-on-close
        :close-on-click-modal="false"
    >
      <SoapPieceManage
          :production="rowData"
          :update="onPiecesManaged"
          @cancel="pieceManageVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="soapProduction">
import { ref, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Search, CirclePlusFilled, View } from '@element-plus/icons-vue';
import {
  queryByPageSoapProduction,
  deleteSoapProduction,
  updateSoapProductionStatus
} from '../api';
import SoapProductionCreate from '../components/soap-production-create.vue';
import SoapProductionDetail from '../components/soap-production-detail.vue';
import SoapPieceEdit from '../components/soap-piece-edit.vue';
import SoapPieceManage from '../components/soap-piece-manage.vue';

interface TableItem {
  id: number;
  name: string;
  batchNumber: string;
  productionDate: string;
  status: number;
  totalWeight: number;
  totalCost: number;
  totalPiecePrice: number;
  maturationDate: string;
  createdBy: string;
}

const query = reactive({
  name: '',
  status: null as number | null,
  pageIndex: 1,
  pageSize: 10
});
const tableData = ref<TableItem[]>([]);
const pageTotal = ref(0);

const summaryStats = computed(() => {
  let totalCost = 0;
  let totalSellingPrice = 0;
  tableData.value.forEach(row => {
    totalCost += row.totalCost || 0;
    totalSellingPrice += row.totalPiecePrice || 0;
  });
  return { totalCost, totalSellingPrice, estimatedProfit: totalSellingPrice - totalCost };
});

// 毛利率 = (售价 - 成本) / 售价 × 100%
const marginOf = (row: TableItem) => {
  const price = row.totalPiecePrice || 0;
  const cost = row.totalCost || 0;
  return price > 0 ? (price - cost) / price * 100 : 0;
};

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

const getData = async () => {
  const res = await queryByPageSoapProduction(query);
  tableData.value = res.data.content || [];
  pageTotal.value = res.data.totalElements || 0;
};
getData();

const handleSearch = () => {
  query.pageIndex = 1;
  getData();
};

const handlePageChange = (val: number) => {
  query.pageIndex = val;
  getData();
};

// 状态流转
const handleChangeStatus = async (row: TableItem, newStatus: number) => {
  const labels: Record<number, string> = { 2: '确定已脱模吗？', 4: '确定开始晾晒吗？' };
  try {
    await ElMessageBox.confirm(labels[newStatus] || '确定要更新状态吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    });
    await updateSoapProductionStatus(row.id, newStatus);
    ElMessage.success('状态更新成功');
    getData();
  } catch (e: any) {
    if (e !== 'cancel' && e?.message !== 'cancel') {
      ElMessage.error('操作失败: ' + (e?.response?.data?.message || e?.message || '未知错误'));
    }
  }
};

// 标记成熟：需要输入成熟后实际重量
// 校验：成熟后重量必须 ≤ 晾晒前重量（皂在晾晒过程中只会越来越干，不会变重）
// 参考上限：优先 cutWeight（切块后），其次 totalWeight（入模总重）
const handleMature = (row: any) => {
  const upper = row.cutWeight || row.totalWeight || 0;
  const upperLabel = row.cutWeight ? `切块后 ${row.cutWeight}g` : (row.totalWeight ? `入模时 ${row.totalWeight}g` : '');
  ElMessageBox.prompt('请输入成熟后的实际重量（通常比切块后轻）', '标记成熟', {
    confirmButtonText: '确定', cancelButtonText: '取消',
    inputType: 'number',
    inputPlaceholder: upper > 0 ? `成熟后总重量(g)，需 ≤ ${upper}g` : '成熟后总重量(g)',
    inputValidator: (val: string) => {
      const w = parseFloat(val);
      if (!w || w <= 0) return '请输入有效重量';
      if (upper > 0 && w > upper) return `重量不可大于晾晒前${upperLabel ? '（' + upperLabel + '）' : ''}，皂只会越晾越干`;
      return true;
    },
  }).then(async ({ value }) => {
    const w = parseFloat(value);
    await updateSoapProductionStatus(row.id, 5, w);
    ElMessage.success('已标记为成熟！');
    getData();
  }).catch(() => {});
};

// 删除
const handleDelete = async (row: TableItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除批次「${row.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    await deleteSoapProduction(row.id);
    ElMessage.success('删除成功');
    getData();
  } catch (e: any) {
    if (e !== 'cancel' && e?.message !== 'cancel') {
      console.error('删除失败:', e);
    }
  }
};

// 创建对话框
const createVisible = ref(false);
const openCreateDialog = () => { createVisible.value = true; };
const closeCreateDialog = () => { createVisible.value = false; };
const onCreated = () => {
  closeCreateDialog();
  getData();
};

// 详情对话框
const detailVisible = ref(false);
const rowData = ref<TableItem | any>({});
const handleView = (row: TableItem) => {
  rowData.value = row;
  detailVisible.value = true;
};

// 切块对话框
const pieceVisible = ref(false);
const openPieceDialog = (row: TableItem) => {
  rowData.value = row;
  pieceVisible.value = true;
};
const onPiecesAdded = () => {
  pieceVisible.value = false;
  getData();
};

// 编辑皂块对话框（已切块后修改）
const pieceManageVisible = ref(false);
const openPieceManage = (row: TableItem) => {
  rowData.value = row;
  pieceManageVisible.value = true;
};
const onPiecesManaged = () => {
  pieceManageVisible.value = false;
  getData();
};
</script>

<style scoped>
.search-box {
  margin-bottom: 20px;
}
.search-input {
  width: 200px;
}
.mr10 {
  margin-right: 10px;
}
</style>
