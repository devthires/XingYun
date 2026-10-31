package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 耗材出入库记录对象 biz_consumable_record
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizConsumableRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID（主键） */
    private Long recordId;

    /** 耗材ID */
    private Long consumableId;

    /** 记录类型（1入库 2出库） */
    private String recordType;

    /** 变动数量 */
    private Integer quantity;

    /** 变动前库存 */
    private Integer beforeQty;

    /** 变动后库存 */
    private Integer afterQty;

    /** 说明 */
    private String content;

    /** 操作人 */
    private String operator;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 耗材编码（非表字段，连表查询用） */
    private String consumableCode;

    /** 耗材名称（非表字段，连表查询用） */
    private String consumableName;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getConsumableId()
    {
        return consumableId;
    }

    public void setConsumableId(Long consumableId)
    {
        this.consumableId = consumableId;
    }

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public Integer getBeforeQty()
    {
        return beforeQty;
    }

    public void setBeforeQty(Integer beforeQty)
    {
        this.beforeQty = beforeQty;
    }

    public Integer getAfterQty()
    {
        return afterQty;
    }

    public void setAfterQty(Integer afterQty)
    {
        this.afterQty = afterQty;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getConsumableCode()
    {
        return consumableCode;
    }

    public void setConsumableCode(String consumableCode)
    {
        this.consumableCode = consumableCode;
    }

    public String getConsumableName()
    {
        return consumableName;
    }

    public void setConsumableName(String consumableName)
    {
        this.consumableName = consumableName;
    }
}
