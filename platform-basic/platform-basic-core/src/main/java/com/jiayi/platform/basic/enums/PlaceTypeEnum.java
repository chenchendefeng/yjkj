package com.jiayi.platform.basic.enums;

public enum PlaceTypeEnum {
    //场所类型
    CERT_1(1, "经营"),
    CERT_2(2, "非经营"),
    CERT_3(3, "围栏采集"),
    CERT_4(4, "其他");

    private int id;
    private String name;

    private PlaceTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameById(int id) {
        for (PlaceTypeEnum pt : values()) {
            if (pt.id == id)
                return pt.getName();
        }
        return null;
    }

    public static Integer getIdByName(String name) {
        for (PlaceTypeEnum pt : values()) {
            if (pt.name.equals(name))
                return pt.getId();
        }
        return null;
    }
}
