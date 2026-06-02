package com.durian.manage.system.domain;

import com.durian.manage.system.common.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * (SoapTools)实体类
 *
 * @author makejava
 * @since 2024-10-29 20:41:50
 */
public class SoapTools implements Serializable {
    private static final long serialVersionUID = -55109487952023703L;
    /**
     * 自增的主键，用于唯一标识每个工具
     */
    private Integer id;
    /**
     * 数据归属用户ID
     */
    private Long ownerUserId;
    /**
     * 工具名称
     */
    private String name;
    /**
     * 工具的品牌名称
     */
    private String brand;
    /**
     * 工具的数量
     */
    private Integer quantity;
    /**
     * 工具的单价
     */
    private Double unitPrice;
    /**
     * 工具的总价
     */
    private Double totalPrice;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 工具记录的创建时间
     */
    private Date created;
    /**
     * 工具记录的更新时间
     */
    private Date modified;
    /**
     * 添加该工具的用户姓名
     */
    private String createdBy;
    /**
     * 最后更新该工具的用户姓名
     */
    private String updatedBy;

    /**
     * 工具记录的创建时间
     */
    private String createdStr;

    /**
     * 工具记录的创建时间
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

