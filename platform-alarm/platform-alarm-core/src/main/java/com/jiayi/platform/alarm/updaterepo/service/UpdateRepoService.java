package com.jiayi.platform.alarm.updaterepo.service;

import com.jiayi.platform.alarm.updaterepo.dao.UpdateResposity;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.dto.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateRepoService {
    @Autowired
    private UpdateResposity updateResposity;

    public JsonObject<?> editMysqlDescription(String tableName, String desc, Integer id) {
        String descName = "";
        String updateTimeName = "";
        switch (tableName) {
            case "user"://用户管理的备注
            case "bkyj_goods_management"://监测预警（物品、人员和策略）的备注；
            case "bkyj_suspects":
            case "bkyj_alarm_strategy":
            case "bkyj_alarm_district"://地域布控的备注
            case "case_suspect"://可疑对像的备注
            case "case_remark_info"://案件资料中备注
                descName = "remark";
                break;
            case "t_src"://数据源的备注
            case "t_device_type"://设备分类的备注
                descName = "description";
                break;
            case "t_vendor"://供应商的备注
                descName = "ex_info";
                break;

            default:
                break;
        }
        switch (tableName) {
            case "user":
            case "bkyj_goods_management":
            case "bkyj_suspects":
            case "bkyj_alarm_strategy":
            case "bkyj_alarm_district":
            case "case_remark_info":
                updateTimeName = "update_at";
                break;
            case "t_src":
            case "t_device_type":
            case "t_vendor":
                updateTimeName = "update_date";
                break;

            default:
                break;
        }
        try {
            if ("case_suspect".equals(tableName)) {//这个没有记录更新时间的字段
                updateResposity.updateByTableNameAndAttr(tableName, "id", id, descName, desc);
            } else {
                updateResposity.updateTimeByTableNameAndAttr(tableName, "id", id, descName, desc, updateTimeName);
            }
        } catch (Exception e) {
            throw new ArgumentException("update attr by table error", e);
        }
        return new JsonObject<>("");
    }
}
