package com.durian.manage.system.domain;

/**
 * 物料单位枚举（前端展示顺序与此处定义顺序一致）
 */
public enum ConsumableUnitEnum {
    G("g", "克(g)", 1.0),
    ML("ml", "毫升(ml)", 1.0),
    MG("mg", "毫克(mg)", 0.001),
    KG("kg", "千克(kg)", 1000.0),
    UG("ug", "微克(ug)", 0.000001),
    NG("ng", "纳克(ng)", 0.000000001),
    L("l", "升(l)", 1000.0),
    UL("ul", "微升(ul)", 0.001),
    CL("cl", "厘升(cl)", 10.0),
    DL("dl", "分升(dl)", 100.0),
    TSP("tsp", "茶匙(tsp)", 5.0),
    TBSP("tbsp", "汤匙(tbsp)", 15.0),
    CUP("cup", "杯(cup)", 240.0),
    DROP("drop", "滴(drop)", 0.05),
    OZ("oz", "盎司(oz)", 28.35),
    LB("lb", "磅(lb)", 453.59),
    /** 以下为计数单位，无克重换算 */
    GE("ge", "个", null),
    ZHANG("zhang", "张", null),
    BAO("bao", "包", null),
    KUN("kun", "捆", null),
    ;

    private final String code;
    private final String desc;
    /** 换算为克的系数，null 表示计数单位不可换算 */
    private final Double toGramsFactor;

    ConsumableUnitEnum(String code, String desc, Double toGramsFactor) {
        this.code = code;
        this.desc = desc;
        this.toGramsFactor = toGramsFactor;
    }

    public String getCode() { return code; }
    public String getDesc() { return desc; }
    /** 获取克重换算系数，null=不可换算 */
    public Double getToGramsFactor() { return toGramsFactor; }

    /** 根据code获取换算系数 */
    public static Double getToGramsFactor(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        for (ConsumableUnitEnum unit : ConsumableUnitEnum.values()) {
            if (unit.code.equalsIgnoreCase(code.trim())) return unit.toGramsFactor;
        }
        return null; // 未知单位
    }

    /** 是否为体积单位（需根据物料类型调整密度） */
    public static boolean isVolumeUnit(String unit) {
        if (unit == null) return false;
        switch (unit.toLowerCase().trim()) {
            case "ml": case "l": case "ul": case "cl": case "dl":
            case "tsp": case "tbsp": case "cup": case "drop":
                return true;
            default: return false;
        }
    }

    /**
     * 数量转克重（自动根据物料类型调整：油脂×0.92，水相×1.0）
     * @param quantity 数量
     * @param unit 单位
     * @param consumableType 物料类型（1=油脂需密度换算，null或其它=1:1）
     */
    public static Double toGrams(Double quantity, String unit, Integer consumableType) {
        if (quantity == null || unit == null) return quantity;
        Double factor = getToGramsFactor(unit);
        if (factor == null) return null;
        // 油脂+体积单位：使用油脂密度 0.92 g/ml
        if (consumableType != null && consumableType == 1 && isVolumeUnit(unit)) {
            return quantity * factor * 0.92;
        }
        return quantity * factor;
    }

    /** 克重转数量（自动根据物料类型反算） */
    public static Double fromGrams(Double grams, String unit, Integer consumableType) {
        if (grams == null || unit == null) return grams;
        Double factor = getToGramsFactor(unit);
        if (factor == null || factor == 0) return null;
        if (consumableType != null && consumableType == 1 && isVolumeUnit(unit)) {
            return grams / (factor * 0.92);
        }
        return grams / factor;
    }

    public static boolean contains(String code) {
        if (code == null || code.trim().isEmpty()) return false;
        for (ConsumableUnitEnum unit : ConsumableUnitEnum.values()) {
            if (unit.code.equalsIgnoreCase(code.trim())) return true;
        }
        return false;
    }
}
