package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizConsumable;
import com.ruoyi.system.domain.BizConsumableRecord;
import com.ruoyi.system.mapper.BizConsumableMapper;
import com.ruoyi.system.service.IBizConsumableService;

/**
 * 耗材库存Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizConsumableServiceImpl implements IBizConsumableService
{
    /** 记录类型：入库 */
    private static final String RECORD_TYPE_IN = "1";

    /** 记录类型：出库 */
    private static final String RECORD_TYPE_OUT = "2";

    @Autowired
    private BizConsumableMapper bizConsumableMapper;

    /**
     * 查询耗材列表
     *
     * @param bizConsumable 耗材
     * @return 耗材集合
     */
    @Override
    public List<BizConsumable> selectBizConsumableList(BizConsumable bizConsumable)
    {
        return bizConsumableMapper.selectBizConsumableList(bizConsumable);
    }

    /**
     * 查询耗材
     *
     * @param consumableId 耗材ID
     * @return 耗材
     */
    @Override
    public BizConsumable selectBizConsumableById(Long consumableId)
    {
        return bizConsumableMapper.selectBizConsumableById(consumableId);
    }

    /**
     * 校验耗材编码是否唯一
     *
     * @param bizConsumable 耗材
     * @return 结果（true 唯一）
     */
    @Override
    public boolean checkConsumableCodeUnique(BizConsumable bizConsumable)
    {
        Long consumableId = bizConsumable.getConsumableId() == null ? -1L : bizConsumable.getConsumableId();
        BizConsumable info = bizConsumableMapper.checkConsumableCodeUnique(bizConsumable.getConsumableCode());
        if (StringUtils.isNotNull(info) && consumableId.longValue() != info.getConsumableId().longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 新增耗材
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    @Override
    public int insertBizConsumable(BizConsumable bizConsumable)
    {
        bizConsumable.setCreateTime(DateUtils.getNowDate());
        return bizConsumableMapper.insertBizConsumable(bizConsumable);
    }

    /**
     * 修改耗材
     *
     * @param bizConsumable 耗材
     * @return 结果
     */
    @Override
    public int updateBizConsumable(BizConsumable bizConsumable)
    {
        bizConsumable.setUpdateTime(DateUtils.getNowDate());
        return bizConsumableMapper.updateBizConsumable(bizConsumable);
    }

    /**
     * 耗材入库
     *
     * @param consumableId 耗材ID
     * @param quantity 入库数量
     * @param content 说明
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int stockIn(Long consumableId, Integer quantity, String content, String operator)
    {
        return changeStockWithRecord(consumableId, quantity, RECORD_TYPE_IN, content, operator);
    }

    /**
     * 耗材出库（库存不足时拦截）
     *
     * @param consumableId 耗材ID
     * @param quantity 出库数量
     * @param content 说明
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int stockOut(Long consumableId, Integer quantity, String content, String operator)
    {
        return changeStockWithRecord(consumableId, quantity, RECORD_TYPE_OUT, content, operator);
    }

    /**
     * 库存变更 + 出入库记录（事务内完成）
     *
     * @param consumableId 耗材ID
     * @param quantity 变动数量（正数）
     * @param recordType 记录类型
     * @param content 说明
     * @param operator 操作人
     * @return 结果
     */
    private int changeStockWithRecord(Long consumableId, Integer quantity, String recordType, String content, String operator)
    {
        if (quantity == null || quantity <= 0)
        {
            throw new ServiceException("变动数量必须为正整数");
        }
        BizConsumable dbConsumable = bizConsumableMapper.selectBizConsumableById(consumableId);
        if (StringUtils.isNull(dbConsumable))
        {
            throw new ServiceException("耗材不存在或已被删除");
        }
        int beforeQty = dbConsumable.getStockQty() == null ? 0 : dbConsumable.getStockQty();
        if (RECORD_TYPE_OUT.equals(recordType) && beforeQty < quantity)
        {
            throw new ServiceException("库存不足，当前库存 " + beforeQty + " " + dbConsumable.getUnit());
        }
        int rows = bizConsumableMapper.changeStock(consumableId,
                RECORD_TYPE_OUT.equals(recordType) ? -quantity : quantity, operator);
        if (rows > 0)
        {
            BizConsumableRecord record = new BizConsumableRecord();
            record.setConsumableId(consumableId);
            record.setRecordType(recordType);
            record.setQuantity(quantity);
            record.setBeforeQty(beforeQty);
            record.setAfterQty(RECORD_TYPE_OUT.equals(recordType) ? beforeQty - quantity : beforeQty + quantity);
            record.setContent(content);
            record.setOperator(operator);
            record.setCreateTime(DateUtils.getNowDate());
            bizConsumableMapper.insertConsumableRecord(record);
        }
        return rows;
    }

    /**
     * 查询出入库记录列表
     *
     * @param bizConsumableRecord 查询条件
     * @return 记录集合
     */
    @Override
    public List<BizConsumableRecord> selectConsumableRecordList(BizConsumableRecord bizConsumableRecord)
    {
        return bizConsumableMapper.selectConsumableRecordList(bizConsumableRecord);
    }

    /**
     * 批量删除耗材
     *
     * @param consumableIds 需要删除的耗材ID
     * @return 结果
     */
    @Override
    public int deleteBizConsumableByIds(Long[] consumableIds)
    {
        return bizConsumableMapper.deleteBizConsumableByIds(consumableIds);
    }
}
