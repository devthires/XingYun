<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="任务名称" prop="inventoryName">
        <el-input v-model="queryParams.inventoryName" placeholder="请输入任务名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="盘点状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option v-for="dict in biz_inventory_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:inventory:add']">创建盘点任务</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['biz:inventory:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inventoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="盘点单号" prop="inventoryNo" width="170" align="center" />
      <el-table-column label="任务名称" prop="inventoryName" :show-overflow-tooltip="true" />
      <el-table-column label="进度" width="180" align="center">
        <template #default="scope">
          <el-progress :percentage="progressOf(scope.row)" :stroke-width="12" :color="progressColor" />
        </template>
      </el-table-column>
      <el-table-column label="应盘/已盘/正常/盘亏" width="160" align="center">
        <template #default="scope">
          <span>{{ scope.row.totalCount }} / {{ scope.row.checkCount }} / {{ scope.row.normalCount }} / {{ scope.row.lossCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :options="biz_inventory_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建人" prop="createBy" width="90" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
      <el-table-column label="完成时间" prop="finishTime" width="160" align="center" />
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:inventory:query']">盘点明细</el-button>
          <el-button v-if="scope.row.status === '0'" link type="success" icon="CircleCheck" @click="handleFinish(scope.row)" v-hasPermi="['biz:inventory:edit']">完成盘点</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:inventory:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 创建盘点任务对话框 -->
    <el-dialog title="创建盘点任务" v-model="open" width="480px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 16px"
        title="将对全部在册资产建立账面快照，同一时间只允许一个进行中的盘点任务" />
      <el-form ref="inventoryRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="任务名称" prop="inventoryName">
          <el-input v-model="form.inventoryName" placeholder="如：2026年11月资产月度盘点" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">开始盘点</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 盘点明细抽屉 -->
    <el-drawer v-model="detailOpen" :title="detailTitle" size="70%">
      <el-row :gutter="16" class="mb8" v-if="detail.inventory">
        <el-col :span="6"><el-statistic title="应盘总数" :value="detail.stats.total || 0" /></el-col>
        <el-col :span="6"><el-statistic title="已盘数量" :value="detail.stats.checked || 0" /></el-col>
        <el-col :span="6"><el-statistic title="盘点正常" :value="detail.stats.normal || 0" value-style="color:#67C23A" /></el-col>
        <el-col :span="6"><el-statistic title="盘亏" :value="detail.stats.loss || 0" value-style="color:#F56C6C" /></el-col>
      </el-row>
      <el-table v-loading="detailLoading" :data="detail.items">
        <el-table-column label="资产编码" prop="snapshotCode" width="130" align="center" />
        <el-table-column label="资产名称" prop="snapshotName" :show-overflow-tooltip="true" />
        <el-table-column label="存放位置快照" prop="snapshotLocation" :show-overflow-tooltip="true" />
        <el-table-column label="账面状态" prop="bookStatus" width="90" align="center">
          <template #default="scope">
            <dict-tag :options="biz_asset_status" :value="scope.row.bookStatus" />
          </template>
        </el-table-column>
        <el-table-column label="当前状态" prop="currentStatus" width="90" align="center">
          <template #default="scope">
            <dict-tag :options="biz_asset_status" :value="scope.row.currentStatus" />
          </template>
        </el-table-column>
        <el-table-column label="盘点结果" width="90" align="center">
          <template #default="scope">
            <dict-tag :options="biz_inventory_result" :value="scope.row.result" />
          </template>
        </el-table-column>
        <el-table-column label="盘点人" prop="checker" width="90" align="center" />
        <el-table-column label="盘点时间" prop="checkTime" width="160" align="center" />
        <el-table-column label="差异说明" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="操作" width="160" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <template v-if="detail.inventory && detail.inventory.status === '0'">
              <el-button v-if="scope.row.result === '0'" link type="success" icon="Check" @click="handleCheck(scope.row, '1')" v-hasPermi="['biz:inventory:edit']">正常</el-button>
              <el-button v-if="scope.row.result === '0'" link type="danger" icon="CloseBold" @click="handleCheck(scope.row, '2')" v-hasPermi="['biz:inventory:edit']">盘亏</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="Inventory">
import { listInventory, getInventory, addInventory, checkInventoryItem, finishInventory, delInventory } from "@/api/biz/inventory";

const { proxy } = getCurrentInstance();
const { biz_inventory_status, biz_inventory_result, biz_asset_status } = proxy.useDict("biz_inventory_status", "biz_inventory_result", "biz_asset_status");

const inventoryList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const detailLoading = ref(false);
const detailTitle = ref("");
const detail = ref({ inventory: null, items: [], stats: {} });
const progressColor = [
  { color: "#F56C6C", percentage: 50 },
  { color: "#E6A23C", percentage: 80 },
  { color: "#67C23A", percentage: 100 }
];

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    inventoryName: undefined,
    status: undefined
  },
  rules: {
    inventoryName: [{ required: true, message: "任务名称不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

function progressOf(row) {
  if (!row.totalCount) return 0;
  return Math.round((row.checkCount / row.totalCount) * 100);
}

/** 查询盘点任务列表 */
function getList() {
  loading.value = true;
  listInventory(queryParams.value).then(response => {
    inventoryList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 表单重置 */
function reset() {
  form.value = {
    inventoryId: undefined,
    inventoryName: undefined,
    remark: undefined
  };
  proxy.resetForm("inventoryRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 创建盘点任务按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
}

/** 提交创建 */
function submitForm() {
  proxy.$refs["inventoryRef"].validate(valid => {
    if (valid) {
      addInventory(form.value).then(response => {
        proxy.$modal.msgSuccess("盘点任务创建成功，已建立账面快照");
        open.value = false;
        getList();
      });
    }
  });
}

/** 查看盘点明细 */
function handleDetail(row) {
  detailLoading.value = true;
  detailOpen.value = true;
  detailTitle.value = "盘点明细 - " + row.inventoryName;
  getInventory(row.inventoryId).then(response => {
    detail.value = response.data;
    detailLoading.value = false;
  });
}

/** 盘点确认（result: 1正常 2盘亏） */
function handleCheck(row, result) {
  const doCheck = (remark) => {
    checkInventoryItem({ itemId: row.itemId, result: result, remark: remark }).then(() => {
      proxy.$modal.msgSuccess(result === "1" ? "已确认正常" : "已标记盘亏");
      handleDetail({ inventoryId: row.inventoryId, inventoryName: detailTitle.value.replace("盘点明细 - ", "") });
      getList();
    });
  };
  if (result === "2") {
    proxy.$prompt("请输入盘亏差异说明（必填）", "标记盘亏", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      inputPattern: /^.+$/,
      inputErrorMessage: "差异说明不能为空"
    }).then(({ value }) => {
      doCheck(value);
    }).catch(() => {});
  } else {
    doCheck("");
  }
}

/** 完成盘点 */
function handleFinish(row) {
  proxy.$modal.confirm('是否完成盘点任务"' + row.inventoryName + '"？完成后不可再修改盘点结果。').then(() => {
    return finishInventory(row.inventoryId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("盘点已完成");
  }).catch(() => {});
}

/** 删除按钮操作 */
function handleDelete(row) {
  const inventoryIds = row.inventoryId || ids.value;
  proxy.$modal.confirm('是否确认删除盘点任务编号为"' + inventoryIds + '"的数据项？明细将一并删除。').then(function () {
    return delInventory(inventoryIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.inventoryId);
  multiple.value = !selection.length;
}

function cancel() {
  open.value = false;
  reset();
}

getList();
</script>
