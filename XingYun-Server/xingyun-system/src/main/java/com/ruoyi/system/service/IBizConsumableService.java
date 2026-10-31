package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizConsumable;
import com.ruoyi.system.domain.BizConsumableRecord;

/**
 * 耗材库存Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizConsumableService
{
    /**
     * 查询耗材列表
     *
     * @param bizConsumable 耗材
     * @return 耗材集合
     */
    public List<BizConsumable> selectBizConsumableList(BizConsumable bizConsumable);

    /**
     * 查询耗材
     *
     * @param consumableId 耗材ID
     * @return 耗材
     */
    public BizConsumable selectBizConsumableById(Long consumableId);

    /**
     * 校验耗材编码是否唯一
     *
     * @param bizConsumable 耗材
     * @return 结果（true 唯一）
     */
    public boolean checkConsumableCodeUnique(BizConsumable bizConsumable);

    /**
     * 新增耗材
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    public int insertBizConsumable(BizConsumable bizConsumable);

    /**
     * 修改耗材
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    public int updateBizConsumable(BizConsumable bizConsumable);

    /**
     * 耗材入库
     *
     * @param consumableId 耗材ID
     * @param quantity 入库数量（正数）
     * @param content 说明
     * @param operator 操作人
     * @return 结果
     */
    public int stockIn(Long consumableId, Integer quantity, String content, String operator);

    /**
     * 耗材出库（库存不足时拦截）
     *
     * @param consumableId 耗材ID
     * @param quantity 出库数量（正数）
     * @param content 说明
     * @param operator 操作人
     * @return 结果
     */
    public int stockOut(Long consumableId, Integer quantity, String content, String operator);

    /**
     * 查询出入库记录列表
     *
     * @param bizConsumableRecord 查询条件
     * @return 记录集合
     */
    public List<BizConsumableRecord> selectConsumableRecordList(BizConsumableRecord bizConsumableRecord);

    /**
     * 批量删除耗材
     *
     * @param consumableIds 需要删除的耗材ID
     * @return 结果
     */
    public int deleteBizConsumableByIds(Long[] consumableIds);
}
