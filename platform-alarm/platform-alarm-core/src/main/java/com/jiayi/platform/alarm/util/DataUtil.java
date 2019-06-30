package com.jiayi.platform.alarm.util;

import com.jiayi.platform.alarm.dto.MapRegion;
import com.jiayi.platform.alarm.enums.AssembleType;
import com.jiayi.platform.alarm.enums.AlarmType;
import com.jiayi.platform.common.exception.ValidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class DataUtil {

    public static void checkData(Integer type, MapRegion mapRegion, Map<String, String> exInfo) {
        AlarmType strategyType = AlarmType.getTaskStatusByValue(type);
        if (strategyType == null) {
            throw new ValidException("策略类型错误");
        }
        switch (strategyType) {
            case INSCOPE:
            case OUTSCOPE:
                checkMapRegion(mapRegion);
                break;
            case DISAPPEAR:
                checkDisappear(mapRegion, exInfo);
                break;
            case ASSEMBLE:
                checkAssemble(mapRegion, exInfo);
                break;
            default:
                break;
        }
    }

    private static void checkAssemble(MapRegion mapRegion, Map<String, String> exInfo) {
        if (exInfo == null) {
            throw new ValidException("聚集信息不能为空");
        } else {
            if (StringUtils.isBlank(exInfo.get("nnt")) || "0".equals(exInfo.get("nnt"))
                    || !StringUtils.isNumeric(exInfo.get("nnt"))) {
                throw new ValidException("告警人数不能为空或只能输入数字");
            }
            if (StringUtils.isBlank(exInfo.get("Period")) || "0".equals(exInfo.get("Period"))
                    || !StringUtils.isNumeric(exInfo.get("Period"))) {
                throw new ValidException("统计时段不能为空或只能输入数字");
            }
            if (StringUtils.isBlank(exInfo.get("typeS"))) {
                throw new ValidException("聚集类型不能为空");
            } else {
                if (AssembleType.Area.equals(AssembleType.getEnumByName(exInfo.get("typeS")))) {
                    checkMapRegion(mapRegion);
                } else if (AssembleType.DISTANCE.equals(AssembleType.getEnumByName(exInfo.get("typeS")))) {
                    if (StringUtils.isBlank(exInfo.get("distance")) || "0".equals(exInfo.get("distance"))
                            || !StringUtils.isNumeric(exInfo.get("distance"))) {
                        throw new ValidException("范围距离不能为空或只能输入数字");
                    }
                } else {
                    throw new ValidException("聚集类型错误");
                }
            }
        }
    }

    private static void checkDisappear(MapRegion mapRegion, Map<String, String> exInfo) {
        checkMapRegion(mapRegion);
        if (exInfo == null || StringUtils.isBlank(exInfo.get("timeSE"))) {
            throw new ValidException("消失时间不能为空");
        } else if (!StringUtils.isNumeric(exInfo.get("timeSE"))) {
            throw new ValidException("消失时间只能输入数字");
        }
    }

    private static void checkMapRegion(MapRegion mapRegion) {
//        AlarmRegion obj = JSON.parseObject(mapRegion, AlarmRegion.class);
        if (mapRegion == null || CollectionUtils.isEmpty(mapRegion.getPoints())) {
            throw new ValidException("布控区域不能为空");
        } else if (mapRegion.getPoints().size() != 4) {
            throw new ValidException("经纬度错误");
        }
    }
}
