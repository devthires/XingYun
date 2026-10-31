import request from '@/utils/request'

// 查询耗材列表
export function listConsumable(query) {
  return request({
    url: '/biz/consumable/list',
    method: 'get',
    params: query
  })
}

// 查询耗材详细
export function getConsumable(consumableId) {
  return request({
    url: '/biz/consumable/' + consumableId,
    method: 'get'
  })
}

// 新增耗材
export function addConsumable(data) {
  return request({
    url: '/biz/consumable',
    method: 'post',
    data: data
  })
}

// 修改耗材
export function updateConsumable(data) {
  return request({
    url: '/biz/consumable',
    method: 'put',
    data: data
  })
}

// 耗材入库
export function stockIn(data) {
  return request({
    url: '/biz/consumable/stockIn',
    method: 'put',
    data: data
  })
}

// 耗材出库
export function stockOut(data) {
  return request({
    url: '/biz/consumable/stockOut',
    method: 'put',
    data: data
  })
}

// 查询出入库记录
export function listConsumableRecord(query) {
  return request({
    url: '/biz/consumable/records',
    method: 'get',
    params: query
  })
}

// 删除耗材
export function delConsumable(consumableId) {
  return request({
    url: '/biz/consumable/' + consumableId,
    method: 'delete'
  })
}
