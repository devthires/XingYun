import request from '@/utils/request'

// 查询报修单列表
export function listRepair(query) {
  return request({
    url: '/biz/repair/list',
    method: 'get',
    params: query
  })
}

// 查询报修单详细
export function getRepair(repairId) {
  return request({
    url: '/biz/repair/' + repairId,
    method: 'get'
  })
}

// 新增报修单
export function addRepair(data) {
  return request({
    url: '/biz/repair',
    method: 'post',
    data: data
  })
}

// 修改报修单
export function updateRepair(data) {
  return request({
    url: '/biz/repair',
    method: 'put',
    data: data
  })
}

// 修改报修状态（处理中/已完成）
export function changeRepairStatus(repairId, status) {
  return request({
    url: '/biz/repair/changeStatus',
    method: 'put',
    data: { repairId: repairId, status: status }
  })
}

// 删除报修单
export function delRepair(repairId) {
  return request({
    url: '/biz/repair/' + repairId,
    method: 'delete'
  })
}
