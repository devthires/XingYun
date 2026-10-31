package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BizAssetInfo;

/**
 * 资产信息Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizAssetInfoMapper
{
    /**
     * 查询资产信息
     *
     * @param assetId 资产ID
     * @return 资产信息（资产不存在或已删除时返回 null）
     */
    public BizAssetInfo selectBizAssetInfoByAssetId(Long assetId);

    /**
     * 查询资产信息列表（连表 sys_user 带出责任人昵称）
     *
     * @param bizAssetInfo 资产信息
     * @return 资产信息集合
     */
    public List<BizAssetInfo> selectBizAssetInfoList(BizAssetInfo bizAssetInfo);

    /**
     * 按编码查询资产（编码唯一性校验用）
     *
     * @param assetCode 资产编码
     * @return 资产信息
     */
    public BizAssetInfo checkAssetCodeUnique(String assetCode);

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
     * 删除资产信息（逻辑删除）
     *
     * @param assetId 资产ID
     * @return 结果
     */
    public int deleteBizAssetInfoByAssetId(Long assetId);

    /**
     * 批量删除资产信息（逻辑删除）
     *
     * @param assetIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAssetInfoByAssetIds(Long[] assetIds);

    /**
     * 同步修改资产状态
     *
     * @param assetId 资产ID
     * @param status 资产状态
     * @return 结果
     */
    public int updateAssetStatus(@Param("assetId") Long assetId, @Param("status") String status);

    /**
     * 统计指定前缀的资产编码已占用数量（自动编码用，含已删除数据防止冲突）
     *
     * @param assetCodePrefix 资产编码前缀
     * @return 数量
     */
    public int countAssetCodeByPrefix(String assetCodePrefix);
}
