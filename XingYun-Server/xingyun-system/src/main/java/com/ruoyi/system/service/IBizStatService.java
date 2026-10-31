package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * 数据看板统计Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizStatService
{
    /**
     * 看板汇总：卡片数字 + 资产状态分布 + 近7天报修趋势 + 最近报修记录
     *
     * @return 汇总数据Map
     */
    public Map<String, Object> getDashboardSummary();

    /**
     * 数据大屏：分类分布 + 领用状态分布 + 近30天报修趋势 + 资产总值 + 低库存耗材
     *
     * @return 大屏数据Map
     */
    public Map<String, Object> getBigScreenData();
}
