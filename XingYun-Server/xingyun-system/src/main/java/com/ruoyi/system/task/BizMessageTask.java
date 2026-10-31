package com.ruoyi.system.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.IBizAssetUseService;

/**
 * 资产业务定时任务（供 sys_job 定时任务调用）
 * 说明：任务壳放在 ruoyi-system —— ruoyi-quartz 模块只依赖 ruoyi-common，
 * 若把任务类放 quartz 模块将无法注入业务 Service（模块依赖方向不允许）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Component("bizMessageTask")
public class BizMessageTask
{
    private static final Logger log = LoggerFactory.getLogger(BizMessageTask.class);

    @Autowired
    private IBizAssetUseService bizAssetUseService;

    /**
     * 扫描逾期未归还的领用单，向领用人发送站内提醒（每张单只提醒一次）
     * 对应 sys_job：invokeTarget = bizMessageTask.remindOverdueAssetUse
     */
    public void remindOverdueAssetUse()
    {
        int reminded = bizAssetUseService.remindOverdueUse();
        log.info("【资产领用逾期提醒】本次共发送 {} 条逾期提醒", reminded);
    }
}
