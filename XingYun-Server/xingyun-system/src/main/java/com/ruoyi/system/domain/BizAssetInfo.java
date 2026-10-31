package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资产信息对象 biz_asset_info
 *
 * @author ruoyi
 * @date 2026-10-29
 */
public class BizAssetInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资产ID（主键） */
    private Long assetId;

    /** 资产编码（唯一，留空时后端自动生成） */
    @Excel(name = "资产编码")
    private String assetCode;

    /** 资产名称 */
    @Excel(name = "资产名称")
    private String assetName;

    /** 资产型号/规格 */
    @Excel(name = "资产型号")
    private String assetModel;

    /** 资产分类（字典 biz_asset_category） */
    @Excel(name = "资产分类", dictType = "biz_asset_category")
    private String category;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 采购价格（元） */
    @Excel(name = "采购价格")
    private BigDecimal price;

    /** 采购日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date purchaseDate;

    /** 存放位置 */
    @Excel(name = "存放位置")
    private String location;

    /** 保管责任人ID（关联 sys_user.user_id） */
    private Long ownerId;

    /** 资产图片（相对路径） */
    private String pic;

    /** 资产状态（0闲置 1在用 2维修中 3报废） */
    @Excel(name = "资产状态", readConverterExp = "0=闲置,1=在用,2=维修中,3=报废")
    private String status;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 责任人昵称（非表字段，连表 sys_user 查询用） */
    @Excel(name = "责任人")
    private String ownerName;

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    @NotBlank(message = "资产名称不能为空")
    @Size(min = 0, max = 100, message = "资产名称长度不能超过100个字符")
    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    public String getAssetModel()
    {
        return assetModel;
    }

    public void setAssetModel(String assetModel)
    {
        this.assetModel = assetModel;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Date getPurchaseDate()
    {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate)
    {
        this.purchaseDate = purchaseDate;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public Long getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getPic()
    {
        return pic;
    }

    public void setPic(String pic)
    {
        this.pic = pic;
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

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("assetId", getAssetId())
            .append("assetCode", getAssetCode())
            .append("assetName", getAssetName())
            .append("assetModel", getAssetModel())
            .append("category", getCategory())
            .append("brand", getBrand())
            .append("price", getPrice())
            .append("purchaseDate", getPurchaseDate())
            .append("location", getLocation())
            .append("ownerId", getOwnerId())
            .append("pic", getPic())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("ownerName", getOwnerName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
