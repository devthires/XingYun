package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BizConsumable;
import com.ruoyi.system.domain.BizConsumableRecord;

/**
 * 耗材库存Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizConsumableMapper
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
     * 按编码查询耗材（编码唯一性校验用）
     *
     * @param consumableCode 耗材编码
     * @return 耗材
     */
    public BizConsumable checkConsumableCodeUnique(String consumableCode);

    /**
     * 新增耗材
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    public int insertBizConsumable(BizConsumable bizConsumable);

    /**
     * 修改耗材（基础信息）
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    public int updateBizConsumable(BizConsumable bizConsumable);

    /**
     * 库存增减（SQL 内做加减，避免并发覆盖）
     *
     * @param consumableId 耗材ID
     * @param quantity 变动数量（正数入库，负数出库）
     * @param operator 操作人
     * @return 结果
     */
    public int changeStock(@Param("consumableId") Long consumableId, @Param("quantity") Integer quantity, @Param("operator") String operator);

    /**
     * 删除耗材（逻辑删除）
     *
     * @param consumableId 耗材ID
     * @return 结果
     */
    public int deleteBizConsumableById(Long consumableId);

    /**
     * 批量删除耗材（逻辑删除）
     *
     * @param consumableIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizConsumableByIds(Long[] consumableIds);

    /**
     * 新增出入库记录
     *
     * @param bizConsumableRecord 出入库记录
     * @return 结果
     */
    public int insertConsumableRecord(BizConsumableRecord bizConsumableRecord);

    /**
     * 查询耗材的出入库记录列表
     *
     * @param bizConsumableRecord 查询条件
     * @return 记录集合
     */
    public List<BizConsumableRecord> selectConsumableRecordList(BizConsumableRecord bizConsumableRecord);
}
