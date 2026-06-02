<template>
  <div>
    <div class="container">
      <div class="search-box">
        <el-row :gutter="20">
          <el-col :span="24">
            <div style="display: flex; align-items: center; margin-bottom: 15px;">
              <span style="width: 80px; text-align: right; margin-right: 10px;">物料名称:</span>
              <el-input v-model="query.name" placeholder="请输入物料名称" class="search-input mr10" clearable style="flex: 1; max-width: 200px;"></el-input>
              
              <span style="width: 80px; text-align: right; margin-right: 10px; margin-left: 20px;">品牌名称:</span>
              <el-input v-model="query.brand" placeholder="请输入品牌名称" class="search-input mr10" clearable style="flex: 1; max-width: 200px;"></el-input>
              
              <span style="width: 80px; text-align: right; margin-right: 10px; margin-left: 20px;">物料类型:</span>
              <el-select v-model="query.consumableType" placeholder="请选择物料类型" clearable class="search-input mr10" style="flex: 1; max-width: 200px;">
                <el-option
                  v-for="item in consumableTypes"
                  :key="item.code"
                  :label="item.desc"
                  :value="item.code">
                </el-option>
              </el-select>
            </div>
            
            <div style="display: flex; align-items: center;">
              <span style="width: 80px; text-align: right; margin-right: 10px;">采购渠道:</span>
              <el-input v-model="query.purchaseChannel" placeholder="请输入采购渠道" class="search-input mr10" clearable style="flex: 1; max-width: 200px;"></el-input>
              
              <span style="width: 80px; text-align: right; margin-right: 10px; margin-left: 20px;">物料状态:</span>
              <el-select v-model="query.status" placeholder="请选择物料状态" clearable class="search-input mr10" style="flex: 1; max-width: 150px;">
                <el-option label="可用" :value="1"></el-option>
                <el-option label="已用完" :value="2"></el-option>
                <el-option label="已过期" :value="3"></el-option>
              </el-select>
              
              <span style="width: 80px; text-align: right; margin-right: 10px; margin-left: 40px;">采购时间:</span>
              <el-date-picker
                v-model="purchaseTimeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="flex: 1; max-width: 350px;">
              </el-date-picker>
            </div>
          </el-col>
        </el-row>
        
        <div style="margin-top: 15px;">
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button type="warning" :icon="CirclePlusFilled" @click="visible = true">新增物料</el-button>
        </div>
      </div>
      
      <!-- 筛选条件显示 -->
      <div v-if="hasActiveFilters" class="filter-tags">
        <div style="margin-bottom: 10px; font-weight: bold;">当前筛选条件：</div>
        <el-tag v-if="query.name" closable @close="clearFilter('name')">物料名称: {{ query.name }}</el-tag>
        <el-tag v-if="query.brand" closable @close="clearFilter('brand')">品牌名称: {{ query.brand }}</el-tag>
        <el-tag v-if="query.consumableType !== null && query.consumableType !== undefined" closable @close="clearFilter('consumableType')">
          物料类型: {{ getConsumableTypeText(query.consumableType) }}
        </el-tag>
        <el-tag v-if="query.purchaseChannel" closable @close="clearFilter('purchaseChannel')">
          采购渠道: {{ query.purchaseChannel }}
        </el-tag>
        <el-tag v-if="query.status !== null && query.status !== undefined" closable @close="clearFilter('status')">
          物料状态: {{ getStatusText(query.status) }}
        </el-tag>
        <el-tag v-if="purchaseTimeRange[0] || purchaseTimeRange[1]" closable @close="clearFilter('purchaseTime')">
          采购时间: {{ purchaseTimeRange[0] || '开始' }} 至 {{ purchaseTimeRange[1] || '结束' }}
        </el-tag>
        <el-button type="text" @click="clearAllFilters" style="margin-left: 10px;">清除所有筛选</el-button>
      </div>
      
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="name" label="名称" align="center"></el-table-column>
        <el-table-column prop="brand" label="品牌名称" align="center"></el-table-column>
        <el-table-column prop="quantity" label="数量" align="center"></el-table-column>
        <el-table-column prop="unit" label="单位" align="center"></el-table-column>
        <el-table-column label="物料类型" align="center">
          <template #default="scope">
            {{ getConsumableTypeText(scope.row.consumableType) }}
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" align="center"></el-table-column>
        <el-table-column prop="totalPrice" label="总价" align="center"></el-table-column>
        <el-table-column prop="purchaseChannel" label="采购渠道" align="center"></el-table-column>
        <el-table-column prop="purchaseTime" label="采购时间" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.purchaseTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedBy" label="修改人" align="center"></el-table-column>
        <el-table-column prop="modifiedStr" label="修改时间" align="center"></el-table-column>

        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button type="warning" size="small" :icon="View" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button
                type="primary"
                size="small"
                :icon="Edit"
                @click="handleEdit(scope.$index, scope.row)"
                v-permiss="15"
            >
              编辑
            </el-button>
            <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click="handleDelete(scope.$index, scope.row)"
            >
              删除
            </el-button>
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
    <el-dialog
        :title="idEdit ? '编辑物料' : '新增物料'"
        v-model="visible"
        width="500px"
        destroy-on-close
        :close-on-click-modal="false"
        @close="closeDialog"
    >
      <TableEdit :data="rowData" :edit="idEdit" :update="updateData"/>
    </el-dialog>
    <el-dialog title="查看物料" v-model="visible1" width="700px" destroy-on-close>
      <TableDetail :data="rowData"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="basetable">
import {ref, reactive, onMounted, computed} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Delete, Edit, Search, CirclePlusFilled, View} from '@element-plus/icons-vue';
import {deleteSoapConsumables, queryByPageSoapConsumables, getConsumableTypes} from '../api';
import TableEdit from '../components/consumables-edit.vue';
import TableDetail from '../components/consumables-detail.vue';

interface TableItem {
  id: number;
  name: string;
  brand: string;
  quantity: number;
  unit: string;
  consumableType: number;
  unitPrice: number;
  totalPrice: number;
  purchaseChannel: string;
  purchaseTime: string;
  updatedBy: string,
  modifiedStr: string,
}

const query = reactive({
  address: '',
  name: '',
  brand: '',
  consumableType: null,
  purchaseChannel: '',
  status: 1, // 默认只查询可用状态(1)的物料
  startDate: '',
  endDate: '',
  pageIndex: 1,
  pageSize: 10
});

// 采购时间范围
const purchaseTimeRange = ref<[string, string]>(['', '']);

const tableData = ref<TableItem[]>([]);
const pageTotal = ref(0);

const consumableTypes = ref([]);

const loadConsumableTypes = async () => {
  try {
    const response = await getConsumableTypes();
    consumableTypes.value = response.data.data || [];
  } catch (error) {
    ElMessage.error('获取物料类型失败');
    console.error(error);
  }
};

// 物料类型映射
const getConsumableTypeText = (type: number) => {
  const typeMap: { [key: number]: string } = {
    0: '其它',
    1: '油脂',
    2: '精油',
    3: '添加物',
    4: '水相',
    5: '包装材料'
  };
  return typeMap[type] || '其它';
};

// 状态映射
const getStatusText = (status: number) => {
  const statusMap: { [key: number]: string } = {
    0: '已删除',
    1: '可用',
    2: '已用完',
    3: '已过期'
  };
  return statusMap[status] || '未知';
};

// 日期格式化函数
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return dateString;
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

// 获取表格数据
const getData = async () => {
  // 处理日期范围参数
  if (purchaseTimeRange.value && purchaseTimeRange.value.length === 2) {
    query.startDate = purchaseTimeRange.value[0];
    query.endDate = purchaseTimeRange.value[1];
  } else {
    query.startDate = '';
    query.endDate = '';
  }
  
  const res = await queryByPageSoapConsumables(query);
  tableData.value = res.data.content;
  pageTotal.value = res.data.totalElements || 50;
};

// 组件挂载时加载物料类型
onMounted(() => {
  loadConsumableTypes();
  getData();
});

// 查询操作
const handleSearch = () => {
  query.pageIndex = 1;
  getData();
};
// 分页导航
const handlePageChange = (val: number) => {
  query.pageIndex = val;
  getData();
};

// 删除操作
const handleDelete = (index: number, row: TableItem) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除"' + row.name + '"这条记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
        const idToDelete = row.id; // 获取要删除的那一行的 id
        try {
          await deleteSoapConsumables(idToDelete);
          ElMessage.success('删除成功');
          tableData.value.splice(index, 1);
        } catch (error) {
          ElMessage.error('删除失败');
          console.error(error);
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除');
      });
};

const visible = ref(false);
let idx: number = -1;
const idEdit = ref(false);
const rowData = ref({});
const handleEdit = (index: number, row: TableItem) => {
  idx = index;
  rowData.value = row;
  idEdit.value = true;
  visible.value = true;
};
const updateData = (row: TableItem) => {
  idEdit.value ? (tableData.value[idx] = row) : tableData.value.unshift(row);
  console.log(tableData.value);
  closeDialog();
};

const closeDialog = () => {
  visible.value = false;
  idEdit.value = false;
};

const visible1 = ref(false);
const handleView = (row: TableItem) => {
  rowData.value = row;
  visible1.value = true;
};

// 检查是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return query.name || 
         query.brand || 
         (query.consumableType !== null && query.consumableType !== undefined) || 
         query.purchaseChannel || 
         (query.status !== 1) || // 状态不是默认的"可用"就算激活了筛选
         purchaseTimeRange.value[0] || 
         purchaseTimeRange.value[1];
});

// 清除特定筛选条件
const clearFilter = (filterName: string) => {
  switch (filterName) {
    case 'name':
      query.name = '';
      break;
    case 'brand':
      query.brand = '';
      break;
    case 'consumableType':
      query.consumableType = null;
      break;
    case 'purchaseChannel':
      query.purchaseChannel = '';
      break;
    case 'status':
      query.status = 1; // 默认恢复到"可用"状态
      break;
    case 'purchaseTime':
      query.startDate = '';
      query.endDate = '';
      purchaseTimeRange.value = ['', ''];
      break;
  }
  handleSearch();
};

// 清除所有筛选条件
const clearAllFilters = () => {
  query.name = '';
  query.brand = '';
  query.consumableType = null;
  query.purchaseChannel = '';
  query.status = 1; // 默认恢复到"可用"状态
  query.startDate = '';
  query.endDate = '';
  purchaseTimeRange.value = ['', ''];
  handleSearch();
};

</script>

<style scoped>
.search-box {
  margin-bottom: 20px;
}

.filter-tags {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.filter-tags .el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.search-input {
  width: 200px;
}

.search-label {
  white-space: nowrap;
  margin-right: 10px;
}

.mr10 {
  margin-right: 10px;
}

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>