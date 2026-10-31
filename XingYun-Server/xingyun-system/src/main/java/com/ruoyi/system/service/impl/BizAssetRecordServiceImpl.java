package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.BizAssetRecord;
import com.ruoyi.system.mapper.BizAssetRecordMapper;
import com.ruoyi.system.service.IBizAssetRecordService;

/**
 * 资产变更流水Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizAssetRecordServiceImpl implements IBizAssetRecordService
{
    @Autowired
    private BizAssetRecordMapper bizAssetRecordMapper;

    /**
     * 查询资产变更流水列表
     *
     * @param bizAssetRecord 变更流水
     * @return 变更流水
     */
    @Override
    public List<BizAssetRecord> selectBizAssetRecordList(BizAssetRecord bizAssetRecord)
    {
        return bizAssetRecordMapper.selectBizAssetRecordList(bizAssetRecord);
    }

    /**
     * 新增资产变更流水
     *
     * @param bizAssetRecord 变更流水
     * @return 结果
     */
    @Override
    public int insertBizAssetRecord(BizAssetRecord bizAssetRecord)
    {
        return bizAssetRecordMapper.insertBizAssetRecord(bizAssetRecord);
    }
}
