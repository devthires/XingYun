<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="报修单号" prop="repairNo">
        <el-input v-model="queryParams.repairNo" placeholder="请输入报修单号" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="报修状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option v-for="dict in biz_repair_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="报修时间" style="width: 308px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:repair:add']">新增报修</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['biz:repair:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['biz:repair:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="repairList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报修单号" prop="repairNo" width="130" align="center" />
      <el-table-column label="资产名称" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="报修人" prop="repairUser" width="90" align="center" />
      <el-table-column label="联系电话" prop="repairPhone" width="120" align="center" />
      <el-table-column label="问题描述" prop="problemDesc" :show-overflow-tooltip="true" />
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :options="biz_repair_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="处理人" prop="handlerName" width="90" align="center" />
      <el-table-column label="完成时间" prop="finishTime" width="160" align="center" />
      <el-table-column label="报修时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="230" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-if="scope.row.status === '0'" link type="primary" icon="VideoPlay" @click="handleProcess(scope.row)" v-hasPermi="['biz:repair:edit']">开始处理</el-button>
          <el-button v-if="scope.row.status === '1'" link type="success" icon="CircleCheck" @click="handleFinish(scope.row)" v-hasPermi="['biz:repair:edit']">完成</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:repair:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:repair:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改报修单对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="repairRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="报修资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="选择报修资产（可报修闲置/在用资产）" filterable style="width: 100%" :disabled="form.repairId != undefined">
            <el-option v-for="asset in repairableAssets" :key="asset.assetId" :label="asset.assetCode + ' / ' + asset.assetName" :value="asset.assetId" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="报修人" prop="repairUser">
              <el-input v-model="form.repairUser" placeholder="请输入报修人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="repairPhone">
              <el-input v-model="form.repairPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述" prop="problemDesc">
          <el-input v-model="form.problemDesc" type="textarea" :rows="3" placeholder="请描述故障现象" />
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

<script setup name="Repair">
import { listRepair, getRepair, addRepair, updateRepair, delRepair, changeRepairStatus } from "@/api/biz/repair";
import { listAsset } from "@/api/biz/asset";

const { proxy } = getCurrentInstance();
const { biz_repair_status } = proxy.useDict("biz_repair_status");

const repairList = ref([]);
const repairableAssets = ref([]);
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
    repairNo: undefined,
    assetName: undefined,
    status: undefined
  },
  rules: {
    assetId: [{ required: true, message: "报修资产不能为空", trigger: "change" }],
    repairUser: [{ required: true, message: "报修人不能为空", trigger: "blur" }],
    problemDesc: [{ required: true, message: "问题描述不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询报修单列表 */
function getList() {
  loading.value = true;
  listRepair(proxy.addDateRange(queryParams.value, dateRange.value, "CreateTime")).then(response => {
    repairList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 可报修资产下拉选项（排除维修中/报废） */
function getRepairableAssets() {
  listAsset({ pageNum: 1, pageSize: 999 }).then(response => {
    repairableAssets.value = (response.rows || []).filter(a => a.status !== "2" && a.status !== "3");
  });
}

/** 表单重置 */
function reset() {
  form.value = {
    repairId: undefined,
    repairNo: undefined,
    assetId: undefined,
    repairUser: undefined,
    repairPhone: undefined,
    problemDesc: undefined,
    remark: undefined
  };
  proxy.resetForm("repairRef");
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

/** 新增按钮操作 */
function handleAdd() {
  reset();
  getRepairableAssets();
  open.value = true;
  title.value = "新增报修";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getRepair(row.repairId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改报修单";
  });
}

/** 开始处理：报修单转处理中，资产自动变为维修中 */
function handleProcess(row) {
  proxy.$modal.confirm('是否开始处理报修单"' + row.repairNo + '"？资产将变为维修中状态。').then(() => {
    return changeRepairStatus(row.repairId, "1");
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已开始处理");
  }).catch(() => {});
}

/** 完成：记录完成时间，资产恢复为闲置 */
function handleFinish(row) {
  proxy.$modal.confirm('是否完成报修单"' + row.repairNo + '"？资产将恢复为闲置状态。').then(() => {
    return changeRepairStatus(row.repairId, "2");
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("维修完成");
  }).catch(() => {});
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["repairRef"].validate(valid => {
    if (valid) {
      if (form.value.repairId != undefined) {
        updateRepair(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRepair(form.value).then(response => {
          proxy.$modal.msgSuccess("报修提交成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const repairIds = row.repairId || ids.value;
  proxy.$modal.confirm('是否确认删除报修单编号为"' + repairIds + '"的数据项？').then(function () {
    return delRepair(repairIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("biz/repair/export", {
    ...queryParams.value
  }, `设备报修单_${new Date().getTime()}.xlsx`);
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.repairId);
  multiple.value = !selection.length;
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();
</script>
