package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BizAssetInventoryItem;

/**
 * 资产盘点明细Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizAssetInventoryItemMapper
{
    /**
     * 查询盘点单的明细列表（连表 biz_asset_info 带出资产当前状态）
     *
     * @param inventoryId 盘点单ID
     * @return 明细集合
     */
    public List<BizAssetInventoryItem> selectItemsByInventoryId(Long inventoryId);

    /**
     * 查询盘点明细
     *
     * @param itemId 明细ID
     * @return 明细
     */
    public BizAssetInventoryItem selectItemById(Long itemId);

    /**
     * 批量新增盘点明细
     *
     * @param items 明细集合
     * @return 结果
     */
    public int insertItems(List<BizAssetInventoryItem> items);

    /**
     * 修改盘点结果
     *
     * @param item 盘点明细
     * @return 结果
     */
    public int updateItemResult(BizAssetInventoryItem item);

    /**
     * 按盘点单删除明细（物理删除，随盘点单一起清理）
     *
     * @param inventoryIds 盘点单ID集合
     * @return 结果
     */
    public int deleteItemsByInventoryIds(@Param("inventoryIds") Long[] inventoryIds);
}
