package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizAssetUse;

/**
 * 资产领用Service接口
 *
 * @author ruoyi
 * @date 2026-10-28
 */
public interface IBizAssetUseService
{
    /**
     * 查询资产领用
     *
     * @param useId 领用ID
     * @return 资产领用
     */
    public BizAssetUse selectBizAssetUseByUseId(Long useId);

    /**
     * 查询资产领用列表
     *
     * @param bizAssetUse 资产领用
     * @return 资产领用集合
     */
    public List<BizAssetUse> selectBizAssetUseList(BizAssetUse bizAssetUse);

    /**
     * 新增资产领用
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    public int insertBizAssetUse(BizAssetUse bizAssetUse);

    /**
     * 修改资产领用
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    public int updateBizAssetUse(BizAssetUse bizAssetUse);

    /**
     * 修改领用状态（审批通过/驳回/归还，流转后同步资产状态）
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    public int changeUseStatus(BizAssetUse bizAssetUse);

    /**
     * 批量删除资产领用
     *
     * @param useIds 需要删除的领用ID
     * @return 结果
     */
    public int deleteBizAssetUseByUseIds(Long[] useIds);

    /**
     * 删除资产领用信息
     *
     * @param useId 领用ID
     * @return 结果
     */
    public int deleteBizAssetUseByUseId(Long useId);

    /**
     * 扫描逾期未归还的领用单并发送站内提醒（定时任务调用，每张单只提醒一次）
     *
     * @return 本次提醒条数
     */
    public int remindOverdueUse();
}
