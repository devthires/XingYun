package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.BizAssetInventory;

/**
 * 资产盘点单Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizAssetInventoryMapper
{
    /**
     * 查询资产盘点单列表
     *
     * @param bizAssetInventory 盘点单
     * @return 盘点单集合
     */
    public List<BizAssetInventory> selectBizAssetInventoryList(BizAssetInventory bizAssetInventory);

    /**
     * 查询资产盘点单
     *
     * @param inventoryId 盘点单ID
     * @return 盘点单
     */
    public BizAssetInventory selectBizAssetInventoryById(Long inventoryId);

    /**
     * 统计进行中的盘点任务数量（同时只允许一个盘点任务）
     *
     * @return 数量
     */
    public int countActiveInventory();

    /**
     * 新增资产盘点单
     *
     * @param bizAssetInventory 盘点单
     * @return 结果
     */
    public int insertBizAssetInventory(BizAssetInventory bizAssetInventory);

    /**
     * 修改资产盘点单（状态/数量统计/完成时间）
     *
     * @param bizAssetInventory 盘点单
     * @return 结果
     */
    public int updateBizAssetInventory(BizAssetInventory bizAssetInventory);

    /**
     * 删除资产盘点单（逻辑删除）
     *
     * @param inventoryId 盘点单ID
     * @return 结果
     */
    public int deleteBizAssetInventoryById(Long inventoryId);

    /**
     * 批量删除资产盘点单（逻辑删除）
     *
     * @param inventoryIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAssetInventoryByIds(Long[] inventoryIds);
}
