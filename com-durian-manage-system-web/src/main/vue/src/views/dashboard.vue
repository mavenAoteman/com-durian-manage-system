<template>
  <div class="dashboard">
    <!-- 时间段选择 -->
    <div class="range-bar">
      <el-radio-group v-model="rangeType" size="small" @change="onRangeChange">
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="year">本年</el-radio-button>
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="custom">自定义</el-radio-button>
      </el-radio-group>
      <el-date-picker v-if="rangeType === 'custom'" v-model="customRange" type="daterange" range-separator="至"
        start-placeholder="开始" end-placeholder="结束" size="small" format="yyyy-MM-dd" value-format="yyyy-MM-dd"
        @change="onRangeChange" style="margin-left:8px;"/>
    </div>

    <!-- 核心盈利大卡 -->
    <el-row :gutter="16" class="core-row">
      <el-col :span="8">
        <el-card class="core-card cost-card" shadow="hover">
          <div class="core-label">💰 总成本</div>
          <div class="core-num cost-color">¥ {{ coreStats.totalMaterialCost }}</div>
          <div class="core-sub">内包装 + 外包装 + 赠品</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="core-card revenue-card" shadow="hover">
          <div class="core-label">🧾 总售价</div>
          <div class="core-num revenue-color">¥ {{ coreStats.totalSalesRevenue }}</div>
          <div class="core-sub">已完成打包皂块售价</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="core-card" :class="coreStats.totalProfit >= 0 ? 'profit-card' : 'loss-card'" shadow="hover">
          <div class="core-label">📈 {{ coreStats.totalProfit >= 0 ? '预计收益' : '亏损' }}</div>
          <div class="core-num" :class="coreStats.totalProfit >= 0 ? 'profit-color' : 'cost-color'">¥ {{ coreStats.totalProfit }}</div>
          <div class="core-sub">总售价 - 总成本</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- KPI 顶部小卡片 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :span="6" v-for="kpi in kpiCards" :key="kpi.label">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-inner">
            <div class="kpi-icon" :style="{ background: kpi.bg }">{{ kpi.icon }}</div>
            <div class="kpi-body">
              <div class="kpi-num">{{ kpi.value }}</div>
              <div class="kpi-label">{{ kpi.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中间两列 -->
    <el-row :gutter="16" class="mid-row">
      <el-col :span="12">
        <el-card class="panel-card" shadow="hover">
          <template #header><div class="panel-title">📦 生产批次状态</div></template>
          <div class="pipeline">
            <div class="pipe-item" v-for="s in productionStatuses" :key="s.status">
              <div class="pipe-dot" :style="{ background: s.color }"></div>
              <div class="pipe-info">
                <div class="pipe-label">{{ s.label }}</div>
                <div class="pipe-count" :style="{ color: s.color }">{{ s.count }} 批</div>
              </div>
              <div class="pipe-bar">
                <div class="pipe-fill" :style="{ width: s.pct + '%', background: s.color }"></div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="panel-card" shadow="hover">
          <template #header>
            <div class="panel-title">⚠️ 库存预警 <span class="sub">快用完 / 已过期</span></div>
          </template>
          <div v-if="alerts.length === 0" class="no-data">✅ 库存充足，暂无预警</div>
          <div v-else class="alert-list">
            <div class="alert-item" v-for="a in alerts" :key="a.id">
              <span class="alert-name">{{ a.name }}</span>
              <span class="alert-info" :style="{ color: a.level }">{{ a.msg }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部图表 + 最近记录 -->
    <el-row :gutter="16" class="bot-row">
      <el-col :span="12">
        <el-card class="panel-card" shadow="hover">
          <template #header><div class="panel-title">📈 近30天打包成本趋势</div></template>
          <div v-if="trendLabels.length === 0" class="no-data">暂无数据</div>
          <schart v-else class="schart" canvasId="pkgTrend" :options="trendOptions"></schart>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="panel-card" shadow="hover">
          <template #header><div class="panel-title">📋 最近打包记录</div></template>
          <div v-if="recentPackages.length === 0" class="no-data">暂无记录</div>
          <el-table v-else :data="recentPackages" border size="small" :show-header="false">
            <el-table-column prop="batchNumber" label="批次号" min-width="140"/>
            <el-table-column label="状态" width="100" align="center">
              <template #default="scope">
                <span class="status-badge" :class="'s' + scope.row.status">{{ statusLabel(scope.row.status) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="成本" width="90" align="right">
              <template #default="scope">{{ scope.row.totalMaterialCost || 0 }} 元</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import Schart from 'vue-schart';
import { queryByPageSoapPiece, queryByPageSoapPackage, queryByPageSoapProduction, queryByPageSoapConsumables, getDashboardStats, getDashboardStatsByRange } from '../api';

const rangeType = ref('month');
const customRange = ref<[string, string] | null>(null);

const coreStats = ref({ totalMaterialCost: '--', totalSalesRevenue: '--', totalProfit: '--' });

const kpiCards = ref([
  { label: '在售皂块', value: '--', color: '#409eff', bg: 'rgba(64,158,255,0.12)', icon: '🧼' },
  { label: '待内包装', value: '--', color: '#e6a23c', bg: 'rgba(230,162,60,0.12)', icon: '📦' },
  { label: '待外包装', value: '--', color: '#f56c6c', bg: 'rgba(245,108,108,0.12)', icon: '🚚' },
  { label: '已完成打包', value: '--', color: '#67c23a', bg: 'rgba(103,194,58,0.12)', icon: '✅' },
]);

const productionStatuses = ref([
  { status: 1, label: '生产中', count: 0, color: '#409eff', pct: 0 },
  { status: 2, label: '晾皂中', count: 0, color: '#e6a23c', pct: 0 },
  { status: 3, label: '成熟中', count: 0, color: '#f56c6c', pct: 0 },
  { status: 4, label: '已切块', count: 0, color: '#67c23a', pct: 0 },
]);

const alerts = ref<any[]>([]);
const recentPackages = ref<any[]>([]);
const trendLabels = ref<string[]>([]);
const trendData = ref<number[]>([]);

const statusLabel = (s: number) => ({ 1: '待内包装', 2: '待外包装', 3: '已完成' }[s] || '未知');

const trendOptions = computed(() => ({
  type: 'bar', bgColor: 'transparent',
  title: { text: '', style: { fontSize: 0 } },
  labels: trendLabels.value,
  datasets: [{ label: '打包成本(元)', data: trendData.value, fillColor: 'rgba(64,158,255,0.3)' }],
}));

const getDateRange = () => {
  const now = new Date();
  const fmt = (d: Date) => d.toISOString().split('T')[0];
  if (rangeType.value === 'month') {
    const y = now.getFullYear(), m = now.getMonth();
    return { startDate: fmt(new Date(y, m, 1)), endDate: fmt(now) };
  }
  if (rangeType.value === 'year') {
    return { startDate: fmt(new Date(now.getFullYear(), 0, 1)), endDate: fmt(now) };
  }
  return { startDate: null, endDate: null };
};

const loadDashboard = async () => {
  try {
    const range = getDateRange();
    const fmt = (d: Date) => d.toISOString().split('T')[0];
    const endDate = fmt(new Date());
    const startDate30 = fmt(new Date(Date.now() - 30 * 24 * 3600 * 1000));

    const [statsRes, pieceRes, pkgRes1, pkgRes2, pkgRes3, prodRes, recentRes] = await Promise.all([
      getDashboardStatsByRange(range.startDate || undefined, range.endDate || undefined),
      queryByPageSoapPiece({ pageIndex: 1, pageSize: 1, status: 1 }),
      queryByPageSoapPackage({ pageIndex: 1, pageSize: 1, status: 1 }),
      queryByPageSoapPackage({ pageIndex: 1, pageSize: 1, status: 2 }),
      queryByPageSoapPackage({ pageIndex: 1, pageSize: 1, status: 3 }),
      queryByPageSoapProduction({ pageIndex: 1, pageSize: 100 }),
      queryByPageSoapPackage({ pageIndex: 1, pageSize: 10 }),
    ]);

    const s = statsRes.data || {};
    coreStats.value.totalMaterialCost = s.totalMaterialCost ?? '--';
    coreStats.value.totalSalesRevenue = s.totalSalesRevenue ?? '--';
    coreStats.value.totalProfit = s.totalProfit ?? '--';

    kpiCards.value[0].value = pieceRes.data?.totalElements || 0;
    kpiCards.value[1].value = pkgRes1.data?.totalElements || 0;
    kpiCards.value[2].value = pkgRes2.data?.totalElements || 0;
    kpiCards.value[3].value = pkgRes3.data?.totalElements || 0;

    // 30天趋势
    const pkgTrendRes = await queryByPageSoapPackage({ pageIndex: 1, pageSize: 100, startDate: startDate30, endDate });
    const pkgs: any[] = pkgTrendRes.data?.content || [];
    const byDay: Record<string, number> = {};
    pkgs.forEach((p: any) => {
      if (!p.created) return;
      const day = p.created.split('T')[0];
      byDay[day] = (byDay[day] || 0) + (p.totalMaterialCost || 0);
    });
    const days = Object.keys(byDay).sort();
    trendLabels.value = days.map(d => d.slice(5).replace('-', '/'));
    trendData.value = days.map(d => Math.round(byDay[d] * 100) / 100);

    // 生产批次
    const prods: any[] = prodRes.data?.content || [];
    const counts: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 };
    prods.forEach((p: any) => { if (counts[p.status] !== undefined) counts[p.status]++; });
    const maxCount = Math.max(...Object.values(counts), 1);
    productionStatuses.value.forEach(s => { s.count = counts[s.status] || 0; s.pct = maxCount > 0 ? Math.round((s.count / maxCount) * 100) : 0; });

    // 库存预警
    const lowStock: any[] = [];
    const consPage = await queryByPageSoapConsumables({ pageIndex: 1, pageSize: 200, status: 1 });
    (consPage.data?.content || []).forEach((c: any) => { if (c.quantity != null && c.quantity <= 50) lowStock.push(c); });
    alerts.value = lowStock.slice(0, 8).map(c => ({ id: c.id, name: c.name, msg: `剩 ${c.quantity}${c.unit || ''}`, level: c.quantity <= 10 ? '#f56c6c' : '#e6a23c' }));

    // 最近打包
    recentPackages.value = (recentRes.data?.content || []).slice(0, 8);

  } catch (e) { console.error('dashboard load error', e); }
};

const onRangeChange = () => {
  if (rangeType.value === 'custom' && !customRange.value) return;
  loadDashboard();
};

onMounted(loadDashboard);
</script>

<style scoped>
.dashboard { padding: 20px; background: #f5f7fa; min-height: 100vh; }

.range-bar { margin-bottom: 16px; display: flex; align-items: center; }

.core-row { margin-bottom: 16px; }

.core-card {
  border-radius: 16px;
  border: none;
  text-align: center;
  padding: 28px 16px;
  transition: transform 0.2s;
}
.core-card:hover { transform: translateY(-2px); }

.cost-card { border-top: 4px solid #f56c6c; }
.revenue-card { border-top: 4px solid #409eff; }
.profit-card { border-top: 4px solid #67c23a; }
.loss-card { border-top: 4px solid #f56c6c; }

.core-label { font-size: 14px; color: #999; margin-bottom: 10px; }
.core-num { font-size: 36px; font-weight: bold; line-height: 1; margin-bottom: 6px; }
.core-sub { font-size: 11px; color: #bbb; }
.cost-color { color: #f56c6c; }
.revenue-color { color: #409eff; }
.profit-color { color: #67c23a; }

.kpi-row { margin-bottom: 16px; }
.kpi-card { border: none; border-radius: 12px; }
.kpi-inner { display: flex; align-items: center; gap: 14px; padding: 4px; }
.kpi-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.kpi-body { flex: 1; }
.kpi-num { font-size: 24px; font-weight: bold; line-height: 1.1; color: #222; }
.kpi-label { font-size: 12px; color: #999; margin-top: 2px; }

.mid-row { margin-bottom: 16px; }
.bot-row { margin-bottom: 16px; }
.panel-card { border: none; border-radius: 12px; }
.panel-title { font-size: 14px; color: #333; font-weight: bold; }
.sub { font-size: 12px; color: #999; font-weight: normal; margin-left: 6px; }
.no-data { text-align: center; padding: 30px; color: #999; font-size: 13px; }

.pipeline { display: flex; flex-direction: column; gap: 14px; }
.pipe-item { display: flex; align-items: center; gap: 10px; }
.pipe-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.pipe-info { display: flex; align-items: baseline; gap: 8px; width: 80px; flex-shrink: 0; min-width: 0; }
.pipe-label { font-size: 13px; color: #666; }
.pipe-count { font-size: 14px; font-weight: bold; }
.pipe-bar { flex: 1; height: 6px; background: #eee; border-radius: 3px; overflow: hidden; }
.pipe-fill { height: 100%; border-radius: 3px; transition: width 0.5s; }

.alert-list { display: flex; flex-direction: column; gap: 8px; }
.alert-item { display: flex; align-items: center; gap: 8px; font-size: 13px; padding: 4px 0; border-bottom: 1px solid #f0f0f0; }
.alert-item:last-child { border-bottom: none; }
.alert-name { color: #333; flex: 1; }
.alert-info { font-size: 12px; font-weight: bold; }

.status-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: bold; }
.status-badge.s1 { background: rgba(230,162,60,0.15); color: #e6a23c; }
.status-badge.s2 { background: rgba(245,108,108,0.15); color: #f56c6c; }
.status-badge.s3 { background: rgba(103,194,58,0.15); color: #67c23a; }

.schart { width: 100%; height: 220px; }
</style>