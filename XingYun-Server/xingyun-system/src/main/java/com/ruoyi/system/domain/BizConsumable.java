package com.ruoyi.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 耗材库存对象 biz_consumable
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizConsumable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 耗材ID（主键） */
    private Long consumableId;

    /** 耗材编码（唯一） */
    @Excel(name = "耗材编码")
    private String consumableCode;

    /** 耗材名称 */
    @Excel(name = "耗材名称")
    private String consumableName;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String spec;

    /** 计量单位（个/箱/卷/盒） */
    @Excel(name = "计量单位")
    private String unit;

    /** 当前库存数量 */
    @Excel(name = "当前库存")
    private Integer stockQty;

    /** 库存预警阈值（低于该值预警） */
    @Excel(name = "预警阈值")
    private Integer warnQty;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    public Long getConsumableId()
    {
        return consumableId;
    }

    public void setConsumableId(Long consumableId)
    {
        this.consumableId = consumableId;
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

    public String getSpec()
    {
        return spec;
    }

    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public Integer getStockQty()
    {
        return stockQty;
    }

    public void setStockQty(Integer stockQty)
    {
        this.stockQty = stockQty;
    }

    public Integer getWarnQty()
    {
        return warnQty;
    }

    public void setWarnQty(Integer warnQty)
    {
        this.warnQty = warnQty;
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
            .append("consumableId", getConsumableId())
            .append("consumableCode", getConsumableCode())
            .append("consumableName", getConsumableName())
            .append("spec", getSpec())
            .append("unit", getUnit())
            .append("stockQty", getStockQty())
            .append("warnQty", getWarnQty())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
