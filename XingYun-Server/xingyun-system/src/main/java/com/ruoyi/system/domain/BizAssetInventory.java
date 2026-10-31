package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资产盘点单对象 biz_asset_inventory
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizAssetInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 盘点单ID（主键） */
    private Long inventoryId;

    /** 盘点单号（PD+yyyyMMddHHmmss） */
    @Excel(name = "盘点单号")
    private String inventoryNo;

    /** 盘点任务名称 */
    @Excel(name = "盘点任务名称")
    private String inventoryName;

    /** 盘点状态（0盘点中 1已完成） */
    @Excel(name = "盘点状态", readConverterExp = "0=盘点中,1=已完成")
    private String status;

    /** 应盘总数 */
    @Excel(name = "应盘总数")
    private Integer totalCount;

    /** 已盘数量 */
    @Excel(name = "已盘数量")
    private Integer checkCount;

    /** 正常数量 */
    @Excel(name = "正常数量")
    private Integer normalCount;

    /** 盘亏数量 */
    @Excel(name = "盘亏数量")
    private Integer lossCount;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    public Long getInventoryId()
    {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId)
    {
        this.inventoryId = inventoryId;
    }

    public String getInventoryNo()
    {
        return inventoryNo;
    }

    public void setInventoryNo(String inventoryNo)
    {
        this.inventoryNo = inventoryNo;
    }

    @NotBlank(message = "盘点任务名称不能为空")
    @Size(min = 0, max = 100, message = "盘点任务名称长度不能超过100个字符")
    public String getInventoryName()
    {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName)
    {
        this.inventoryName = inventoryName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getCheckCount()
    {
        return checkCount;
    }

    public void setCheckCount(Integer checkCount)
    {
        this.checkCount = checkCount;
    }

    public Integer getNormalCount()
    {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount)
    {
        this.normalCount = normalCount;
    }

    public Integer getLossCount()
    {
        return lossCount;
    }

    public void setLossCount(Integer lossCount)
    {
        this.lossCount = lossCount;
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
            .append("inventoryId", getInventoryId())
            .append("inventoryNo", getInventoryNo())
            .append("inventoryName", getInventoryName())
            .append("status", getStatus())
            .append("totalCount", getTotalCount())
            .append("checkCount", getCheckCount())
            .append("normalCount", getNormalCount())
            .append("lossCount", getLossCount())
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
