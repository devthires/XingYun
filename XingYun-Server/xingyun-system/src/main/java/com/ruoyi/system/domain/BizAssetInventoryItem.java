package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资产盘点明细对象 biz_asset_inventory_item
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizAssetInventoryItem implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 明细ID（主键） */
    private Long itemId;

    /** 盘点单ID */
    private Long inventoryId;

    /** 资产ID */
    private Long assetId;

    /** 资产编码快照 */
    private String snapshotCode;

    /** 资产名称快照 */
    private String snapshotName;

    /** 存放位置快照 */
    private String snapshotLocation;

    /** 账面状态快照（0闲置 1在用 2维修中 3报废） */
    private String bookStatus;

    /** 盘点结果（0待盘点 1正常 2盘亏） */
    private String result;

    /** 盘点人 */
    private String checker;

    /** 盘点时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /** 差异说明 */
    private String remark;

    /** 资产当前状态（非表字段，连表 biz_asset_info 查询用） */
    private String currentStatus;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getInventoryId()
    {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId)
    {
        this.inventoryId = inventoryId;
    }

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getSnapshotCode()
    {
        return snapshotCode;
    }

    public void setSnapshotCode(String snapshotCode)
    {
        this.snapshotCode = snapshotCode;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName)
    {
        this.snapshotName = snapshotName;
    }

    public String getSnapshotLocation()
    {
        return snapshotLocation;
    }

    public void setSnapshotLocation(String snapshotLocation)
    {
        this.snapshotLocation = snapshotLocation;
    }

    public String getBookStatus()
    {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus)
    {
        this.bookStatus = bookStatus;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getChecker()
    {
        return checker;
    }

    public void setChecker(String checker)
    {
        this.checker = checker;
    }

    public Date getCheckTime()
    {
        return checkTime;
    }

    public void setCheckTime(Date checkTime)
    {
        this.checkTime = checkTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getCurrentStatus()
    {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus)
    {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("inventoryId", getInventoryId())
            .append("assetId", getAssetId())
            .append("snapshotCode", getSnapshotCode())
            .append("snapshotName", getSnapshotName())
            .append("snapshotLocation", getSnapshotLocation())
            .append("bookStatus", getBookStatus())
            .append("result", getResult())
            .append("checker", getChecker())
            .append("checkTime", getCheckTime())
            .append("remark", getRemark())
            .append("currentStatus", getCurrentStatus())
            .toString();
    }
}
