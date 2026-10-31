import request from '@/utils/request'

// 获取首页看板汇总（卡片数字 + 资产状态分布 + 近7天报修趋势 + 最近报修记录）
export function getDashboardSummary() {
  return request({
    url: '/biz/dashboard/summary',
    method: 'get'
  })
}
