package com.jiayi.platform.basic.enums;

public enum PlaceLabelType {

    LABEL_TYPE(0, "标签分类"),
    LEVEL_ONE_LABEL(1, "一级标签"),
    LEVEL_TWO_LABEL(2, "二级标签");

    private int type;
    private String desc;

    PlaceLabelType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static PlaceLabelType getByType(int type){
        for(PlaceLabelType placeLabelType : PlaceLabelType.values()){
            if(placeLabelType.getType() == type){
                return placeLabelType;
            }
        }
        return null;
    }
}
