package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.BizAssetUse;

/**
 * 资产领用Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-28
 */
public interface BizAssetUseMapper
{
    /**
     * 查询资产领用
     *
     * @param useId 领用ID
     * @return 资产领用
     */
    public BizAssetUse selectBizAssetUseByUseId(Long useId);

    /**
     * 查询资产领用列表（连表 biz_asset_info / sys_user，支持按状态等条件分页查询）
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
     * 修改领用状态（已归还时自动维护实际归还时间，回退时清空）
     *
     * @param bizAssetUse 资产领用
     * @return 结果
     */
    public int changeUseStatus(BizAssetUse bizAssetUse);

    /**
     * 删除资产领用（逻辑删除）
     *
     * @param useId 领用ID
     * @return 结果
     */
    public int deleteBizAssetUseByUseId(Long useId);

    /**
     * 批量删除资产领用（逻辑删除）
     *
     * @param useIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAssetUseByUseIds(Long[] useIds);

    /**
     * 统计资产进行中的领用申请数量（状态：审批中/使用中）
     *
     * @param assetId 资产ID
     * @return 数量
     */
    public int countActiveUseByAssetId(Long assetId);

    /**
     * 查询逾期未归还且未提醒过的领用单（定时任务用）
     *
     * @return 领用单集合
     */
    public List<BizAssetUse> selectOverdueUseOrders();

    /**
     * 标记领用单已发送逾期提醒
     *
     * @param useId 领用单ID
     * @return 结果
     */
    public int markOverdueReminded(Long useId);
}
