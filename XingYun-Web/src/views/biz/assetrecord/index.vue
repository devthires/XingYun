<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="资产编码" prop="assetCode">
        <el-input v-model="queryParams.assetCode" placeholder="请输入资产编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="变更类型" prop="recordType">
        <el-select v-model="queryParams.recordType" placeholder="请选择类型" clearable style="width: 160px">
          <el-option v-for="dict in biz_asset_record_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间" style="width: 308px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList">
      <el-table-column label="流水ID" prop="recordId" width="80" align="center" />
      <el-table-column label="资产编码" prop="assetCode" width="130" align="center" />
      <el-table-column label="资产名称" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="变更类型" prop="recordType" width="100" align="center">
        <template #default="scope">
          <dict-tag :options="biz_asset_record_type" :value="scope.row.recordType" />
        </template>
      </el-table-column>
      <el-table-column label="变更说明" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="状态流转" width="130" align="center">
        <template #default="scope">
          <span v-if="scope.row.beforeStatus != null">
            {{ statusName(scope.row.beforeStatus) }}
            <el-icon style="vertical-align: middle"><ArrowRight /></el-icon>
            {{ statusName(scope.row.afterStatus) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" prop="operator" width="100" align="center" />
      <el-table-column label="操作时间" prop="createTime" width="160" align="center" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="AssetRecord">
import { listRecord } from "@/api/biz/record";
import { ArrowRight } from "@element-plus/icons-vue";

const { proxy } = getCurrentInstance();
const { biz_asset_record_type } = proxy.useDict("biz_asset_record_type");

const recordList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);

/** 资产状态名称（与字典 biz_asset_status 一致，仅用于流水状态流转展示） */
const statusNames = { "0": "闲置", "1": "在用", "2": "维修中", "3": "报废" };

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetCode: undefined,
    assetName: undefined,
    recordType: undefined
  }
});

const { queryParams } = toRefs(data);

function statusName(status) {
  return statusNames[status] || status || "-";
}

/** 查询变更流水列表 */
function getList() {
  loading.value = true;
  listRecord(proxy.addDateRange(queryParams.value, dateRange.value, "CreateTime")).then(response => {
    recordList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

getList();
</script>
