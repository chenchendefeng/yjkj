package com.jiayi.platform.judge.enums;

public enum JudgeDetailType {
    LINE_DETAIL("路线碰撞详细轨迹"),
    AREA_CONDITION("区域碰撞匹配结果"),
    AREA_DETAIL("区域碰撞详细轨迹"),
    APPEAR_DETAIL("对象出现详细轨迹"),
    DISAPPEAR_DETAIL("对象消失详细轨迹"),
    TRACK_DETAIL("碰撞详细轨迹（包括路线/区域/出现/消失）"),
    FOLLOW_DETAIL("伴随分析比对结果"),
    MULTI_TRACK_DETAIL("多轨碰撞比对结果"),
    TRACK_QUERY_DETAIL("轨迹查询详细轨迹"),
    DEVICE_ANALYSIS_DETAIL("区域分析详细轨迹"),
    DEVICE_ANALYSIS_STAT("区域分析图表统计"),
    LOCATION_ANALYSIS_DETAIL("地点分析详细轨迹"),
    MOVEMENT_ANALYSIS_DETAIL("驻留分析详细轨迹"),
    MULTI_FEATURE_DETAIL("多特征分析匹配结果"),
    MULTI_FEATURE_TRACK_DETAIL("多特征分析详细轨迹"),
    INTIMATE_RELATION_DETAIL("亲密关系分析匹配结果"),
    INTIMATE_RELATION_TRACK_DETAIL("亲密关系分析详细轨迹")
    ;

    private String description;

    JudgeDetailType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
