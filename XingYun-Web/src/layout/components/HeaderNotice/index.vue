<template>
  <div>
    <el-popover ref="noticePopover" placement="bottom-end" :width="360" trigger="manual" v-model:visible="noticeVisible" popper-class="notice-popover">
      <!-- 弹出内容：公告 | 我的消息 双 Tab -->
      <el-tabs v-model="activeTab" class="notice-tabs">
        <el-tab-pane label="通知公告" name="notice">
          <div class="notice-header">
            <span class="notice-mark-all" @click="markAllRead">公告全部已读</span>
          </div>
          <div v-if="noticeLoading" class="notice-loading">
            <el-icon class="is-loading"><Loading /></el-icon> 加载中...
          </div>
          <div v-else-if="noticeList.length === 0" class="notice-empty">
            <el-icon style="font-size:24px;display:block;margin-bottom:6px;"><Postcard /></el-icon>
            暂无公告
          </div>
          <div v-else>
            <div v-for="item in noticeList" :key="item.noticeId" class="notice-item" :class="{ 'is-read': item.isRead }" @click="previewNotice(item)">
              <el-tag size="small" :type="item.noticeType === '1' ? 'warning' : 'success'" class="notice-tag">
                {{ item.noticeType === '1' ? '通知' : '公告' }}
              </el-tag>
              <span class="notice-item-title">{{ item.noticeTitle }}</span>
              <span class="notice-item-date">{{ item.createTime }}</span>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="message">
          <template #label>
            我的消息
            <el-badge v-if="msgUnread > 0" :value="msgUnread" :max="99" class="tab-badge" />
          </template>
          <div class="notice-header">
            <span class="notice-mark-all" @click="markAllMsgRead">消息全部已读</span>
          </div>
          <div v-if="msgLoading" class="notice-loading">
            <el-icon class="is-loading"><Loading /></el-icon> 加载中...
          </div>
          <div v-else-if="msgList.length === 0" class="notice-empty">
            <el-icon style="font-size:24px;display:block;margin-bottom:6px;"><ChatDotRound /></el-icon>
            暂无消息
          </div>
          <div v-else>
            <div v-for="item in msgList" :key="item.messageId" class="notice-item" :class="{ 'is-read': item.readFlag === '1' }" @click="openMessageCenter(item)">
              <el-tag size="small" :type="msgTagType(item.msgType)" class="notice-tag">
                {{ msgTypeName(item.msgType) }}
              </el-tag>
              <span class="notice-item-title">{{ item.title }}</span>
              <span class="notice-item-date">{{ item.createTime }}</span>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 触发器 -->
      <template #reference>
        <div class="right-menu-item hover-effect notice-trigger" @mouseenter="onNoticeEnter" @mouseleave="onNoticeLeave">
          <svg-icon icon-class="bell" />
          <span v-if="totalUnread > 0" class="notice-badge">{{ totalUnread }}</span>
        </div>
      </template>
    </el-popover>

    <!-- 公告预览弹窗 -->
    <notice-detail-view ref="noticeViewRef" />
  </div>
</template>

<script setup>
import NoticeDetailView from './DetailView'
import { listNoticeTop, markNoticeRead, markNoticeReadAll } from '@/api/system/notice'
import { listMyMessage, getUnreadCount, markMessageRead, markAllMessageRead } from '@/api/biz/message'

const noticePopover = ref(null)
const noticeList = ref([])
const noticeUnread = ref(0)
const noticeLoading = ref(false)
const noticeVisible = ref(false)
const noticeLeaveTimer = ref(null)
const activeTab = ref('notice')
const msgList = ref([])
const msgUnread = ref(0)
const msgLoading = ref(false)
const router = useRouter()
const { proxy } = getCurrentInstance()

/** 铃铛角标 = 公告未读 + 站内消息未读 */
const totalUnread = computed(() => noticeUnread.value + msgUnread.value)

/** 消息类型展示（与字典 biz_message_type 一致） */
const msgTypeNames = { '1': '领用审批', '2': '逾期提醒', '3': '盘点', '4': '系统通知' }
const msgTypeColors = { '1': 'primary', '2': 'danger', '3': 'warning', '4': 'info' }

function msgTypeName(type) {
  return msgTypeNames[type] || '系统通知'
}

function msgTagType(type) {
  return msgTypeColors[type] || 'info'
}

// 加载顶部公告列表
function loadNoticeTop() {
  noticeLoading.value = true
  listNoticeTop().then(res => {
    noticeList.value = res.data || []
    noticeUnread.value = res.unreadCount !== undefined ? res.unreadCount : noticeList.value.filter(n => !n.isRead).length
  }).finally(() => {
    noticeLoading.value = false
  })
}

// 加载我的消息（最近10条）与未读数
function loadMyMessages() {
  msgLoading.value = true
  listMyMessage({ pageNum: 1, pageSize: 10 }).then(res => {
    msgList.value = res.rows || []
  }).finally(() => {
    msgLoading.value = false
  })
  getUnreadCount().then(res => {
    msgUnread.value = Number(res.data || 0)
  })
}

onMounted(() => {
  loadNoticeTop()
  loadMyMessages()
})

// 鼠标移入铃铛区域
function onNoticeEnter() {
  clearTimeout(noticeLeaveTimer.value)
  // 每次打开前刷新消息，保证角标与列表新鲜
  loadMyMessages()
  noticeVisible.value = true
  nextTick(() => {
    const popper = noticePopover.value?.popperRef?.contentRef
    if (popper && !popper._noticeBound) {
      popper._noticeBound = true
      popper.addEventListener('mouseenter', () => clearTimeout(noticeLeaveTimer.value))
      popper.addEventListener('mouseleave', () => {
        noticeLeaveTimer.value = setTimeout(() => { noticeVisible.value = false }, 100)
      })
    }
  })
}

// 鼠标离开铃铛区域
function onNoticeLeave() {
  noticeLeaveTimer.value = setTimeout(() => { noticeVisible.value = false }, 150)
}

// 预览公告详情
function previewNotice(item) {
  if (!item.isRead) {
    markNoticeRead(item.noticeId).catch(() => {})
    const idx = noticeList.value.indexOf(item)
    if (idx !== -1) noticeList.value[idx] = { ...item, isRead: true }
    noticeUnread.value = Math.max(0, noticeUnread.value - 1)
  }
  proxy.$refs["noticeViewRef"].open(item.noticeId)
}

// 公告全部已读
function markAllRead() {
  const ids = noticeList.value.map(n => n.noticeId).join(',')
  if (!ids) return
  markNoticeReadAll(ids).catch(() => {})
  noticeList.value = noticeList.value.map(n => ({ ...n, isRead: true }))
  noticeUnread.value = 0
}

// 点击站内消息：标记已读并跳转消息中心查看全文
function openMessageCenter(item) {
  if (item.readFlag === '0') {
    markMessageRead(item.messageId).catch(() => {})
    msgUnread.value = Math.max(0, msgUnread.value - 1)
  }
  noticeVisible.value = false
  router.push('/message')
}

// 消息全部已读
function markAllMsgRead() {
  markAllMessageRead().then(() => {
    msgUnread.value = 0
    msgList.value = msgList.value.map(n => ({ ...n, readFlag: '1' }))
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.notice-trigger {
  position: relative;
  transform: translateX(-6px);
  .svg-icon { width: 1.2em; height: 1.2em; vertical-align: -0.2em; }
  .notice-badge {
    position: absolute;
    top: 7px;
    right: -3px;
    background: #f56c6c;
    color: #fff;
    border-radius: 10px;
    font-size: 10px;
    height: 16px;
    line-height: 16px;
    padding: 0 4px;
    min-width: 16px;
    text-align: center;
    white-space: nowrap;
    pointer-events: none;
  }
}
.notice-popover { padding: 0 !important; }
.notice-tabs {
  padding: 0 4px 4px;
  :deep(.el-tabs__header) { margin-bottom: 0; }
  .tab-badge { margin-left: 4px; }
}
.notice-popover .notice-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 8px 14px;
  background: #f7f9fb;
  border-bottom: 1px solid #eee;
  font-size: 13px;
  font-weight: 600;
  color: #333;
}
.notice-popover .notice-mark-all {
  font-size: 12px;
  color: var(--el-color-primary);
  font-weight: normal;
  cursor: pointer;
}
.notice-popover .notice-mark-all:hover { color: #2b7cc1; }
.notice-popover .notice-loading,
.notice-popover .notice-empty {
  padding: 24px;
  text-align: center;
  color: #bbb;
  font-size: 12px;
  line-height: 1.8;
}
.notice-popover .notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.15s;
}
.notice-popover .notice-item:last-child { border-bottom: none; }
.notice-popover .notice-item:hover { background: #f7f9fb; }
.notice-popover .notice-item.is-read .notice-tag,
.notice-popover .notice-item.is-read .notice-item-title,
.notice-popover .notice-item.is-read .notice-item-date { opacity: 0.45; filter: grayscale(1); color: #999; }
.notice-popover .notice-tag { flex-shrink: 0; }
.notice-popover .notice-item-title {
  flex: 1;
  font-size: 12px;
  color: #333;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.notice-popover .notice-item-date {
  flex-shrink: 0;
  font-size: 11px;
  color: #bbb;
}
</style>
