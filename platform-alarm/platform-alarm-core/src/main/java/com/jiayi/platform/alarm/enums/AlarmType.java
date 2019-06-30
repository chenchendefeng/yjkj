package com.jiayi.platform.alarm.enums;

/**
 * 策略类型
 */
public enum AlarmType {
    OTHER(0, "未知", "未知"),
    TOUCH(2, "出现", "出现"),
    INSCOPE(3, "入圈", "入圈"),
    OUTSCOPE(4, "出圈", "出圈"),
    DISAPPEAR(5, "消失", "消失"),
    ASSEMBLE(6, "聚集", "指定区域聚集"),
    DYNAMIC_ASSEMBLE(7,"" , "动态聚集"),
    BOUNDARY(8, "边界", "边界")
    ;

    private int type;
    private String description;
    private String msgDescription;

    AlarmType(int type, String description, String msgDescription) {
        this.type = type;
        this.description = description;
        this.msgDescription = msgDescription;
    }

    public static AlarmType getTaskStatusByValue(int type) {
        for (AlarmType types : AlarmType.values()) {
            if (types.getType() == type) {
                return types;
            }
        }
        return OTHER;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getMsgDescription() {
        return msgDescription;
    }
}
