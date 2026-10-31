package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.BizAssetRecord;
import com.ruoyi.system.service.IBizAssetRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 资产变更流水操作处理（只读：流水由各业务动作自动写入，不提供增删改接口）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/record")
@Tag(name = "资产管理-变更流水")
public class BizAssetRecordController extends BaseController
{
    @Autowired
    private IBizAssetRecordService bizAssetRecordService;

    /**
     * 查询资产变更流水列表
     */
    @PreAuthorize("@ss.hasPermi('biz:record:list')")
    @Operation(summary = "查询资产变更流水列表")
    @GetMapping("/list")
    public TableDataInfo list(BizAssetRecord bizAssetRecord)
    {
        startPage();
        List<BizAssetRecord> list = bizAssetRecordService.selectBizAssetRecordList(bizAssetRecord);
        return getDataTable(list);
    }
}
