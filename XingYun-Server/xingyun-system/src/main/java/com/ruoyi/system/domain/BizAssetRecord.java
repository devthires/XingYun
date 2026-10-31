package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资产变更流水对象 biz_asset_record（只增不改的审计表）
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizAssetRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流水ID（主键） */
    private Long recordId;

    /** 资产ID */
    private Long assetId;

    /** 变更类型（1入库 2领用出库 3归还入库 4送修 5维修完成 6变更 7注销） */
    private String recordType;

    /** 变更说明 */
    private String content;

    /** 变更前资产状态 */
    private String beforeStatus;

    /** 变更后资产状态 */
    private String afterStatus;

    /** 操作人 */
    private String operator;

    /** 资产编码（非表字段，连表 biz_asset_info 查询用） */
    private String assetCode;

    /** 资产名称（非表字段，连表 biz_asset_info 查询用） */
    private String assetName;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getBeforeStatus()
    {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus)
    {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus()
    {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus)
    {
        this.afterStatus = afterStatus;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("assetId", getAssetId())
            .append("recordType", getRecordType())
            .append("content", getContent())
            .append("beforeStatus", getBeforeStatus())
            .append("afterStatus", getAfterStatus())
            .append("operator", getOperator())
            .append("createTime", getCreateTime())
            .append("assetCode", getAssetCode())
            .append("assetName", getAssetName())
            .toString();
    }
}
