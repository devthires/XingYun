package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizMessage;
import com.ruoyi.system.mapper.BizMessageMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.IBizMessageService;

/**
 * 站内消息Service业务层处理
 * 消息一律以当前登录人为收件人做隔离，防止越权读写他人消息
 *
 * @author ruoyi
 * @date 2026-10-29
 */
@Service
public class BizMessageServiceImpl implements IBizMessageService
{
    @Autowired
    private BizMessageMapper bizMessageMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询当前登录人的消息列表
     *
     * @param bizMessage 查询条件
     * @return 消息集合
     */
    @Override
    public List<BizMessage> selectMyMessages(BizMessage bizMessage)
    {
        bizMessage.setUserId(SecurityUtils.getUserId());
        return bizMessageMapper.selectBizMessageList(bizMessage);
    }

    /**
     * 查询当前登录人未读消息数量
     *
     * @return 数量
     */
    @Override
    public int countMyUnread()
    {
        return bizMessageMapper.countUnread(SecurityUtils.getUserId());
    }

    /**
     * 发送站内消息
     *
     * @param userId 收件人ID
     * @param msgType 消息类型
     * @param title 消息标题
     * @param content 消息内容
     * @return 结果
     */
    @Override
    public int sendMessage(Long userId, String msgType, String title, String content)
    {
        if (StringUtils.isNull(userId))
        {
            return 0;
        }
        BizMessage message = new BizMessage();
        message.setUserId(userId);
        message.setMsgType(msgType);
        message.setTitle(title);
        message.setContent(content);
        return bizMessageMapper.insertBizMessage(message);
    }

    /**
     * 按用户名发送站内消息（收件人以用户名表示时的便捷入口）
     *
     * @param userName 登录账号
     * @param msgType 消息类型
     * @param title 消息标题
     * @param content 消息内容
     * @return 结果
     */
    @Override
    public int sendMessageByUserName(String userName, String msgType, String title, String content)
    {
        if (StringUtils.isEmpty(userName))
        {
            return 0;
        }
        SysUser user = sysUserMapper.selectUserByUserName(userName);
        if (StringUtils.isNull(user))
        {
            return 0;
        }
        return sendMessage(user.getUserId(), msgType, title, content);
    }

    /**
     * 标记消息已读
     *
     * @param messageId 消息ID
     * @return 结果
     */
    @Override
    public int markRead(Long messageId)
    {
        return bizMessageMapper.markRead(messageId, SecurityUtils.getUserId());
    }

    /**
     * 全部已读
     *
     * @return 结果
     */
    @Override
    public int markAllRead()
    {
        return bizMessageMapper.markAllRead(SecurityUtils.getUserId());
    }
}
