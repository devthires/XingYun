import request from '@/utils/request'

// 查询资产列表
export function listAsset(query) {
  return request({
    url: '/biz/asset/list',
    method: 'get',
    params: query
  })
}

// 查询资产详细
export function getAsset(assetId) {
  return request({
    url: '/biz/asset/' + assetId,
    method: 'get'
  })
}

// 新增资产
export function addAsset(data) {
  return request({
    url: '/biz/asset',
    method: 'post',
    data: data
  })
}

// 修改资产
export function updateAsset(data) {
  return request({
    url: '/biz/asset',
    method: 'put',
    data: data
  })
}

// 删除资产
export function delAsset(assetId) {
  return request({
    url: '/biz/asset/' + assetId,
    method: 'delete'
  })
}
