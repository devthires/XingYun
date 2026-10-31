<template>
  <div class="bigscreen" ref="screenRef">
    <div class="bs-header">
      <span class="bs-title">星云设备与资产管理系统 - 数据大屏</span>
      <div class="bs-tools">
        <span class="bs-time">{{ nowTime }}</span>
        <el-button size="small" text @click="toggleFullscreen">
          <el-icon style="vertical-align: middle"><FullScreen /></el-icon>
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
      </div>
    </div>

    <!-- KPI 指标 -->
    <el-row :gutter="16" class="bs-kpis">
      <el-col :xs="12" :sm="6" v-for="kpi in kpiList" :key="kpi.label">
        <div class="bs-kpi">
          <div class="bs-kpi-value" :style="{ color: kpi.color }">{{ kpi.value }}</div>
          <div class="bs-kpi-label">{{ kpi.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="bs-panel">
          <div class="bs-panel-title">资产分类分布</div>
          <div ref="categoryRef" class="bs-chart"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="bs-panel">
          <div class="bs-panel-title">资产状态分布</div>
          <div ref="statusRef" class="bs-chart"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="bs-panel">
          <div class="bs-panel-title">资产领用状态</div>
          <div ref="useRef" class="bs-chart"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="bs-panel">
          <div class="bs-panel-title">耗材库存预警</div>
          <div class="bs-table">
            <div v-if="consumableLowList.length === 0" class="bs-empty">库存全部正常</div>
            <div v-for="item in consumableLowList" :key="item.consumableName" class="bs-table-row">
              <span class="bs-table-name">{{ item.consumableName }}（{{ item.spec }}）</span>
              <span class="bs-table-qty">{{ item.stockQty }} / {{ item.warnQty }} {{ item.unit }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 趋势 -->
    <div class="bs-panel bs-trend-panel">
      <div class="bs-panel-title">近30天报修趋势</div>
      <div ref="trendRef" class="bs-chart-lg"></div>
    </div>
  </div>
</template>

<script setup name="BigScreen">
import * as echarts from "echarts";
import { FullScreen } from "@element-plus/icons-vue";
import { getBigScreenData } from "@/api/biz/bigscreen";

const { proxy } = getCurrentInstance();
const screenRef = ref(null);
const categoryRef = ref(null);
const statusRef = ref(null);
const useRef = ref(null);
const trendRef = ref(null);
const isFullscreen = ref(false);
const nowTime = ref("");
const consumableLowList = ref([]);
let charts = [];
let timer = null;

/** 资产分类展示名（与字典 biz_asset_category 一致） */
const categoryNames = { "1": "电子设备", "2": "办公家具", "3": "网络设备", "4": "工具仪器", "5": "其他" };
/** 领用状态展示名（与字典 biz_asset_use_status 一致） */
const useStatusNames = { "0": "审批中", "1": "使用中", "2": "已归还", "3": "已驳回" };

const kpiList = computed(() => [
  { label: "资产总数", value: kpiData.assetCount, color: "#409EFF" },
  { label: "资产总值（元）", value: formatMoney(kpiData.assetValueTotal), color: "#67C23A" },
  { label: "维修中资产", value: statusCount("2"), color: "#E6A23C" },
  { label: "库存预警耗材", value: consumableLowList.value.length, color: "#F56C6C" }
]);

const kpiData = reactive({ assetCount: 0, assetValueTotal: 0 });

function formatMoney(v) {
  return Number(v || 0).toLocaleString();
}

function statusCount(status) {
  const hit = raw.assetStatusStats.find(s => s.status === status);
  return hit ? Number(hit.cnt) : 0;
}

const raw = reactive({ assetStatusStats: [] });

function makeChart(el, option) {
  if (!el) return null;
  const chart = echarts.init(el);
  chart.setOption(option);
  charts.push(chart);
  return chart;
}

const darkText = "#a6c6ff";
const darkLine = "rgba(166,198,255,0.15)";

/** 资产分类玫瑰饼图 */
function initCategoryChart(stats) {
  makeChart(categoryRef.value, {
    tooltip: { trigger: "item", formatter: "{b}：{c} 台（{d}%）" },
    legend: { bottom: 0, textStyle: { color: darkText }, icon: "circle" },
    series: [{
      type: "pie",
      radius: ["30%", "65%"],
      center: ["50%", "44%"],
      roseType: "radius",
      itemStyle: { borderRadius: 5, borderColor: "#0b1c3a", borderWidth: 2 },
      label: { show: false },
      data: stats.map(s => ({ name: categoryNames[s.category] || "未知", value: Number(s.cnt) }))
    }]
  });
}

/** 资产状态环图 */
function initStatusChart(stats) {
  makeChart(statusRef.value, {
    tooltip: { trigger: "item", formatter: "{b}：{c} 台（{d}%）" },
    legend: { bottom: 0, textStyle: { color: darkText }, icon: "circle" },
    series: [{
      type: "pie",
      radius: ["40%", "65%"],
      center: ["50%", "44%"],
      itemStyle: { borderRadius: 5, borderColor: "#0b1c3a", borderWidth: 2 },
      label: { show: false },
      data: stats.map(s => ({
        name: statusName(s.status),
        value: Number(s.cnt),
        itemStyle: { color: { "0": "#909399", "1": "#409EFF", "2": "#E6A23C", "3": "#F56C6C" }[s.status] }
      }))
    }]
  });
}

function statusName(status) {
  return { "0": "闲置", "1": "在用", "2": "维修中", "3": "报废" }[status] || "未知";
}

/** 领用状态柱状图 */
function initUseChart(stats) {
  makeChart(useRef.value, {
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "4%", bottom: "14%", top: "15%", containLabel: true },
    xAxis: { type: "category", data: stats.map(s => useStatusNames[s.status] || "未知"), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkLine } } },
    yAxis: { type: "value", minInterval: 1, axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkLine } } },
    series: [{
      type: "bar",
      barWidth: "40%",
      data: stats.map(s => Number(s.cnt)),
      itemStyle: { borderRadius: [4, 4, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: "#7b5cff" }, { offset: 1, color: "#409EFF" }
      ]) }
    }]
  });
}

/** 近30天报修趋势折线 */
function initTrendChart(trend) {
  const countMap = {};
  trend.forEach(t => { countMap[t.repairDate] = Number(t.cnt); });
  const days = [];
  const values = [];
  for (let i = 29; i >= 0; i--) {
    const d = new Date(Date.now() - i * 86400000);
    const key = String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
    days.push(key);
    values.push(countMap[key] || 0);
  }
  makeChart(trendRef.value, {
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "4%", bottom: "3%", top: "12%", containLabel: true },
    xAxis: { type: "category", data: days, boundaryGap: false, axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkLine } } },
    yAxis: { type: "value", minInterval: 1, axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkLine } } },
    series: [{
      type: "line",
      smooth: true,
      showSymbol: false,
      data: values,
      lineStyle: { color: "#36D399", width: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: "rgba(54,211,153,0.4)" },
          { offset: 1, color: "rgba(54,211,153,0.02)" }
        ])
      }
    }]
  });
}

/** 加载大屏数据 */
function loadData() {
  getBigScreenData().then(res => {
    const data = res.data || {};
    kpiData.assetCount = (data.valueSummary || {}).assetCount || 0;
    kpiData.assetValueTotal = (data.valueSummary || {}).assetValueTotal || 0;
    raw.assetStatusStats = data.assetStatusStats || [];
    consumableLowList.value = data.consumableLowList || [];
    nextTick(() => {
      charts = [];
      initCategoryChart(data.assetCategoryStats || []);
      initStatusChart(data.assetStatusStats || []);
      initUseChart(data.useStatusStats || []);
      initTrendChart(data.repairTrend30 || []);
    });
  });
}

/** 真·全屏切换 */
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    screenRef.value.requestFullscreen();
    isFullscreen.value = true;
  } else {
    document.exitFullscreen();
    isFullscreen.value = false;
  }
}

function handleResize() {
  charts.forEach(c => c.resize());
}

onMounted(() => {
  loadData();
  window.addEventListener("resize", handleResize);
  timer = setInterval(() => {
    nowTime.value = proxy.parseTime(new Date());
  }, 1000);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  clearInterval(timer);
  charts.forEach(c => c.dispose());
});
</script>

<style scoped lang="scss">
.bigscreen {
  min-height: calc(100vh - 84px);
  margin: -20px;
  padding: 16px 20px;
  background: radial-gradient(ellipse at top, #10345f 0%, #0b1c3a 55%, #081228 100%);
  color: #d9e8ff;

  .bs-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 4px 14px;

    .bs-title {
      font-size: 24px;
      font-weight: 700;
      letter-spacing: 3px;
      background: linear-gradient(90deg, #6ec3ff, #b58cff);
      -webkit-background-clip: text;
      color: transparent;
    }

    .bs-tools {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #a6c6ff;
    }
  }

  .bs-kpis {
    margin-bottom: 16px;

    .bs-kpi {
      padding: 14px;
      margin-bottom: 12px;
      text-align: center;
      border: 1px solid rgba(110, 195, 255, 0.25);
      border-radius: 8px;
      background: rgba(16, 52, 95, 0.5);

      .bs-kpi-value {
        font-size: 28px;
        font-weight: 700;
      }

      .bs-kpi-label {
        margin-top: 4px;
        font-size: 13px;
        color: #a6c6ff;
      }
    }
  }

  .bs-panel {
    height: 330px;
    margin-bottom: 16px;
    padding: 12px;
    border: 1px solid rgba(110, 195, 255, 0.25);
    border-radius: 8px;
    background: rgba(16, 52, 95, 0.5);

    .bs-panel-title {
      margin-bottom: 4px;
      font-size: 15px;
      font-weight: 600;
      color: #d9e8ff;

      &::before {
        content: "";
        display: inline-block;
        width: 4px;
        height: 14px;
        margin-right: 8px;
        vertical-align: -2px;
        background: #6ec3ff;
        border-radius: 2px;
      }
    }

    .bs-chart {
      width: 100%;
      height: 270px;
    }

    .bs-table {
      max-height: 270px;
      padding: 4px 6px;
      overflow: auto;

      .bs-empty {
        padding: 40px 0;
        text-align: center;
        color: #6d8ab8;
      }

      .bs-table-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 10px 8px;
        border-bottom: 1px dashed rgba(166, 198, 255, 0.2);

        .bs-table-name {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .bs-table-qty {
          color: #ff9f9f;
        }
      }
    }
  }

  .bs-trend-panel {
    height: 300px;

    .bs-chart-lg {
      width: 100%;
      height: 240px;
    }
  }
}
</style>
