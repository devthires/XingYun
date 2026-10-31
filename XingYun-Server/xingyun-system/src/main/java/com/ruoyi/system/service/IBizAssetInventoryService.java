package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.BizAssetInventory;
import com.ruoyi.system.domain.BizAssetInventoryItem;

/**
 * 资产盘点Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizAssetInventoryService
{
    /**
     * 查询盘点单列表
     *
     * @param bizAssetInventory 盘点单
     * @return 盘点单集合
     */
    public List<BizAssetInventory> selectBizAssetInventoryList(BizAssetInventory bizAssetInventory);

    /**
     * 查询盘点单详情（含盘点明细与进度统计）
     *
     * @param inventoryId 盘点单ID
     * @return {inventory: 盘点单, items: 明细列表, stats: 进度统计}
     */
    public Map<String, Object> selectBizAssetInventoryById(Long inventoryId);

    /**
     * 创建盘点任务：对全部在册资产建立账面快照
     *
     * @param bizAssetInventory 盘点单
     * @return 结果
     */
    public int createInventory(BizAssetInventory bizAssetInventory);

    /**
     * 盘点确认（正常/盘亏），同时刷新盘点单进度统计
     *
     * @param item 盘点明细（itemId、result、remark）
     * @return 结果
     */
    public int checkItem(BizAssetInventoryItem item);

    /**
     * 完成盘点（全部明细确认后方可完成）
     *
     * @param inventoryId 盘点单ID
     * @return 结果
     */
    public int finishInventory(Long inventoryId);

    /**
     * 批量删除盘点任务（连同明细）
     *
     * @param inventoryIds 需要删除的盘点单ID
     * @return 结果
     */
    public int deleteBizAssetInventoryByIds(Long[] inventoryIds);
}
