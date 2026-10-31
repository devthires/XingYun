import request from '@/utils/request'

// 查询我的消息列表
export function listMyMessage(query) {
  return request({
    url: '/biz/message/list',
    method: 'get',
    params: query
  })
}

// 查询我的未读消息数量
export function getUnreadCount() {
  return request({
    url: '/biz/message/unreadCount',
    method: 'get'
  })
}

// 标记消息已读
export function markMessageRead(messageId) {
  return request({
    url: '/biz/message/read/' + messageId,
    method: 'put'
  })
}

// 全部已读
export function markAllMessageRead() {
  return request({
    url: '/biz/message/readAll',
    method: 'put'
  })
}
