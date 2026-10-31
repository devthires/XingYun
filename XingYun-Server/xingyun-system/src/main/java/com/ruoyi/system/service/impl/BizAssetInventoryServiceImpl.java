package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizAssetInfo;
import com.ruoyi.system.domain.BizAssetInventory;
import com.ruoyi.system.domain.BizAssetInventoryItem;
import com.ruoyi.system.mapper.BizAssetInfoMapper;
import com.ruoyi.system.mapper.BizAssetInventoryItemMapper;
import com.ruoyi.system.mapper.BizAssetInventoryMapper;
import com.ruoyi.system.service.IBizAssetInventoryService;
import com.ruoyi.system.service.IBizMessageService;

/**
 * 资产盘点Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizAssetInventoryServiceImpl implements IBizAssetInventoryService
{
    /** 盘点状态：盘点中 */
    private static final String INVENTORY_STATUS_DOING = "0";

    /** 盘点状态：已完成 */
    private static final String INVENTORY_STATUS_FINISHED = "1";

    /** 盘点结果：待盘点 */
    private static final String ITEM_RESULT_PENDING = "0";

    /** 盘点结果：正常 */
    private static final String ITEM_RESULT_NORMAL = "1";

    /** 盘点结果：盘亏 */
    private static final String ITEM_RESULT_LOSS = "2";

    @Autowired
    private BizAssetInventoryMapper bizAssetInventoryMapper;

    @Autowired
    private BizAssetInventoryItemMapper bizAssetInventoryItemMapper;

    @Autowired
    private BizAssetInfoMapper bizAssetInfoMapper;

    @Autowired
    private IBizMessageService bizMessageService;

    /**
     * 查询盘点单列表
     *
     * @param bizAssetInventory 盘点单
     * @return 盘点单集合
     */
    @Override
    public List<BizAssetInventory> selectBizAssetInventoryList(BizAssetInventory bizAssetInventory)
    {
        return bizAssetInventoryMapper.selectBizAssetInventoryList(bizAssetInventory);
    }

    /**
     * 查询盘点单详情（含盘点明细与进度统计）
     *
     * @param inventoryId 盘点单ID
     * @return {inventory, items, stats}
     */
    @Override
    public Map<String, Object> selectBizAssetInventoryById(Long inventoryId)
    {
        BizAssetInventory inventory = bizAssetInventoryMapper.selectBizAssetInventoryById(inventoryId);
        if (StringUtils.isNull(inventory))
        {
            throw new ServiceException("盘点任务不存在或已被删除");
        }
        List<BizAssetInventoryItem> items = bizAssetInventoryItemMapper.selectItemsByInventoryId(inventoryId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", items.size());
        stats.put("checked", items.stream().filter(i -> !ITEM_RESULT_PENDING.equals(i.getResult())).count());
        stats.put("normal", items.stream().filter(i -> ITEM_RESULT_NORMAL.equals(i.getResult())).count());
        stats.put("loss", items.stream().filter(i -> ITEM_RESULT_LOSS.equals(i.getResult())).count());

        Map<String, Object> result = new HashMap<>();
        result.put("inventory", inventory);
        result.put("items", items);
        result.put("stats", stats);
        return result;
    }

    /**
     * 创建盘点任务：对全部在册资产建立账面快照
     *
     * @param bizAssetInventory 盘点单
     * @return 结果
     */
    @Override
    @Transactional
    public int createInventory(BizAssetInventory bizAssetInventory)
    {
        // 同时只允许一个进行中的盘点任务，避免盘点结果相互覆盖
        if (bizAssetInventoryMapper.countActiveInventory() > 0)
        {
            throw new ServiceException("已有进行中的盘点任务，请先完成或删除后再创建");
        }
        List<BizAssetInfo> assets = bizAssetInfoMapper.selectBizAssetInfoList(new BizAssetInfo());
        if (assets.isEmpty())
        {
            throw new ServiceException("资产台账为空，无可盘点的资产");
        }
        bizAssetInventory.setInventoryNo("PD" + DateUtils.dateTimeNow("yyyyMMddHHmmss"));
        bizAssetInventory.setStatus(INVENTORY_STATUS_DOING);
        bizAssetInventory.setTotalCount(assets.size());
        bizAssetInventory.setCheckCount(0);
        bizAssetInventory.setNormalCount(0);
        bizAssetInventory.setLossCount(0);
        bizAssetInventory.setCreateTime(DateUtils.getNowDate());
        int rows = bizAssetInventoryMapper.insertBizAssetInventory(bizAssetInventory);
        if (rows > 0)
        {
            // 账面快照：盘点以创建时刻的台账为准，后续台账变更不影响本任务的账面数据
            List<BizAssetInventoryItem> items = new java.util.ArrayList<>();
            for (BizAssetInfo asset : assets)
            {
                BizAssetInventoryItem item = new BizAssetInventoryItem();
                item.setInventoryId(bizAssetInventory.getInventoryId());
                item.setAssetId(asset.getAssetId());
                item.setSnapshotCode(asset.getAssetCode());
                item.setSnapshotName(asset.getAssetName());
                item.setSnapshotLocation(asset.getLocation());
                item.setBookStatus(asset.getStatus());
                item.setResult(ITEM_RESULT_PENDING);
                items.add(item);
            }
            bizAssetInventoryItemMapper.insertItems(items);
        }
        return rows;
    }

    /**
     * 盘点确认（正常/盘亏），同时刷新盘点单进度统计
     *
     * @param item 盘点明细（itemId、result、remark）
     * @return 结果
     */
    @Override
    public int checkItem(BizAssetInventoryItem item)
    {
        if (!ITEM_RESULT_NORMAL.equals(item.getResult()) && !ITEM_RESULT_LOSS.equals(item.getResult()))
        {
            throw new ServiceException("盘点结果只能为：正常或盘亏");
        }
        if (ITEM_RESULT_LOSS.equals(item.getResult()) && StringUtils.isEmpty(item.getRemark()))
        {
            throw new ServiceException("标记盘亏时必须填写差异说明");
        }
        BizAssetInventoryItem dbItem = bizAssetInventoryItemMapper.selectItemById(item.getItemId());
        if (StringUtils.isNull(dbItem))
        {
            throw new ServiceException("盘点明细不存在");
        }
        BizAssetInventory inventory = bizAssetInventoryMapper.selectBizAssetInventoryById(dbItem.getInventoryId());
        if (StringUtils.isNull(inventory) || !INVENTORY_STATUS_DOING.equals(inventory.getStatus()))
        {
            throw new ServiceException("盘点任务不在进行中，无法确认");
        }
        if (!ITEM_RESULT_PENDING.equals(dbItem.getResult()))
        {
            throw new ServiceException("该资产已盘点，请勿重复操作");
        }
        item.setChecker(item.getChecker());
        int rows = bizAssetInventoryItemMapper.updateItemResult(item);
        if (rows > 0)
        {
            refreshInventoryCounts(inventory.getInventoryId(), item.getChecker());
        }
        return rows;
    }

    /**
     * 完成盘点（全部明细确认后方可完成）
     *
     * @param inventoryId 盘点单ID
     * @return 结果
     */
    @Override
    public int finishInventory(Long inventoryId)
    {
        BizAssetInventory inventory = bizAssetInventoryMapper.selectBizAssetInventoryById(inventoryId);
        if (StringUtils.isNull(inventory) || !INVENTORY_STATUS_DOING.equals(inventory.getStatus()))
        {
            throw new ServiceException("盘点任务不存在或已完成");
        }
        List<BizAssetInventoryItem> items = bizAssetInventoryItemMapper.selectItemsByInventoryId(inventoryId);
        long pending = items.stream().filter(i -> ITEM_RESULT_PENDING.equals(i.getResult())).count();
        if (pending > 0)
        {
            throw new ServiceException("还有 " + pending + " 项资产未盘点，无法完成盘点");
        }
        BizAssetInventory update = new BizAssetInventory();
        update.setInventoryId(inventoryId);
        update.setStatus(INVENTORY_STATUS_FINISHED);
        update.setFinishTime(DateUtils.getNowDate());
        int rows = bizAssetInventoryMapper.updateBizAssetInventory(update);
        if (rows > 0)
        {
            // 盘点结果汇总通知任务创建人（以用户名定位收件人，用户不存在时静默跳过）
            bizMessageService.sendMessageByUserName(inventory.getCreateBy(), "3",
                    "盘点任务已完成",
                    "盘点任务[" + inventory.getInventoryName() + "]已完成：应盘 " + inventory.getTotalCount()
                            + "，正常 " + inventory.getNormalCount() + "，盘亏 " + inventory.getLossCount() + "。");
        }
        return rows;
    }

    /**
     * 刷新盘点单进度统计（已盘/正常/盘亏）
     *
     * @param inventoryId 盘点单ID
     * @param operator 操作人
     */
    private void refreshInventoryCounts(Long inventoryId, String operator)
    {
        List<BizAssetInventoryItem> items = bizAssetInventoryItemMapper.selectItemsByInventoryId(inventoryId);
        BizAssetInventory update = new BizAssetInventory();
        update.setInventoryId(inventoryId);
        update.setCheckCount((int) items.stream().filter(i -> !ITEM_RESULT_PENDING.equals(i.getResult())).count());
        update.setNormalCount((int) items.stream().filter(i -> ITEM_RESULT_NORMAL.equals(i.getResult())).count());
        update.setLossCount((int) items.stream().filter(i -> ITEM_RESULT_LOSS.equals(i.getResult())).count());
        update.setUpdateBy(operator);
        bizAssetInventoryMapper.updateBizAssetInventory(update);
    }

    /**
     * 批量删除盘点任务（连同明细）
     *
     * @param inventoryIds 需要删除的盘点单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteBizAssetInventoryByIds(Long[] inventoryIds)
    {
        int rows = bizAssetInventoryMapper.deleteBizAssetInventoryByIds(inventoryIds);
        if (rows > 0)
        {
            bizAssetInventoryItemMapper.deleteItemsByInventoryIds(inventoryIds);
        }
        return rows;
    }
}
