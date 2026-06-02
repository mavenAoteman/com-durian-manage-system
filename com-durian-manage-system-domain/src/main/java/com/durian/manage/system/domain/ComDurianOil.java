package com.durian.manage.system.domain;

import com.durian.manage.system.common.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * (ComDurianOil)实体类
 *
 * @author makejava
 * @since 2024-03-21 11:29:24
 */
public class ComDurianOil implements Serializable {
    private static final long serialVersionUID = 403366837475867086L;
    /**
     * 自增id
     */
    private Integer id;
    /**
     * 油中文名
     */
    private String oilChineseName;
    /**
     * 油英文名
     */
    private String oilEnglishName;
    /**
     * 氢氧化钠皂化价
     */
    private Float sodiumHydroxideSaponificationValue;
    /**
     * 氢氧化钾皂化价
     */
    private Float potassiumHydroxideSaponificationValue;
    /**
     * ins值
     */
    private Float insValue;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 修改人
     */
    private String modifier;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 工具记录的创建时间
     */
    private String createdStr;

    /**
     * 工具记录的创建时间
     */
    private String modifiedStr;

    /**
     * 数据归属用户ID。0 = 系统默认（所有人可见，只有超管可改）
     */
    private Long ownerUserId;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOilChineseName() {
        return oilChineseName;
    }

    public void setOilChineseName(String oilChineseName) {
        this.oilChineseName = oilChineseName;
    }

    public String getOilEnglishName() {
        return oilEnglishName;
    }

    public void setOilEnglishName(String oilEnglishName) {
        this.oilEnglishName = oilEnglishName;
    }

    public Float getSodiumHydroxideSaponificationValue() {
        return sodiumHydroxideSaponificationValue;
    }

    public void setSodiumHydroxideSaponificationValue(Float sodiumHydroxideSaponificationValue) {
        this.sodiumHydroxideSaponificationValue = sodiumHydroxideSaponificationValue;
    }

    public Float getPotassiumHydroxideSaponificationValue() {
        return potassiumHydroxideSaponificationValue;
    }

    public void setPotassiumHydroxideSaponificationValue(Float potassiumHydroxideSaponificationValue) {
        this.potassiumHydroxideSaponificationValue = potassiumHydroxideSaponificationValue;
    }

    public Float getInsValue() {
        return insValue;
    }

    public void setInsValue(Float insValue) {
        this.insValue = insValue;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        this.createdStr = DateUtils.dateToString(createTime);
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
        this.modifiedStr = DateUtils.dateToString(modifyTime);
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

