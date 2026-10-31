<template>
  <div class="app-container home">
    <!-- 顶部标题 -->
    <div class="dashboard-header">
      <span class="dashboard-title">星云设备与资产管理系统-数据看板</span>
      <el-button type="warning" plain icon="DataAnalysis" @click="router.push('/bigscreen')">进入数据大屏</el-button>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="20">
      <el-col :xs="12" :sm="12" :lg="6" v-for="card in cardList" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" :style="{ backgroundColor: card.bg, color: card.color }">
              <el-icon :size="30"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="chart-title">资产状态分布</span></template>
          <div ref="pieRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="chart-title">近7天报修趋势</span></template>
          <div ref="trendRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近报修记录 -->
    <el-card shadow="never" class="record-card">
      <template #header>
        <div class="record-header">
          <span class="record-title">最近报修记录</span>
          <el-tag type="info" effect="plain" size="small">最新10条</el-tag>
        </div>
      </template>
      <el-table :data="recentRepairs" style="width: 100%">
        <el-table-column label="报修单号" prop="repairNo" align="center" width="140" />
        <el-table-column label="资产名称" prop="assetName" align="center" :show-overflow-tooltip="true" />
        <el-table-column label="报修人" prop="repairUser" align="center" width="100" />
        <el-table-column label="问题描述" prop="problemDesc" align="center" :show-overflow-tooltip="true" />
        <el-table-column label="报修状态" prop="status" align="center" width="100">
          <template #default="scope">
            <dict-tag :options="biz_repair_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="报修时间" prop="createTime" align="center" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="Index">
import * as echarts from "echarts";
import { Box, Warning, Tools, CirclePlus } from "@element-plus/icons-vue";
import { getDashboardSummary } from "@/api/biz/dashboard";

const { proxy } = getCurrentInstance();
const router = useRouter();
const { biz_repair_status } = proxy.useDict("biz_repair_status");

const pieRef = ref(null);
const trendRef = ref(null);
const recentRepairs = ref([]);
let pieChart = null;
let trendChart = null;

/** 卡片汇总数字（来自统计接口） */
const counts = ref({ assetTotal: 0, pendingRepairs: 0, repairingAssets: 0, todayAssets: 0 });

const cardList = computed(() => [
  { label: "资产总数", value: counts.value.assetTotal, icon: Box, color: "#409EFF", bg: "#ecf5ff" },
  { label: "待处理报修", value: counts.value.pendingRepairs, icon: Warning, color: "#F56C6C", bg: "#fef0f0" },
  { label: "维修中设备", value: counts.value.repairingAssets, icon: Tools, color: "#E6A23C", bg: "#fdf6ec" },
  { label: "今日新增资产", value: counts.value.todayAssets, icon: CirclePlus, color: "#67C23A", bg: "#f0f9eb" }
]);

/** 资产状态展示配置（与字典 biz_asset_status 保持一致） */
const statusConfig = {
  "0": { name: "闲置", color: "#909399" },
  "1": { name: "在用", color: "#409EFF" },
  "2": { name: "维修中", color: "#E6A23C" },
  "3": { name: "报废", color: "#F56C6C" }
};

/** 加载看板数据 */
function loadSummary() {
  getDashboardSummary().then(res => {
    const data = res.data || {};
    counts.value = data.counts || counts.value;
    recentRepairs.value = data.recentRepairs || [];
    nextTick(() => {
      initPieChart(data.assetStatusStats || []);
      initTrendChart(data.repairTrend || []);
    });
  });
}

/** 资产状态分布饼图 */
function initPieChart(stats) {
  if (!pieRef.value) return;
  if (pieChart == null) {
    pieChart = echarts.init(pieRef.value);
  }
  const total = stats.reduce((sum, s) => sum + Number(s.cnt), 0);
  pieChart.setOption({
    tooltip: { trigger: "item", formatter: "{b}：{c} 台（{d}%）" },
    legend: { bottom: "0%", icon: "circle" },
    series: [
      {
        type: "pie",
        radius: ["40%", "65%"],
        center: ["50%", "45%"],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: "#fff", borderWidth: 2 },
        label: { show: false },
        data: stats.map(s => ({
          name: (statusConfig[s.status] || {}).name || "未知",
          value: Number(s.cnt),
          itemStyle: { color: (statusConfig[s.status] || {}).color }
        }))
      }
    ]
  });
  if (total === 0) {
    pieChart.setOption({ title: { text: "暂无数据", left: "center", top: "center", textStyle: { color: "#909399", fontSize: 14 } } });
  }
}

/** 近7天报修趋势柱状图（后端只返回有数据的日期，前端补零） */
function initTrendChart(trend) {
  if (!trendRef.value) return;
  if (trendChart == null) {
    trendChart = echarts.init(trendRef.value);
  }
  const countMap = {};
  trend.forEach(t => { countMap[t.repairDate] = Number(t.cnt); });
  const days = [];
  const values = [];
  for (let i = 6; i >= 0; i--) {
    const d = new Date(Date.now() - i * 86400000);
    const key = String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
    days.push(key);
    values.push(countMap[key] || 0);
  }
  trendChart.setOption({
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "4%", bottom: "3%", top: "12%", containLabel: true },
    xAxis: { type: "category", data: days, boundaryGap: false },
    yAxis: { type: "value", minInterval: 1 },
    series: [
      {
        type: "line",
        smooth: true,
        symbolSize: 8,
        data: values,
        itemStyle: { color: "#409EFF" },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(64,158,255,0.35)" },
            { offset: 1, color: "rgba(64,158,255,0.05)" }
          ])
        }
      }
    ]
  });
}

function handleResize() {
  if (pieChart != null) pieChart.resize();
  if (trendChart != null) trendChart.resize();
}

onMounted(() => {
  loadSummary();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (pieChart != null) pieChart.dispose();
  if (trendChart != null) trendChart.dispose();
});
</script>

<style scoped lang="scss">
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 16px 20px;
  border-radius: 6px;
  background: linear-gradient(135deg, #409eff 0%, #7b5cff 100%);
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);

  .dashboard-title {
    font-size: 22px;
    font-weight: 700;
    color: #fff;
    letter-spacing: 2px;
  }
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-4px);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .stat-body {
    display: flex;
    align-items: center;
  }

  .stat-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    margin-right: 16px;
    border-radius: 12px;
    flex-shrink: 0;
  }

  .stat-info {
    .stat-value {
      font-size: 30px;
      font-weight: 700;
      line-height: 1.2;
    }

    .stat-label {
      margin-top: 4px;
      font-size: 14px;
      color: #909399;
    }
  }
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 8px;

  .chart-title {
    font-size: 16px;
    font-weight: 600;
  }
}

.chart-box {
  width: 100%;
  height: 300px;
}

.record-card {
  border-radius: 8px;

  .record-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .record-title {
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
