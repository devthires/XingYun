package com.ruoyi.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.BizRepairOrder;
import com.ruoyi.system.service.IBizRepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 设备报修单操作处理
 *
 * @author ruoyi
 * @date 2026-10-28
 */
@RestController
@RequestMapping("/biz/repair")
@Tag(name = "资产管理-报修单")
public class BizRepairOrderController extends BaseController
{
    @Autowired
    private IBizRepairOrderService bizRepairOrderService;

    /**
     * 查询设备报修单列表（连表返回设备名称，支持按报修状态等条件分页）
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:list')")
    @Operation(summary = "查询设备报修单列表（连表返回设备名称，支持按报修状态等条件分页）")
    @GetMapping("/list")
    public TableDataInfo list(BizRepairOrder bizRepairOrder)
    {
        startPage();
        List<BizRepairOrder> list = bizRepairOrderService.selectBizRepairOrderList(bizRepairOrder);
        return getDataTable(list);
    }

    /**
     * 导出设备报修单列表
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:export')")
    @Log(title = "设备报修单", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出设备报修单列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizRepairOrder bizRepairOrder)
    {
        List<BizRepairOrder> list = bizRepairOrderService.selectBizRepairOrderList(bizRepairOrder);
        ExcelUtil<BizRepairOrder> util = new ExcelUtil<BizRepairOrder>(BizRepairOrder.class);
        util.exportExcel(response, list, "设备报修单数据");
    }

    /**
     * 获取设备报修单详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:query')")
    @Operation(summary = "获取设备报修单详细信息")
    @GetMapping(value = "/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Long repairId)
    {
        return success(bizRepairOrderService.selectBizRepairOrderByRepairId(repairId));
    }

    /**
     * 新增设备报修单（设备正在维修中时会被业务层拦截）
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:add')")
    @Log(title = "设备报修单", businessType = BusinessType.INSERT)
    @Operation(summary = "新增设备报修单（设备正在维修中时会被业务层拦截）")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizRepairOrder bizRepairOrder)
    {
        bizRepairOrder.setCreateBy(getUsername());
        return toAjax(bizRepairOrderService.insertBizRepairOrder(bizRepairOrder));
    }

    /**
     * 修改设备报修单
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:edit')")
    @Log(title = "设备报修单", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改设备报修单")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizRepairOrder bizRepairOrder)
    {
        bizRepairOrder.setUpdateBy(getUsername());
        return toAjax(bizRepairOrderService.updateBizRepairOrder(bizRepairOrder));
    }

    /**
     * 修改报修单状态（待处理/处理中/已完成，流转后同步设备状态）
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:edit')")
    @Log(title = "设备报修单", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改报修单状态（待处理/处理中/已完成，流转后同步设备状态）")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BizRepairOrder bizRepairOrder)
    {
        bizRepairOrder.setUpdateBy(getUsername());
        return toAjax(bizRepairOrderService.changeRepairStatus(bizRepairOrder));
    }

    /**
     * 删除设备报修单
     */
    @PreAuthorize("@ss.hasPermi('biz:repair:remove')")
    @Log(title = "设备报修单", businessType = BusinessType.DELETE)
    @Operation(summary = "删除设备报修单")
    @DeleteMapping("/{repairIds}")
    public AjaxResult remove(@PathVariable Long[] repairIds)
    {
        return toAjax(bizRepairOrderService.deleteBizRepairOrderByRepairIds(repairIds));
    }
}
