package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizAssetRecord;

/**
 * 资产变更流水Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizAssetRecordService
{
    /**
     * 查询资产变更流水列表
     *
     * @param bizAssetRecord 变更流水
     * @return 变更流水集合
     */
    public List<BizAssetRecord> selectBizAssetRecordList(BizAssetRecord bizAssetRecord);

    /**
     * 新增资产变更流水（供各业务模块落审计记录）
     *
     * @param bizAssetRecord 变更流水
     * @return 结果
     */
    public int insertBizAssetRecord(BizAssetRecord bizAssetRecord);
}
