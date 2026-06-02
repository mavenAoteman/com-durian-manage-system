package com.durian.manage.system.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * 手工皂生产记录(SoapProduction)实体类
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public class SoapProduction implements Serializable {
    private static final long serialVersionUID = -22344251408331355L;
    /**
     * 自增主键
     */
    private Integer id;
    /**
     * 数据归属用户ID
     */
    private Long ownerUserId;
    /**
     * 皂的名称
     */
    private String name;
    /**
     * 生产批次号（唯一）
     */
    private String batchNumber;
    /**
     * 生产日期
     */
    private Date productionDate;
    /**
     * 状态（1: 生产中, 2: 成熟期, 3: 已成熟, 4: 已切块入库）
     */
    private Integer status;
    /**
     * 总成本（自动计算）
     */
    private Double totalCost;
    /**
     * 入模总重量(g)
     */
    private Double totalWeight;
    /**
     * 切块后总重量(g)
     */
    private Double cutWeight;
    /**
     * 成熟后实际重量(g)
     */
    private Double actualWeight;
    /**
     * 预计成熟日期
     */
    private Date maturationDate;
    /**
     * 实际成熟日期
     */
    private Date actualMaturationDate;
    /**
     * 备注
     */
    private String notes;

    private Date created;

    private Date modified;

    private String createdBy;

    private String updatedBy;

    /**
     * 生产时使用的物料列表（非持久化字段，仅用于请求传输）
     */
    private transient java.util.List<SoapConsumablesUsage> usages;

    /**
     * 生产关联的皂块列表（非持久化字段，仅用于响应传输）
     */
    private transient java.util.List<SoapPiece> pieces;

    /**
     * 皂块总售价（非持久化字段，用于列表汇总展示）
     */
    private transient Double totalPiecePrice;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public Date getProductionDate() { return productionDate; }
    public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public Double getCutWeight() { return cutWeight; }
    public void setCutWeight(Double cutWeight) { this.cutWeight = cutWeight; }

    public Double getActualWeight() { return actualWeight; }
    public void setActualWeight(Double actualWeight) { this.actualWeight = actualWeight; }

    public Date getMaturationDate() { return maturationDate; }
    public void setMaturationDate(Date maturationDate) { this.maturationDate = maturationDate; }

    public Date getActualMaturationDate() { return actualMaturationDate; }
    public void setActualMaturationDate(Date actualMaturationDate) { this.actualMaturationDate = actualMaturationDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }
    public void setModified(Date modified) { this.modified = modified; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public java.util.List<SoapConsumablesUsage> getUsages() { return usages; }
    public void setUsages(java.util.List<SoapConsumablesUsage> usages) { this.usages = usages; }

    public java.util.List<SoapPiece> getPieces() { return pieces; }
    public void setPieces(java.util.List<SoapPiece> pieces) { this.pieces = pieces; }

    public Double getTotalPiecePrice() { return totalPiecePrice; }
    public void setTotalPiecePrice(Double totalPiecePrice) { this.totalPiecePrice = totalPiecePrice; }
}
