package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资产领用对象 biz_asset_use
 *
 * @author ruoyi
 * @date 2026-10-28
 */
public class BizAssetUse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 领用ID（主键） */
    private Long useId;

    /** 资产ID（逻辑外键，关联 biz_asset_info.asset_id） */
    @Excel(name = "资产ID")
    private Long assetId;

    /** 领用人ID（逻辑外键，关联 sys_user.user_id） */
    @Excel(name = "领用人ID")
    private Long userId;

    /** 领用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "领用时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    /** 预计归还时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预计归还时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expectReturnTime;

    /** 实际归还时间（状态为已归还时填写） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际归还时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actualReturnTime;

    /** 状态（0审批中 1使用中 2已归还 3已驳回） */
    @Excel(name = "状态", readConverterExp = "0=审批中,1=使用中,2=已归还,3=已驳回")
    private String status;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 资产名称（非表字段，连表 biz_asset_info 查询用） */
    @Excel(name = "资产名称")
    private String assetName;

    /** 资产编码（非表字段，连表 biz_asset_info 查询用） */
    @Excel(name = "资产编码")
    private String assetCode;

    /** 领用人昵称（非表字段，连表 sys_user 查询用） */
    @Excel(name = "领用人")
    private String nickName;

    public Long getUseId()
    {
        return useId;
    }

    public void setUseId(Long useId)
    {
        this.useId = useId;
    }

    @NotNull(message = "领用资产不能为空")
    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    @NotNull(message = "领用人不能为空")
    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Date getUseTime()
    {
        return useTime;
    }

    public void setUseTime(Date useTime)
    {
        this.useTime = useTime;
    }

    public Date getExpectReturnTime()
    {
        return expectReturnTime;
    }

    public void setExpectReturnTime(Date expectReturnTime)
    {
        this.expectReturnTime = expectReturnTime;
    }

    public Date getActualReturnTime()
    {
        return actualReturnTime;
    }

    public void setActualReturnTime(Date actualReturnTime)
    {
        this.actualReturnTime = actualReturnTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("useId", getUseId())
            .append("assetId", getAssetId())
            .append("userId", getUserId())
            .append("useTime", getUseTime())
            .append("expectReturnTime", getExpectReturnTime())
            .append("actualReturnTime", getActualReturnTime())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("assetName", getAssetName())
            .append("assetCode", getAssetCode())
            .append("nickName", getNickName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
