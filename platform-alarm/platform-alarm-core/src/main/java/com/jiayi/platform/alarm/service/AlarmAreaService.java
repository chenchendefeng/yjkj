package com.jiayi.platform.alarm.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiayi.platform.alarm.dto.*;
import com.jiayi.platform.basic.dao.DeviceDao;
import com.jiayi.platform.basic.dao.DeviceSubTypeDao;
import com.jiayi.platform.basic.dto.DeviceLocationDetailDto;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.util.RedisUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.dto.PageResult;
import com.jiayi.platform.report.dao.mysql.AlarmAreaDao;
import com.jiayi.platform.report.entity.AlarmArea;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlarmAreaService {
	private static Logger log = LoggerFactory.getLogger(AlarmAreaService.class);

	@Autowired
	private AlarmAreaDao alarmAreaDao;

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private DeviceSubTypeDao deviceSubTypeDao;

	@Autowired
	private RedisUtil redisUtil;

	private static String ALARM_CONFIG = "ALARM_CONFIG";
	private static Map<Long, Set<AlarmAreaDeviceDto>> areaDevice = Maps.newHashMap();

	public void modConfig (AlarmConfRequest request) {
		redisUtil.put(ALARM_CONFIG, request);// todo 布控预警设置存redis?
	}

	public AlarmConfRequest findAlarmConfig () {
		AlarmConfRequest request = redisUtil.get(ALARM_CONFIG);
		if (request == null) {
			request = new AlarmConfRequest();
		}
		return request;// todo 布控预警设置存redis?
	}

	private Set<AlarmAreaDeviceDto> getAreaDevices (AlarmArea alarmAreaInfo, Set<AlarmAreaDeviceDto> devices) {
		Set<AlarmAreaDeviceDto> tempDevices = new HashSet<>();
		for (AlarmAreaDeviceDto device : devices) {
			if (alarmAreaInfo.getId() != 0) {
				//检查设备在不在告警区域
				if (alarmAreaInfo != null
						&& !alarmAreaInfo.getGeography().isWithin(device.getFloatLatitude(), device.getFloatLongitude())) {
					continue;
				}
				device.addAlarmAreaId(alarmAreaInfo.getId());
			}
			tempDevices.add(device);
		}

		return tempDevices;
	}

	//fixme DeviceLocationDetailDto和AlarmAreaDeviceDto相似，是否可以直接查询，去掉转换过程
	private Set<AlarmAreaDeviceDto> getAllDevices () {
		Set<DeviceLocationDetailDto> deviceLocations = deviceDao.selectDeviceLocation();
		Set<AlarmAreaDeviceDto> devices = new HashSet<>();
		deviceLocations.forEach(dto -> {
			AlarmAreaDeviceDto device = new AlarmAreaDeviceDto(dto);
			devices.add(device);
		});
		return devices;
	}

	/**
	 * 根据区域ID取得告警区域下所有的设备
	 */
	public Set<AlarmAreaDeviceDto> setAlarmAreaDevices (AlarmArea alarmAreaInfo) {
		Set<AlarmAreaDeviceDto> devices = getAllDevices();
		Set<AlarmAreaDeviceDto> tempDevices = getAreaDevices(alarmAreaInfo, devices);
		if (alarmAreaInfo.getParentId() == 0) {
			return tempDevices;
		}
		AlarmArea areaInfo = findById(alarmAreaInfo.getParentId());
		Set<AlarmAreaDeviceDto> parentDevices = getAreaDevices(areaInfo, devices);
		tempDevices = tempDevices.stream().filter(a -> parentDevices.contains(a)).collect(Collectors.toSet());
		areaDevice.put(alarmAreaInfo.getId(), tempDevices);
		return tempDevices;
	}

	public AlarmArea add (AlarmAreaRequest request) {
		int count = alarmAreaDao.isNameUsed(request.getName(), request.getParentAreaId());
		if (count > 0) {
			throw new ArgumentException("预警区域名称重复");
		}
		double area = calculateArea(request.getMapRegion());
//        if(area<=0){
//            throw new ArgumentException("预警区域不是合法多边形，不能交叉！");
//        }
		AlarmArea areaInfo = new AlarmArea();
		areaInfo.setName(request.getName());
		areaInfo.setMapRegion(JSON.toJSONString(request.getMapRegion()));
		areaInfo.setArea(Math.floor(area / 10000.0));
		areaInfo.setStartTime(new Date(request.getStartTime()));
		areaInfo.setEndTime(new Date(request.getEndTime()));
		areaInfo.setWarningNum(request.getWarningNum());
		areaInfo.setMaxNum(request.getMaxNum());
		areaInfo.setPeriod(request.getPeriod());
		areaInfo.setFactor(request.getFactor());
		areaInfo.setEnable(request.getEnable() != 0);
		areaInfo.setValid(true);
		areaInfo.setParentId(request.getParentAreaId());
		Date curDate = new Date();
		areaInfo.setCreateAt(curDate);
		areaInfo.setUpdateAt(curDate);
		AlarmArea savedInfo = alarmAreaDao.save(areaInfo);
		ThreadPoolUtil.getInstance().submit(() -> {
			setAlarmAreaDevices(savedInfo);
		});
		log.info("Add alarm area {} successfully.", savedInfo.getId());
		return savedInfo;
	}

	public void delete (Long id) {
		if (!alarmAreaDao.existsById(id)) {
			log.error("area id:{} not exist", id);
			throw new IllegalArgumentException("area id not exist");
		}
//        alarmAreaDao.setValid(id, false);

		Specification<AlarmArea> specification = (root, query, cb) -> {
			List<Predicate> listWhere = new ArrayList<>();
			listWhere.add(cb.equal(root.get("parentId"), id));
			listWhere.add(cb.equal(root.get("id"), id));
			Predicate[] predicatesWhereArr = new Predicate[listWhere.size()];
			Predicate predicatesWhere = cb.or(listWhere.toArray(predicatesWhereArr));
			return query.where(predicatesWhere).getRestriction();
		};
		List<AlarmArea> listAlarmArea = alarmAreaDao.findAll(specification);
		alarmAreaDao.deleteAll(listAlarmArea);

		log.info("Delete alarm area {} successfully.", id);
	}

	public List<AlarmArea> batchUpdate (AlarmBatchModRequest request) {
		List<AlarmArea> areaInfos = Lists.newArrayList();
		AlarmAreaRequest alarmAreaRequest = new AlarmAreaRequest();
		alarmAreaRequest.setEnable(request.getEnable());
		alarmAreaRequest.setFactor(request.getFactor());
		alarmAreaRequest.setMaxNum(request.getMaxNum());
		alarmAreaRequest.setWarningNum(request.getWarningNum());
		alarmAreaRequest.setPeriod(request.getPeriod());
		for (int i = 0; i < request.getAreaIds().length; i++) {
			AlarmArea areaInfo = update(request.getAreaIds()[i], alarmAreaRequest);
			areaInfos.add(areaInfo);
		}
		return areaInfos;
	}

	public AlarmArea findById (Long id) {
		return alarmAreaDao.findById(id).orElseThrow(() -> new IllegalArgumentException("area id not exist"));
	}

	public AlarmArea update (Long id, AlarmAreaRequest request) {
		AlarmArea areaInfo = findById(id);
		if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(areaInfo.getName())) {
			int count = alarmAreaDao.isNameUsed(request.getName(), request.getParentAreaId());
			if (count > 0) {
				throw new ArgumentException("预警区域名称重复");
			}
		}
		updateAreaInfo(areaInfo, request);
		alarmAreaDao.save(areaInfo);
		ThreadPoolUtil.getInstance().submit(() -> {
					setAlarmAreaDevices(areaInfo);
				}
		);
		log.info("Update alarm area {} successfully.", id);
		return areaInfo;
	}

	public PageResult list (AlarmAreaQueryRequest request) {
		List<AlarmAreaResponse> responseList = new ArrayList<>();

		Specification<AlarmArea> specification = (root, query, cb) -> {
			List<Predicate> listOr = new ArrayList<Predicate>();
			Predicate p1 = cb.equal(root.get("parentId"), request.getParentAreaId());
			listOr.add(cb.or(p1));
			if (1 == request.getIsParentArea()) {
				Predicate p2 = cb.equal(root.get("id"), request.getParentAreaId());
				listOr.add(cb.or(p2));
			}

			List<Predicate> listAnd = new ArrayList<Predicate>();
			listAnd.add(cb.equal(root.get("valid"), true));
//            list.add(cb.equal(root.get("enable"), request.getEnable()));
			if (StringUtils.isNotBlank(request.getName())) {
				listAnd.add(cb.like(root.get("name"), "%" + request.getName().trim() + "%"));
			}

			Predicate predicateAnd = cb.and(listAnd.toArray(new Predicate[listAnd.size()]));
			Predicate predicateOr = cb.or(listOr.toArray(new Predicate[listOr.size()]));
			return query.where(predicateAnd, predicateOr).getRestriction();
		};

		List<Sort.Order> orders = new ArrayList<>();
		orders.add(new Sort.Order(Sort.Direction.DESC, "id"));

		if (request.getPage() == null) {
			request.setPage(0);
		}
		if (request.getSize() == null) {
			request.setSize(10);
		}

		Pageable pageable = new PageRequest(request.getPage(), request.getSize(), new Sort(orders));
		Page<AlarmArea> iter = alarmAreaDao.findAll(specification, pageable);
		Map<Long, Long> subArea = subAreaNum();
		for (Iterator<AlarmArea> areaInfoIt = iter.iterator(); areaInfoIt.hasNext(); ) {
			AlarmAreaResponse response = new AlarmAreaResponse();
			AlarmArea areaInfo = areaInfoIt.next();
			response.setId(areaInfo.getId());
			response.setName(areaInfo.getName());
			response.setMapRegion(JSON.parseObject(areaInfo.getMapRegion(), AlarmRegion.class));
			response.setArea(areaInfo.getArea());
//            response.setPlaceId(areaInfo.getPlaceId().toString());
			response.setStartTime(areaInfo.getStartTime().getTime());
			response.setEndTime(areaInfo.getEndTime().getTime());
			response.setWarningNum(areaInfo.getWarningNum());
			response.setMaxNum(areaInfo.getMaxNum());
			response.setPeriod(areaInfo.getPeriod());
			response.setFactor(areaInfo.getFactor());
			response.setEnable(areaInfo.getEnable() ? 1 : 0);
			List<Long> areaIds = Lists.newArrayList();
			areaIds.add(areaInfo.getId());
			List<AreaDevice> areaDevices = getDevicesInAreas(areaIds);
			response.setDeviceNum(areaDevices.get(0).getDeviceIds().size());
			if (areaInfo.getParentId() == 0) {
				Long subAreaNum = subArea.get(areaInfo.getId()) == null ? 0L : subArea.get(areaInfo.getId());
				response.setSubAreaNum(subAreaNum);
			}
			responseList.add(response);
		}
		List<AlarmArea> list = iter.getContent();
		return new PageResult<>(responseList, iter.getTotalElements(), request.getPage(), list.size());
	}

	private Map<Long, Long> subAreaNum () {
		List<Object[]> result = alarmAreaDao.subAreaNum();
		Map<Long, Long> map = null;
		if (result != null && !result.isEmpty()) {
			map = new HashMap<>();
			for (Object[] object : result) {
				map.put(((Long) object[0]), (Long) object[1]);
			}
		}
		return map;
	}

	public List<AreaDevice> getDevicesInAreas (List<Long> areaIds) {
		long start = System.currentTimeMillis();
		List<AreaDevice> results = new ArrayList<>();
		Iterable<AlarmArea> areaInfoList = alarmAreaDao.findAllById(areaIds);
		for (AlarmArea areaInfo : areaInfoList) {
			List<String> deviceIds = new ArrayList<>();
			Set<AlarmAreaDeviceDto> devices = areaDevice.get(areaInfo.getId());
			if (devices != null) {
				devices.forEach(a -> {
					deviceIds.add(a.getDeviceId() + "");
				});
			} else {
				Set<AlarmAreaDeviceDto> devices1 = setAlarmAreaDevices(areaInfo);
				devices1.forEach(a -> {
					deviceIds.add(a.getDeviceId() + "");
				});
			}
			results.add(new AreaDevice(areaInfo.getId(), deviceIds));
		}
		log.debug("fetch device list using: " + (System.currentTimeMillis() - start) + "ms.");
		return results;
	}

//    public JsonObject<List<AreaDevice>> getDevicesInAreas(List<Long> areaIds) {
//        long start = System.currentTimeMillis();
//        List<AreaDevice> results = new ArrayList<>();
//        Iterable<AdjustDeviceLocation> deviceList = adjustDeviceDao.findAll();
//        Iterable<AlarmAreaInfo> areaInfoList = alarmAreaDao.findAllById(areaIds);
//        for (AlarmAreaInfo areaInfo : areaInfoList) {
//            AlarmRegion region = JSON.parseObject(areaInfo.getMapRegion(), AlarmRegion.class);
//            if (region.getPoints().size() < 4) {
//                log.error("area points size is not 4");
//                throw new IllegalArgumentException("area points size is not 4");
//            }
//            double minLat = region.getPoints().get(0).getLatitude();
//            double minLng = region.getPoints().get(0).getLongitude();
//            double maxLat = region.getPoints().get(0).getLatitude();
//            double maxLng = region.getPoints().get(0).getLongitude();
//            for (int i = 1; i < 4; i++) {
//                minLat = Math.min(minLat, region.getPoints().get(i).getLatitude());
//                minLng = Math.min(minLng, region.getPoints().get(i).getLongitude());
//                maxLat = Math.max(maxLat, region.getPoints().get(i).getLatitude());
//                maxLng = Math.max(maxLng, region.getPoints().get(i).getLongitude());
//            }
//            List<String> deviceIds = new ArrayList<>();
//            for (AdjustDeviceLocation device : deviceList) {
//                double lat = device.getLatitude() / 1000000000000.0;
//                double lng = device.getLongitude() / 1000000000000.0;
//                if (lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng) {
//                    deviceIds.add(device.getId().toString());
//                }
//            }
//            results.add(new AreaDevice(areaInfo.getId(), deviceIds));
//        }
//        log.debug("fetch device list using: " + (System.currentTimeMillis() - start) + "ms.");
//        return new JsonObject<>(results);
//    }

	public void adjustDeviceLocation (DeviceLocation deviceLocation) {
		long longitudeL = (long) (deviceLocation.getLongitude() * 1000000000000L);
		long latitudeL = (long) (deviceLocation.getLatitude() * 1000000000000L);
		//同时纠正设备的经纬度
		deviceDao.adjustDevice(Long.parseLong(deviceLocation.getId()), longitudeL, latitudeL, new Date());
		//更新redis缓存数据
//        ThreadPoolUtil.getInstance().submit(() -> {
//                    deviceManager.getAllDevicesByDB(true);
//                }
//        );
	}

	public JsonObject<List<DeviceVo>> getAdjustedDevices (Long areaId, Integer deviceType) {
		List<DeviceSubType> deviceSubTypes = deviceSubTypeDao.findAll();
		Map<Integer, List<DeviceSubType>> deviceSubTypeMap = deviceSubTypes.stream()
				.collect(Collectors.groupingBy(DeviceSubType::getDeviceType));
		List<DeviceSubType> selectDeviceSubTypes = deviceSubTypeMap.get(deviceType);
		List<DeviceVo> results = new ArrayList<>();
		long start = System.currentTimeMillis();
		Set<AlarmAreaDeviceDto> deviceList = getAllDevices();
		if (CollectionUtils.isEmpty(selectDeviceSubTypes)) {
			for (AlarmAreaDeviceDto device : deviceList) {
				DeviceVo result = new DeviceVo(device.getId(), device.getLongitude() / 1000000000000.0,
						device.getLatitude() / 1000000000000.0, device.getPlaceId(), device.getName());
				results.add(result);
			}
			return new JsonObject<>(results);
		}
		if (areaId == 0) {
			for (AlarmAreaDeviceDto device : deviceList) {
				if (isSelectDeviceType(selectDeviceSubTypes, device)) {
					DeviceVo result = new DeviceVo(device.getId(), device.getLongitude() / 1000000000000.0,
							device.getLatitude() / 1000000000000.0, device.getPlaceId(), device.getName());
					results.add(result);
				}
			}
		} else {
			List<Long> areaIds = Lists.newArrayList();
			areaIds.add(areaId);
			Iterable<AlarmArea> areaInfoList = alarmAreaDao.findAllById(areaIds);
			for (AlarmArea alarmAreaInfo : areaInfoList) {
				for (AlarmAreaDeviceDto device : deviceList) {
					if (isSelectDeviceType(selectDeviceSubTypes, device)) {
						if (alarmAreaInfo.getId() != 0) {
							//检查设备在不在告警区域
							if (alarmAreaInfo != null
									&& !alarmAreaInfo.getGeography().isWithin(device.getFloatLatitude(), device.getFloatLongitude())) {
								continue;
							}
							DeviceVo result = new DeviceVo(device.getId(), device.getLongitude() / 1000000000000.0,
									device.getLatitude() / 1000000000000.0, device.getPlaceId(), device.getName());
							results.add(result);
						}
					}
				}
			}
		}
		log.debug("fetch devices using: " + (System.currentTimeMillis() - start) + "ms, device num: " + results.size());
		return new JsonObject<>(results);
	}

	private boolean isSelectDeviceType (List<DeviceSubType> selectDeviceSubTypes, AlarmAreaDeviceDto device) {
		List<Integer> types = selectDeviceSubTypes.stream().map(DeviceSubType::getId).collect(Collectors.toList());
		int type = (int) device.getType();
		return types.contains(type);
	}

	private double calculateArea (AlarmRegion region) {
		if (region.getPoints().size() < 4) {
			log.error("area points size is not 4");
			throw new IllegalArgumentException("area points size is not 4");
		}
		if (region.getType().toLowerCase().equals("rect")) {
			log.error("area type: " + region.getType() + " not supported");
			double minLat = region.getPoints().get(0).getLatitude();
			double minLng = region.getPoints().get(0).getLongitude();
			double maxLat = region.getPoints().get(0).getLatitude();
			double maxLng = region.getPoints().get(0).getLongitude();
			for (int i = 1; i < 4; i++) {
				minLat = Math.min(minLat, region.getPoints().get(i).getLatitude());
				minLng = Math.min(minLng, region.getPoints().get(i).getLongitude());
				maxLat = Math.max(maxLat, region.getPoints().get(i).getLatitude());
				maxLng = Math.max(maxLng, region.getPoints().get(i).getLongitude());
			}
			return LocationUtils.rectArea(minLng, minLat, maxLng, maxLat);
		} else if (region.getType().toLowerCase().equals("polygon")) {
			List<double[]> points = new ArrayList<>();
			PolygonArea tp = new PolygonArea();
			region.getPoints().forEach(a -> {
				double[] point = new double[2];
				point[0] = a.getLongitude();
				point[1] = a.getLatitude();
				points.add(point);
			});
			if (tp.isIntersectsLine(points)) {
				throw new ArgumentException("预警区域不是合法多边形，不能交叉！");
			}
			return tp.calculateArea(points);
		}
		return 0;
	}

	private void updateAreaInfo (AlarmArea areaInfo, AlarmAreaRequest request) {
		if (StringUtils.isNotBlank(request.getName()))
			areaInfo.setName(request.getName());
		if (request.getMapRegion() != null) {
			areaInfo.setMapRegion(JSON.toJSONString(request.getMapRegion()));
			areaInfo.setArea(calculateArea(request.getMapRegion()));
		}
		if (request.getPlaceId() != null)
			areaInfo.setPlaceId(Long.parseLong(request.getPlaceId()));
		if (request.getStartTime() != null)
			areaInfo.setStartTime(new Date(request.getStartTime()));
		if (request.getEndTime() != null)
			areaInfo.setEndTime(new Date(request.getEndTime()));
		if (request.getWarningNum() != null)
			areaInfo.setWarningNum(request.getWarningNum());
		if (request.getMaxNum() != null)
			areaInfo.setMaxNum(request.getMaxNum());
		if (request.getPeriod() != null)
			areaInfo.setPeriod(request.getPeriod());
		if (request.getFactor() != null)
			areaInfo.setFactor(request.getFactor());
		if (request.getEnable() != null)
			areaInfo.setEnable(request.getEnable() != 0);

		areaInfo.setUpdateAt(new Date());
	}
}
