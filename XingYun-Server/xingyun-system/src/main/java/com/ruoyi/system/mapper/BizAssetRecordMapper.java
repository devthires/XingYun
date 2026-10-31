package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BizAssetRecord;

/**
 * 资产变更流水Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizAssetRecordMapper
{
    /**
     * 查询资产变更流水列表（连表 biz_asset_info 带出资产编码/名称）
     *
     * @param bizAssetRecord 变更流水
     * @return 变更流水集合
     */
    public List<BizAssetRecord> selectBizAssetRecordList(BizAssetRecord bizAssetRecord);

    /**
     * 新增资产变更流水
     *
     * @param bizAssetRecord 变更流水
     * @return 结果
     */
    public int insertBizAssetRecord(BizAssetRecord bizAssetRecord);

    /**
     * 统计资产的历史流水数量（资产删除保护用）
     *
     * @param assetId 资产ID
     * @return 数量
     */
    public int countRecordByAssetId(@Param("assetId") Long assetId);
}
