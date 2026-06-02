package com.durian.manage.system.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SoapPackage implements Serializable {
    private static final long serialVersionUID = 8901234567890123456L;

    private Integer id;
    /** 数据归属用户ID */
    private Long ownerUserId;
    private String batchNumber;
    /** 1=待内包装, 2=待外包装, 3=已完成 */
    private Integer status;
    private Double totalMaterialCost;
    private String notes;
    private Date created;
    private Date modified;

    private transient List<SoapPackagePiece> pieces;
    private transient List<SoapPackageMaterial> materials;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Double getTotalMaterialCost() { return totalMaterialCost; }
    public void setTotalMaterialCost(Double totalMaterialCost) { this.totalMaterialCost = totalMaterialCost; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }
    public Date getModified() { return modified; }
    public void setModified(Date modified) { this.modified = modified; }
    public List<SoapPackagePiece> getPieces() { return pieces; }
    public void setPieces(List<SoapPackagePiece> pieces) { this.pieces = pieces; }
    public List<SoapPackageMaterial> getMaterials() { return materials; }
    public void setMaterials(List<SoapPackageMaterial> materials) { this.materials = materials; }
}
