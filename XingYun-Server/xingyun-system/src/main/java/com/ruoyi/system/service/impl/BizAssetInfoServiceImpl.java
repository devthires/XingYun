package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizAssetInfo;
import com.ruoyi.system.domain.BizAssetRecord;
import com.ruoyi.system.mapper.BizAssetInfoMapper;
import com.ruoyi.system.mapper.BizAssetRecordMapper;
import com.ruoyi.system.mapper.BizAssetUseMapper;
import com.ruoyi.system.mapper.BizRepairOrderMapper;
import com.ruoyi.system.service.IBizAssetInfoService;

/**
 * 资产信息Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizAssetInfoServiceImpl implements IBizAssetInfoService
{
    /** 资产状态：闲置 */
    private static final String ASSET_STATUS_IDLE = "0";

    /** 资产状态：在用 */
    private static final String ASSET_STATUS_IN_USE = "1";

    /** 资产状态名称（流水内容展示用，与字典 biz_asset_status 一致） */
    private static final String[] ASSET_STATUS_NAMES = {"闲置", "在用", "维修中", "报废"};

    /** 变更流水类型：入库 */
    private static final String RECORD_TYPE_INPUT = "1";

    /** 变更流水类型：变更 */
    private static final String RECORD_TYPE_UPDATE = "6";

    /** 变更流水类型：注销 */
    private static final String RECORD_TYPE_CANCEL = "7";

    @Autowired
    private BizAssetInfoMapper bizAssetInfoMapper;

    @Autowired
    private BizAssetUseMapper bizAssetUseMapper;

    @Autowired
    private BizRepairOrderMapper bizRepairOrderMapper;

    @Autowired
    private BizAssetRecordMapper bizAssetRecordMapper;

    /**
     * 查询资产信息
     *
     * @param assetId 资产ID
     * @return 资产信息
     */
    @Override
    public BizAssetInfo selectBizAssetInfoByAssetId(Long assetId)
    {
        return bizAssetInfoMapper.selectBizAssetInfoByAssetId(assetId);
    }

    /**
     * 查询资产信息列表
     *
     * @param bizAssetInfo 资产信息
     * @return 资产信息
     */
    @Override
    public List<BizAssetInfo> selectBizAssetInfoList(BizAssetInfo bizAssetInfo)
    {
        return bizAssetInfoMapper.selectBizAssetInfoList(bizAssetInfo);
    }

    /**
     * 校验资产编码是否唯一
     *
     * @param bizAssetInfo 资产信息
     * @return 结果（true 唯一）
     */
    @Override
    public boolean checkAssetCodeUnique(BizAssetInfo bizAssetInfo)
    {
        Long assetId = bizAssetInfo.getAssetId() == null ? -1L : bizAssetInfo.getAssetId();
        BizAssetInfo info = bizAssetInfoMapper.checkAssetCodeUnique(bizAssetInfo.getAssetCode());
        if (StringUtils.isNotNull(info) && assetId.longValue() != info.getAssetId().longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 新增资产信息
     *
     * @param bizAssetInfo 资产信息
     * @return 结果
     */
    @Override
    public int insertBizAssetInfo(BizAssetInfo bizAssetInfo)
    {
        // 编码留空时自动生成：ZC + yyyyMMdd + 3位流水
        if (StringUtils.isEmpty(bizAssetInfo.getAssetCode()))
        {
            bizAssetInfo.setAssetCode(generateAssetCode());
        }
        // 新资产默认为闲置
        if (StringUtils.isEmpty(bizAssetInfo.getStatus()))
        {
            bizAssetInfo.setStatus(ASSET_STATUS_IDLE);
        }
        bizAssetInfo.setCreateTime(DateUtils.getNowDate());
        int rows = bizAssetInfoMapper.insertBizAssetInfo(bizAssetInfo);
        if (rows > 0)
        {
            insertAssetRecord(bizAssetInfo.getAssetId(), RECORD_TYPE_INPUT,
                    "资产入库：" + bizAssetInfo.getAssetCode() + " " + bizAssetInfo.getAssetName(),
                    null, bizAssetInfo.getStatus(), bizAssetInfo.getCreateBy());
        }
        return rows;
    }

    /**
     * 生成资产编码：ZC + yyyyMMdd + 3位流水
     *
     * @return 资产编码
     */
    private String generateAssetCode()
    {
        String prefix = "ZC" + DateUtils.dateTimeNow("yyyyMMdd");
        int seq = bizAssetInfoMapper.countAssetCodeByPrefix(prefix) + 1;
        return prefix + String.format("%03d", seq);
    }

    /**
     * 修改资产信息（责任人或状态变化时落变更流水）
     *
     * @param bizAssetInfo 资产信息
     * @return 结果
     */
    @Override
    public int updateBizAssetInfo(BizAssetInfo bizAssetInfo)
    {
        BizAssetInfo dbAsset = bizAssetInfoMapper.selectBizAssetInfoByAssetId(bizAssetInfo.getAssetId());
        bizAssetInfo.setUpdateTime(DateUtils.getNowDate());
        int rows = bizAssetInfoMapper.updateBizAssetInfo(bizAssetInfo);
        if (rows > 0 && StringUtils.isNotNull(dbAsset))
        {
            boolean ownerChanged = bizAssetInfo.getOwnerId() != null
                    && !bizAssetInfo.getOwnerId().equals(dbAsset.getOwnerId());
            boolean statusChanged = StringUtils.isNotEmpty(bizAssetInfo.getStatus())
                    && !bizAssetInfo.getStatus().equals(dbAsset.getStatus());
            if (ownerChanged || statusChanged)
            {
                BizAssetInfo updated = bizAssetInfoMapper.selectBizAssetInfoByAssetId(bizAssetInfo.getAssetId());
                StringBuilder content = new StringBuilder("资产信息变更");
                if (ownerChanged)
                {
                    content.append("，责任人由[").append(StringUtils.nvl(dbAsset.getOwnerName(), "未设置"))
                           .append("]变更为[").append(updated == null ? "" : StringUtils.nvl(updated.getOwnerName(), "未设置")).append("]");
                }
                if (statusChanged)
                {
                    content.append("，状态由[").append(statusName(dbAsset.getStatus()))
                           .append("]变更为[").append(statusName(bizAssetInfo.getStatus())).append("]");
                }
                insertAssetRecord(bizAssetInfo.getAssetId(), RECORD_TYPE_UPDATE, content.toString(),
                        dbAsset.getStatus(), updated == null ? null : updated.getStatus(), bizAssetInfo.getUpdateBy());
            }
        }
        return rows;
    }

    /**
     * 状态码转状态名称（流水内容展示用）
     *
     * @param status 状态码
     * @return 状态名称
     */
    private String statusName(String status)
    {
        try
        {
            return ASSET_STATUS_NAMES[Integer.parseInt(status)];
        }
        catch (Exception e)
        {
            return status;
        }
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
     * 批量删除资产信息（逻辑删除），在用、存在进行中领用申请或有历史报修记录的资产不允许删除
     *
     * @param assetIds 需要删除的资产ID
     * @return 结果
     */
    @Override
    public int deleteBizAssetInfoByAssetIds(Long[] assetIds)
    {
        // 删除前采集资产信息：既做删除保护，又用于注销流水（逻辑删除后查不回数据）
        List<BizAssetInfo> toDelete = new java.util.ArrayList<>();
        for (Long assetId : assetIds)
        {
            BizAssetInfo asset = bizAssetInfoMapper.selectBizAssetInfoByAssetId(assetId);
            if (StringUtils.isNull(asset))
            {
                continue;
            }
            if (ASSET_STATUS_IN_USE.equals(asset.getStatus()))
            {
                throw new ServiceException("资产[" + asset.getAssetName() + "]使用中，不能删除");
            }
            if (bizAssetUseMapper.countActiveUseByAssetId(assetId) > 0)
            {
                throw new ServiceException("资产[" + asset.getAssetName() + "]存在进行中的领用申请，不能删除");
            }
            if (bizRepairOrderMapper.countRepairByAssetId(assetId) > 0)
            {
                throw new ServiceException("资产[" + asset.getAssetName() + "]存在历史报修记录，不能删除");
            }
            toDelete.add(asset);
        }
        int rows = bizAssetInfoMapper.deleteBizAssetInfoByAssetIds(assetIds);
        if (rows > 0)
        {
            for (BizAssetInfo asset : toDelete)
            {
                insertAssetRecord(asset.getAssetId(), RECORD_TYPE_CANCEL,
                        "资产注销（台账删除）：" + asset.getAssetCode() + " " + asset.getAssetName(),
                        asset.getStatus(), asset.getStatus(), asset.getUpdateBy());
            }
        }
        return rows;
    }

    /**
     * 删除资产信息
     *
     * @param assetId 资产ID
     * @return 结果
     */
    @Override
    public int deleteBizAssetInfoByAssetId(Long assetId)
    {
        return deleteBizAssetInfoByAssetIds(new Long[] { assetId });
    }
}
