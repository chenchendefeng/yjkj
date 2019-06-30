package com.jiayi.platform.judge.enums;

import com.jiayi.platform.common.exception.ArgumentException;

public enum RequestType {
    /**
     * 伴随
     */
    FOLLOW_COLLISION("collision_follow", "伴随分析","judge_result_follow", true),
    /**
     * 热点
     */
//    AP_COLLISION("collision_ap", "热点碰撞","", false),
    /**
     * 区域
     */
    AREA_COLLISION("collision_area", "区域碰撞","judge_result_area", true),
    /**
     * 路线
     */
    LINE_COLLISION("collision_path", "路线碰撞","judge_result_line", true),
    /**
     * 对象出现
     */
    APPEAR_COLLISION("collision_appear", "对象出现","judge_result_appear", true),
    /**
     * 对象消失
     */
    DISAPPEAR_COLLISION("collision_disappear", "对象消失","judge_result_appear", true),
    /**
     * 多轨
     */
    MULTI_TRACK_COLLISION("collision_multi_track", "多轨碰撞","judge_result_multi_track", true),
    /**
     * 二次碰撞
     */
    AGGREGATE_COLLISION("collision_aggregate", "二次碰撞","judge_result_aggregate", false),
    /**
     * 区域分析
     */
    DEVICE_ANALYSIS("device_analysis", "区域分析","judge_result_device_analysis", true),
    /**
     * 轨迹合并
     */
    TRACK_MERGE("track_merge", "轨迹合并","judge_result_track_merge", true),
    /**
     * 轨迹查询
     */
    TRACK_QUERY("track_query", "轨迹查询","judge_result_track_query", true),
    /**
     * 电子档案轨迹查询
     */
//    E_RECORD_TRACK_QUERY("e_record_track_query", "电子档案轨迹查询","judge_result_track_query", false),
    /**
     * 规律分析（包括地点分析和出行分析）
     */
    RULE_ANALYSIS("rule_analysis", "规律分析","", false),
    /**
     * 出行分析
     */
    LOCATION_ANALYSIS("location_analysis", "规律-地点分析","", true),
    /**
     * 地点分析
     */
    MOVEMENT_ANALYSIS("movement_analysis", "规律-驻留分析","", true),
    /**
     * 轨迹比对
     */
    TRACK_COMPARE("track_compare", "轨迹比对","", true),
    /**
     * 数据挖掘库
     */
    MINING_REPO("mining_repo", "数据挖掘库","mining_nocturnal_detection,mining_resident_population", false),
    /**
     * 多特征分析
     */
    MULTI_FEATURE_ANALYSIS("multifeature_analysis", "多特征分析","judge_result_multi_feature", true),
    /**
     * 亲密关系分析
     */
    INTIMATE_RELATION_ANALYSIS("intimate_relationship_analysis", "亲密关系分析","judge_result_intimate_relation", true),

    /**
     * 文件导入
     */
    FILE_IMPORT("file_import", "文件导入", "judge_result_file_import", false),

    /**
     * 库导入
     */
    REPO_IMPORT("repo_import", "库导入", "", false);

    private String typeName;
    private String description;
    private String tabName;
    private boolean isShow; // 分析记录是否需要展示

    public String getTabName () {
        return tabName;
    }

    public void setTabName (String tabName) {
        this.tabName = tabName;
    }

    RequestType(String typeName, String description, String tabName, boolean isShow) {
        this.typeName = typeName;
        this.description = description;
        this.tabName = tabName;
        this.isShow = isShow;
    }

    public String typeName() {
        return typeName;
    }

    public String description() {
        return description;
    }

    public boolean isShow() {
        return isShow;
    }

    public static RequestType getRequestType(String typeName) {
        for (RequestType requestType : values()) {
            if (typeName.toLowerCase().equals(requestType.typeName()))
                return requestType;
        }
        throw new ArgumentException("invalid request type name");
    }

    /**
     * 该类型查询结果是否缓存
     */
//    public boolean isCacheResultType() {
//        return this == RequestType.APPEAR_COLLISION || this == RequestType.DISAPPEAR_COLLISION
//                || this == RequestType.FOLLOW_COLLISION || this == RequestType.MULTI_TRACK_COLLISION
//                || this == RequestType.AGGREGATE_COLLISION;
//    }
}