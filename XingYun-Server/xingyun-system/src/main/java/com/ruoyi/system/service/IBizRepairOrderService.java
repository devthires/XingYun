package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizRepairOrder;

/**
 * 设备报修单Service接口
 *
 * @author ruoyi
 * @date 2026-10-28
 */
public interface IBizRepairOrderService
{
    /**
     * 查询设备报修单
     *
     * @param repairId 报修单ID
     * @return 设备报修单
     */
    public BizRepairOrder selectBizRepairOrderByRepairId(Long repairId);

    /**
     * 查询设备报修单列表
     *
     * @param bizRepairOrder 设备报修单
     * @return 设备报修单集合
     */
    public List<BizRepairOrder> selectBizRepairOrderList(BizRepairOrder bizRepairOrder);

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
     * 修改报修单状态（流转后同步设备状态）
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    public int changeRepairStatus(BizRepairOrder bizRepairOrder);

    /**
     * 批量删除设备报修单
     *
     * @param repairIds 需要删除的报修单ID
     * @return 结果
     */
    public int deleteBizRepairOrderByRepairIds(Long[] repairIds);

    /**
     * 删除设备报修单信息
     *
     * @param repairId 报修单ID
     * @return 结果
     */
    public int deleteBizRepairOrderByRepairId(Long repairId);
}
