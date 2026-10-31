package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备报修单对象 biz_repair_order
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizRepairOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报修单ID（主键） */
    private Long repairId;

    /** 报修单号（唯一，规则：BX+yyyyMMdd+3位流水，如 BX20260810001） */
    @Excel(name = "报修单号")
    private String repairNo;

    /** 资产ID（关联 biz_asset_info.asset_id） */
    @Excel(name = "资产ID")
    private Long assetId;

    /** 资产名称（非表字段，连表 biz_asset_info 查询用） */
    @Excel(name = "资产名称")
    private String assetName;

    /** 报修人 */
    @Excel(name = "报修人")
    private String repairUser;

    /** 报修人联系电话 */
    @Excel(name = "联系电话")
    private String repairPhone;

    /** 问题描述 */
    @Excel(name = "问题描述")
    private String problemDesc;

    /** 报修状态（0待处理 1处理中 2已完成） */
    @Excel(name = "报修状态", readConverterExp = "0=待处理,1=处理中,2=已完成")
    private String status;

    /** 处理人（维修人员） */
    @Excel(name = "处理人")
    private String handlerName;

    /** 完成时间（状态为已完成时填写） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    public Long getRepairId()
    {
        return repairId;
    }

    public void setRepairId(Long repairId)
    {
        this.repairId = repairId;
    }

    public String getRepairNo()
    {
        return repairNo;
    }

    public void setRepairNo(String repairNo)
    {
        this.repairNo = repairNo;
    }

    @NotNull(message = "报修资产不能为空")
    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    @NotBlank(message = "报修人不能为空")
    @Size(min = 0, max = 64, message = "报修人长度不能超过64个字符")
    public String getRepairUser()
    {
        return repairUser;
    }

    public void setRepairUser(String repairUser)
    {
        this.repairUser = repairUser;
    }

    @Size(min = 0, max = 20, message = "联系电话长度不能超过20个字符")
    public String getRepairPhone()
    {
        return repairPhone;
    }

    public void setRepairPhone(String repairPhone)
    {
        this.repairPhone = repairPhone;
    }

    @NotBlank(message = "问题描述不能为空")
    @Size(min = 0, max = 500, message = "问题描述长度不能超过500个字符")
    public String getProblemDesc()
    {
        return problemDesc;
    }

    public void setProblemDesc(String problemDesc)
    {
        this.problemDesc = problemDesc;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Size(min = 0, max = 64, message = "处理人长度不能超过64个字符")
    public String getHandlerName()
    {
        return handlerName;
    }

    public void setHandlerName(String handlerName)
    {
        this.handlerName = handlerName;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("repairId", getRepairId())
            .append("repairNo", getRepairNo())
            .append("assetId", getAssetId())
            .append("assetName", getAssetName())
            .append("repairUser", getRepairUser())
            .append("repairPhone", getRepairPhone())
            .append("problemDesc", getProblemDesc())
            .append("status", getStatus())
            .append("handlerName", getHandlerName())
            .append("finishTime", getFinishTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
