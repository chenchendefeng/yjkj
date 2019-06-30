package com.jiayi.platform.basic.serviceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiayi.platform.basic.dao.*;
import com.jiayi.platform.basic.dto.*;
import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.basic.entity.DeviceTimeStatistic;
import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import com.jiayi.platform.basic.request.DeviceStatSearchRequest;
import com.jiayi.platform.basic.request.FaultDeviceRequest;
import com.jiayi.platform.basic.util.IdGenerator;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.web.util.CsvWriter;
import com.jiayi.platform.common.web.util.ExportUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DeviceCountService {
	public static final Logger log = LoggerFactory.getLogger(DeviceCountService.class);
	//    @Autowired
//    private RedisUtil redisUtil;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private DeviceTimeStreamStatisticDao deviceTimeStreamStatisticDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private DeviceSubTypeDao deviceSubTypeDao;
	@Autowired
	private SrcDao srcDao;
	@Autowired
	private DeviceTimeStatisticDao deviceTimeStatisticDao;
	// todo 配置以下值
	@Value ("${prodcut.city.id:441200}")
	private int cityId;// 配置的城市id
	@Value ("${device.time.err.time:15}")
	private int errTime;// 时间错误范围(单位：分)
	@Value ("${device.online.time:3600}")
	private int onlineTime;// 在线时间范围(单位：秒)
	@Value ("${device.active.time:7200}")
	private int activeTime;// 活跃时间范围(单位：秒)

//    @Autowired
//    private DataCountManager dataCountManager;

	private List<FaultDeviceDto> faultDeviceDto;

	/**
	 * 返回故障设备统计列表
	 *
	 * @param faultDeviceRequest
	 * @return
	 */
	public FaultDevicePageDto<FaultDeviceDto> getFaultDeviceList (FaultDeviceRequest faultDeviceRequest) {
		List<FaultDeviceDto> retFaultDeviceDto = selectFaultDeviceList(faultDeviceRequest);// 查询kudu
		if (CollectionUtils.isEmpty(retFaultDeviceDto)) {
			return new FaultDevicePageDto<FaultDeviceDto>(Lists.newArrayList(), 0l, faultDeviceRequest.getPage(), faultDeviceRequest.getSize(),
					0, 0);
		}
		List<String> codes = retFaultDeviceDto.stream().map(a -> a.getCode()).distinct().collect(Collectors.toList());
		List<String> srcs = retFaultDeviceDto.stream().map(b -> b.getSrc()).distinct().collect(Collectors.toList());
		// Object[] = {srcCode,deviceCode,address,srcName}
		List<Device> addressList = deviceDao.isCodeUsed(codes);
		List<Object[]> srcList = srcDao.findSrcs(srcs);

		Map<String, String> addressMap = new HashMap<>();// key=deviceId, value=address
		addressList.forEach(aMap -> {
			addressMap.put(aMap.getCode() + aMap.getSrc(), aMap.getAddress());
		});

		Map<String, String> srcMap = new HashMap<>();
		srcList.forEach(sMap -> {
			srcMap.put(sMap[0].toString(), sMap[1].toString());
		});

		retFaultDeviceDto.forEach(dto -> {
			dto.setAddress(null == addressMap.get(dto.getCode() + dto.getSrc()) ? "" : addressMap.get(dto.getCode() + dto.getSrc()));
			dto.setSrc(srcMap.get(dto.getSrc()));
		});
		if (faultDeviceRequest.getPage() == null)
			faultDeviceRequest.setPage(0);
		if (faultDeviceRequest.getSize() == null)
			faultDeviceRequest.setSize(10);

		int noCreateCount = retFaultDeviceDto.stream().filter(a -> a.getNoCreate() == 1).collect(Collectors.toList())
				.size();
		int errTimeCount = retFaultDeviceDto.stream().filter(a -> Math.abs(a.getDiffTime()) > errTime * 60 * 1000l)
				.collect(Collectors.toList()).size();
		int start = faultDeviceRequest.getPage() * faultDeviceRequest.getSize();
		int count = retFaultDeviceDto.size();
		int end = count - start > faultDeviceRequest.getSize() ? start + faultDeviceRequest.getSize() : count;
		if (start > end)
			return new FaultDevicePageDto<FaultDeviceDto>(Lists.newArrayList(), (long) count,
					faultDeviceRequest.getPage(), 0, 0, 0);
		List<FaultDeviceDto> list = retFaultDeviceDto.subList(start, end);
		return new FaultDevicePageDto<FaultDeviceDto>(list, (long) count, faultDeviceRequest.getPage(), list.size(),
				noCreateCount, errTimeCount);
	}

	/**
	 * 故障设备统计
	 */
	public List<FaultDeviceDto> selectFaultDeviceList (FaultDeviceRequest faultDeviceRequest) {
//        FaultDeviceRequest search = faultDeviceRequest;
//        if (StringUtils.isNotBlank(faultDeviceRequest.getCode())) {
//            search.setCode("'%" + search.getCode().replaceAll("//s*", "") + "%'");
//        }
//        if (StringUtils.isNotBlank(faultDeviceRequest.getIp())) {
//            search.setIp("'%" + search.getIp().replaceAll("//s*", "") + "%'");
//        }
//        return deviceTimeStreamStatisticDao.getFaultDeviceList(search, (Instant.now().minus(Duration.ofDays(2)).toEpochMilli()),
//                errTime * 60 * 1000l);
		if (null == faultDeviceRequest.getPage())
			faultDeviceRequest.setPage(0);
		if (null == faultDeviceRequest.getSize())
			faultDeviceRequest.setSize(10);
		return this.findFaultDevices(faultDeviceRequest);
//        Sort sort = new Sort(Sort.Direction.DESC, "recordTime");
//        Pageable pageable = PageRequest.of(faultDeviceRequest.getPage(), faultDeviceRequest.getSize(), sort);
//        Page<DeviceTimeStreamStatistic> data = deviceTimeStreamStatisticDao.findAll(this.specification(faultDeviceRequest), pageable);
//        List<FaultDeviceDto> result = new ArrayList<>();
//        data.get().forEach(item -> {
//            FaultDeviceDto dto = new FaultDeviceDto();
//            dto.setSrc(item.getSrc());
//            dto.setCode(item.getCode());
//            dto.setStartTime(item.getDataStartTime());
//            dto.setEndTime(item.getDataEndTime());
//            dto.setIpPort(item.getIpPort());
//            dto.setAddress("?");//todo: Device的信息怎么获取?
////            dto.setNoCreate();//todo: 怎么判断是时间错误还是未录入？
//            dto.setDiffTime((int) (item.getDataEndTime() - item.getRecordTime()));
//            result.add(dto);
//        });
	}

//    private Specification<DeviceTimeStreamStatistic> specification(FaultDeviceRequest request) {
//        return (root, query, cb) -> {
//            List<Predicate> list = new ArrayList<Predicate>();
//            if (StringUtils.isNotBlank(request.getCode())) {
//                String code = request.getCode().replaceAll("//s*", "");
//                list.add(cb.like(root.get("code"), "%" + code.trim() + "%"));
//            }
//            if (StringUtils.isNotBlank(request.getIp())) {
//                String ip = "'%" + request.getIp().replaceAll("//s*", "") + "%'";
//                list.add(cb.like(root.get("ipPort"), "%" + ip.trim() + "%"));
//            }
//            Join<DeviceTimeStreamStatistic, Device> join = root.join("device", JoinType.LEFT);
//            if (null != request.getType()) {// 故障类型：1：未创建，2：时间错误
//                if (1 == request.getType()) {
//                    list.add(cb.isNull(cb.and(cb.equal(root.get("src"), join.get("src")), cb.equal(root.get("code"), join.get("code")))));
//                }
//                if (2 == request.getType()) {
//                    list.add(cb.greaterThanOrEqualTo(cb.abs(cb.diff(root.get("recordTime"), root.get("dataEndTime"))), errTime * 60 * 1000l));
//                }
//            } else {
//                list.add(cb.or(cb.isNull(cb.and(cb.equal(root.get("src"), join.get("src")), cb.equal(root.get("code"), join.get("code")))), cb
// .greaterThanOrEqualTo(cb.abs(cb.diff(root.get("recordTime"), root.get("dataEndTime"))), errTime * 60 * 1000l));)
//            }
//            return cb.and(list.toArray(new Predicate[0]));
//        };
//    }

	private List<FaultDeviceDto> findFaultDevices (FaultDeviceRequest request) {
		StringBuilder hql = new StringBuilder("select new com.jiayi.platform.basic.dto.FaultDeviceDto(\n" +
				"s.src,s.code,s.dataStartTime,s.dataEndTime,\n" +
				"s.ipPort,s.recordTime,d.address,CASE WHEN d.address is null THEN 1 ELSE 0 END,(s.recordTime-s.dataEndTime))\n" +
				"from DeviceTimeStreamStatistic s\n" +
				"left join Device d on d.src=s.src and d.code=s.code\n");
		hql.append(" where s.recordTime>").append(Instant.now().minus(Duration.ofDays(2)).toEpochMilli());
		if (null != request.getCode())
			hql.append(" and s.code like '%").append(request.getCode()).append("%'");
		if (null != request.getIp())
			hql.append(" and s.ipPort like '%").append(request.getIp()).append("%'");
		if (null == request.getType()) {
			hql.append(" and (abs(s.recordTime - s.dataEndTime)>").append(errTime * 60 * 1000l)
					.append(" or d.address is null)");
		} else {
			if (2 == request.getType())
				hql.append(" and abs(s.recordTime - s.dataEndTime)>").append(errTime * 60 * 1000l);
			if (1 == request.getType())
				hql.append(" and d.address is null");
		}
		hql.append(" order by s.recordTime desc");
		try {
			Query<FaultDeviceDto> query = (Query<FaultDeviceDto>) entityManager.createQuery(hql.toString());
			return query.list();
		} catch (Exception e) {
			throw new DBException(ErrorEnum.DB_ERROR.message());
		}
	}

	public void exportDeviceByDistrict (DeviceStatSearchRequest deviceStatSearchRequest, HttpServletResponse resp) {
		try {
			List<DistrictDeviceDto> districtDeviceDtos = getDeviceByCity(deviceStatSearchRequest);
			List<String> contents = Lists.newArrayList();
			contents.add(ExportUtil.genContent(districtDeviceDtos));
			String fileName = "exportDeviceByDistrict_" + System.currentTimeMillis();
			ExportUtil.doExport(contents, "行政区域,场所总数,设备总量,在线数量,离线数量,周活跃设备,30天新增", fileName, resp);
		} catch (Exception e) {
			log.error("exportDeviceAnalysisList search error", e);
			throw new DBException("exportDeviceAnalysisList impala search error", e);
		}
	}

	public void exportDeviceByVender (DeviceStatSearchRequest deviceStatSearchRequest, HttpServletResponse resp) {
		try {
			List<VendorDeviceDto> districtDeviceDtos = getDeviceByVender(deviceStatSearchRequest);
			List<String> contents = Lists.newArrayList();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("utf-8"));
			districtDeviceDtos.forEach(a -> {
				int count = 0;
				for (TypeDeviceDto content : a.getList()) {
					content.setDeviceTotalCount(a.getDeviceTotal());
					String[] colTmpValues = content.getContent().split(",");
					// 在第一行的第一个字段增加供应商
					String[] colValues = new String[colTmpValues.length + 1];
					if (count == 0) {
						colValues[0] = a.getVender();
					} else {
						colValues[0] = "";
					}
					count++;
					for (int i = 1; i < colValues.length; i++) {
						colValues[i] = colTmpValues[i - 1];
					}
					try {
						csvWriter.writeRecord(colValues, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String[] colValues = a.getContent().split(",");
				try {
					csvWriter.writeRecord(colValues, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			csvWriter.flush();
			csvWriter.close();
			byte[] buffer = stream.toByteArray();
			stream.close();
			String data = Charset.forName("utf-8").decode(ByteBuffer.wrap(buffer)).toString();
			contents.add(data);
			String fileName = "exportDeviceByDistrict_" + System.currentTimeMillis();
			ExportUtil.doExport(contents, "供应商,设备类型,场所总数,设备总量,在线数量,离线数量,周活跃设备,30天新增", fileName, resp);
		} catch (Exception e) {
			log.error("exportDeviceAnalysisList search error", e);
			throw new DBException("exportDeviceAnalysisList impala search error", e);
		}
	}

	public void exportDeviceByDepartment (DeviceStatSearchRequest deviceStatSearchRequest, HttpServletResponse resp) {
		try {
			List<DepartmentDeviceDto> departDeviceDtos = getDeviceByDepartment(deviceStatSearchRequest).getData();
			List<String> contents = Lists.newArrayList();
			contents.add(ExportUtil.genContent(departDeviceDtos));
			String fileName = "exportDeviceByDepartment_" + System.currentTimeMillis();
			ExportUtil.doExport(contents, "所属部门,设备总量,在线数量,离线数量,合格,不合格,待定,7天上报,30天新增", fileName, resp);
		} catch (Exception e) {
			log.error("exportDeviceAnalysisList search error", e);
			throw new DBException("exportDeviceAnalysisList impala search error", e);
		}
	}

	public List<VendorAndDeviceStatusDto> getDevice () {
		String statSql = getDeviceByCitySql();
		try {
			Query<?> query = (Query<?>) entityManager.createNativeQuery(statSql);
			List<Object[]> objects = (List<Object[]>) query.list();
			List<VendorAndDeviceStatusDto> deviceVendors = new ArrayList<>();
			Map<String, DeviceTimeStreamStatistic> deviceStatusMap = this.getDeviceStatusMap();
			Map<String, DeviceTimeStatistic> deviceThresholdMap = this.getDeviceThresholdMap();
			objects.forEach(a -> {
				VendorAndDeviceStatusDto deviceVendor = new VendorAndDeviceStatusDto(a, true);
				setDeviceStatus(deviceVendor, deviceStatusMap, deviceThresholdMap);
				deviceVendors.add(deviceVendor);
			});
			return deviceVendors;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(ErrorEnum.DB_ERROR.message(), e);
		}
	}

	private String getDeviceByCitySql(){
		String statSql = "SELECT device.create_at createAt,city.id cityId,city.name NAME,device.id deviceId,place.id placeId,\n" +
				"subType.id deviceType,subType.`name` subName,CONCAT(device.src,device.`code`)\n" +
				"FROM `t_device` device\n" +
				"LEFT JOIN `t_place` place ON place.`id`=device.`place_id`\n" +
				"LEFT JOIN `code_city` city  ON place.`district`=city.`id`\n" +
				"LEFT JOIN `t_device_sub_type` subType ON subType.id = device.type\n" +
				"WHERE place.city=" + cityId +
				" AND city.id IS NOT NULL";
		return statSql;
	}

	public List<DistrictDeviceDto> getDeviceByCity (DeviceStatSearchRequest deviceStatSearchRequest) {
		String statSql = getDeviceByCitySql();
		if (StringUtils.isNotBlank(deviceStatSearchRequest.getDistrict())) {
			statSql += " AND place.district=" + deviceStatSearchRequest.getDistrict();
		}
		if (deviceStatSearchRequest.getDepartmentIds() != null) {
			String setDepartIds = "(" + StringUtils.join(deviceStatSearchRequest.getDepartmentIds()) + ")";
			statSql += " AND place.department_id in " + setDepartIds;
		}
		String dateFormat = "yyyy-MM-dd hh:mm:ss";
		try {
			if (StringUtils.isNotEmpty(deviceStatSearchRequest.getBeginDate()) && StringUtils.isNotEmpty(deviceStatSearchRequest.getEndDate())) {
				DateUtils.parseDate(deviceStatSearchRequest.getBeginDate(), dateFormat);
				DateUtils.parseDate(deviceStatSearchRequest.getEndDate(), dateFormat);
				statSql += " AND device.create_at between '" + deviceStatSearchRequest.getBeginDate() + "'" +
						" AND '" + deviceStatSearchRequest.getEndDate() + "'";
			}
		} catch (ParseException e) {
			log.error("日期ParseException", e);
		}
		if (deviceStatSearchRequest.getPlaceTagId() != null) {
			statSql += " AND place.place_type=" + deviceStatSearchRequest.getPlaceTagId();
		}
		// 这里的deviceType是指设备子类型
		if (deviceStatSearchRequest.getDeviceType() != null) {
			statSql += " AND device.type=" + deviceStatSearchRequest.getDeviceType();
		}
		if (deviceStatSearchRequest.getVendorId() != null) {
			statSql += " AND device.vendor_id=" + deviceStatSearchRequest.getVendorId();
		}
		try {
			Query<?> query = (Query<?>) entityManager.createNativeQuery(statSql);
			List<Object[]> objects = (List<Object[]>) query.list();
			List<VendorAndDeviceStatusDto> deviceVendors = new ArrayList<>();
			Map<Integer, DistrictDeviceDto> districtIdMap = Maps.newHashMap();
			Map<String, DeviceTimeStreamStatistic> deviceStatusMap = this.getDeviceStatusMap();
			Map<String, DeviceTimeStatistic> deviceThresholdMap = this.getDeviceThresholdMap();
			objects.forEach(a -> {
				VendorAndDeviceStatusDto deviceVendor = new VendorAndDeviceStatusDto(a, true);
				setDeviceStatus(deviceVendor, deviceStatusMap, deviceThresholdMap);
				if (deviceStatSearchRequest.getIsOnline() == -1
						|| deviceVendor.getIsOnline() == deviceStatSearchRequest.getIsOnline()) {
					deviceVendors.add(deviceVendor);
					DistrictDeviceDto districtDeviceDto = new DistrictDeviceDto();
					districtDeviceDto.setDistrict(deviceVendor.getName());
					districtDeviceDto.setDistrictId(deviceVendor.getId());
					districtIdMap.put(deviceVendor.getId(), districtDeviceDto);
				}

			});
			Map<Integer, List<VendorAndDeviceStatusDto>> groupByCityMap = deviceVendors.stream()
					.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getId));
			groupByCityMap.keySet().forEach(b -> {
				DistrictDeviceDto districtDeviceDto = districtIdMap.get(b);
				List<VendorAndDeviceStatusDto> venderDevice = groupByCityMap.get(b);
				setDeviceCount(districtDeviceDto, venderDevice, deviceStatSearchRequest.getIsOnline());
			});
			if (log.isDebugEnabled()) {
				Map<Integer, List<VendorAndDeviceStatusDto>> groupByOnline =
                        deviceVendors.stream().collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIsOnline));
				List<VendorAndDeviceStatusDto> onlineDevs = groupByOnline.get(new Integer(1));
				List<DeviceStatusInfoDto> statusList = deviceTimeStreamStatisticDao.findAllDeviceStatus(onlineTime, activeTime);
				List<String> deviceStatusInfoDtos = statusList
						.stream().filter(a -> a.getIsOnline() == 1).map(a -> DeviceStatusInfoDto.getKey(a.getDeviceId())).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(onlineDevs)) {
					List<String> keys = onlineDevs.stream().map(a -> DeviceStatusInfoDto.getKey(a.getDeviceId())).collect(Collectors.toList());
					deviceStatusInfoDtos.removeAll(keys);
					log.debug("在线不在结果里面的数据：" + deviceStatusInfoDtos);
				}
			}
			return districtIdMap.values().stream().collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(ErrorEnum.DB_ERROR.message(), e);
		}
	}

	public List<VendorDeviceDto> getDeviceByVender (DeviceStatSearchRequest deviceStatSearchRequest) {
		String statSql = "SELECT device.create_at createAt,vendor.id id ,vendor.name vendorName,\n" +
				"device.id deviceId,place.id placeId,subType.id typeId,subType.`name`,\n" +
				"CONCAT(device.src,device.`code`)\n" +
				"FROM t_device device\n" +
				"LEFT JOIN `t_vendor` vendor ON vendor.id = device.vendor_id\n" +
				"LEFT JOIN `t_device_sub_type` subType ON subType.id = device.type\n" +
				"LEFT JOIN t_place place ON place.id=device.place_id\n" +
				"WHERE place.city=" + cityId;
		if (StringUtils.isNotBlank(deviceStatSearchRequest.getDistrict())) {
			statSql += " AND place.district=" + deviceStatSearchRequest.getDistrict();
		}
		if (deviceStatSearchRequest.getDepartmentIds() != null) {
			String setDepartIds = "(";
			for (Integer departmentId : deviceStatSearchRequest.getDepartmentIds()) {
				setDepartIds += departmentId + ",";
			}
			setDepartIds = setDepartIds.substring(0, setDepartIds.length() - 1) + ")";
			statSql += " AND place.department_id in " + setDepartIds;
		}
		if (deviceStatSearchRequest.getVendorId() != null) {
			statSql += " AND vendor.id=" + deviceStatSearchRequest.getVendorId();
		}
		String dateFormat = "yyyy-MM-dd hh:mm:ss";
		try {
			if (StringUtils.isNotEmpty(deviceStatSearchRequest.getBeginDate()) && StringUtils.isNotEmpty(deviceStatSearchRequest.getEndDate())) {
				DateUtils.parseDate(deviceStatSearchRequest.getBeginDate(), dateFormat);
				DateUtils.parseDate(deviceStatSearchRequest.getEndDate(), dateFormat);
				statSql += " AND device.create_at between '" + deviceStatSearchRequest.getBeginDate() + "'" + " AND '" + deviceStatSearchRequest.getEndDate() + "'";
			}
		} catch (ParseException e) {
			log.error("device.create_at parse error", e);
		}
		if (deviceStatSearchRequest.getPlaceTagId() != null) {
			statSql += " AND place.place_type=" + deviceStatSearchRequest.getPlaceTagId();
		}
		if (deviceStatSearchRequest.getDeviceType() != null) {
			statSql += " AND device.type=" + deviceStatSearchRequest.getDeviceType();
		}
		try {
			Query<?> query = (Query<?>) entityManager.createNativeQuery(statSql);
			List<Object[]> objects = (List<Object[]>) query.list();
			List<VendorAndDeviceStatusDto> deviceVendors = new ArrayList<>();// 某一供应商的某个设备子类型的统计数据
			Map<Integer, VendorDeviceDto> vendorIdMap = Maps.newHashMap();// 按供应商分组统计
			Map<String, DeviceTimeStreamStatistic> deviceStatusMap = this.getDeviceStatusMap();//查询deviceTimeStreamStatistic
			Map<String, DeviceTimeStatistic> deviceThresholdMap = this.getDeviceThresholdMap();
			objects.forEach(a -> {
				VendorAndDeviceStatusDto deviceVendor;
				if (null != a[1]) {
					deviceVendor = new VendorAndDeviceStatusDto(a, true);
				} else {// 供应商为空设为未知
					deviceVendor = new VendorAndDeviceStatusDto(a, false);
				}
				setDeviceStatus(deviceVendor, deviceStatusMap, deviceThresholdMap);// 设备在线、合格状态
				if (deviceStatSearchRequest.getIsOnline() == -1
						|| deviceVendor.getIsOnline() == deviceStatSearchRequest.getIsOnline()) {
					deviceVendors.add(deviceVendor);// 某一供应商的分组数据
					VendorDeviceDto venderDeviceDto = new VendorDeviceDto();
					venderDeviceDto.setVender(deviceVendor.getName());
					venderDeviceDto.setVerderId(deviceVendor.getId());
					vendorIdMap.put(deviceVendor.getId(), venderDeviceDto);
				}
			});
			Map<Integer, List<VendorAndDeviceStatusDto>> groupByVenderMap = deviceVendors.stream()
					.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getId));//按供应商id分组
			List<DeviceSubType> subTypeList = deviceSubTypeDao.findAll();
			Map<Integer, String> subTypeMap = subTypeList.stream().collect(Collectors.toMap(DeviceSubType::getId, DeviceSubType::getName, (k1, k2) -> k1));
			groupByVenderMap.keySet().forEach(b -> {
				List<VendorAndDeviceStatusDto> venderDevice = groupByVenderMap.get(b);//某一供应商下的集合(含多种设备子类型)
				Map<String, List<VendorAndDeviceStatusDto>> groupByType = venderDevice.stream()
						.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIdAndType));
				groupByType.keySet().forEach(c -> {//集合按子类型再划分，并加上供应商id作为标识
					String[] keys = c.split("\\|");//key = 供应商id + "|" + 设备子类型id;
					List<VendorAndDeviceStatusDto> venderAndTypeDevice = groupByType.get(c);//某一供应商下某个设备子类型的集合
					VendorDeviceDto venderDeviceDto = vendorIdMap.get(new Integer(keys[0]));
					TypeDeviceDto typeDeviceDto = new TypeDeviceDto();
					if (StringUtils.isNotBlank(keys[1]) && !"null".equals(keys[1])) {
						typeDeviceDto.setType(Integer.parseInt(keys[1]));
						typeDeviceDto.setTypeName(subTypeMap.get(Integer.parseInt(keys[1])));//设备子类型的信息
					}
					setDeviceCount(typeDeviceDto, venderAndTypeDevice, deviceStatSearchRequest.getIsOnline());//统计在线设备数，30天新增设备等数据
					venderDeviceDto.add(typeDeviceDto);//添加到list中
				});
			});
			List<VendorDeviceDto> list = vendorIdMap.values().stream().collect(Collectors.toList());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(ErrorEnum.DB_ERROR.message());
		}
	}

	public Map<String, DeviceTimeStreamStatistic> getDeviceStatusMap () {
		Iterable<DeviceTimeStreamStatistic> deviceStatus = deviceTimeStreamStatisticDao.findAll();
		Map<String, DeviceTimeStreamStatistic> deviceStatusMap = new HashMap<>();
		deviceStatus.forEach(info -> {
			deviceStatusMap.put(info.getSrc() + info.getCode(), info);
		});
		return deviceStatusMap;
	}

	public Map<String, DeviceTimeStatistic> getDeviceThresholdMap () {
		Iterable<DeviceTimeStatistic> deviceStatus = deviceTimeStatisticDao.findAll();
		Map<String, DeviceTimeStatistic> deviceThresholdMap = new HashMap<>();
		deviceStatus.forEach(info -> {
			deviceThresholdMap.put(info.getSrc() + info.getCode(), info);
		});
		return deviceThresholdMap;
	}

	public VendorPageDto<DepartmentDeviceDto> getDeviceByDepartment (DeviceStatSearchRequest deviceStatSearchRequest) {
		String statSql = "SELECT device.create_at,depart.id id,depart.name name,device.id deviceId,\n"
				+ "deviceSubType.id AS deviceType,depart.pid pid,CONCAT(device.src,device.`code`)\n"
				+ "FROM t_device device\n"
				+ "LEFT JOIN `t_place` place ON place.id = device.place_id\n"
				+ "LEFT JOIN `department` depart ON depart.id = place.department_id\n"
				+ "LEFT JOIN `t_device_sub_type` deviceSubType ON deviceSubType.id = device.type\n"
				+ "WHERE depart.id is not null AND place.city=" + cityId;
		if (StringUtils.isNotBlank(deviceStatSearchRequest.getDistrict())) {
			statSql += " AND place.district=" + deviceStatSearchRequest.getDistrict();
		}
		if (deviceStatSearchRequest.getDepartmentIds() != null) {
			String setDepartIds = "(" + StringUtils.join(deviceStatSearchRequest.getDepartmentIds(), ",") + ")";
			statSql += " AND place.department_id in " + setDepartIds;
		}
		String dateFormat = "yyyy-MM-dd hh:mm:ss";
		try {
			if (StringUtils.isNotEmpty(deviceStatSearchRequest.getBeginDate())) {
				DateUtils.parseDate(deviceStatSearchRequest.getBeginDate(), dateFormat);
				statSql += " AND device.create_at>='" + deviceStatSearchRequest.getBeginDate() + "'";
			}
			if (StringUtils.isNotEmpty(deviceStatSearchRequest.getEndDate())) {
				DateUtils.parseDate(deviceStatSearchRequest.getEndDate(), dateFormat);
				statSql += " AND device.create_at<='" + deviceStatSearchRequest.getEndDate() + "'";
			}
		} catch (ParseException e) {
			log.error("device.createAt parse error", e);
		}
		if (deviceStatSearchRequest.getPlaceTagId() != null) {
			statSql += " AND place.place_type=" + deviceStatSearchRequest.getPlaceTagId();
		}
		if (deviceStatSearchRequest.getDeviceType() != null) {
			statSql += " AND device.type=" + deviceStatSearchRequest.getDeviceType();
		}
		if (deviceStatSearchRequest.getVendorId() != null) {
			statSql += " AND device.vendor_id=" + deviceStatSearchRequest.getVendorId();
		}
		Query<?> query = (Query<?>) entityManager.createNativeQuery(statSql);
		List<Object[]> objects = (List<Object[]>) query.list();

		List<VendorAndDeviceStatusDto> deviceVendors = new ArrayList<>();
		Map<Integer, DepartmentDeviceDto> departIdMap = Maps.newHashMap();
		Map<String, DeviceTimeStreamStatistic> deviceStatusMap = this.getDeviceStatusMap();
		Map<String, DeviceTimeStatistic> deviceThresholdMap = this.getDeviceThresholdMap();
		objects.forEach(a -> {
			try {
				VendorAndDeviceStatusDto deviceVendor = new VendorAndDeviceStatusDto(a);
				setDeviceStatus(deviceVendor, deviceStatusMap, deviceThresholdMap);
				if (deviceStatSearchRequest.getIsOnline() == -1
						|| deviceVendor.getIsOnline() == deviceStatSearchRequest.getIsOnline()) {
					deviceVendors.add(deviceVendor);
					DepartmentDeviceDto departDeviceDto = new DepartmentDeviceDto();
					departDeviceDto.setDepartmentName(deviceVendor.getName());
					departDeviceDto.setDepartmentId(deviceVendor.getId());
					departDeviceDto.setDepartmentPid(deviceVendor.getPid());
					departIdMap.put(deviceVendor.getId(), departDeviceDto);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		Map<Integer, List<VendorAndDeviceStatusDto>> groupByDepartMap = deviceVendors.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getId));
		groupByDepartMap.keySet().forEach(b -> {
			DepartmentDeviceDto departDeviceDto = departIdMap.get(b);
			List<VendorAndDeviceStatusDto> venderDevice = groupByDepartMap.get(b);
			setDeviceCount(departDeviceDto, venderDevice, deviceStatSearchRequest.getIsOnline());
		});
		List<DepartmentDeviceDto> departAll = departIdMap.values().stream().collect(Collectors.toList());

		if (deviceStatSearchRequest.getPage() == null) {
			deviceStatSearchRequest.setPage(0);
		}
		if (deviceStatSearchRequest.getSize() == null) {
			deviceStatSearchRequest.setSize(10);
		}

		int start = deviceStatSearchRequest.getPage() * deviceStatSearchRequest.getSize();
		int count = departAll.size();
		int end = count - start > deviceStatSearchRequest.getSize() ? start + deviceStatSearchRequest.getSize() : count;
		if (CollectionUtils.isEmpty(departAll) || start > end) {
			return new VendorPageDto<DepartmentDeviceDto>(Lists.newArrayList(), (long) count,
					deviceStatSearchRequest.getPage(), 0);
		}
		List<DepartmentDeviceDto> list = departAll.subList(start, end);
		return new VendorPageDto<DepartmentDeviceDto>(list, (long) count, deviceStatSearchRequest.getPage(), list.size());
	}

	/**
	 * 设置设备状态，将统计出来的设备状态设置到deviceVendor上。
	 *
	 * @param deviceVendor
	 */
	private void setDeviceStatus (VendorAndDeviceStatusDto deviceVendor, Map<String, DeviceTimeStreamStatistic> deviceStatusMap, Map<String,
            DeviceTimeStatistic> deviceThresholdMap) {
		if (deviceVendor == null) {
			return;
		}
//        Device device = devices.get(deviceVendor.getDeviceId());
		DeviceTimeStreamStatistic deviceTimeStreamStatistic = deviceStatusMap.get(deviceVendor.getSrcAndCode());
		DeviceTimeStatistic deviceTimeStatistic = deviceThresholdMap.get(deviceVendor.getSrcAndCode());
		long now = new Date().getTime();
		if (null != deviceTimeStreamStatistic) {
			Integer isOnline;
			if (null == deviceTimeStreamStatistic.getHeartbeatTime() || 0 == deviceTimeStreamStatistic.getHeartbeatTime())
				isOnline = 0;
			else
				isOnline = now - onlineTime * 1000 > deviceTimeStreamStatistic.getHeartbeatTime() ? 0 : 1;
			Integer isActive;
			if (null == deviceTimeStreamStatistic.getDataEndTime() && 0 == deviceTimeStreamStatistic.getDataEndTime())
				isActive = 0;
			else
				isActive = now - activeTime * 1000 > deviceTimeStreamStatistic.getDataEndTime() ? 0 : 1;
			DeviceStatusInfoDto oldStatus = new DeviceStatusInfoDto(deviceVendor.getDeviceId(), isOnline, isActive,
					null == deviceTimeStatistic ? null : deviceTimeStatistic.getQualify(),
					deviceTimeStreamStatistic.getDataStartTime(), deviceTimeStreamStatistic.getDataEndTime(),
					null == deviceTimeStatistic ? null : deviceTimeStatistic.getThreshold(),
					null == deviceTimeStatistic ? null : deviceTimeStatistic.getAverage());
			deviceVendor.setIsOnline(null == oldStatus.getIsOnline() ? 0 : oldStatus.getIsOnline());
			deviceVendor.setIsQulified(null == oldStatus.getIsQulified() ? 2 : oldStatus.getIsQulified());
			deviceVendor.setIsActive(null == oldStatus.getIsActive() ? 0 : oldStatus.getIsActive());
			deviceVendor.setDeviceStatusInfoDto(oldStatus);
			Long latest = oldStatus.getLatest();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
			Date before7Day = calendar.getTime();
			if (null != latest && latest >= before7Day.getTime()) {
				deviceVendor.setIsOneWeek(1);
			}
		} else {
			deviceVendor.setIsOnline(0);
			deviceVendor.setIsQulified(2);
		}
	}

	/**
	 * 设置某设备类型厂商的统计信息
	 *
	 * @param dto           某设备类型统计
	 * @param deviceStatues 该类型所有设备的状态
	 * @param isOnline      是否统计在线 离线 -1 不统计 1在线 0离线
	 */
	private void setDeviceCount (DeviceStatisticDto dto, List<VendorAndDeviceStatusDto> deviceStatues, int isOnline) {
		int count = deviceStatues.size();
		dto.setDeviceCount(count);
		dto.setDeviceTotalCount(count);
		Map<Long, Long> groupByPlaceIdeMap = deviceStatues.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getPlaceId, Collectors.counting()));
		dto.setPlaceCount(new Long(groupByPlaceIdeMap.size()));//场所总数

		Map<Integer, Long> groupByOnlineMap = deviceStatues.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIsOnline, Collectors.counting()));//在线状态
		Long offlineCount = groupByOnlineMap.get(new Integer(0));//离线设备数
		Long onlineCount = groupByOnlineMap.get(new Integer(1));//在线设备数
		dto.setOnlineCount(onlineCount == null ? 0l : onlineCount);
		dto.setOfflineCount(offlineCount == null ? 0l : offlineCount);
		Map<Integer, Long> groupByQulifiedMap = deviceStatues.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIsQulified, Collectors.counting()));//合格状态
		Long unqualifiedCount = groupByQulifiedMap.get(new Integer(0));//不合格设备数
		Long qualifiedCount = groupByQulifiedMap.get(new Integer(1));//合格设备数
		Long undeterminedCount = groupByQulifiedMap.get(new Integer(2));//待定设备数
		dto.setUnqualifiedCount(unqualifiedCount == null ? 0l : unqualifiedCount);
		dto.setQualifiedCount(qualifiedCount == null ? 0l : qualifiedCount);
		dto.setUndeterminedCount(undeterminedCount == null ? 0l : undeterminedCount);
		// 还要sum7天有上报数据的设备数
		Map<Integer, Long> groupBy7DayMap = deviceStatues.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIsOneWeek, Collectors.counting()));
		Long sevenDaysCount = groupBy7DayMap.get(new Integer(1)) == null ? 0l : groupBy7DayMap.get(new Integer(1));
		dto.setSevenDaysCount(sevenDaysCount);
		// count30天新增设备
		Map<Integer, Long> groupBy30DayMap = deviceStatues.stream()
				.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getIsOneMonth, Collectors.counting()));
		Long monthDaysCount = groupBy30DayMap.get(new Integer(1)) == null ? 0l : groupBy30DayMap.get(new Integer(1));
		dto.setNewDevicesCount(monthDaysCount);

		if (isOnline != -1) {
			// 在线并且合格的
			Map<String, Long> groupByMap = deviceStatues.stream()
					.collect(Collectors.groupingBy(VendorAndDeviceStatusDto::getOnlineQulified, Collectors.counting()));
			dto.setQualifiedCount(groupByMap.get("11") == null ? 0l : groupByMap.get("11"));
			// 在线并且不合格的
			dto.setUnqualifiedCount(groupByMap.get("10") == null ? 0l : groupByMap.get("10"));
		}
	}

}
