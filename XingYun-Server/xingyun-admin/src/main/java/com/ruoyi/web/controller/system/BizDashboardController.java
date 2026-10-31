package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IBizStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 数据看板操作处理（仅要求登录，不挂业务权限标识）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/dashboard")
@Tag(name = "首页-数据看板")
public class BizDashboardController extends BaseController
{
    @Autowired
    private IBizStatService bizStatService;

    /**
     * 看板汇总：卡片数字 + 资产状态分布 + 近7天报修趋势 + 最近报修记录
     */
    @Operation(summary = "看板汇总：卡片数字 + 资产状态分布 + 近7天报修趋势 + 最近报修记录")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(bizStatService.getDashboardSummary());
    }

    /**
     * 数据大屏：分类分布 + 领用状态分布 + 近30天报修趋势 + 资产总值 + 低库存耗材
     */
    @Operation(summary = "数据大屏：分类分布 + 领用状态分布 + 近30天报修趋势 + 资产总值 + 低库存耗材")
    @GetMapping("/bigscreen")
    public AjaxResult bigscreen()
    {
        return success(bizStatService.getBigScreenData());
    }
}
