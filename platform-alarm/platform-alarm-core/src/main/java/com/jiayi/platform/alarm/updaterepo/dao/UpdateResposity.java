package com.jiayi.platform.alarm.updaterepo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UpdateResposity {

    @Update("update ${tableName} set ${updateAttr}=#{updateAttrVal} where ${condition}=#{conditionVal}")
    public void updateByTableNameAndAttr(@Param("tableName") String tableName, @Param("condition") String condition, @Param("conditionVal") Integer conditionVal, @Param("updateAttr") String updateAttr, @Param("updateAttrVal") String updateAttrVal);

    @Update("update ${tableName} set  ${updateAttr}=#{updateAttrVal},${updateTimeAttr}=date_format(now(),'%Y-%m-%d %H:%i:%s') where ${condition}=#{conditionVal}")
    public void updateTimeByTableNameAndAttr(@Param("tableName") String tableName, @Param("condition") String condition, @Param("conditionVal") Integer conditionVal, @Param("updateAttr") String updateAttr, @Param("updateAttrVal") String updateAttrVal, @Param("updateTimeAttr") String updateTimeAttr);
}
