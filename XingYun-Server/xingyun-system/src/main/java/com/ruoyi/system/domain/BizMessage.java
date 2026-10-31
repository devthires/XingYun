package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 站内消息对象 biz_message（按人收件箱）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID（主键） */
    private Long messageId;

    /** 收件人ID（sys_user.user_id） */
    private Long userId;

    /** 消息类型（1领用审批 2逾期提醒 3盘点 4系统通知） */
    private String msgType;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 已读标志（0未读 1已读） */
    private String readFlag;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getMsgType()
    {
        return msgType;
    }

    public void setMsgType(String msgType)
    {
        this.msgType = msgType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getReadFlag()
    {
        return readFlag;
    }

    public void setReadFlag(String readFlag)
    {
        this.readFlag = readFlag;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }
}
