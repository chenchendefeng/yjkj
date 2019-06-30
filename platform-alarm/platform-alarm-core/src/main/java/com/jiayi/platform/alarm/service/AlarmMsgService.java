package com.jiayi.platform.alarm.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.alarm.dao.AlarmMsgDao;
import com.jiayi.platform.alarm.dto.*;
import com.jiayi.platform.alarm.entity.AlarmMsgBean;
import com.jiayi.platform.alarm.enums.AlarmType;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.web.dto.PageResult;
import com.jiayi.platform.report.dao.mysql.AlarmAreaDao;
import com.jiayi.platform.report.entity.AlarmArea;
import com.jiayi.platform.report.geography.Point;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AlarmMsgService {

    @Autowired
    private AlarmMsgDao alarmMsgDao;

    @Autowired
    private AlarmAreaDao alarmAreaDao;

    @Autowired
    private EntityManager entityManager;

    public PageResult findCaseAlarmMsg(AlarmCaseMsgSearchVo searchVo) {
        String sql = caseAlarmMsgSql(searchVo);
        String countSql = "select count(distinct group_id) from (" + sql + ")a";
        Query<?> query = (Query<?>) entityManager.createNativeQuery(countSql);
        BigInteger count = (BigInteger) query.getSingleResult();
        int limit = searchVo.getSize();
        int offset = searchVo.getPage() * searchVo.getSize();
        String listSql = "select min(a.id),min(a.case_id),min(a.case_name),min(a.alarm_type),UNIX_TIMESTAMP(min(a.alarm_time))*1000 as alarm_time,min(a.`status`),\n" +
                "min(a.alarm_suspect), min(a.object_type), min(a.object_value),min(a.device_id),min(a.address),min(a.latitude),min(a.longitude),min(a.assemble_group_id),\n" +
                "min(a.map_region), min(a.ex_info)\n" +
                "from (" + sql + ")a group by a.group_id order by min(a.alarm_time) desc\n" +
                "limit " + limit + " offset " + offset;
        query = (Query<?>) entityManager.createNativeQuery(listSql);
        List<Object[]> list = (List<Object[]>) query.list();
        List<AlarmCaseMsgDto> datas = Lists.newArrayList();
        for (Object[] objects : list) {
            Long id = Long.valueOf(objects[0].toString());
            Long caseId = objects[1] == null ? null : Long.valueOf(objects[1].toString());
            String caseName = objects[2] == null ? null : objects[2].toString();
            String strategyType = objects[3] == null ? null : AlarmType.getTaskStatusByValue(Integer.valueOf(objects[3].toString())).getMsgDescription();
            Long alarmTime = objects[4] == null ? null : Long.valueOf(objects[4].toString());
            String status = objects[5] == null ? null : objects[5].toString().equals("0") ? "未读" : "已读";
            String assembleGroupId = objects[13] == null ? null : objects[13].toString();
            String mapRegion = objects[14] == null ? null : objects[14].toString();
            String exInfo = objects[15] == null ? null : objects[15].toString();
            AlarmCaseMsgDto alarmCaseMsgDto = new AlarmCaseMsgDto(id, caseId, caseName, strategyType, status, assembleGroupId, mapRegion, exInfo);
            if (StringUtils.isNotBlank(assembleGroupId)) {
                //按聚集组织唯一标识查询聚集策略
                alarmCaseMsgDto.setAlarmSuspects(findByAssembleGroupId(assembleGroupId));
            }else{
                String name = objects[6] == null ? null : objects[6].toString();
                String objectType = objects[7] == null ? null : objects[7].toString();
                String objectValue = objects[8] == null ? null : objects[8].toString();
                String deviceId = objects[9] == null ? null : objects[9].toString();
                String address = objects[10] == null ? null : objects[10].toString();
                double lat = objects[11] == null ? 0 : Long.valueOf(objects[11].toString()) / Math.pow(10, 12);
                double lng = objects[12] == null ? 0 : Long.valueOf(objects[12].toString()) / Math.pow(10, 12);
                AlarmSuspectInfo alarmSuspectInfo = new AlarmSuspectInfo(alarmTime, name, objectType, objectValue, deviceId, address, lat, lng);
                alarmCaseMsgDto.getAlarmSuspects().add(alarmSuspectInfo);
            }
            datas.add(alarmCaseMsgDto);
        }
        return new PageResult(datas, count.longValue(), searchVo.getPage(), datas.size());
    }

    private String caseAlarmMsgSql(AlarmCaseMsgSearchVo searchVo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select msg.*,c.case_name,strategy.`name`,strategy.map_region,strategy.ex_info,d.address,d.latitude,d.longitude,\n"+
                "case when msg.assemble_group_id is null or msg.assemble_group_id='' then msg.id else msg.assemble_group_id end as group_id\n" +
                "from bkyj_alarm_msg msg\n" +
                "left join bkyj_alarm_strategy strategy on msg.strategy_id=strategy.id\n" +
                "left join t_device d on msg.device_id = d.id\n" +
                "left join case_info c on msg.case_id = c.id\n" +
                "left join case_assists ca on ca.case_id=c.id\n");
        //0非地域布控消息
        sql.append("where is_district=0 ");
        //案件创建人或协办人才能查询预警消息
        sql.append(" and (c.creator_id=").append(searchVo.getUserId()).append(" or ca.assists_id=").append(searchVo.getUserId()).append(")");
        if (StringUtils.isNotBlank(searchVo.getCaseName())) {
            sql.append(" and c.case_name like '%").append(searchVo.getCaseName().trim()).append("%'");
        }
        if(StringUtils.isNotBlank(searchVo.getSuspects())){
            sql.append(" and msg.alarm_suspect like '%").append(searchVo.getSuspects().trim()).append("%'");
        }
        if(StringUtils.isNotBlank(searchVo.getObjectType())){
            sql.append(" and msg.object_type='").append(searchVo.getObjectType()).append("'");
        }
        if(StringUtils.isNotBlank(searchVo.getObjectValue())){
            sql.append(" and msg.object_value like '%").append(searchVo.getObjectValue().trim()).append("%'");
        }
        if(searchVo.getType() != null && searchVo.getType() != 0){
            sql.append(" and msg.alarm_type=").append(searchVo.getType());
        }
        if(searchVo.getStartTime() != 0 && searchVo.getEndTime() != 0){
            sql.append(" and UNIX_TIMESTAMP(msg.alarm_time)*1000 between ").append(searchVo.getStartTime()).append(" and ").append(searchVo.getEndTime());
        }
        if(searchVo.getStatus() != null){
            sql.append(" and msg.status=").append(searchVo.getStatus());
        }
        return sql.toString();
    }

    private List<AlarmSuspectInfo> findByAssembleGroupId(String assembleGroupId){
        StringBuilder sql = new StringBuilder();
        sql.append("select msg.alarm_suspect,msg.object_type, msg.object_value,msg.device_id,d.address,d.latitude,d.longitude,UNIX_TIMESTAMP(msg.alarm_time)*1000\n" +
                "from bkyj_alarm_msg msg\n" +
                "left join t_device d on msg.device_id = d.id\n");
        sql.append("where msg.assemble_group_id='").append(assembleGroupId).append("'");
        Query<?> query = (Query<?>) entityManager.createNativeQuery(sql.toString());
        List<Object[]> list = (List<Object[]>) query.list();
        List<AlarmSuspectInfo> alarmSuspects = new ArrayList<>();
        for (Object[] objects : list) {
            String name = objects[0] == null ? null : objects[0].toString();
            String objectType = objects[1] == null ? null : objects[1].toString();
            String objectValue = objects[2] == null ? null : objects[2].toString();
            String deviceId = objects[3] == null ? null : objects[3].toString();
            String address = objects[4] == null ? null : objects[4].toString();
            double lat = objects[5] == null ? 0 : Long.valueOf(objects[5].toString()) / Math.pow(10, 12);
            double lng = objects[6] == null ? 0 : Long.valueOf(objects[6].toString()) / Math.pow(10, 12);
            Long alarmTime = objects[7] == null ? null : Long.valueOf(objects[7].toString());
            AlarmSuspectInfo alarmSuspectInfo = new AlarmSuspectInfo(alarmTime, name, objectType, objectValue, deviceId, address, lat, lng);
            alarmSuspects.add(alarmSuspectInfo);
        }
        return alarmSuspects;
    }

//    public void updateAlarmMsg(List<Long> msgIds) {
//        alarmMsgDao.updateAlarmMsg(msgIds , new Date());
//    }

    public void updateAlarmMsg(Long id) {
        AlarmMsgBean msg = alarmMsgDao.findById(id).orElseThrow(() -> new ValidException("预警消息不存在"));
        try {
            if(StringUtils.isBlank(msg.getAssembleGroupId())){
                alarmMsgDao.updateAlarmMsg(id, new Date());
            }else{
                alarmMsgDao.updateAlarmMsgByAssembleGroupId(msg.getAssembleGroupId(), new Date());
            }
        } catch (Exception e) {
            throw new ServiceException("update alarm msg error", e);
        }
    }

    public PageResult<AlarmDistrictMsgDto> findDistrictAlarmMsg(AlarmDistrictMsgSearchVo searchVo) {
        String sql = districtAlarmMsgSql(searchVo);
        String countSql = "select count(distinct group_id) from (" + sql + ")a";
        Query<?> query = (Query<?>) entityManager.createNativeQuery(countSql);
        BigInteger count = (BigInteger) query.getSingleResult();
        int limit = searchVo.getSize();
        int offset = searchVo.getPage() * searchVo.getSize();
        String listSql = "select min(a.id),min(a.`name`),min(a.district),min(a.alarm_type),UNIX_TIMESTAMP(min(a.alarm_time)) * 1000 as alarm_time,min(a.`status`),\n" +
                "min(a.alarm_suspect), min(a.object_type), min(a.object_value),min(a.device_id),min(a.address),min(a.latitude),min(a.longitude),min(a.assemble_group_id),\n" +
                "min(a.map_region), min(a.ex_info)\n" +
                "from (" + sql + ")a group by a.group_id order by min(a.alarm_time) desc\n" +
                "limit " + limit + " offset " + offset;
        query = (Query<?>) entityManager.createNativeQuery(listSql);
        List<Object[]> list = (List<Object[]>) query.list();
        List<AlarmDistrictMsgDto> datas = Lists.newArrayList();
        for (Object[] objects : list) {
            Long id = Long.valueOf(objects[0].toString());
            String alarmName = objects[1] == null ? null : objects[1].toString();
            Integer district = objects[2] == null ? null : Integer.valueOf(objects[2].toString());
            String strategyType = objects[3] == null ? null : AlarmType.getTaskStatusByValue(Integer.valueOf(objects[3].toString())).getMsgDescription();
            Long alarmTime = objects[4] == null ? null : Long.valueOf(objects[4].toString());
            String status = objects[5] == null ? null : objects[5].toString().equals("0") ? "未读" : "已读";

            String assembleGroupId = objects[13] == null ? null : objects[13].toString();
            String mapRegion = objects[14] == null ? null : objects[14].toString();
            String exInfo = objects[15] == null ? null : objects[15].toString();
            AlarmDistrictMsgDto alarmDistrictMsgDto = new AlarmDistrictMsgDto(id, alarmName, district, strategyType, status, assembleGroupId, mapRegion, exInfo);
            if (StringUtils.isNotBlank(assembleGroupId)) {
                //按聚集组织唯一标识查询聚集策略
                alarmDistrictMsgDto.setAlarmSuspects(findByAssembleGroupId(assembleGroupId));
            }else{
                String name = objects[6] == null ? null : objects[6].toString();
                String objectType = objects[7] == null ? null : objects[7].toString();
                String objectValue = objects[8] == null ? null : objects[8].toString();
                String deviceId = objects[9] == null ? null : objects[9].toString();
                String address = objects[10] == null ? null : objects[10].toString();
                double lat = objects[11] == null ? 0 : Long.valueOf(objects[11].toString()) / Math.pow(10, 12);
                double lng = objects[12] == null ? 0 : Long.valueOf(objects[12].toString()) / Math.pow(10, 12);
                AlarmSuspectInfo alarmSuspectInfo = new AlarmSuspectInfo(alarmTime, name, objectType, objectValue, deviceId, address, lat, lng);
                alarmDistrictMsgDto.getAlarmSuspects().add(alarmSuspectInfo);
            }
            datas.add(alarmDistrictMsgDto);
        }
        return new PageResult(datas, count.longValue(), searchVo.getPage(), datas.size());
    }

    private String districtAlarmMsgSql(AlarmDistrictMsgSearchVo searchVo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select msg.*,alarm.`name`,alarm.district,alarm.map_region,alarm.ex_info,d.address,d.latitude,d.longitude,\n" +
                "case when msg.assemble_group_id is null or msg.assemble_group_id='' then msg.id else msg.assemble_group_id end as group_id\n" +
                "from bkyj_alarm_msg msg\n" +
                "left join bkyj_alarm_district alarm on msg.strategy_id-10000000 = alarm.id\n" +
                "left join t_device d on msg.device_id = d.id\n");
        //1地域布控消息
        sql.append("where msg.is_district=1 ");
        if(searchVo.getUserId() != null){//TODO 消息推送人员才能查询预警消息？
            List<String> users = buildSearchUserStr(searchVo.getUserId());
            sql.append(" and (");
            for(int i = 0; i < users.size(); ++i){
                sql.append(" alarm.user_id like '%").append(users.get(i)).append("%'");
                if(i < users.size() - 1){
                    sql.append(" or ");
                }
            }
            sql.append(")");
        }
        if(StringUtils.isNotBlank(searchVo.getName())){
            sql.append(" and alarm.`name` like '%").append(searchVo.getName().trim()).append("%'");
        }
        if(searchVo.getType() != null && searchVo.getType() != 0){
            sql.append(" and msg.alarm_type=").append(searchVo.getType());
        }
        if(searchVo.getDistrict() != null){
            sql.append(" and alarm.district=").append(searchVo.getDistrict());
        }
        if(CollectionUtils.isNotEmpty(searchVo.getObjType())){
            List<String> objTypes = searchVo.getObjType();
            sql.append(" and (");
            for(int i = 0; i < objTypes.size(); ++i){
                sql.append(" msg.object_type='").append(objTypes.get(i)).append("'");
                if(i < objTypes.size() - 1){
                    sql.append(" or ");
                }
            }
            sql.append(")");
        }
        if(searchVo.getStartTime() != 0 && searchVo.getEndTime() != 0){
            sql.append(" and UNIX_TIMESTAMP(msg.alarm_time)*1000 between ").append(searchVo.getStartTime()).append(" and ").append(searchVo.getEndTime());
        }
        if(searchVo.getStatus() != null){
            sql.append(" and msg.status=").append(searchVo.getStatus());
        }
        return sql.toString();
    }

    private List<String> buildSearchUserStr(Integer userId){
        List<String> list = Lists.newArrayList("[%s]","[%s,",",%s,",",%s]");
        return list.stream().map(a -> String.format(a, userId)).collect(Collectors.toList());
    }

    public List<PlaceAlarmMsgVo> getRecentAlarmMsgs(Long id) {
        AlarmArea areaInfo = alarmAreaDao.findById(id).orElseThrow(()->new IllegalArgumentException("area id not exist"));
        //求中心点
        List<Point> points = areaInfo.getGeography().getPoints();
        double placeLat = points.stream().mapToDouble(Point::getLatitude).average().getAsDouble();//平均值
        double placeLng = points.stream().mapToDouble(Point::getLongitude).average().getAsDouble();//平均值
        int listSize = 50;
        String listSql = "select a.alarm_suspect,a.alarm_time,b.`longitude`,b.`latitude` " +
                "from bkyj_alarm_msg a " +
                "left join t_device b on a.device_id=b.id " +
                "where a.alarm_type = 1 " +
                "order by alarm_time desc limit " + listSize;
        Query<?> query = (Query<?>) entityManager.createNativeQuery(listSql);
        @SuppressWarnings("unchecked")
        List<Object[]> list = (List<Object[]>) query.list();
        List<PlaceAlarmMsgVo> datas = Lists.newArrayList();

        for (Object[] objects : list) {
            String suspect = objects[0] == null ? null : objects[0].toString();
            Long happen = objects[1] == null ? null : ((Timestamp) objects[1]).getTime();

            Long longitude = objects[2] == null ? 0L : Long.parseLong(objects[2].toString());
            Long latitude = objects[3] == null ? 0L : Long.parseLong(objects[3].toString());

            long timeDiff = (System.currentTimeMillis() - happen) / (1000 * 60);
            if (timeDiff < 0) {
                log.warn("msg record time:{} is later than current time:{}",
                        new Date(happen), new Date(happen + timeDiff));
            }
            // 只返回1天之内的消息
            if (timeDiff < 24 * 6 && timeDiff >= 0) {
                double lat = latitude / 1000000000000.0;
                double lng = longitude / 1000000000000.0;
                // TODO: 改为根据placeId获取场所信息
                String place = areaInfo.getName();
                long distance = (long) LocationUtils.distance(lng, lat, placeLng, placeLat);
                PlaceAlarmMsgVo alarmMsgVo = new PlaceAlarmMsgVo(suspect, timeDiff, place, distance);
                datas.add(alarmMsgVo);
            }
        }
        return datas;
    }
}
