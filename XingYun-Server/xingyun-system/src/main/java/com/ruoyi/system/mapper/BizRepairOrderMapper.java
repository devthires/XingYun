package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.BizRepairOrder;

/**
 * 设备报修单Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-28
 */
public interface BizRepairOrderMapper
{
    /**
     * 查询设备报修单
     *
     * @param repairId 报修单ID
     * @return 设备报修单
     */
    public BizRepairOrder selectBizRepairOrderByRepairId(Long repairId);

    /**
     * 查询设备报修单列表（连表 biz_asset_info，支持按报修状态等条件分页查询）
     *
     * @param bizRepairOrder 设备报修单
     * @return 设备报修单集合
     */
    public List<BizRepairOrder> selectBizRepairOrderList(BizRepairOrder bizRepairOrder);

    /**
     * 查询最近报修单（首页看板用）
     *
     * @param limit 条数
     * @return 设备报修单集合
     */
    public List<BizRepairOrder> selectRecentRepairOrders(int limit);

    /**
     * 新增设备报修单
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    public int insertBizRepairOrder(BizRepairOrder bizRepairOrder);

    /**
     * 修改设备报修单
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    public int updateBizRepairOrder(BizRepairOrder bizRepairOrder);

    /**
     * 修改报修单状态（已完成时自动维护完成时间，回退时清空）
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    public int changeRepairStatus(BizRepairOrder bizRepairOrder);

    /**
     * 删除设备报修单（逻辑删除）
     *
     * @param repairId 报修单ID
     * @return 结果
     */
    public int deleteBizRepairOrderByRepairId(Long repairId);

    /**
     * 批量删除设备报修单（逻辑删除）
     *
     * @param repairIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizRepairOrderByRepairIds(Long[] repairIds);

    /**
     * 统计指定前缀的单号已占用数量（生成流水号用，含已删除数据防止单号冲突）
     *
     * @param repairNoPrefix 报修单号前缀
     * @return 数量
     */
    public int countRepairNoByPrefix(String repairNoPrefix);

    /**
     * 统计资产的历史报修单数量（资产删除保护用，含已删除单据）
     *
     * @param assetId 资产ID
     * @return 数量
     */
    public int countRepairByAssetId(Long assetId);
}
