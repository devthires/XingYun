<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="耗材编码" prop="consumableCode">
        <el-input v-model="queryParams.consumableCode" placeholder="请输入耗材编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="耗材名称" prop="consumableName">
        <el-input v-model="queryParams.consumableName" placeholder="请输入耗材名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="库存状态" prop="params.lowStock">
        <el-select v-model="queryParams.params.lowStock" placeholder="全部" clearable style="width: 140px" @change="handleQuery">
          <el-option label="仅看低库存" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:consumable:add']">新增耗材</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['biz:consumable:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="consumableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="耗材编码" prop="consumableCode" width="120" align="center" />
      <el-table-column label="耗材名称" prop="consumableName" :show-overflow-tooltip="true" />
      <el-table-column label="规格型号" prop="spec" :show-overflow-tooltip="true" />
      <el-table-column label="单位" prop="unit" width="70" align="center" />
      <el-table-column label="当前库存" width="110" align="center">
        <template #default="scope">
          <span :style="lowStock(scope.row) ? 'color:#F56C6C;font-weight:700' : ''">{{ scope.row.stockQty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预警阈值" prop="warnQty" width="90" align="center" />
      <el-table-column label="库存预警" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="lowStock(scope.row)" type="danger" size="small" effect="plain">库存不足</el-tag>
          <el-tag v-else type="success" size="small" effect="plain">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="success" icon="Top" @click="handleStock(scope.row, 'in')" v-hasPermi="['biz:consumable:edit']">入库</el-button>
          <el-button link type="warning" icon="Bottom" @click="handleStock(scope.row, 'out')" v-hasPermi="['biz:consumable:edit']">出库</el-button>
          <el-button link type="primary" icon="Document" @click="handleRecords(scope.row)" v-hasPermi="['biz:consumable:query']">记录</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:consumable:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:consumable:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改耗材对话框 -->
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="consumableRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="耗材编码" prop="consumableCode">
              <el-input v-model="form.consumableCode" placeholder="如 HC-005" :disabled="form.consumableId != undefined" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="耗材名称" prop="consumableName">
              <el-input v-model="form.consumableName" placeholder="请输入耗材名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="spec">
              <el-input v-model="form.spec" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如 箱/个/盒" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预警阈值" prop="warnQty">
              <el-input-number v-model="form.warnQty" :min="0" :max="99999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始库存" prop="stockQty" v-if="form.consumableId == undefined">
              <el-input-number v-model="form.stockQty" :min="0" :max="999999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- 入库/出库对话框 -->
    <el-dialog :title="stockForm.type === 'in' ? '耗材入库' : '耗材出库'" v-model="stockOpen" width="440px" append-to-body>
      <el-alert v-if="stockForm.type === 'out'" type="warning" :closable="false" show-icon style="margin-bottom: 14px"
        :title="'当前库存：' + stockForm.beforeQty + ' ' + stockForm.unit + '，出库后剩余：' + (stockForm.beforeQty - (stockForm.quantity || 0))" />
      <el-form ref="stockRef" :model="stockForm" :rules="stockRules" label-width="90px">
        <el-form-item label="变动数量" prop="quantity">
          <el-input-number v-model="stockForm.quantity" :min="1" :max="999999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明" prop="content">
          <el-input v-model="stockForm.content" type="textarea" placeholder="如：采购到货 / 部门领用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitStock">确 定</el-button>
          <el-button @click="stockOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 出入库记录抽屉 -->
    <el-drawer v-model="recordOpen" :title="recordTitle" size="60%">
      <el-form :inline="true" :model="recordQuery" ref="recordQueryRef">
        <el-form-item label="类型">
          <el-select v-model="recordQuery.recordType" placeholder="全部" clearable style="width: 120px" @change="getRecords">
            <el-option v-for="dict in biz_consumable_record_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="recordDateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-"
            start-placeholder="开始" end-placeholder="结束" style="width: 240px" @change="getRecords" />
        </el-form-item>
      </el-form>
      <el-table v-loading="recordLoading" :data="recordList">
        <el-table-column label="类型" prop="recordType" width="80" align="center">
          <template #default="scope">
            <dict-tag :options="biz_consumable_record_type" :value="scope.row.recordType" />
          </template>
        </el-table-column>
        <el-table-column label="变动数量" prop="quantity" width="90" align="center" />
        <el-table-column label="变动前" prop="beforeQty" width="80" align="center" />
        <el-table-column label="变动后" prop="afterQty" width="80" align="center" />
        <el-table-column label="说明" prop="content" :show-overflow-tooltip="true" />
        <el-table-column label="操作人" prop="operator" width="100" align="center" />
        <el-table-column label="操作时间" prop="createTime" width="160" align="center" />
      </el-table>
      <pagination v-show="recordTotal > 0" :total="recordTotal" v-model:page="recordQuery.pageNum" v-model:limit="recordQuery.pageSize" @pagination="getRecords" />
    </el-drawer>
  </div>
</template>

<script setup name="Consumable">
import { listConsumable, getConsumable, addConsumable, updateConsumable, delConsumable, stockIn, stockOut, listConsumableRecord } from "@/api/biz/consumable";

const { proxy } = getCurrentInstance();
const { biz_consumable_record_type } = proxy.useDict("biz_consumable_record_type");

const consumableList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const stockOpen = ref(false);
const recordOpen = ref(false);
const recordLoading = ref(false);
const recordTitle = ref("");
const recordList = ref([]);
const recordTotal = ref(0);
const recordDateRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    consumableCode: undefined,
    consumableName: undefined,
    params: { lowStock: undefined }
  },
  rules: {
    consumableCode: [{ required: true, message: "耗材编码不能为空", trigger: "blur" }],
    consumableName: [{ required: true, message: "耗材名称不能为空", trigger: "blur" }]
  },
  stockForm: {},
  stockRules: {
    quantity: [{ required: true, message: "变动数量不能为空", trigger: "blur" }]
  },
  recordQuery: { pageNum: 1, pageSize: 10, consumableId: undefined, recordType: undefined }
});

const { queryParams, form, rules, stockForm, stockRules, recordQuery } = toRefs(data);

function lowStock(row) {
  return row.stockQty != null && row.warnQty != null && row.stockQty <= row.warnQty;
}

/** 查询耗材列表 */
function getList() {
  loading.value = true;
  listConsumable(queryParams.value).then(response => {
    consumableList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 表单重置 */
function reset() {
  form.value = {
    consumableId: undefined,
    consumableCode: undefined,
    consumableName: undefined,
    spec: undefined,
    unit: undefined,
    stockQty: 0,
    warnQty: 0,
    remark: undefined
  };
  proxy.resetForm("consumableRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.params.lowStock = undefined;
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增耗材";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getConsumable(row.consumableId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改耗材";
  });
}

/** 入库/出库按钮操作 */
function handleStock(row, type) {
  stockForm.value = {
    type: type,
    consumableId: row.consumableId,
    consumableName: row.consumableName,
    unit: row.unit || "",
    beforeQty: row.stockQty || 0,
    quantity: 1,
    content: ""
  };
  stockOpen.value = true;
}

/** 提交入库/出库 */
function submitStock() {
  proxy.$refs["stockRef"].validate(valid => {
    if (valid) {
      const request = stockForm.value.type === "in" ? stockIn : stockOut;
      request({
        consumableId: stockForm.value.consumableId,
        quantity: stockForm.value.quantity,
        content: stockForm.value.content
      }).then(() => {
        proxy.$modal.msgSuccess(stockForm.value.type === "in" ? "入库成功" : "出库成功");
        stockOpen.value = false;
        getList();
      });
    }
  });
}

/** 查看出入库记录 */
function handleRecords(row) {
  recordQuery.value.consumableId = row.consumableId;
  recordQuery.value.pageNum = 1;
  recordDateRange.value = [];
  recordTitle.value = "出入库记录 - " + row.consumableName;
  recordOpen.value = true;
  getRecords();
}

/** 查询记录列表 */
function getRecords() {
  recordLoading.value = true;
  const range = proxy.addDateRange(recordQuery.value, recordDateRange.value, "CreateTime");
  listConsumableRecord(range).then(response => {
    recordList.value = response.rows;
    recordTotal.value = response.total;
    recordLoading.value = false;
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["consumableRef"].validate(valid => {
    if (valid) {
      if (form.value.consumableId != undefined) {
        updateConsumable(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addConsumable(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const consumableIds = row.consumableId || ids.value;
  proxy.$modal.confirm('是否确认删除耗材编号为"' + consumableIds + '"的数据项？').then(function () {
    return delConsumable(consumableIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.consumableId);
  multiple.value = !selection.length;
}

function cancel() {
  open.value = false;
  reset();
}

getList();
</script>
