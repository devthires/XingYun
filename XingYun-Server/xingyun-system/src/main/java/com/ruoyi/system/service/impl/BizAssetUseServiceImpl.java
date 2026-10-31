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
import com.ruoyi.system.domain.BizAssetUse;
import com.ruoyi.system.mapper.BizAssetInfoMapper;
import com.ruoyi.system.mapper.BizAssetRecordMapper;
import com.ruoyi.system.mapper.BizAssetUseMapper;
import com.ruoyi.system.service.IBizAssetUseService;
import com.ruoyi.system.service.IBizMessageService;

/**
 * 资产领用Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-28
 */
@Service
public class BizAssetUseServiceImpl implements IBizAssetUseService
{
    /** 领用状态：审批中 */
    private static final String USE_STATUS_APPROVAL = "0";

    /** 领用状态：使用中 */
    private static final String USE_STATUS_IN_USE = "1";

    /** 领用状态：已归还 */
    private static final String USE_STATUS_RETURNED = "2";

    /** 领用状态：已驳回 */
    private static final String USE_STATUS_REJECTED = "3";

    /** 资产状态：闲置 */
    private static final String ASSET_STATUS_IDLE = "0";

    /** 资产状态：在用 */
    private static final String ASSET_STATUS_IN_USE = "1";

    /** 变更流水类型：领用出库 */
    private static final String RECORD_TYPE_LEND = "2";

    /** 变更流水类型：归还入库 */
    private static final String RECORD_TYPE_BACK = "3";

    /** 消息类型：领用审批 */
    private static final String MSG_TYPE_USE_AUDIT = "1";

    /** 消息类型：逾期提醒 */
    private static final String MSG_TYPE_OVERDUE = "2";

    @Autowired
    private BizAssetUseMapper bizAssetUseMapper;

    @Autowired
    private BizAssetInfoMapper bizAssetInfoMapper;

    @Autowired
    private BizAssetRecordMapper bizAssetRecordMapper;

    @Autowired
    private IBizMessageService bizMessageService;

    /**
     * 查询资产领用
     *
     * @param useId 领用ID
     * @return 资产领用
     */
    @Override
    public BizAssetUse selectBizAssetUseByUseId(Long useId)
    {
        return bizAssetUseMapper.selectBizAssetUseByUseId(useId);
    }

    /**
     * 查询资产领用列表
     *
     * @param bizAssetUse 资产领用
     * @return 资产领用
     */
    @Override
    public List<BizAssetUse> selectBizAssetUseList(BizAssetUse bizAssetUse)
    {
        return bizAssetUseMapper.selectBizAssetUseList(bizAssetUse);
    }

    /**
     * 新增资产领用
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    @Override
    public int insertBizAssetUse(BizAssetUse bizAssetUse)
    {
        // 资产不存在或已删除时不允许领用
        BizAssetInfo asset = bizAssetInfoMapper.selectBizAssetInfoByAssetId(bizAssetUse.getAssetId());
        if (StringUtils.isNull(asset))
        {
            throw new ServiceException("领用资产不存在，请重新选择");
        }
        // 只有“闲置”资产可以发起领用，其余状态一律拦截
        if (!ASSET_STATUS_IDLE.equals(asset.getStatus()))
        {
            throw new ServiceException("该资产已被占用，无法领用");
        }
        // 防止同一资产存在未走完的领用单（审批中/使用中）时重复申请
        if (bizAssetUseMapper.countActiveUseByAssetId(bizAssetUse.getAssetId()) > 0)
        {
            throw new ServiceException("该资产已有进行中的领用申请，请勿重复申请");
        }
        // 领用时间留空时默认为当前时间，新申请默认为审批中
        if (StringUtils.isNull(bizAssetUse.getUseTime()))
        {
            bizAssetUse.setUseTime(DateUtils.getNowDate());
        }
        bizAssetUse.setStatus(USE_STATUS_APPROVAL);
        bizAssetUse.setCreateTime(DateUtils.getNowDate());
        return bizAssetUseMapper.insertBizAssetUse(bizAssetUse);
    }

    /**
     * 修改资产领用
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    @Override
    public int updateBizAssetUse(BizAssetUse bizAssetUse)
    {
        bizAssetUse.setUpdateTime(DateUtils.getNowDate());
        return bizAssetUseMapper.updateBizAssetUse(bizAssetUse);
    }

    /**
     * 修改领用状态（状态机校验），并同步资产状态：
     * 审批中 → 审批通过（使用中，资产置为在用）/ 已驳回（必填原因）；
     * 使用中 → 已归还（记录实际归还时间，资产恢复闲置）
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    @Override
    @Transactional
    public int changeUseStatus(BizAssetUse bizAssetUse)
    {
        String status = bizAssetUse.getStatus();
        // 状态流转只允许：审批通过(1)、归还(2)、驳回(3)
        if (!USE_STATUS_IN_USE.equals(status) && !USE_STATUS_RETURNED.equals(status)
                && !USE_STATUS_REJECTED.equals(status))
        {
            throw new ServiceException("非法的领用状态值：" + status);
        }
        BizAssetUse dbUse = bizAssetUseMapper.selectBizAssetUseByUseId(bizAssetUse.getUseId());
        if (StringUtils.isNull(dbUse))
        {
            throw new ServiceException("领用单不存在或已被删除");
        }
        // 状态机校验：审批动作只能作用于“审批中”的单据，归还只能作用于“使用中”的单据
        String fromStatus = dbUse.getStatus();
        if (USE_STATUS_IN_USE.equals(status) || USE_STATUS_REJECTED.equals(status))
        {
            if (!USE_STATUS_APPROVAL.equals(fromStatus))
            {
                throw new ServiceException("该领用单已完成审批，请勿重复操作");
            }
        }
        else if (USE_STATUS_RETURNED.equals(status))
        {
            if (!USE_STATUS_IN_USE.equals(fromStatus))
            {
                throw new ServiceException("仅使用中的领用单可以归还");
            }
        }
        // 驳回必须留下原因，便于申请人追溯
        if (USE_STATUS_REJECTED.equals(status) && StringUtils.isEmpty(bizAssetUse.getRemark()))
        {
            throw new ServiceException("驳回时必须填写驳回原因");
        }
        int rows = bizAssetUseMapper.changeUseStatus(bizAssetUse);
        if (rows > 0)
        {
            if (USE_STATUS_IN_USE.equals(status))
            {
                // 审批通过：资产进入在用状态
                bizAssetInfoMapper.updateAssetStatus(dbUse.getAssetId(), ASSET_STATUS_IN_USE);
                insertAssetRecord(dbUse.getAssetId(), RECORD_TYPE_LEND,
                        "领用单审批通过，资产领用出库", ASSET_STATUS_IDLE, ASSET_STATUS_IN_USE, bizAssetUse.getUpdateBy());
                bizMessageService.sendMessage(dbUse.getUserId(), MSG_TYPE_USE_AUDIT,
                        "领用申请已通过", "您申请领用的资产[" + dbUse.getAssetName() + "]已审批通过，请及时领取并爱护使用。");
            }
            else if (USE_STATUS_RETURNED.equals(status))
            {
                // 归还：资产恢复为闲置，可再次领用
                bizAssetInfoMapper.updateAssetStatus(dbUse.getAssetId(), ASSET_STATUS_IDLE);
                insertAssetRecord(dbUse.getAssetId(), RECORD_TYPE_BACK,
                        "资产归还入库", ASSET_STATUS_IN_USE, ASSET_STATUS_IDLE, bizAssetUse.getUpdateBy());
            }
            else if (USE_STATUS_REJECTED.equals(status))
            {
                // 驳回：通知申请人及驳回原因
                bizMessageService.sendMessage(dbUse.getUserId(), MSG_TYPE_USE_AUDIT,
                        "领用申请被驳回",
                        "您申请领用的资产[" + dbUse.getAssetName() + "]被驳回。驳回原因：" + StringUtils.nvl(bizAssetUse.getRemark(), "未填写"));
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
     * 扫描逾期未归还的领用单并发送站内提醒（定时任务调用，每张单只提醒一次）
     *
     * @return 本次提醒条数
     */
    @Override
    public int remindOverdueUse()
    {
        List<BizAssetUse> overdueOrders = bizAssetUseMapper.selectOverdueUseOrders();
        int count = 0;
        for (BizAssetUse useOrder : overdueOrders)
        {
            bizMessageService.sendMessage(useOrder.getUserId(), MSG_TYPE_OVERDUE,
                    "领用资产逾期未归还",
                    "您领用的资产[" + useOrder.getAssetName() + "]已超过预计归还时间（"
                            + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", useOrder.getExpectReturnTime())
                            + "），请尽快归还。");
            bizAssetUseMapper.markOverdueReminded(useOrder.getUseId());
            count++;
        }
        return count;
    }

    /**
     * 批量删除资产领用（逻辑删除）
     *
     * @param useIds 需要删除的领用ID
     * @return 结果
     */
    @Override
    public int deleteBizAssetUseByUseIds(Long[] useIds)
    {
        return bizAssetUseMapper.deleteBizAssetUseByUseIds(useIds);
    }

    /**
     * 删除资产领用信息（逻辑删除）
     *
     * @param useId 领用ID
     * @return 结果
     */
    @Override
    public int deleteBizAssetUseByUseId(Long useId)
    {
        return bizAssetUseMapper.deleteBizAssetUseByUseId(useId);
    }
}
