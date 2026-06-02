package com.durian.manage.system.domain;

import java.io.Serializable;

public class SoapPackageMaterial implements Serializable {
    private static final long serialVersionUID = 123456789012345678L;

    private Integer id;
    private Integer packageId;
    private Integer consumableId;
    /** inner=内包装, outer=外包装 */
    private String materialType;
    private Double quantityUsed;
    private Double unitPrice;
    private String unit;

    private transient String consumableName;
    private transient String consumableBrand;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Integer getConsumableId() { return consumableId; }
    public void setConsumableId(Integer consumableId) { this.consumableId = consumableId; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public Double getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(Double quantityUsed) { this.quantityUsed = quantityUsed; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getConsumableName() { return consumableName; }
    public void setConsumableName(String consumableName) { this.consumableName = consumableName; }
    public String getConsumableBrand() { return consumableBrand; }
    public void setConsumableBrand(String consumableBrand) { this.consumableBrand = consumableBrand; }
}
