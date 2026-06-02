<template>
  <div>
    <div class="container">
      <div class="search-box">
        <el-select v-model="query.status" placeholder="状态筛选" clearable style="width:180px;margin-right:10px;">
          <el-option label="待内包装" :value="1"/>
          <el-option label="待外包装" :value="2"/>
          <el-option label="已完成" :value="3"/>
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="warning" :icon="CirclePlusFilled" @click="openCreate">新建打包</el-button>
      </div>

      <el-table :data="tableData" border header-cell-class-name="table-header">
        <el-table-column prop="batchNumber" label="打包批次号" width="190" align="center"/>
        <el-table-column label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalMaterialCost" label="打包总成本" width="130" align="center">
          <template #default="scope">{{ scope.row.totalMaterialCost || 0 }} 元</template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="120"/>
        <el-table-column prop="created" label="创建时间" width="170" align="center">
          <template #default="scope">{{ fmt(scope.row.created) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="380" align="center" fixed="right">
          <template #default="scope">
            <el-button type="warning" size="small" :icon="View" @click="handleView(scope.row)">查看</el-button>
            <el-button type="primary" size="small" :icon="EditPen" @click="handleEdit(scope.row)">修改</el-button>
            <el-button v-if="scope.row.status===1" type="success" size="small" @click="handleStatus(scope.row,2)">外包装完成</el-button>
            <el-button v-if="scope.row.status===2" type="primary" size="small" @click="handleStatus(scope.row,3)">完成打包</el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex" :page-size="query.pageSize" :total="pageTotal" @current-change="handlePageChange"/>
      </div>
    </div>

    <!-- 新建打包对话框 -->
    <el-dialog title="新建打包" v-model="createVisible" width="900px" destroy-on-close :close-on-click-modal="false">
      <PackageCreate :update="onCreated" @cancel="createVisible=false"/>
    </el-dialog>

    <!-- 详情 -->
    <el-dialog title="打包详情" v-model="detailVisible" width="800px" destroy-on-close>
      <PackageDetail :id="detailId"/>
    </el-dialog>

    <!-- 修改打包：皂块/内包装/赠品/外包装/备注 全可编辑 -->
    <el-dialog title="修改打包" v-model="editVisible" width="980px" destroy-on-close :close-on-click-modal="false">
      <PackageEdit :id="editId" @changed="getData" @cancel="editVisible = false"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="packaging">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Search, CirclePlusFilled, View, EditPen } from '@element-plus/icons-vue';
import { queryByPageSoapPackage, deletePackage, updatePackageStatus } from '../api';
import PackageCreate from '../components/package-create.vue';
import PackageDetail from '../components/package-detail.vue';
import PackageEdit from '../components/package-edit.vue';

const query = reactive({ status: null as number|null, pageIndex: 1, pageSize: 10 });
const tableData = ref<any[]>([]);
const pageTotal = ref(0);

const statusLabel = (s: number) => ({1:'待内包装',2:'待外包装',3:'已完成'}[s]||'未知');
const statusType = (s: number) => ({1:'warning',2:'',3:'success'}[s]||'');
const fmt = (s: string) => { if(!s) return ''; const d=new Date(s); return isNaN(d.getTime())?s:d.toLocaleString(); };

const getData = async () => {
  const res = await queryByPageSoapPackage(query);
  tableData.value = res.data.content || [];
  pageTotal.value = res.data.totalElements || 0;
};
getData();
const handleSearch = () => { query.pageIndex=1; getData(); };
const handlePageChange = (v: number) => { query.pageIndex=v; getData(); };

const handleStatus = async (row: any, status: number) => {
  try {
    await ElMessageBox.confirm('确定更新状态吗？', '提示', {type:'warning'});
    await updatePackageStatus(row.id, status);
    ElMessage.success('状态已更新');
    getData();
  } catch(e: any) { if(e!=='cancel') ElMessage.error(e?.message||'操作失败'); }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除吗？皂块将恢复为在售状态。', '提示', {type:'warning'});
    await deletePackage(row.id);
    ElMessage.success('已删除');
    getData();
  } catch(e: any) { if(e!=='cancel') {} }
};

const createVisible = ref(false);
const openCreate = () => { createVisible.value = true; };
const onCreated = () => { createVisible.value = false; getData(); };

const detailVisible = ref(false);
const detailId = ref(0);
const handleView = (row: any) => { detailId.value = row.id; detailVisible.value = true; };

const editVisible = ref(false);
const editId = ref(0);
const handleEdit = (row: any) => { editId.value = row.id; editVisible.value = true; };
</script>

<style scoped>
.search-box { margin-bottom: 20px; }
</style>
