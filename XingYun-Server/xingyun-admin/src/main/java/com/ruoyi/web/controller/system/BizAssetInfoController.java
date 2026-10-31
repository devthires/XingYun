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
import com.ruoyi.system.domain.BizAssetInfo;
import com.ruoyi.system.service.IBizAssetInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 资产信息操作处理
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/asset")
@Tag(name = "资产管理-资产台账")
public class BizAssetInfoController extends BaseController
{
    @Autowired
    private IBizAssetInfoService bizAssetInfoService;

    /**
     * 查询资产列表（连表返回责任人昵称）
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:list')")
    @Operation(summary = "查询资产列表（连表返回责任人昵称）")
    @GetMapping("/list")
    public TableDataInfo list(BizAssetInfo bizAssetInfo)
    {
        startPage();
        List<BizAssetInfo> list = bizAssetInfoService.selectBizAssetInfoList(bizAssetInfo);
        return getDataTable(list);
    }

    /**
     * 导出资产列表
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:export')")
    @Log(title = "资产管理", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出资产列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAssetInfo bizAssetInfo)
    {
        List<BizAssetInfo> list = bizAssetInfoService.selectBizAssetInfoList(bizAssetInfo);
        ExcelUtil<BizAssetInfo> util = new ExcelUtil<BizAssetInfo>(BizAssetInfo.class);
        util.exportExcel(response, list, "资产台账数据");
    }

    /**
     * 获取资产详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:query')")
    @Operation(summary = "获取资产详细信息")
    @GetMapping(value = "/{assetId}")
    public AjaxResult getInfo(@PathVariable("assetId") Long assetId)
    {
        return success(bizAssetInfoService.selectBizAssetInfoByAssetId(assetId));
    }

    /**
     * 新增资产（编码留空时后端自动生成）
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:add')")
    @Log(title = "资产管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增资产（编码留空时后端自动生成）")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizAssetInfo bizAssetInfo)
    {
        if (!bizAssetInfoService.checkAssetCodeUnique(bizAssetInfo))
        {
            return error("新增资产'" + bizAssetInfo.getAssetName() + "'失败，资产编码已存在");
        }
        bizAssetInfo.setCreateBy(getUsername());
        return toAjax(bizAssetInfoService.insertBizAssetInfo(bizAssetInfo));
    }

    /**
     * 修改资产
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:edit')")
    @Log(title = "资产管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改资产")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizAssetInfo bizAssetInfo)
    {
        if (!bizAssetInfoService.checkAssetCodeUnique(bizAssetInfo))
        {
            return error("修改资产'" + bizAssetInfo.getAssetName() + "'失败，资产编码已存在");
        }
        bizAssetInfo.setUpdateBy(getUsername());
        return toAjax(bizAssetInfoService.updateBizAssetInfo(bizAssetInfo));
    }

    /**
     * 删除资产（在用或存在进行中领用申请的资产会被业务层拦截）
     */
    @PreAuthorize("@ss.hasPermi('biz:asset:remove')")
    @Log(title = "资产管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除资产（在用或存在进行中领用申请的资产会被业务层拦截）")
    @DeleteMapping("/{assetIds}")
    public AjaxResult remove(@PathVariable Long[] assetIds)
    {
        return toAjax(bizAssetInfoService.deleteBizAssetInfoByAssetIds(assetIds));
    }
}
