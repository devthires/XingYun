import request from '@/utils/request'

// 查询盘点任务列表
export function listInventory(query) {
  return request({
    url: '/biz/inventory/list',
    method: 'get',
    params: query
  })
}

// 查询盘点任务详情（含明细与进度统计）
export function getInventory(inventoryId) {
  return request({
    url: '/biz/inventory/' + inventoryId,
    method: 'get'
  })
}

// 创建盘点任务
export function addInventory(data) {
  return request({
    url: '/biz/inventory',
    method: 'post',
    data: data
  })
}

// 盘点确认（正常/盘亏）
export function checkInventoryItem(data) {
  return request({
    url: '/biz/inventory/checkItem',
    method: 'put',
    data: data
  })
}

// 完成盘点
export function finishInventory(inventoryId) {
  return request({
    url: '/biz/inventory/finish/' + inventoryId,
    method: 'put'
  })
}

// 删除盘点任务
export function delInventory(inventoryId) {
  return request({
    url: '/biz/inventory/' + inventoryId,
    method: 'delete'
  })
}
