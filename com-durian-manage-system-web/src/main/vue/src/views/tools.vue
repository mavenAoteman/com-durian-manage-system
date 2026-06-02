<template>
  <div>
    <div class="container">
      <div class="search-box">
        <div class="search-left">
          <el-input v-model="query.name" placeholder="工具名称" class="search-input mr10" clearable></el-input>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button type="warning" :icon="CirclePlusFilled" @click="visible = true">新增工具</el-button>
        </div>
        <div class="total-amount-panel">
          <span class="total-amount-label">工具合计金额</span>
          <span class="total-amount-value">{{ toolsTotalAmount }} 元</span>
        </div>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column>
        <el-table-column prop="name" label="工具名称" align="center"></el-table-column>
        <el-table-column prop="brand" label="品牌名称" align="center"></el-table-column>
        <el-table-column prop="quantity" label="数量" align="center"></el-table-column>
        <el-table-column prop="unitPrice" label="单价(单位:元)" align="center"></el-table-column>
        <el-table-column prop="totalPrice" label="总价(单位:元)" align="center"></el-table-column>
        <el-table-column prop="createdBy" label="创建人" align="center"></el-table-column>
        <el-table-column prop="updatedBy" label="修改人" align="center"></el-table-column>
        <el-table-column prop="createdStr" label="创建时间" align="center"></el-table-column>
        <el-table-column prop="modifiedStr" label="修改时间" align="center"></el-table-column>

        <el-table-column label="操作" width="280" align="center">
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
                @click="handleDelete(scope.$index)"
                v-permiss="16"
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
        :title="idEdit ? '编辑工具' : '新增工具'"
        v-model="visible"
        width="500px"
        destroy-on-close
        :close-on-click-modal="false"
        @close="closeDialog"
    >
      <TableEdit :data="rowData" :edit="idEdit" :update="updateData"/>
    </el-dialog>
    <el-dialog title="查看工具" v-model="visible1" width="700px" destroy-on-close>
      <TableDetail :data="rowData"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="basetable">
import {computed, ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Delete, Edit, Search, CirclePlusFilled, View} from '@element-plus/icons-vue';
import {deleteSoapTools, queryByPageSoapTools} from '../api';
import TableEdit from '../components/tools-edit.vue';
import TableDetail from '../components/tools-detail.vue';
import {useRouter} from 'vue-router';

const router = useRouter();

interface TableItem {
  id: number;
  name: string;
  brand: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  createdBy: string,
  updatedBy: string,
  createdStr: string,
  modifiedStr: string,
}

const query = reactive({
  address: '',
  name: '',
  pageIndex: 1,
  pageSize: 10
});
const tableData = ref<TableItem[]>([]);
const pageTotal = ref(0);
const toolsTotalAmount = computed(() => {
  const total = tableData.value.reduce((sum, item) => {
    return sum + Number(item.totalPrice || 0);
  }, 0);
  return total.toFixed(2);
});
// 获取表格数据
const getData = async () => {
  const res = await queryByPageSoapTools(query);
  tableData.value = res.data.content;
  pageTotal.value = res.data.totalElements || 50;
};
getData();

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
const handleDelete = (index: number) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    type: 'warning'
  }).then(() => {
        const idToDelete = tableData.value[index].id; // 获取要删除的那一行的 id
        const res = deleteSoapTools(idToDelete);
        console.log(res)
        if (res) {
          ElMessage.success('删除成功');
          tableData.value.splice(index, 1);
        }
      })
      .catch(() => {
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

// 跳转到物料管理页面
const goToConsumables = () => {
  router.push('/consumables');
};
</script>

<style scoped>
.search-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-left {
  display: flex;
  align-items: center;
}

.total-amount-panel {
  min-width: 190px;
  height: 40px;
  padding: 0 14px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
}

.total-amount-label {
  font-size: 13px;
  color: #606266;
}

.total-amount-value {
  font-size: 15px;
  font-weight: 700;
  color: #409eff;
}

.search-input {
  width: 200px;
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
