package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BizAssetInventory;
import com.ruoyi.system.domain.BizAssetInventoryItem;
import com.ruoyi.system.service.IBizAssetInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 资产盘点操作处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/inventory")
@Tag(name = "资产管理-资产盘点")
public class BizAssetInventoryController extends BaseController
{
    @Autowired
    private IBizAssetInventoryService bizAssetInventoryService;

    /**
     * 查询盘点任务列表
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:list')")
    @Operation(summary = "查询盘点任务列表")
    @GetMapping("/list")
    public TableDataInfo list(BizAssetInventory bizAssetInventory)
    {
        startPage();
        List<BizAssetInventory> list = bizAssetInventoryService.selectBizAssetInventoryList(bizAssetInventory);
        return getDataTable(list);
    }

    /**
     * 获取盘点任务详情（含明细与进度统计）
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:query')")
    @Operation(summary = "获取盘点任务详情（含明细与进度统计）")
    @GetMapping(value = "/{inventoryId}")
    public AjaxResult getInfo(@PathVariable("inventoryId") Long inventoryId)
    {
        return success(bizAssetInventoryService.selectBizAssetInventoryById(inventoryId));
    }

    /**
     * 创建盘点任务（对全部在册资产建立账面快照）
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:add')")
    @Log(title = "资产盘点", businessType = BusinessType.INSERT)
    @Operation(summary = "创建盘点任务（对全部在册资产建立账面快照）")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizAssetInventory bizAssetInventory)
    {
        bizAssetInventory.setCreateBy(getUsername());
        return toAjax(bizAssetInventoryService.createInventory(bizAssetInventory));
    }

    /**
     * 盘点确认（正常/盘亏）
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:edit')")
    @Log(title = "资产盘点", businessType = BusinessType.UPDATE)
    @Operation(summary = "盘点确认（正常/盘亏）")
    @PutMapping("/checkItem")
    public AjaxResult checkItem(@RequestBody BizAssetInventoryItem item)
    {
        item.setChecker(getUsername());
        return toAjax(bizAssetInventoryService.checkItem(item));
    }

    /**
     * 完成盘点
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:edit')")
    @Log(title = "资产盘点", businessType = BusinessType.UPDATE)
    @Operation(summary = "完成盘点")
    @PutMapping("/finish/{inventoryId}")
    public AjaxResult finish(@PathVariable("inventoryId") Long inventoryId)
    {
        return toAjax(bizAssetInventoryService.finishInventory(inventoryId));
    }

    /**
     * 删除盘点任务（连同明细）
     */
    @PreAuthorize("@ss.hasPermi('biz:inventory:remove')")
    @Log(title = "资产盘点", businessType = BusinessType.DELETE)
    @Operation(summary = "删除盘点任务（连同明细）")
    @DeleteMapping("/{inventoryIds}")
    public AjaxResult remove(@PathVariable Long[] inventoryIds)
    {
        return toAjax(bizAssetInventoryService.deleteBizAssetInventoryByIds(inventoryIds));
    }
}
