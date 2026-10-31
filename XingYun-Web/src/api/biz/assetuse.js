import request from '@/utils/request'

// 查询资产领用列表
export function listAssetUse(query) {
  return request({
    url: '/biz/use/list',
    method: 'get',
    params: query
  })
}

// 查询资产领用详细
export function getAssetUse(useId) {
  return request({
    url: '/biz/use/' + useId,
    method: 'get'
  })
}

// 新增资产领用
export function addAssetUse(data) {
  return request({
    url: '/biz/use',
    method: 'post',
    data: data
  })
}

// 修改资产领用
export function updateAssetUse(data) {
  return request({
    url: '/biz/use',
    method: 'put',
    data: data
  })
}

// 修改领用状态（审批通过/归还/驳回）
export function changeAssetUseStatus(useId, status, remark) {
  return request({
    url: '/biz/use/changeStatus',
    method: 'put',
    data: { useId: useId, status: status, remark: remark }
  })
}

// 删除资产领用
export function delAssetUse(useId) {
  return request({
    url: '/biz/use/' + useId,
    method: 'delete'
  })
}
