import request from '@/utils/request'

// 查询资产变更流水列表
export function listRecord(query) {
  return request({
    url: '/biz/record/list',
    method: 'get',
    params: query
  })
}
