package com.jiayi.platform.alarm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.jiayi.platform.alarm.dao.AlarmDistrictDao;
import com.jiayi.platform.alarm.dto.AlarmDistrictDto;
import com.jiayi.platform.alarm.dto.AlarmDistrictRequest;
import com.jiayi.platform.alarm.dto.AlarmDistrictSearchVo;
import com.jiayi.platform.alarm.entity.AlarmDistrict;
import com.jiayi.platform.alarm.util.CityCodeUtil;
import com.jiayi.platform.alarm.util.DataUtil;
import com.jiayi.platform.basic.dao.CityDao;
import com.jiayi.platform.basic.dto.CityDto;
import com.jiayi.platform.basic.entity.City;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlarmDistrictService {

    private final static Logger LOG = LoggerFactory.getLogger(AlarmDistrictService.class);

    @Autowired
    private AlarmDistrictDao alarmDistrictDao;
    //    @Autowired
//    private RedisUtil redisUtil;
    @Autowired
    private CityDao cityDao;

    private static List<CityDto> cityList = null;

    private final static String SEPRATOR = ",";

    public PageResult<AlarmDistrictDto> findAlarmDistrictList(AlarmDistrictSearchVo searchVo) {
        Specification<AlarmDistrict> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotBlank(searchVo.getName())) {
                list.add(cb.like(root.get("name"), "%" + searchVo.getName().trim() + "%"));
            }
            if (searchVo.getType() != null) {
                list.add(cb.equal(root.get("type"), searchVo.getType()));
            }
            if (searchVo.getDistrict() != null) {
                list.add(cb.equal(root.get("district"), searchVo.getDistrict()));
            }
            if (searchVo.getStartTime() != null && searchVo.getEndTime() != null
                    && searchVo.getStartTime().getTime() != searchVo.getEndTime().getTime()) {
                list.add(cb.between(root.get("createAt"), searchVo.getStartTime(), searchVo.getEndTime()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        };
        Sort sort = new Sort(Sort.Direction.DESC, "updateAt");
        Pageable pageable = PageRequest.of(searchVo.getPage(), searchVo.getSize(), sort);
        Page<AlarmDistrict> page = alarmDistrictDao.findAll(specification, pageable);
        List<AlarmDistrict> list = page.getContent();

        return new PageResult<>(transform(list), page.getTotalElements(), searchVo.getPage(), list.size());
    }

    private List<AlarmDistrictDto> transform(List<AlarmDistrict> list) {
        return list.stream().map(alarmDistrict -> {
            BeanCopier copier = BeanCopier.create(AlarmDistrict.class, AlarmDistrictDto.class, false);
            AlarmDistrictDto alarmDistrictDto = new AlarmDistrictDto();
            copier.copy(alarmDistrict, alarmDistrictDto, null);
            if (alarmDistrict.getBeLongValid() == 1) {
                alarmDistrictDto.setStartTime(null);
                alarmDistrictDto.setEndTime(null);
            }
            String userId = alarmDistrict.getUserId();
            alarmDistrictDto.setObjTypes(Arrays.asList(alarmDistrict.getObjType().split(SEPRATOR)));
            alarmDistrictDto.setUserIds(Arrays.asList(userId.substring(1, userId.length() - 1).split(SEPRATOR)));
            return alarmDistrictDto;
        }).collect(Collectors.toList());
    }

    public AlarmDistrict findById(Long id) {
        return alarmDistrictDao.findById(id).orElseThrow(() -> new DBException("find alarmdistrict by id error"));
    }

    public AlarmDistrict addAlarmDistrict(AlarmDistrictRequest request) {
        DataUtil.checkData(request.getType(), request.getMapRegion(), request.getExInfo());
        AlarmDistrict alarmDistrict = new AlarmDistrict();
        this.setAlarmDistrict(alarmDistrict, request);
        Date date = new Date();
        alarmDistrict.setCreateAt(date);
        alarmDistrict.setUpdateAt(date);
        alarmDistrict.setStatus(1);
        try {
            alarmDistrictDao.save(alarmDistrict);
        } catch (Exception e) {
            LOG.error("alarm district add error, request {}", request);
            throw new ArgumentException("alarm district add error", e);
        }
//        long startTime = alarmDistrict.getStartTime().getTime();
//        long endTime = alarmDistrict.getEndTime().getTime();
//        if(startTime < date.getTime() && endTime > date.getTime() || request.getBeLongValid() == 1){//在有效期内存入redis
//            ThreadPoolUtil.getInstance().submit(() -> addDistrictTouchAlarm(alarmDistrict));
//        }
        return alarmDistrict;
    }

    public AlarmDistrict updateAlarmDistrict(Long id, AlarmDistrictRequest request) {
        DataUtil.checkData(request.getType(), request.getMapRegion(), request.getExInfo());
        AlarmDistrict alarmDistrict = findById(id);
        setAlarmDistrict(alarmDistrict, request);
        Date date = new Date();
        alarmDistrict.setUpdateAt(date);
        try {
            alarmDistrictDao.save(alarmDistrict);
        } catch (Exception e) {
            LOG.error("alarm district update error, request {}", request);
            throw new ArgumentException("alarm district update error", e);
        }
//        ThreadPoolUtil.getInstance().submit(() -> updateDistrictTouchAlarm(alarmDistrict));
        return alarmDistrict;
    }

    public void deleteAlarmDistrict(Long id) {
//        AlarmDistrict alarmDistrict = findById(id);
        try {
            alarmDistrictDao.deleteById(id);
//            ThreadPoolUtil.getInstance().submit(() -> deleteDistrictTouchAlarm(alarmDistrict));
        } catch (Exception e) {
            throw new ArgumentException("alarm district delete error", e);
        }
    }

    private void setAlarmDistrict(AlarmDistrict alarmDistrict, AlarmDistrictRequest request) {
        alarmDistrict.setName(request.getName());
        alarmDistrict.setType(request.getType());
        if (request.getMapRegion() != null)
            alarmDistrict.setMapRegion(JSON.toJSONString(request.getMapRegion(), SerializerFeature.DisableCircularReferenceDetect));
        if (request.getExInfo() != null)
            alarmDistrict.setExInfo(JSON.toJSONString(request.getExInfo(), SerializerFeature.DisableCircularReferenceDetect));
        alarmDistrict.setDistrict(request.getDistrict());
        alarmDistrict.setObjType(StringUtils.join(request.getObjTypes(), SEPRATOR));
        if (request.getBeLongValid() == 1) {
            alarmDistrict.setStartTime(new Date());
            alarmDistrict.setEndTime(new Date(7952313600000L));//设置长期时间
        } else {
            alarmDistrict.setStartTime(request.getStartTime());
            alarmDistrict.setEndTime(request.getEndTime());
        }
        alarmDistrict.setBeLongValid(request.getBeLongValid());
        alarmDistrict.setUserId(String.format("[%s]", StringUtils.join(request.getUserIds(), SEPRATOR)));
        alarmDistrict.setRemark(request.getRemark());
    }

//    /**
//     * 根据布控类型选择对应写入redis方法（目前只有触碰类型）
//     * @param alarmDistrict
//     */
//    private void addDistrictAlarm(AlarmDistrict alarmDistrict) {
//        AlarmDistrictType type = AlarmDistrictType.getDistrictTouchAlarm(alarmDistrict.getType());
//        switch (type){
//            case DISTRICT_TOUCH_ALARM:
//                addDistrictTouchAlarm(alarmDistrict);
//                break;
//            default:break;
//        }
//    }

//    /**
//     * 添加地域触碰
//     * @param alarmDistrict
//     */
//    private void addDistrictTouchAlarm(AlarmDistrict alarmDistrict) {
//        LOG.info("start add alarmDistrict to redis");
//        String key = CacheKeyUtils.getDistrictAlarmKey(alarmDistrict.getType());
//        List<AlarmDistrictTouchInfo> list = Arrays.stream(alarmDistrict.getObjType().split(SEPRATOR))
//                .map(a -> new AlarmDistrictTouchInfo(alarmDistrict.getId(), alarmDistrict.getDistrict(), a))
//                .collect(Collectors.toList());
//        List<AlarmDistrictTouchInfo> result = redisUtil.get(key);
//        if(result == null)
//            result = new ArrayList<>();
//        result.addAll(list);
//        redisUtil.put(key, result);
//        LOG.info("add alarmDistrict to redis success");
//    }
//
//    /**
//     * 删除地域触碰
//     * @param alarmDistrict
//     */
//    private void deleteDistrictTouchAlarm(AlarmDistrict alarmDistrict) {
//        String key = CacheKeyUtils.getDistrictAlarmKey(alarmDistrict.getType());
//        List<AlarmDistrictTouchInfo> result = redisUtil.get(key);
//        if(CollectionUtils.isNotEmpty(result)){
//            redisUtil.put(key, result.stream().filter(a -> a.getId() != alarmDistrict.getId()).collect(Collectors.toList()));
//        }
//        LOG.info("delete alarmDistrict from redis success");
//    }

//    /**
//     * 更新地域触碰
//     * @param alarmDistrict
//     */
//    private void updateDistrictTouchAlarm(AlarmDistrict alarmDistrict){
//        this.deleteDistrictTouchAlarm(alarmDistrict);
//        Date date = new Date();
//        long startTime = alarmDistrict.getStartTime().getTime();
//        long endTime = alarmDistrict.getEndTime().getTime();
//        if(startTime < date.getTime() && endTime > date.getTime() || alarmDistrict.getBeLongValid() == 1) {
//            this.addDistrictTouchAlarm(alarmDistrict);
//        }
//    }

    @PostConstruct
    public void cityInit() {
        List<City> list = cityDao.findByLevelIn(Lists.newArrayList("1", "2"));
        Map<Long, CityDto> districtMap = list.stream()
                .map(a -> new CityDto(a.getId(), a.getName(), a.getMergerName()))
                .collect(Collectors.toMap(CityDto::getId, Function.identity(), (k1, k2) -> k2));

        Map<Long, List<City>> groupByPidMap = list.stream().collect(Collectors.groupingBy(City::getPid));
        // 第一级省
        List<City> oneGradeCity = groupByPidMap.get(0L);
        List<CityDto> oneGradeCityDto = Lists.newArrayList();
        oneGradeCity.forEach(a -> {
            CityDto city1Dto = new CityDto(a.getId(), a.getName(), a.getMergerName());
            // 第二级市
            List<City> twoGradeCity = groupByPidMap.get(a.getId());
            List<Long> cityIds = Lists.newArrayList(110000L, 120000L, 310000L, 500000L);
            if (CollectionUtils.isNotEmpty(twoGradeCity) && !cityIds.contains(a.getId())) {
                List<CityDto> twoGradeCityDto = Lists.newArrayList();
                twoGradeCity.forEach(b -> {
                    CityDto city2Dto = new CityDto(b.getId(), b.getName(), b.getMergerName());
                    twoGradeCityDto.add(city2Dto);
                });
                city1Dto.setNextLevel(twoGradeCityDto);
            }
            oneGradeCityDto.add(city1Dto);
        });
        CityCodeUtil.districtMap = districtMap;
        cityList = oneGradeCityDto;
    }

    public List<CityDto> getCity() {
        if (cityList == null) {
            cityInit();
        }
        return cityList;
    }

    public void modifyStatusById(Long id, int status) {
        if(status != 0 && status != 1){
            throw new ArgumentException("request status error");
        }
        AlarmDistrict alarmDistrict = findById(id);
        if (status == alarmDistrict.getStatus()) {
            return;
        }
        if (status == 1) {
            long endTime = alarmDistrict.getEndTime().getTime();
            Date date = new Date();
            if (endTime < date.getTime()) {//过期
                throw new ValidException("布控时间已过有效期");
            }
        }
        alarmDistrict.setStatus(status);
        alarmDistrictDao.save(alarmDistrict);
    }
}
