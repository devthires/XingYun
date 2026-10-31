package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BizMessage;

/**
 * 站内消息Service接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface IBizMessageService
{
    /**
     * 查询当前登录人的消息列表
     *
     * @param bizMessage 查询条件（userId 以当前登录人为准）
     * @return 消息集合
     */
    public List<BizMessage> selectMyMessages(BizMessage bizMessage);

    /**
     * 查询当前登录人未读消息数量
     *
     * @return 数量
     */
    public int countMyUnread();

    /**
     * 发送站内消息（供各业务动作调用）
     *
     * @param userId 收件人ID
     * @param msgType 消息类型（1领用审批 2逾期提醒 3盘点 4系统通知）
     * @param title 消息标题
     * @param content 消息内容
     * @return 结果
     */
    public int sendMessage(Long userId, String msgType, String title, String content);

    /**
     * 按登录账号发送站内消息（收件人以用户名表示时的便捷入口，账号不存在时静默跳过）
     *
     * @param userName 登录账号
     * @param msgType 消息类型
     * @param title 消息标题
     * @param content 消息内容
     * @return 结果
     */
    public int sendMessageByUserName(String userName, String msgType, String title, String content);

    /**
     * 标记消息已读
     *
     * @param messageId 消息ID
     * @return 结果
     */
    public int markRead(Long messageId);

    /**
     * 全部已读
     *
     * @return 结果
     */
    public int markAllRead();
}
