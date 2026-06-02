package com.durian.manage.system.domain;

import java.io.Serializable;

public class SoapPackagePiece implements Serializable {
    private static final long serialVersionUID = 9012345678901234567L;

    private Integer id;
    private Integer packageId;
    private Integer pieceId;

    // transient: join fields
    private transient String pieceName;
    private transient String pieceBatchNumber;
    private transient Double pieceWeight;
    private transient Double piecePrice;
    private transient Integer pieceStatus;
    private transient String productionName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Integer getPieceId() { return pieceId; }
    public void setPieceId(Integer pieceId) { this.pieceId = pieceId; }
    public String getPieceName() { return pieceName; }
    public void setPieceName(String pieceName) { this.pieceName = pieceName; }
    public String getPieceBatchNumber() { return pieceBatchNumber; }
    public void setPieceBatchNumber(String pieceBatchNumber) { this.pieceBatchNumber = pieceBatchNumber; }
    public Double getPieceWeight() { return pieceWeight; }
    public void setPieceWeight(Double pieceWeight) { this.pieceWeight = pieceWeight; }
    public Double getPiecePrice() { return piecePrice; }
    public void setPiecePrice(Double piecePrice) { this.piecePrice = piecePrice; }
    public Integer getPieceStatus() { return pieceStatus; }
    public void setPieceStatus(Integer pieceStatus) { this.pieceStatus = pieceStatus; }
    public String getProductionName() { return productionName; }
    public void setProductionName(String productionName) { this.productionName = productionName; }
}
