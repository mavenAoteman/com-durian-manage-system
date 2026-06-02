package com.durian.manage.system.domain;

/**
 * 物料类型枚举
 * 0: 其它, 1: 油脂, 2: 精油, 3: 添加物, 4: 水相, 5: 包装材料, 6: 赠品
 */
public enum ConsumableTypeEnum {
    OTHER(0, "其它"),
    OIL(1, "油脂"),
    ESSENTIAL_OIL(2, "精油"),
    ADDITIVE(3, "添加物"),
    WATER_PHASE(4, "水相"),
    PACKAGING_MATERIALS(5, "包装材料"),
    GIFT(6, "赠品"),
            ;

    private final int code;
    private final String desc;

    ConsumableTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ConsumableTypeEnum fromCode(int code) {
        for (ConsumableTypeEnum type : ConsumableTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return OTHER;
    }
}