package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.BizRepairOrder;
import com.ruoyi.system.mapper.BizRepairOrderMapper;
import com.ruoyi.system.mapper.BizStatMapper;
import com.ruoyi.system.service.IBizStatService;

/**
 * 数据看板统计Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizStatServiceImpl implements IBizStatService
{
    /** 看板最近报修记录条数 */
    private static final int RECENT_REPAIR_LIMIT = 10;

    @Autowired
    private BizStatMapper bizStatMapper;

    @Autowired
    private BizRepairOrderMapper bizRepairOrderMapper;

    /**
     * 看板汇总：卡片数字 + 资产状态分布 + 近7天报修趋势 + 最近报修记录
     *
     * @return 汇总数据Map
     */
    @Override
    public Map<String, Object> getDashboardSummary()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("counts", bizStatMapper.selectDashboardCount());
        result.put("assetStatusStats", bizStatMapper.selectAssetStatusStats());
        result.put("repairTrend", bizStatMapper.selectRepairTrend());
        List<BizRepairOrder> recentRepairs = bizRepairOrderMapper.selectRecentRepairOrders(RECENT_REPAIR_LIMIT);
        result.put("recentRepairs", recentRepairs);
        return result;
    }

    /**
     * 数据大屏：分类分布 + 领用状态分布 + 近30天报修趋势 + 资产总值 + 低库存耗材
     *
     * @return 大屏数据Map
     */
    @Override
    public Map<String, Object> getBigScreenData()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("valueSummary", bizStatMapper.selectAssetValueSummary());
        result.put("assetCategoryStats", bizStatMapper.selectAssetCategoryStats());
        result.put("assetStatusStats", bizStatMapper.selectAssetStatusStats());
        result.put("useStatusStats", bizStatMapper.selectUseStatusStats());
        result.put("repairTrend30", bizStatMapper.selectRepairTrend30());
        result.put("consumableLowList", bizStatMapper.selectConsumableLowList());
        return result;
    }
}
