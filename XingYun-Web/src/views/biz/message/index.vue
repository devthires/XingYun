<template>
  <div class="app-container">
    <el-card shadow="never" class="msg-card">
      <template #header>
        <div class="msg-header">
          <span class="msg-title">消息中心</span>
          <el-button type="primary" link icon="Checked" @click="handleMarkAllRead">全部已读</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" ref="queryRef">
        <el-form-item label="消息类型" prop="msgType">
          <el-select v-model="queryParams.msgType" placeholder="全部类型" clearable style="width: 160px" @change="handleQuery">
            <el-option v-for="dict in biz_message_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="已读状态" prop="readFlag">
          <el-select v-model="queryParams.readFlag" placeholder="全部状态" clearable style="width: 140px" @change="handleQuery">
            <el-option label="未读" value="0" />
            <el-option label="已读" value="1" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="messageList" :row-class-name="rowClassName">
        <el-table-column label="类型" prop="msgType" width="100" align="center">
          <template #default="scope">
            <dict-tag :options="biz_message_type" :value="scope.row.msgType" />
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" width="180">
          <template #default="scope">
            <el-badge is-dot :hidden="scope.row.readFlag === '1'">{{ scope.row.title }}</el-badge>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" :show-overflow-tooltip="true" />
        <el-table-column label="状态" prop="readFlag" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.readFlag === '1' ? 'info' : 'danger'" effect="plain" size="small">
              {{ scope.row.readFlag === '1' ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接收时间" prop="createTime" width="160" align="center" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.readFlag === '0'" link type="primary" @click="handleRead(scope.row)">标记已读</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="Message">
import { listMyMessage, markMessageRead, markAllMessageRead } from "@/api/biz/message";

const { proxy } = getCurrentInstance();
const { biz_message_type } = proxy.useDict("biz_message_type");

const messageList = ref([]);
const loading = ref(true);
const total = ref(0);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    msgType: undefined,
    readFlag: undefined
  }
});

const { queryParams } = toRefs(data);

function rowClassName({ row }) {
  return row.readFlag === "0" ? "unread-row" : "";
}

/** 查询我的消息列表 */
function getList() {
  loading.value = true;
  listMyMessage(queryParams.value).then(response => {
    messageList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 标记单条已读 */
function handleRead(row) {
  markMessageRead(row.messageId).then(() => {
    getList();
  });
}

/** 全部已读 */
function handleMarkAllRead() {
  markAllMessageRead().then(() => {
    proxy.$modal.msgSuccess("已全部标记为已读");
    getList();
  });
}

getList();
</script>

<style scoped>
.msg-card {
  border-radius: 8px;
}
.msg-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.msg-title {
  font-size: 16px;
  font-weight: 600;
}
:deep(.unread-row) {
  font-weight: 600;
}
</style>
