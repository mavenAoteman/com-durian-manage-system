package com.durian.manage.system.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * 物料使用明细(SoapConsumablesUsage)实体类
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public class SoapConsumablesUsage implements Serializable {
    private static final long serialVersionUID = -74628910642674611L;
    
    private Integer id;
    /**
     * 关联皂的生产记录ID
     */
    private Integer soapId;
    /**
     * 关联物料ID
     */
    private Integer consumableId;
    /**
     * 物料使用量（支持小数，如0.5升）
     */
    private Double quantityUsed;
    /**
     * 使用时的物料单价（快照）
     */
    private Double unitPrice;
    /**
     * 使用时的单位
     */
    private String unit;

    private Date created;

    // ---- 以下为 JOIN 查询的 transient 展示字段 ----
    private transient String consumableName;
    private transient String consumableBrand;
    private transient String productionName;
    private transient String productionBatchNumber;
    /** 小计（克重 × 克单价），由后端计算填充 */
    private transient Double subtotal;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSoapId() {
        return soapId;
    }

    public void setSoapId(Integer soapId) {
        this.soapId = soapId;
    }

    public Integer getConsumableId() {
        return consumableId;
    }

    public void setConsumableId(Integer consumableId) {
        this.consumableId = consumableId;
    }

    public Double getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Double quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getConsumableName() { return consumableName; }
    public void setConsumableName(String consumableName) { this.consumableName = consumableName; }

    public String getConsumableBrand() { return consumableBrand; }
    public void setConsumableBrand(String consumableBrand) { this.consumableBrand = consumableBrand; }

    public String getProductionName() { return productionName; }
    public void setProductionName(String productionName) { this.productionName = productionName; }

    public String getProductionBatchNumber() { return productionBatchNumber; }
    public void setProductionBatchNumber(String productionBatchNumber) { this.productionBatchNumber = productionBatchNumber; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

}

