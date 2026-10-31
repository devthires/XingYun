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
import com.ruoyi.system.domain.BizConsumable;
import com.ruoyi.system.domain.BizConsumableRecord;
import com.ruoyi.system.service.IBizConsumableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 耗材库存操作处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/consumable")
@Tag(name = "耗材管理-耗材库存")
public class BizConsumableController extends BaseController
{
    @Autowired
    private IBizConsumableService bizConsumableService;

    /**
     * 查询耗材列表
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:list')")
    @Operation(summary = "查询耗材列表")
    @GetMapping("/list")
    public TableDataInfo list(BizConsumable bizConsumable)
    {
        startPage();
        List<BizConsumable> list = bizConsumableService.selectBizConsumableList(bizConsumable);
        return getDataTable(list);
    }

    /**
     * 查询出入库记录列表（耗材ID/类型/日期范围）
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:query')")
    @Operation(summary = "查询出入库记录列表（耗材ID/类型/日期范围）")
    @GetMapping("/records")
    public TableDataInfo records(BizConsumableRecord bizConsumableRecord)
    {
        startPage();
        List<BizConsumableRecord> list = bizConsumableService.selectConsumableRecordList(bizConsumableRecord);
        return getDataTable(list);
    }

    /**
     * 获取耗材详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:query')")
    @Operation(summary = "获取耗材详细信息")
    @GetMapping(value = "/{consumableId}")
    public AjaxResult getInfo(@PathVariable("consumableId") Long consumableId)
    {
        return success(bizConsumableService.selectBizConsumableById(consumableId));
    }

    /**
     * 新增耗材
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:add')")
    @Log(title = "耗材库存", businessType = BusinessType.INSERT)
    @Operation(summary = "新增耗材")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizConsumable bizConsumable)
    {
        if (!bizConsumableService.checkConsumableCodeUnique(bizConsumable))
        {
            return error("新增耗材'" + bizConsumable.getConsumableName() + "'失败，耗材编码已存在");
        }
        bizConsumable.setCreateBy(getUsername());
        return toAjax(bizConsumableService.insertBizConsumable(bizConsumable));
    }

    /**
     * 修改耗材（基础信息，库存变动走出入库接口）
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:edit')")
    @Log(title = "耗材库存", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改耗材（基础信息，库存变动走出入库接口）")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizConsumable bizConsumable)
    {
        if (!bizConsumableService.checkConsumableCodeUnique(bizConsumable))
        {
            return error("修改耗材'" + bizConsumable.getConsumableName() + "'失败，耗材编码已存在");
        }
        bizConsumable.setUpdateBy(getUsername());
        return toAjax(bizConsumableService.updateBizConsumable(bizConsumable));
    }

    /**
     * 耗材入库
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:edit')")
    @Log(title = "耗材库存", businessType = BusinessType.UPDATE)
    @Operation(summary = "耗材入库")
    @PutMapping("/stockIn")
    public AjaxResult stockIn(@RequestBody BizConsumableRecord record)
    {
        return toAjax(bizConsumableService.stockIn(record.getConsumableId(), record.getQuantity(),
                record.getContent(), getUsername()));
    }

    /**
     * 耗材出库（库存不足会被业务层拦截）
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:edit')")
    @Log(title = "耗材库存", businessType = BusinessType.UPDATE)
    @Operation(summary = "耗材出库（库存不足会被业务层拦截）")
    @PutMapping("/stockOut")
    public AjaxResult stockOut(@RequestBody BizConsumableRecord record)
    {
        return toAjax(bizConsumableService.stockOut(record.getConsumableId(), record.getQuantity(),
                record.getContent(), getUsername()));
    }

    /**
     * 删除耗材
     */
    @PreAuthorize("@ss.hasPermi('biz:consumable:remove')")
    @Log(title = "耗材库存", businessType = BusinessType.DELETE)
    @Operation(summary = "删除耗材")
    @DeleteMapping("/{consumableIds}")
    public AjaxResult remove(@PathVariable Long[] consumableIds)
    {
        return toAjax(bizConsumableService.deleteBizConsumableByIds(consumableIds));
    }
}
