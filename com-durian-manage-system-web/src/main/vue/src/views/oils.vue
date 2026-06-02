<template>
  <div>
    <div class="container">
      <div class="search-box">
        <el-input v-model="query.name" placeholder="油脂名称" class="search-input mr10" clearable></el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="warning" :icon="CirclePlusFilled" @click="visible = true">新增油脂</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column>
        <el-table-column prop="oilChineseName" label="名称" align="center"></el-table-column>
        <el-table-column prop="oilEnglishName" label="英文名" align="center"></el-table-column>
        <el-table-column prop="sodiumHydroxideSaponificationValue" label="NaOH皂化价" width="130" align="center"></el-table-column>
        <el-table-column prop="insValue" label="INS" align="center"></el-table-column>
        <el-table-column prop="creator" label="创建人" align="center"></el-table-column>
        <el-table-column prop="modifier" label="修改人" align="center"></el-table-column>
        <el-table-column prop="createdStr" label="创建时间" align="center"></el-table-column>
        <el-table-column prop="modifiedStr" label="修改时间" align="center"></el-table-column>

        <el-table-column label="操作" width="280" align="center">
          <template #default="scope">
            <el-button type="warning" size="small" :icon="View" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button
                v-if="canModify(scope.row)"
                type="primary"
                size="small"
                :icon="Edit"
                @click="handleEdit(scope.$index, scope.row)"
            >
              编辑
            </el-button>
            <el-button
                v-if="canModify(scope.row)"
                type="danger"
                size="small"
                :icon="Delete"
                @click="handleDelete(scope.$index)"
            >
              删除
            </el-button>
            <el-tag v-else type="info" size="small">公共数据</el-tag>
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
        :title="idEdit ? '编辑油脂' : '新增油脂'"
        v-model="visible"
        width="650px"
        destroy-on-close
        :close-on-click-modal="false"
        @close="closeDialog"
    >
      <TableEdit :data="rowData" :edit="idEdit" :update="updateData" />
    </el-dialog>
    <el-dialog title="查看用户详情" v-model="visible1" width="700px" destroy-on-close>
      <TableDetail :data="rowData" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="basetable">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Edit, Search, CirclePlusFilled, View } from '@element-plus/icons-vue';
import {fetchData, queryByPageOils, deleteOil} from '../api/index';
import TableEdit from '../components/oils-edit.vue';
import TableDetail from '../components/oils-detail.vue';
import { useUserStore } from '../store/user';

const userStore = useUserStore();

// 当前用户是否可以编辑/删除某行
const canModify = (row: any) => {
  if (userStore.isSuperAdmin) return true;
  return row && row.ownerUserId === userStore.userId;
};

interface TableItem {
  id: number;
  ownerUserId: number;
  oilChineseName: string;
  oilEnglishName: string;
  sodiumHydroxideSaponificationValue: number;
  insValue: number;
  creator: string;
  modifier: string;
  createTime: string;
  modifyTime: string;
}

const query = reactive({
  address: '',
  name: '',
  pageIndex: 1,
  pageSize: 10
});
const tableData = ref<TableItem[]>([]);
const pageTotal = ref(0);
// 获取表格数据
const getData = async () => {
  const res = await queryByPageOils(query);
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
  ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    .then(async () => {
      const row = tableData.value[index];
      await deleteOil(row.id);
      ElMessage.success('删除成功');
      getData();
    })
    .catch(() => {});
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
</script>

<style scoped>
:deep(.el-table th) { white-space: nowrap; }
.search-box {
  margin-bottom: 20px;
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