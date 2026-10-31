package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizAssetInfo;
import com.ruoyi.system.domain.BizAssetRecord;
import com.ruoyi.system.domain.BizRepairOrder;
import com.ruoyi.system.mapper.BizAssetInfoMapper;
import com.ruoyi.system.mapper.BizAssetRecordMapper;
import com.ruoyi.system.mapper.BizRepairOrderMapper;
import com.ruoyi.system.service.IBizRepairOrderService;

/**
 * 设备报修单Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizRepairOrderServiceImpl implements IBizRepairOrderService
{
    /** 报修状态：待处理 */
    private static final String REPAIR_STATUS_PENDING = "0";

    /** 报修状态：处理中 */
    private static final String REPAIR_STATUS_PROCESSING = "1";

    /** 报修状态：已完成 */
    private static final String REPAIR_STATUS_FINISHED = "2";

    /** 资产状态：维修中 */
    private static final String ASSET_STATUS_REPAIRING = "2";

    /** 资产状态：闲置 */
    private static final String ASSET_STATUS_IDLE = "0";

    /** 资产状态：报废 */
    private static final String ASSET_STATUS_SCRAPPED = "3";

    /** 变更流水类型：送修 */
    private static final String RECORD_TYPE_TO_REPAIR = "4";

    /** 变更流水类型：维修完成 */
    private static final String RECORD_TYPE_REPAIRED = "5";

    @Autowired
    private BizRepairOrderMapper bizRepairOrderMapper;

    @Autowired
    private BizAssetInfoMapper bizAssetInfoMapper;

    @Autowired
    private BizAssetRecordMapper bizAssetRecordMapper;

    /**
     * 查询设备报修单
     *
     * @param repairId 报修单ID
     * @return 设备报修单
     */
    @Override
    public BizRepairOrder selectBizRepairOrderByRepairId(Long repairId)
    {
        return bizRepairOrderMapper.selectBizRepairOrderByRepairId(repairId);
    }

    /**
     * 查询设备报修单列表
     *
     * @param bizRepairOrder 设备报修单
     * @return 设备报修单
     */
    @Override
    public List<BizRepairOrder> selectBizRepairOrderList(BizRepairOrder bizRepairOrder)
    {
        return bizRepairOrderMapper.selectBizRepairOrderList(bizRepairOrder);
    }

    /**
     * 新增设备报修单
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    @Override
    public int insertBizRepairOrder(BizRepairOrder bizRepairOrder)
    {
        // 资产不存在或已删除时不允许报修
        BizAssetInfo asset = bizAssetInfoMapper.selectBizAssetInfoByAssetId(bizRepairOrder.getAssetId());
        if (StringUtils.isNull(asset))
        {
            throw new ServiceException("报修资产不存在，请重新选择");
        }
        // 维修中说明已有维修单在处理，报废资产无需维修
        if (ASSET_STATUS_REPAIRING.equals(asset.getStatus()))
        {
            throw new ServiceException("该资产正在维修中，请勿重复报修");
        }
        if (ASSET_STATUS_SCRAPPED.equals(asset.getStatus()))
        {
            throw new ServiceException("该资产已报废，无需报修");
        }
        // 单号留空时按规则自动生成：BX + yyyyMMdd + 3位流水
        if (StringUtils.isEmpty(bizRepairOrder.getRepairNo()))
        {
            bizRepairOrder.setRepairNo(generateRepairNo());
        }
        // 新报修单默认为待处理
        if (StringUtils.isEmpty(bizRepairOrder.getStatus()))
        {
            bizRepairOrder.setStatus(REPAIR_STATUS_PENDING);
        }
        bizRepairOrder.setCreateTime(DateUtils.getNowDate());
        return bizRepairOrderMapper.insertBizRepairOrder(bizRepairOrder);
    }

    /**
     * 生成报修单号：BX + yyyyMMdd + 3位流水（按当日单号最大序号递增）
     *
     * @return 报修单号
     */
    private String generateRepairNo()
    {
        String prefix = "BX" + DateUtils.dateTimeNow("yyyyMMdd");
        // 计数法在并发极低的内部管理场景够用；高并发场景建议改为 Redis 自增或独立发号表
        int seq = bizRepairOrderMapper.countRepairNoByPrefix(prefix) + 1;
        return prefix + String.format("%03d", seq);
    }

    /**
     * 修改设备报修单
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    @Override
    public int updateBizRepairOrder(BizRepairOrder bizRepairOrder)
    {
        bizRepairOrder.setUpdateTime(DateUtils.getNowDate());
        return bizRepairOrderMapper.updateBizRepairOrder(bizRepairOrder);
    }

    /**
     * 修改报修单状态，并同步资产状态与变更流水：
     * 标记处理中 → 资产置为维修中（流水：送修）；标记已完成 → 记录完成时间、资产恢复为闲置（流水：维修完成）
     *
     * @param bizRepairOrder 设备报修单
     * @return 结果
     */
    @Override
    @Transactional
    public int changeRepairStatus(BizRepairOrder bizRepairOrder)
    {
        String status = bizRepairOrder.getStatus();
        if (!REPAIR_STATUS_PENDING.equals(status) && !REPAIR_STATUS_PROCESSING.equals(status)
                && !REPAIR_STATUS_FINISHED.equals(status))
        {
            throw new ServiceException("非法的报修状态值：" + status);
        }
        BizRepairOrder dbOrder = bizRepairOrderMapper.selectBizRepairOrderByRepairId(bizRepairOrder.getRepairId());
        if (StringUtils.isNull(dbOrder))
        {
            throw new ServiceException("报修单不存在或已被删除");
        }
        int rows = bizRepairOrderMapper.changeRepairStatus(bizRepairOrder);
        if (rows > 0)
        {
            if (REPAIR_STATUS_PROCESSING.equals(status))
            {
                bizAssetInfoMapper.updateAssetStatus(dbOrder.getAssetId(), ASSET_STATUS_REPAIRING);
                insertAssetRecord(dbOrder.getAssetId(), RECORD_TYPE_TO_REPAIR,
                        "报修单" + dbOrder.getRepairNo() + "送修：" + dbOrder.getProblemDesc(),
                        null, ASSET_STATUS_REPAIRING, bizRepairOrder.getUpdateBy());
            }
            else if (REPAIR_STATUS_FINISHED.equals(status))
            {
                BizAssetInfo asset = bizAssetInfoMapper.selectBizAssetInfoByAssetId(dbOrder.getAssetId());
                bizAssetInfoMapper.updateAssetStatus(dbOrder.getAssetId(), ASSET_STATUS_IDLE);
                insertAssetRecord(dbOrder.getAssetId(), RECORD_TYPE_REPAIRED,
                        "报修单" + dbOrder.getRepairNo() + "维修完成",
                        asset == null ? null : asset.getStatus(), ASSET_STATUS_IDLE, bizRepairOrder.getUpdateBy());
            }
        }
        return rows;
    }

    /**
     * 写入资产变更流水
     *
     * @param assetId 资产ID
     * @param recordType 变更类型
     * @param content 变更说明
     * @param beforeStatus 变更前状态
     * @param afterStatus 变更后状态
     * @param operator 操作人
     */
    private void insertAssetRecord(Long assetId, String recordType, String content, String beforeStatus,
                                   String afterStatus, String operator)
    {
        BizAssetRecord record = new BizAssetRecord();
        record.setAssetId(assetId);
        record.setRecordType(recordType);
        record.setContent(content);
        record.setBeforeStatus(beforeStatus);
        record.setAfterStatus(afterStatus);
        record.setOperator(operator);
        record.setCreateTime(DateUtils.getNowDate());
        bizAssetRecordMapper.insertBizAssetRecord(record);
    }

    /**
     * 批量删除设备报修单（逻辑删除）
     *
     * @param repairIds 需要删除的报修单ID
     * @return 结果
     */
    @Override
    public int deleteBizRepairOrderByRepairIds(Long[] repairIds)
    {
        return bizRepairOrderMapper.deleteBizRepairOrderByRepairIds(repairIds);
    }

    /**
     * 删除设备报修单信息（逻辑删除）
     *
     * @param repairId 报修单ID
     * @return 结果
     */
    @Override
    public int deleteBizRepairOrderByRepairId(Long repairId)
    {
        return bizRepairOrderMapper.deleteBizRepairOrderByRepairId(repairId);
    }
}
