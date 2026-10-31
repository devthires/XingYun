import request from '@/utils/request'

// 获取数据大屏数据（资产总值/分类分布/状态分布/领用状态/30天趋势/低库存清单）
export function getBigScreenData() {
  return request({
    url: '/biz/dashboard/bigscreen',
    method: 'get'
  })
}
