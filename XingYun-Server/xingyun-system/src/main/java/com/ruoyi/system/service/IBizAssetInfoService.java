package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizAssetInfo;

/**
 * 资产信息Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizAssetInfoService
{
    /**
     * 查询资产信息
     *
     * @param assetId 资产ID
     * @return 资产信息
     */
    public BizAssetInfo selectBizAssetInfoByAssetId(Long assetId);

    /**
     * 查询资产信息列表
     *
     * @param bizAssetInfo 资产信息
     * @return 资产信息集合
     */
    public List<BizAssetInfo> selectBizAssetInfoList(BizAssetInfo bizAssetInfo);

    /**
     * 校验资产编码是否唯一
     *
     * @param bizAssetInfo 资产信息
     * @return 结果（true 唯一）
     */
    public boolean checkAssetCodeUnique(BizAssetInfo bizAssetInfo);

    /**
     * 新增资产信息
     *
     * @param bizAssetInfo 资产信息
     * @return 结果
     */
    public int insertBizAssetInfo(BizAssetInfo bizAssetInfo);

    /**
     * 修改资产信息
     *
     * @param bizAssetInfo 资产信息
     * @return 结果
     */
    public int updateBizAssetInfo(BizAssetInfo bizAssetInfo);

    /**
     * 批量删除资产信息
     *
     * @param assetIds 需要删除的资产ID
     * @return 结果
     */
    public int deleteBizAssetInfoByAssetIds(Long[] assetIds);

    /**
     * 删除资产信息
     *
     * @param assetId 资产ID
     * @return 结果
     */
    public int deleteBizAssetInfoByAssetId(Long assetId);
}
