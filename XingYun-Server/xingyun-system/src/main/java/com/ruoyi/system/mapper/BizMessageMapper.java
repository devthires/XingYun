package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BizMessage;

/**
 * 站内消息Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public interface BizMessageMapper
{
    /**
     * 查询收件人的消息列表
     *
     * @param bizMessage 查询条件（userId 必传，msgType/readFlag 可选）
     * @return 消息集合
     */
    public List<BizMessage> selectBizMessageList(BizMessage bizMessage);

    /**
     * 统计收件人未读消息数量
     *
     * @param userId 收件人ID
     * @return 数量
     */
    public int countUnread(@Param("userId") Long userId);

    /**
     * 新增站内消息
     *
     * @param bizMessage 消息
     * @return 结果
     */
    public int insertBizMessage(BizMessage bizMessage);

    /**
     * 标记消息已读（校验收件人，防止越权改他人消息）
     *
     * @param messageId 消息ID
     * @param userId 收件人ID
     * @return 结果
     */
    public int markRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 全部已读
     *
     * @param userId 收件人ID
     * @return 结果
     */
    public int markAllRead(@Param("userId") Long userId);
}
