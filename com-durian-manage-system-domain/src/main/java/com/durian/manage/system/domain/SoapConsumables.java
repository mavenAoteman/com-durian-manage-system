package com.durian.manage.system.domain;

import com.durian.manage.system.common.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料表(SoapConsumables)实体类
 *
 * @author maven12
 * @since 2025-05-26 20:34:33
 */
public class SoapConsumables implements Serializable {
    private static final long serialVersionUID = 438563024300177894L;

    /**
     * 自增的主键，用于唯一标识每个物料
     */
    private Integer id;
    /**
     * 数据归属用户ID
     */
    private Long ownerUserId;
    /**
     * 物料名称
     */
    private String name;
    /**
     * 物料的品牌名称
     */
    private String brand;
    /**
     * 物料的数量(支持小数)
     */
    private Double quantity;
    /**
     * 物料的单价
     */
    private Double unitPrice;

    /**
     * 单位-默认是克/毫升
     */
    private String unit;

    /**
     * 物料类型（0: 其它, 1: 油脂, 2: 精油, 3: 添加物, 4: 水相）
     * @see com.durian.manage.system.domain.ConsumableTypeEnum
     */
    private Integer consumableType;
    /**
     * 换算克重（数量×单位换算系数），计数单位为null
     */
    private Double weightInGrams;
    /**
     * 物料的总价
     */
    private Double totalPrice;
    /**
     * 状态（0：已删除 1: 可用, 2: 已用完, 3: 已过期）
     */
    private Integer status;
    /**
     * 物料的过期日期
     */
    private Date expiryDate;
    /**
     * 物料记录的创建时间
     */
    private Date created;
    /**
     * 物料记录的更新时间
     */
    private Date modified;
    /**
     * 添加该物料的用户姓名
     */
    private String createdBy;
    /**
     * 最后更新该物料的用户姓名
     */
    private String updatedBy;
    
    /**
     * 采购渠道
     */
    private String purchaseChannel;
    
    /**
     * 采购时间
     */
    private Date purchaseTime;

    /**
     * 记录的创建时间
     */
    private String createdStr;

    /**
     * 记录的创建时间
     */
    private String modifiedStr;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getWeightInGrams() { return weightInGrams; }
    public void setWeightInGrams(Double weightInGrams) { this.weightInGrams = weightInGrams; }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
        this.createdStr = DateUtils.dateToString(created);
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
        this.modifiedStr = DateUtils.dateToString(modified);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedStr() {
        return createdStr;
    }

    public void setCreatedStr(String createdStr) {
        this.createdStr = createdStr;
    }

    public String getModifiedStr() {
        return modifiedStr;
    }

    public void setModifiedStr(String modifiedStr) {
        this.modifiedStr = modifiedStr;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getConsumableType() {
        return consumableType;
    }

    public void setConsumableType(Integer consumableType) {
        this.consumableType = consumableType;
    }
    
    public String getPurchaseChannel() {
        return purchaseChannel;
    }
    
    public void setPurchaseChannel(String purchaseChannel) {
        this.purchaseChannel = purchaseChannel;
    }
    
    public Date getPurchaseTime() {
        return purchaseTime;
    }
    
    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }
}