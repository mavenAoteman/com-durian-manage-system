package com.durian.manage.system.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * 皂块分装(SoapPiece)实体类
 *
 * @author maven12
 * @since 2026-05-01
 */
public class SoapPiece implements Serializable {
    private static final long serialVersionUID = 5061825630654123321L;

    private Integer id;
    /**
     * 数据归属用户ID
     */
    private Long ownerUserId;
    /**
     * 关联生产记录ID
     */
    private Integer productionId;
    /**
     * 皂块名称
     */
    private String pieceName;
    /**
     * 皂块唯一批次号（可追溯到生产批次）
     */
    private String pieceBatchNumber;
    /**
     * 重量(g)
     */
    private Double weight;
    /**
     * 售价
     */
    private Double price;
    /**
     * 1=在售, 2=已售, 3=赠送
     */
    private Integer status;
    /**
     * 备注
     */
    private String notes;

    private Date created;

    private Date modified;

    /** 所属生产批次名称（仅展示用，从 soap_production 关联得到） */
    private transient String productionName;
    /** 所属生产批次号（仅展示用） */
    private transient String productionBatchNumber;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }

    public Integer getProductionId() { return productionId; }
    public void setProductionId(Integer productionId) { this.productionId = productionId; }

    public String getPieceName() { return pieceName; }
    public void setPieceName(String pieceName) { this.pieceName = pieceName; }

    public String getPieceBatchNumber() { return pieceBatchNumber; }
    public void setPieceBatchNumber(String pieceBatchNumber) { this.pieceBatchNumber = pieceBatchNumber; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public Date getModified() { return modified; }
    public void setModified(Date modified) { this.modified = modified; }

    public String getProductionName() { return productionName; }
    public void setProductionName(String productionName) { this.productionName = productionName; }

    public String getProductionBatchNumber() { return productionBatchNumber; }
    public void setProductionBatchNumber(String productionBatchNumber) { this.productionBatchNumber = productionBatchNumber; }
}
