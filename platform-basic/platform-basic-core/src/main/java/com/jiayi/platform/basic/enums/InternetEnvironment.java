package com.jiayi.platform.basic.enums;

public enum InternetEnvironment {
    E1(1, "直连审计设备"),
    E2(2, "通过非三层交换机"),
    E3(3, "通过三层交换机但未开启网管模式"),
    E4(4, "通过三层交换机且开启网管模式"),
    E5(5, "通过路由且是中继模式"),
    E6(6, "通过路由且是路由模式"),
    E7(7, "网络环境不明");

    int id;
    String desc;

    InternetEnvironment(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static String getDescById(int id) {
        for (InternetEnvironment e : values()) {
            if (e.id == id)
                return e.desc;
        }
        return null;
    }

    public static Integer getIdByDesc(String desc) {
        for (InternetEnvironment e : values()) {
            if (e.desc.equals(desc))
                return e.id;
        }
        return null;
    }

    public String desc() {
        return desc;
    }

    public int id() {
        return id;
    }
}
