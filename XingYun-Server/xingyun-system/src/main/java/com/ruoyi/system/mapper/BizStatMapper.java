package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

/**
 * 数据看板统计Mapper接口（只读聚合查询）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizStatMapper
{
    /**
     * 看板汇总数字：资产总数、待处理报修、维修中资产、今日新增资产
     *
     * @return 汇总Map
     */
    public Map<String, Object> selectDashboardCount();

    /**
     * 资产状态分布（饼图）
     *
     * @return [{status, cnt}]
     */
    public List<Map<String, Object>> selectAssetStatusStats();

    /**
     * 近7天报修单数量趋势（按天）
     *
     * @return [{repairDate, cnt}]
     */
    public List<Map<String, Object>> selectRepairTrend();

    /**
     * 近30天报修单数量趋势（大屏用）
     *
     * @return [{repairDate, cnt}]
     */
    public List<Map<String, Object>> selectRepairTrend30();

    /**
     * 资产分类分布（大屏用）
     *
     * @return [{category, cnt}]
     */
    public List<Map<String, Object>> selectAssetCategoryStats();

    /**
     * 领用状态分布（大屏用）
     *
     * @return [{status, cnt}]
     */
    public List<Map<String, Object>> selectUseStatusStats();

    /**
     * 资产总值汇总（大屏用）
     *
     * @return {assetValueTotal, assetCount}
     */
    public Map<String, Object> selectAssetValueSummary();

    /**
     * 低库存耗材清单（大屏用）
     *
     * @return [{consumableName, spec, unit, stockQty, warnQty}]
     */
    public List<Map<String, Object>> selectConsumableLowList();
}
