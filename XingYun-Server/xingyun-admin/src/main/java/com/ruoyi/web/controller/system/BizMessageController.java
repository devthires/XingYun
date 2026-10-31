package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.BizMessage;
import com.ruoyi.system.service.IBizMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 站内消息操作处理（数据按当前登录人隔离，仅要求登录，不挂业务权限标识）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@RestController
@RequestMapping("/biz/message")
@Tag(name = "消息中心")
public class BizMessageController extends BaseController
{
    @Autowired
    private IBizMessageService bizMessageService;

    /**
     * 查询我的消息列表
     */
    @Operation(summary = "查询我的消息列表")
    @GetMapping("/list")
    public TableDataInfo list(BizMessage bizMessage)
    {
        startPage();
        List<BizMessage> list = bizMessageService.selectMyMessages(bizMessage);
        return getDataTable(list);
    }

    /**
     * 查询我的未读消息数量（导航铃铛角标用）
     */
    @Operation(summary = "查询我的未读消息数量（导航铃铛角标用）")
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount()
    {
        return success(bizMessageService.countMyUnread());
    }

    /**
     * 标记消息已读
     */
    @Operation(summary = "标记消息已读")
    @PutMapping("/read/{messageId}")
    public AjaxResult markRead(@PathVariable("messageId") Long messageId)
    {
        return toAjax(bizMessageService.markRead(messageId));
    }

    /**
     * 全部已读
     */
    @Operation(summary = "全部已读")
    @PutMapping("/readAll")
    public AjaxResult markAllRead()
    {
        return toAjax(bizMessageService.markAllRead());
    }
}
