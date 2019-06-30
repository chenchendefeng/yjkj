package com.jiayi.platform.common.enums;

public enum PopulationStatisticType {//1常住人口,2流动人口,3昼伏夜出,4频繁换机,5频繁换号,6频繁更换住址
    V_1(1, "常驻人口"),
    V_2(2, "流动人口"),
    V_3(3, "昼伏夜出"),
    V_4(4, "频繁换机"),
    V_5(5, "频繁换号"),
    V_6(6, "频繁更换住址"),
    V_7(7, "新进人员");
    private Integer code;
    private String desc;

    PopulationStatisticType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        PopulationStatisticType[] values = PopulationStatisticType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].code == code)
                return values[i].desc;
        }
        return null;
    }
}
