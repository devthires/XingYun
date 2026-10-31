<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="资产名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="领用人" prop="nickName">
        <el-input v-model="queryParams.nickName" placeholder="请输入领用人" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option v-for="dict in biz_asset_use_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="领用时间" style="width: 308px">
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
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:use:add']">申请领用</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['biz:use:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['biz:use:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="useList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="资产编码" prop="assetCode" width="120" align="center" />
      <el-table-column label="资产名称" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="领用人" prop="nickName" width="90" align="center" />
      <el-table-column label="领用时间" prop="useTime" width="160" align="center" />
      <el-table-column label="预计归还" prop="expectReturnTime" width="160" align="center" />
      <el-table-column label="实际归还" prop="actualReturnTime" width="160" align="center">
        <template #default="scope">
          <span v-if="scope.row.actualReturnTime">{{ scope.row.actualReturnTime }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="scope">
          <dict-tag :options="biz_asset_use_status" :value="scope.row.status" />
          <div v-if="isOverdue(scope.row)"><el-tag type="danger" size="small" effect="plain">已逾期</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-if="scope.row.status === '0'" link type="primary" icon="Check" @click="handleApprove(scope.row)" v-hasPermi="['biz:use:edit']">通过</el-button>
          <el-button v-if="scope.row.status === '0'" link type="danger" icon="Close" @click="handleReject(scope.row)" v-hasPermi="['biz:use:edit']">驳回</el-button>
          <el-button v-if="scope.row.status === '1'" link type="success" icon="RefreshLeft" @click="handleReturn(scope.row)" v-hasPermi="['biz:use:edit']">归还</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:use:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:use:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 申请领用/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="useRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="领用资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="仅可选择闲置资产" filterable style="width: 100%" :disabled="form.useId != undefined">
            <el-option v-for="asset in idleAssets" :key="asset.assetId" :label="asset.assetCode + ' / ' + asset.assetName" :value="asset.assetId" />
          </el-select>
        </el-form-item>
        <el-form-item label="领用人" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择领用人" filterable style="width: 100%">
            <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="领用时间" prop="useTime">
          <el-date-picker v-model="form.useTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="默认为当前时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计归还" prop="expectReturnTime">
          <el-date-picker v-model="form.expectReturnTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择预计归还时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AssetUse">
import { listAssetUse, getAssetUse, addAssetUse, updateAssetUse, delAssetUse, changeAssetUseStatus } from "@/api/biz/assetuse";
import { listAsset } from "@/api/biz/asset";
import { listUser } from "@/api/system/user";

const { proxy } = getCurrentInstance();
const { biz_asset_use_status } = proxy.useDict("biz_asset_use_status");

const useList = ref([]);
const idleAssets = ref([]);
const userOptions = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetName: undefined,
    nickName: undefined,
    status: undefined
  },
  rules: {
    assetId: [{ required: true, message: "领用资产不能为空", trigger: "change" }],
    userId: [{ required: true, message: "领用人不能为空", trigger: "change" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询领用列表 */
function getList() {
  loading.value = true;
  listAssetUse(proxy.addDateRange(queryParams.value, dateRange.value, "UseTime")).then(response => {
    useList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 闲置资产下拉选项 */
function getIdleAssets() {
  listAsset({ pageNum: 1, pageSize: 999, status: "0" }).then(response => {
    idleAssets.value = response.rows;
  });
}

/** 用户下拉选项 */
function getUserOptions() {
  listUser({ pageNum: 1, pageSize: 999 }).then(response => {
    userOptions.value = response.rows;
  });
}

/** 是否逾期：使用中且已过预计归还时间 */
function isOverdue(row) {
  return row.status === "1" && row.expectReturnTime && new Date(row.expectReturnTime).getTime() < Date.now();
}

/** 表单重置 */
function reset() {
  form.value = {
    useId: undefined,
    assetId: undefined,
    userId: undefined,
    useTime: undefined,
    expectReturnTime: undefined,
    remark: undefined
  };
  proxy.resetForm("useRef");
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

/** 申请领用按钮操作 */
function handleAdd() {
  reset();
  getIdleAssets();
  open.value = true;
  title.value = "申请领用";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getAssetUse(row.useId).then(response => {
    form.value = response.data;
    getIdleAssets();
    open.value = true;
    title.value = "修改领用单";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["useRef"].validate(valid => {
    if (valid) {
      if (form.value.useId != undefined) {
        updateAssetUse(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addAssetUse(form.value).then(response => {
          proxy.$modal.msgSuccess("申请提交成功，等待审批");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 审批通过 */
function handleApprove(row) {
  proxy.$modal.confirm('是否审批通过领用单"' + row.assetName + '"？通过后资产将变为在用状态。').then(() => {
    return changeAssetUseStatus(row.useId, "1");
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("审批通过");
  }).catch(() => {});
}

/** 驳回（必填原因） */
function handleReject(row) {
  proxy.$prompt("请输入驳回原因（必填）", "驳回领用申请", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /^.+$/,
    inputErrorMessage: "驳回原因不能为空"
  }).then(({ value }) => {
    return changeAssetUseStatus(row.useId, "3", value);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已驳回");
  }).catch(() => {});
}

/** 归还 */
function handleReturn(row) {
  proxy.$modal.confirm('是否确认归还资产"' + row.assetName + '"？归还后资产恢复为闲置。').then(() => {
    return changeAssetUseStatus(row.useId, "2");
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("归还成功");
  }).catch(() => {});
}

/** 删除按钮操作 */
function handleDelete(row) {
  const useIds = row.useId || ids.value;
  proxy.$modal.confirm('是否确认删除领用单编号为"' + useIds + '"的数据项？').then(function () {
    return delAssetUse(useIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("biz/use/export", {
    ...queryParams.value
  }, `资产领用_${new Date().getTime()}.xlsx`);
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.useId);
  multiple.value = !selection.length;
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();
getUserOptions();
</script>
