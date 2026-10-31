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
import com.ruoyi.system.domain.BizAssetUse;
import com.ruoyi.system.service.IBizAssetUseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 资产领用操作处理
 *
 * @author ruoyi
 * @date 2026-10-28
 */
@RestController
@RequestMapping("/biz/use")
@Tag(name = "资产管理-资产领用")
public class BizAssetUseController extends BaseController
{
    @Autowired
    private IBizAssetUseService bizAssetUseService;

    /**
     * 查询资产领用列表（连表返回资产名称/编码、领用人昵称，支持按状态等条件分页）
     */
    @PreAuthorize("@ss.hasPermi('biz:use:list')")
    @Operation(summary = "查询资产领用列表（连表返回资产名称/编码、领用人昵称，支持按状态等条件分页）")
    @GetMapping("/list")
    public TableDataInfo list(BizAssetUse bizAssetUse)
    {
        startPage();
        List<BizAssetUse> list = bizAssetUseService.selectBizAssetUseList(bizAssetUse);
        return getDataTable(list);
    }

    /**
     * 导出资产领用列表
     */
    @PreAuthorize("@ss.hasPermi('biz:use:export')")
    @Log(title = "资产领用", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出资产领用列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAssetUse bizAssetUse)
    {
        List<BizAssetUse> list = bizAssetUseService.selectBizAssetUseList(bizAssetUse);
        ExcelUtil<BizAssetUse> util = new ExcelUtil<BizAssetUse>(BizAssetUse.class);
        util.exportExcel(response, list, "资产领用数据");
    }

    /**
     * 获取资产领用详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:use:query')")
    @Operation(summary = "获取资产领用详细信息")
    @GetMapping(value = "/{useId}")
    public AjaxResult getInfo(@PathVariable("useId") Long useId)
    {
        return success(bizAssetUseService.selectBizAssetUseByUseId(useId));
    }

    /**
     * 新增资产领用（资产非闲置时会被业务层拦截）
     */
    @PreAuthorize("@ss.hasPermi('biz:use:add')")
    @Log(title = "资产领用", businessType = BusinessType.INSERT)
    @Operation(summary = "新增资产领用（资产非闲置时会被业务层拦截）")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizAssetUse bizAssetUse)
    {
        bizAssetUse.setCreateBy(getUsername());
        return toAjax(bizAssetUseService.insertBizAssetUse(bizAssetUse));
    }

    /**
     * 修改资产领用
     */
    @PreAuthorize("@ss.hasPermi('biz:use:edit')")
    @Log(title = "资产领用", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改资产领用")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizAssetUse bizAssetUse)
    {
        bizAssetUse.setUpdateBy(getUsername());
        return toAjax(bizAssetUseService.updateBizAssetUse(bizAssetUse));
    }

    /**
     * 修改领用状态（审批通过/归还/驳回，流转后同步资产状态）
     */
    @PreAuthorize("@ss.hasPermi('biz:use:edit')")
    @Log(title = "资产领用", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改领用状态（审批通过/归还/驳回，流转后同步资产状态）")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BizAssetUse bizAssetUse)
    {
        bizAssetUse.setUpdateBy(getUsername());
        return toAjax(bizAssetUseService.changeUseStatus(bizAssetUse));
    }

    /**
     * 删除资产领用
     */
    @PreAuthorize("@ss.hasPermi('biz:use:remove')")
    @Log(title = "资产领用", businessType = BusinessType.DELETE)
    @Operation(summary = "删除资产领用")
    @DeleteMapping("/{useIds}")
    public AjaxResult remove(@PathVariable Long[] useIds)
    {
        return toAjax(bizAssetUseService.deleteBizAssetUseByUseIds(useIds));
    }
}
