<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="资产编码" prop="assetCode">
        <el-input v-model="queryParams.assetCode" placeholder="请输入资产编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="资产分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择分类" clearable style="width: 160px">
          <el-option v-for="dict in biz_asset_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="资产状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option v-for="dict in biz_asset_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="采购日期" style="width: 308px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:asset:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['biz:asset:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['biz:asset:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['biz:asset:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="assetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="资产编码" prop="assetCode" width="130" align="center" />
      <el-table-column label="资产名称" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="型号" prop="assetModel" :show-overflow-tooltip="true" />
      <el-table-column label="分类" prop="category" width="90" align="center">
        <template #default="scope">
          <dict-tag :options="biz_asset_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="品牌" prop="brand" width="90" align="center" />
      <el-table-column label="采购价格" prop="price" width="100" align="right" />
      <el-table-column label="采购日期" prop="purchaseDate" width="110" align="center" />
      <el-table-column label="存放位置" prop="location" :show-overflow-tooltip="true" />
      <el-table-column label="责任人" prop="ownerName" width="90" align="center" />
      <el-table-column label="图片" prop="pic" width="70" align="center">
        <template #default="scope">
          <image-preview v-if="scope.row.pic" :src="scope.row.pic" :width="40" :height="40" />
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="scope">
          <dict-tag :options="biz_asset_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:asset:edit']">修改</el-button>
          <el-button link type="primary" icon="Grid" @click="handleQrLabel(scope.row)" v-hasPermi="['biz:asset:query']">标签</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:asset:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改资产对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="assetRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="留空则自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="型号规格" prop="assetModel">
              <el-input v-model="form.assetModel" placeholder="请输入型号/规格" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="dict in biz_asset_category" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购价格" prop="price">
              <el-input-number v-model="form.price" :precision="2" :min="0" :max="99999999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择采购日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option v-for="dict in biz_asset_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="存放位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人" prop="ownerId">
              <el-select v-model="form.ownerId" placeholder="请选择责任人" clearable filterable style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资产图片" prop="pic">
          <image-upload v-model="form.pic" :limit="1" />
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

    <!-- 资产二维码标签对话框 -->
    <el-dialog title="资产二维码标签" v-model="qrOpen" width="420px" append-to-body>
      <div class="qr-label">
        <div class="qr-title">星云资产</div>
        <img v-if="qrDataUrl" :src="qrDataUrl" class="qr-img" alt="资产二维码" />
        <div class="qr-info">
          <div>编码：{{ qrAsset.assetCode }}</div>
          <div>名称：{{ qrAsset.assetName }}</div>
          <div>位置：{{ qrAsset.location || "-" }}</div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" icon="Printer" @click="printQrLabel">打印标签</el-button>
          <el-button @click="qrOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Asset">
import QRCode from "qrcode";
import { listAsset, getAsset, addAsset, updateAsset, delAsset } from "@/api/biz/asset";
import { listUser } from "@/api/system/user";

const { proxy } = getCurrentInstance();
const { biz_asset_category, biz_asset_status } = proxy.useDict("biz_asset_category", "biz_asset_status");

const assetList = ref([]);
const userOptions = ref([]);
const open = ref(false);
const qrOpen = ref(false);
const qrDataUrl = ref("");
const qrAsset = ref({});
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetCode: undefined,
    assetName: undefined,
    category: undefined,
    status: undefined
  },
  rules: {
    assetName: [{ required: true, message: "资产名称不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询资产列表 */
function getList() {
  loading.value = true;
  listAsset(proxy.addDateRange(queryParams.value, dateRange.value, "PurchaseDate")).then(response => {
    assetList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 责任人下拉选项 */
function getUserOptions() {
  listUser({ pageNum: 1, pageSize: 999 }).then(response => {
    userOptions.value = response.rows;
  });
}

/** 表单重置 */
function reset() {
  form.value = {
    assetId: undefined,
    assetCode: undefined,
    assetName: undefined,
    assetModel: undefined,
    category: undefined,
    brand: undefined,
    price: undefined,
    purchaseDate: undefined,
    location: undefined,
    ownerId: undefined,
    pic: undefined,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("assetRef");
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
  open.value = true;
  title.value = "新增资产";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const assetId = row.assetId || ids.value;
  getAsset(assetId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改资产";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["assetRef"].validate(valid => {
    if (valid) {
      if (form.value.assetId != undefined) {
        updateAsset(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addAsset(form.value).then(response => {
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
  const assetIds = row.assetId || ids.value;
  proxy.$modal.confirm('是否确认删除资产编号为"' + assetIds + '"的数据项？').then(function () {
    return delAsset(assetIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("biz/asset/export", {
    ...queryParams.value
  }, `资产台账_${new Date().getTime()}.xlsx`);
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.assetId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 生成资产二维码标签：内容为扫码后跳转的台账页地址（带资产编码过滤参数） */
function handleQrLabel(row) {
  qrAsset.value = row;
  const target = window.location.origin + "/asset/ledger?assetCode=" + encodeURIComponent(row.assetCode);
  QRCode.toDataURL(target, { width: 220, margin: 1 }).then(url => {
    qrDataUrl.value = url;
    qrOpen.value = true;
  });
}

/** 打印标签：新窗口输出二维码与资产信息 */
function printQrLabel() {
  const win = window.open("", "_blank", "width=420,height=560");
  win.document.write(
    '<html><head><title>资产标签-' + qrAsset.value.assetCode + '</title>' +
    '<style>body{font-family:Microsoft YaHei,sans-serif;text-align:center;padding:20px}' +
    '.qr-title{font-size:20px;font-weight:bold;margin-bottom:10px}.qr-img{width:220px}' +
    '.qr-info{margin-top:10px;font-size:14px;line-height:1.8;text-align:left;display:inline-block}</style></head><body>' +
    '<div class="qr-title">星云资产</div>' +
    '<img class="qr-img" src="' + qrDataUrl.value + '" />' +
    '<div class="qr-info">' +
    '<div>编码：' + (qrAsset.value.assetCode || "-") + '</div>' +
    '<div>名称：' + (qrAsset.value.assetName || "-") + '</div>' +
    '<div>位置：' + (qrAsset.value.location || "-") + '</div>' +
    '</div></body></html>'
  );
  win.document.close();
  win.focus();
  win.print();
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();
getUserOptions();
</script>

<style scoped>
.qr-label {
  text-align: center;
}
.qr-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 10px;
}
.qr-img {
  width: 220px;
}
.qr-info {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.8;
  text-align: left;
  display: inline-block;
}
</style>
