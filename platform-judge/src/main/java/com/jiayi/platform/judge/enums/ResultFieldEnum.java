package com.jiayi.platform.judge.enums;

import com.jiayi.platform.common.enums.CollectType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum ResultFieldEnum {
    MAC("String", "object_value", "objectValue", "MAC", 1),
    CARNO("String", "object_value", "objectValue", "车牌", 2),
    IMEI("String", "object_value", "objectValue", "IMEI", 3),
    IMSI("String", "object_value", "objectValue", "IMSI", 4),
    BEGIN_DATE("Bigint", "min_happen_at", "beginDate", "开始时间", 101),
    END_DATE("Bigint", "max_happen_at", "endDate", "结束时间", 102),
    BEGIN_ADDRESS("String", "from_device_id", "beginAddress", "开始地点", 103),
    END_ADDRESS("String", "to_device_id", "endAddress", "结束地点", 104),
    MATCH_COUNT("Integer", "match_count", "matchCount", "匹配次数", 105),
    UNIQUE_DEV_COUNT("Integer", "unique_dev_count", "uniqueDevCount", "匹配地点数", 106),
    TRACK_COUNT("Integer", "track_count", "trackCount", "轨迹点数", 107),
    MATCH_CONDITION("String", "match_condition", "matchCondition", "匹配条件", 108),
    OBJECT_TYPE_NAME("String", "object_type_name", "objectTypeName", "数据类型", 109),
    OBJECT_VALUE("String", "object_value", "objectValue", "数据值", 110),
    RECORD_AT("Bigint", "start_at", "recordAt", "采集时间", 111),
    DEVICE_ADDRESS("String", "device_address", "deviceAddress", "出现地点", 112),
    DA_MATCH_COUNT("Integer", "match_count", "matchCount", "连接次数", 113),
    DA_BEGIN_DATE("Bigint", "min_happen_at", "minHappenDate", "开始时间", 114),
    DA_END_DATE("Bigint", "max_happen_at", "maxHappenDate", "结束时间", 115),
    TQ_TRACK_COUNT("Integer", "match_count", "trackCount", "轨迹数", 116),
    TQ_UNIQUE_DEV_COUNT("Integer", "unique_dev_count", "deviceCount", "地点数", 117),
    MATCH_DAYS("Integer", "match_days", "matchDays", "匹配天数", 118),
    MATCH_COUNTS("Integer", "match_count", "matchCount", "匹配系数", 119),
    MATCH_TRACKS("Long", "match_tracks", "matchTracks", "匹配轨迹数", 120),
    DESC("String", "desc", "desc", "详细信息", 121),
    MR_OBJ_TYPE("String", "obj_type", "objectType", "特征类型", 122),
    MR_OBJ_VALUE("String", "obj_value", "objectValue", "特征值", 123),
    RI_OBJECT_NAME("String", "object_name", "objectName", "物品名称", 124),
    RI_OBJECT_TYPE("String", "object_type", "objectType", "实体类型", 125),
    RI_OBJECT_VALUE("String", "object_value", "objectValue", "实体值", 126),
    USER_NAME("String", "name", "name", "用户姓名", 127),
    CERT_CODE("String", "cert_code", "certCode", "证件号码", 128),
    PHONE("String", "phone", "phone", "手机号码", 129),
    ADDRESS("String", "address", "address", "家庭住址", 130);

    private String resultType;
    private String resultName;
    private String responseName;
    private String resultDesc;
    private Integer code;

    ResultFieldEnum(String resultType, String resultName, String responseName, String resultDesc, Integer code) {
        this.resultType = resultType;
        this.resultName = resultName;
        this.responseName = responseName;
        this.resultDesc = resultDesc;
        this.code = code;
    }

    public String resultType() {
        return resultType;
    }

    public String resultName() {
        return resultName;
    }

    public String responseName() {
        return responseName;
    }

    public String resultDesc() {
        return resultDesc;
    }

    public Integer code() {
        return code;
    }

    public static List<ResultFieldEnum> getResultInfoList(String requestType, String objectType) {
        List<ResultFieldEnum> typeList = new ArrayList<>();
        if(StringUtils.isNotBlank(objectType)) {
            switch (CollectType.valueOf(objectType.toUpperCase())) {
                case MAC:
                    typeList.add(MAC);
                    break;
                case CARNO:
                    typeList.add(CARNO);
                    break;
                case IMEI:
                    typeList.add(IMEI);
                    break;
                case IMSI:
                    typeList.add(IMSI);
                default:
                    break;
            }
        }

        switch (RequestType.getRequestType(requestType)) {
            case MINING_REPO:
                typeList.clear();
                typeList.add(MR_OBJ_TYPE);
                typeList.add(MR_OBJ_VALUE);
                typeList.add(BEGIN_DATE);
                typeList.add(END_DATE);
                break;
            case REPO_IMPORT:
                typeList.clear();
                typeList.add(RI_OBJECT_NAME);
                typeList.add(RI_OBJECT_TYPE);
                typeList.add(RI_OBJECT_VALUE);
                typeList.add(USER_NAME);
                typeList.add(CERT_CODE);
                typeList.add(PHONE);
                typeList.add(ADDRESS);
                break;
            case APPEAR_COLLISION:
            case DISAPPEAR_COLLISION:
                typeList.add(BEGIN_DATE);
                typeList.add(END_DATE);
                typeList.add(BEGIN_ADDRESS);
                typeList.add(END_ADDRESS);
                break;
            case LINE_COLLISION:
                typeList.add(BEGIN_DATE);
                typeList.add(END_DATE);
                typeList.add(BEGIN_ADDRESS);
                typeList.add(END_ADDRESS);
                typeList.add(MATCH_COUNT);
                typeList.add(UNIQUE_DEV_COUNT);
                break;
            case FOLLOW_COLLISION:
            case MULTI_TRACK_COLLISION:
                typeList.add(BEGIN_DATE);
                typeList.add(END_DATE);
                typeList.add(BEGIN_ADDRESS);
                typeList.add(END_ADDRESS);
                typeList.add(MATCH_COUNT);
                typeList.add(UNIQUE_DEV_COUNT);
                typeList.add(TRACK_COUNT);
                break;
            case AREA_COLLISION:
                typeList.add(BEGIN_DATE);
                typeList.add(END_DATE);
                typeList.add(BEGIN_ADDRESS);
                typeList.add(END_ADDRESS);
                typeList.add(MATCH_COUNT);
                typeList.add(MATCH_CONDITION);
                break;
//            case TRACK_MERGE:
//                typeList.clear();
//                typeList.add(OBJECT_TYPE_NAME);
//                typeList.add(OBJECT_VALUE);
//                typeList.add(RECORD_AT);
//                typeList.add(DEVICE_ADDRESS);
//                break;
            case DEVICE_ANALYSIS:
                typeList.add(DA_MATCH_COUNT);
                typeList.add(DA_BEGIN_DATE);
                typeList.add(DA_END_DATE);
                break;
//            case TRACK_QUERY:
//                typeList.clear();
//                typeList.add(OBJECT_TYPE_NAME);
//                typeList.add(OBJECT_VALUE);
//                typeList.add(BEGIN_DATE);
//                typeList.add(END_DATE);
//                typeList.add(BEGIN_ADDRESS);
//                typeList.add(END_ADDRESS);
//                typeList.add(TQ_TRACK_COUNT);
//                typeList.add(TQ_UNIQUE_DEV_COUNT);
//                break;
//            case RULE_ANALYSIS:
//                typeList.add(RECORD_AT);
//                typeList.add(DEVICE_ADDRESS);
//                break;
            case MULTI_FEATURE_ANALYSIS:
                typeList.clear();
                typeList.add(OBJECT_TYPE_NAME);
                typeList.add(OBJECT_VALUE);
                typeList.add(MATCH_DAYS);
                typeList.add(MATCH_COUNTS);
                typeList.add(MATCH_TRACKS);
//            typeList.add(DESC);
                break;
            case INTIMATE_RELATION_ANALYSIS:
                typeList.clear();
                typeList.add(OBJECT_TYPE_NAME);
                typeList.add(OBJECT_VALUE);
                typeList.add(MATCH_DAYS);
                typeList.add(MATCH_TRACKS);
//            typeList.add(DESC);
                break;
            default:
                break;
        }

        return typeList;
    }

    public static ResultFieldEnum getResultFieldByDesc(String resultDesc, String requestType, String objectType) {
        List<ResultFieldEnum> values = getResultInfoList(requestType, objectType);
        for (ResultFieldEnum resultField : values) {
            if (resultDesc.equals(resultField.resultDesc()))
                return resultField;
        }
        return null;
    }

    public static ResultFieldEnum getResultFieldByCode(Integer code) {
        for (ResultFieldEnum resultField : values()) {
            if (code.equals(resultField.code()))
                return resultField;
        }
        return null;
    }
}
