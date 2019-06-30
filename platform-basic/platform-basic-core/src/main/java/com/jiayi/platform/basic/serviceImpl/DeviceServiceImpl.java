package com.jiayi.platform.basic.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jiayi.platform.basic.dao.*;
import com.jiayi.platform.basic.dto.DeviceDto;
import com.jiayi.platform.basic.dto.DevicePageDto;
import com.jiayi.platform.basic.entity.*;
import com.jiayi.platform.basic.enums.AuthenticationSrcType;
import com.jiayi.platform.basic.enums.InternetEnvironment;
import com.jiayi.platform.basic.request.DeviceRequest;
import com.jiayi.platform.basic.request.DeviceSearchRequest;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.basic.util.FileUtil;
import com.jiayi.platform.basic.util.IdGenerator;
import com.jiayi.platform.basic.util.PlaceDeviceUtil;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.BeanUtils;
import com.jiayi.platform.common.util.MacUtil;
import com.jiayi.platform.common.util.MyDateUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.common.web.dto.PageResult;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.entity.Department;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author : weichengke
 * @date : 2019-03-06 11:11
 */
@Slf4j
@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private DeviceExtensionDao deviceExtensionDao;
    @Autowired
    private DeviceSubTypeDao deviceSubTypeDao;
    @Autowired
    private DeviceModelDao deviceModelDao;
    @Autowired
    private PlaceDao placeDao;
    @Autowired
    private SrcDao srcDao;
    @Autowired
    private VendorDao vendorDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DeviceTypeDao deviceTypeDao;
    @Autowired
    private DeviceTimeStreamStatisticDao deviceTimeStreamStatisticDao;
    @Autowired
    private DeviceTimeStatisticDao deviceTimeStatisticDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Value("${prodcut.city.id: 450300}")
    private int cityId;//配置的城市id
    @Value("${device.collect.time:3600}")
    private String collectTime;
    @Value("${device.online.time:3600}")
    private Long onlineTime;//在线时间范围(单位：秒)
    @Value("${fileimport.upload.path:D://fileimport/upload/}")
    private String uploadPath;
    @Value("${device.flush.url:http://192.168.0.228:8550/status/service/flush}")
    private String flushUrl;
    @Autowired
    private RestTemplate restTemplate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<Device> findAll() {
        return deviceDao.findAllValidDevices();
    }

    @Override
    public List<Device> findByCollect(CollectType collect) {
        return deviceDao.findByCollectType(collect.name());
    }

    @Override
    public List<Device> findByCollectId(Integer collectCode) {
        return deviceDao.findByCollectType(CollectType.getByCode(collectCode).name());
    }

    @Override
    public Device findByPkId(@PathVariable Long pkId) {
        return deviceDao.findOneByPkId(pkId);
    }

    @Override
    public Device findById(Long id) {
        return deviceDao.findOneById(id);
    }

    @Override
    public Map<Long, Device> findByPkIds(Set<Long> pkIds) {
        List<Device> devices = deviceDao.findByPkIdIn(pkIds);
        return devices.stream().filter(d -> d != null).collect(toMap(Device::getPkId, Function.identity(), (a, b) -> a));
    }

    @Override
    public Map<Long, Device> findByIds(Set<Long> ids) {
        List<Device> devices = deviceDao.findByIdIn(ids);
        return devices.stream().filter(d -> d != null).collect(toMap(Device::getId, Function.identity(), (a, b) -> a));
    }

    @Override
    public List<Device> findByMainType(Integer type) {
        return deviceDao.findByMainType(type);
    }

    private StringBuilder buildSql() {
        return new StringBuilder(
                "select new com.jiayi.platform.basic.dto.DeviceDto(device.pkId, device.id, device.name, device.address, device.code, \n" +
                        "device.standardCode, src, device.placeId, place.name, \n" +
                        "place.district, device.placeCode, place.address,\n" +
                        "CASE WHEN (s.heartbeatTime is null or s.heartbeatTime<unix_timestamp(current_timestamp)*1000-" + onlineTime * 1000 + ") THEN 0 ELSE 1 END,\n" +
                        "dts.qualify, dts.average, device.type, e.installerName, \n" +
                        "e.installerPhone, device.installAt, vendor.id, device.longitude, \n" +
                        "device.latitude, s.dataEndTime, dept.id, device.createAt, \n" +
                        "s.ipPort, type.dataType, e.collectionRadius, e.collectionInterval, \n" +
                        "e.dId, e.ssid, CONCAT(e.authType,''), CONCAT(e.authSrc,''), \n" +
                        "CONCAT(e.installType,''), CONCAT(e.internetEnvironment,''), e.installFloor, e.installRoom, \n" +
                        "e.subwayLineInfo, e.subwayVehicleInfo, e.subwayCompartmentNum, e.subwayCarCode, \n" +
                        "e.subwayStationInfo, e.itvAccount, s.softwareVersion, s.firmwareVersion, s.repo,e.model,city.mergerName,e.fixed)\n" +
                        "from Device device\n" +
                        "left join Place place on device.placeId=place.id\n" +
                        "left join Department dept on place.department.id=dept.id\n" +
                        "left join Src src on device.src = src.code\n" +
                        "left join Vendor vendor on device.vendorId=vendor.id\n" +
                        "left join City city on place.district = city.id\n" +
                        "left join DeviceSubType type on type.id = device.type\n" +
                        "left join DeviceTimeStreamStatistic s on s.src = device.src and s.code = device.code\n" +
                        "left join DeviceExtension e on e.src = device.src and e.code = device.code\n" +
                        "left join DeviceTimeStatistic dts on dts.src=device.src and dts.code=device.code\n" +
                        "where device.id is not null");
    }

    private List<DeviceDto> findAll(DeviceSearchRequest request) {
        StringBuilder hql = this.buildSql();
        hql.append(" and place.city=").append(cityId);
        if (StringUtils.isNotBlank(request.getName())) {
            String name = "'%" + request.getName().trim() + "%'";
            hql.append(" and (device.name like ").append(name).append(" or device.address like ").append(name).append(")");
        }
        if (StringUtils.isNotBlank(request.getCode()))
            hql.append(" and device.code like '%").append(MacUtil.toTrimMac(request.getCode().trim())).append("%'");
        if (request.getPlaceId() != null)
            hql.append(" and place.id=").append(request.getPlaceId());
        if (StringUtils.isNotBlank(request.getDistrictId()))
            hql.append(" and place.district=").append(request.getDistrictId());
        if (request.getType() != null)
            hql.append(" and device.type=").append(request.getType());
        if (request.getDepartmentId() != null) {
            if (request.isIgnoreNull())// 不包含部门为空的
                hql.append(" and place.department.id=").append(request.getDepartmentId());
            else
                hql.append(" and (place.department.id=").append(request.getDepartmentId()).append(" or place.department.id is null)");
        }
        if (request.getVendorId() != null)
            hql.append(" and vendor.id=").append(request.getVendorId());
        if (StringUtils.isNotBlank(request.getIp()))
            hql.append(" and s.ipPort like '%").append(request.getIp().trim()).append("%'");
        if (StringUtils.isNotBlank(request.getBeginDate())
                && StringUtils.isNotBlank(request.getEndDate())) {
            hql.append(" and device.installAt between '").append(request.getBeginDate()).append("' and '").append(request.getEndDate()).append("'");
        }
        if (null != request.getIsOnline() && 0 == request.getIsOnline()) {
            hql.append(" and (s.heartbeatTime is null or s.heartbeatTime<unix_timestamp(current_timestamp)*1000-").append(onlineTime * 1000).append(")");
        }
        if (null != request.getIsOnline() && 1 == request.getIsOnline()) {
            hql.append(" and s.heartbeatTime is not null and s.heartbeatTime>=unix_timestamp(current_timestamp)*1000-").append(onlineTime * 1000);
        }
        hql.append(" order by device.pkId desc");
        Query<DeviceDto> query = (Query<DeviceDto>) entityManager.createQuery(hql.toString());
        return query.list();
    }

    private List<DeviceDto> dataProcess(List<DeviceDto> deviceDtos) {
        deviceDtos.forEach(dto -> {//设置所有设备状态，方便后续统计与查询
            dto.setAuthType("0".equals(dto.getAuthType()) ? "认证码认证" : "1".equals(dto.getAuthType()) ? "非认证码认证" : "其他");
            dto.setAuthSrc(AuthenticationSrcType.getDescById(Integer.valueOf(dto.getAuthSrc())));
            dto.setInternetEnvironment(InternetEnvironment.getDescById(Integer.valueOf(dto.getInternetEnvironment())));
            dto.setInstallType("0".equals(dto.getInstallType()) ? "室内" : "室外");
        });
        return deviceDtos;
    }

    public PageResult<?> findAllDevice(DeviceSearchRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            List<DeviceDto> devices = this.findAll(request);//设备管理增加统计，所以直接查全部再手动分页
            log.info("查询设备耗时：" + (System.currentTimeMillis() - startTime) + "ms");
            int start = request.getPage() * request.getSize();
            int count = devices.size();
            int end = count - start > request.getSize() ? start + request.getSize() : count;
            if (CollectionUtils.isEmpty(devices) || start > end)
                return new DevicePageDto<>(Lists.newArrayList(), (long) count, request.getPage(), 0);
            List<DeviceDto> list = devices.subList(start, end);
            list.forEach(device -> {//根据最晚上报时间判断当前设备管理列表页面是否有轨迹数据，用于显示跳转轨迹详情按钮
                if (StringUtils.isNotBlank(device.getCollect()) && device.getCollect().contains("IMSI"))
                    device.setCollect("IMSI");
            });
            Map<Integer, Long> groupByOnline = devices.stream().filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(DeviceDto::getIsOnline, Collectors.counting()));
            Map<Integer, Long> groupByQulified = devices.stream().filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(DeviceDto::getQualify, Collectors.counting()));
            Long onlineCount = groupByOnline.get(1) == null ? 0l : groupByOnline.get(1);
            Long offlineCount = groupByOnline.get(0) == null ? 0l : groupByOnline.get(0);
            Long qualifiedCount = groupByQulified.get(1) == null ? 0l : groupByQulified.get(1);
            Long undeterminedCount = groupByQulified.get(2) == null ? 0l : groupByQulified.get(2);
            Long unqualifiedCount = groupByQulified.get(0) == null ? 0l : groupByQulified.get(0);
            return new DevicePageDto<>(list, (long) count, request.getPage(), list.size(),
                    onlineCount, offlineCount, qualifiedCount, undeterminedCount, unqualifiedCount);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public Device addDevice(DeviceRequest deviceRequest) {
        Place place;
        if (null != deviceRequest.getPlaceId()) {
            place = placeDao.findById(Long.valueOf(deviceRequest.getPlaceId())).orElseThrow(() -> new DBException("场所编码不存在"));
        } else if (null != deviceRequest.getPlaceCode()) {
            place = placeDao.findByCode(deviceRequest.getPlaceCode());
            if (place == null) {
                throw new DBException("场所编码不存在");
            }
        } else {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message());
        }
        Src src = srcDao.findByCode(deviceRequest.getSrc());
        // 同一数据源src下device的code不能重复
        whetherCodeIsUnique(src.getCode(), place.getCode(), deviceRequest.getCode());
        Device device = new Device();
        device.setPlaceCode(place.getCode());
        device.setPlaceId(place.getId());
        device.setSrc(src.getCode());// todo 父数据源?
        Vendor vendor = vendorDao.findById(deviceRequest.getVendorId()).orElseThrow(() -> new DBException("vendor of src not found"));
        device.setVendorId(vendor.getId());
        try {
            this.toEntity(device, deviceRequest);
            BeanUtils.getInstance().copyPropertiesIgnoreNull(device, deviceRequest);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message());
        }

        Long id = IdGenerator.generateDeviceId(src.getCode(), device.getCode());// src和code生成
        device.setId(id);
        device.setCreateAt(new Date());
        device.setStatus(1);
        device.setStandardCode(vendor.getCode() + device.getCode());
        deviceDao.save(device);
        DeviceExtension deviceExtension = new DeviceExtension();
        DeviceTimeStreamStatistic deviceTimeStreamStatistic = new DeviceTimeStreamStatistic();
        try {
            BeanUtils.getInstance().copyPropertiesIgnoreNull(deviceExtension, deviceRequest);
            BeanUtils.getInstance().copyPropertiesIgnoreNull(deviceTimeStreamStatistic, deviceRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceExtension.setSrc(src.getCode());
        deviceExtension.setCode(device.getCode());
        deviceExtensionDao.save(deviceExtension);
        deviceTimeStreamStatistic.setSrc(src.getCode());
        deviceTimeStreamStatistic.setCode(device.getCode());
        deviceTimeStreamStatisticDao.save(deviceTimeStreamStatistic);
        ThreadPoolUtil.getInstance().submit(() -> flushAllDeviceInfo());
        return device;
    }

    public void flushAllDeviceInfo() {
        JSONObject json = null;
        try {
            JSONObject postData = new JSONObject();
            postData.put("descp", "request for post");
            json = restTemplate.postForEntity(flushUrl, postData, JSONObject.class).getBody();
            log.info("flush deviceinfo " + json.getString("message"));
        } catch (Exception e) {
            log.error("flush error :" + json, e);
        }
    }

    public void updateDevice(Long id, DeviceRequest deviceRequest) {

        Device device = deviceDao.findOneByPkId(id);
        if( device==null ){
            throw new ValidException("设备(id:"+id+")不存在。");
        }

        //修改设备状态校验
        Integer status = deviceRequest.getStatus();
        if( status!=null ) {

            //获取设备状态（ 0:离线 | 1:在线 | -1:未知状态 ）
            Integer isOnline = this.getDeviceStatus(device);
            if (isOnline == -1) {
                throw new ValidException("设备状态未知（离线、在线）！");
            }

            //非离线状态的设备，不可以变更为暂停和删除
            if (isOnline != 0 && (status == 0 || status == 2)) {
                throw new ValidException("非离线状态的设备，不能暂停和删除！");
            }
        }

        deviceRequest.setSrc(null);// todo 暂时不让更改数据源，若更改数据源须同时变更扩张表数据

        Place place = placeDao.findById(device.getPlaceId()).orElseThrow(() -> new DBException("place not found"));
        Src src = srcDao.findByCode(device.getSrc());

        if (StringUtils.isNotBlank(deviceRequest.getCode()) && !MacUtil.toTrimMac(deviceRequest.getCode()).equals(device.getCode())) {
            //添加校验：（等待时间内/有轨迹数据时）不能修改设备编码
            Long second = device.getCreateAt().getTime() + Long.parseLong(collectTime) * 1000;
            if (second > new Date().getTime()) {
                throw new ValidException("数据采集等待中，请在" + MyDateUtil.getDateStr(second) + "后修改编码");
            } else {
                Long dataEndTime = deviceTimeStreamStatisticDao.selectEndTime(device.getSrc(), device.getCode());
                if (dataEndTime != null && dataEndTime != 0) {
                    throw new ValidException("修改编码失败：设备已有轨迹数据");
                }
            }
            whetherCodeIsUnique(src.getCode(), place.getCode(), deviceRequest.getCode());
        }


        if (null != deviceRequest.getVendorId() && deviceRequest.getVendorId() != device.getVendorId()) {
            vendorDao.findById(deviceRequest.getVendorId()).orElseThrow(() -> new DBException("vendor not found"));
        }

        try {
            DeviceExtension deviceExtension = deviceExtensionDao.findBySrcAndCode(device.getSrc(), device.getCode());
            if (null == deviceExtension) {
                deviceExtension = new DeviceExtension();
            }
            BeanUtils.getInstance().copyPropertiesIgnoreNull(deviceExtension, deviceRequest);
            deviceExtension.setSrc(device.getSrc());
            deviceExtension.setCode(device.getCode());
            deviceExtensionDao.save(deviceExtension);

            DeviceTimeStreamStatistic deviceTimeStreamStatistic = deviceTimeStreamStatisticDao.selectDeviceStreamStatistic(device.getSrc(), device.getCode());
            if (null == deviceTimeStreamStatistic) {
                deviceTimeStreamStatistic = new DeviceTimeStreamStatistic();
                deviceTimeStreamStatistic.setSrc(device.getSrc());
                deviceTimeStreamStatistic.setCode(device.getCode());
            }
            deviceTimeStreamStatistic.setSoftwareVersion(deviceRequest.getSoftwareVersion());
            deviceTimeStreamStatistic.setFirmwareVersion(deviceRequest.getFirmwareVersion());
            deviceTimeStreamStatisticDao.save(deviceTimeStreamStatistic);
            this.toEntity(device, deviceRequest);
            BeanUtils.getInstance().copyPropertiesIgnoreNull(device, deviceRequest);
            deviceDao.save(device);
            ThreadPoolUtil.getInstance().submit(() -> flushAllDeviceInfo());
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }

    }

    // 1.未达到采集等待时间（即当前时间 减 设备创建时间 小于 采集时间）或 2.有轨迹数据
    public void deleteDevice(Long id) {
        try {
            Device device = deviceDao.findOneByPkId(id);
//        Long second = device.getCreateAt().getTime() + Long.parseLong(collectTime) * 1000;
//        if (second > new Date().getTime()) {
//            throw new ValidException("数据采集等待中，请在" + MyDateUtil.getDateStr(second) + "后操作");
//        } else {
            Long endTime = deviceTimeStreamStatisticDao.selectEndTime(device.getSrc(), device.getCode());
            if (endTime != null) {
                if (endTime != 0)
                    throw new ValidException("删除失败：设备已有轨迹数据");
                else {
                    Set<String> srcAndCodes = new HashSet<>();
                    srcAndCodes.add(device.getSrc() + "|" + device.getCode());
                    List<DeviceTimeStreamStatistic> statusInfo = deviceTimeStreamStatisticDao.findDeviceStatusInfo(srcAndCodes);
                    deviceTimeStreamStatisticDao.delete(statusInfo.get(0));
                }
            }
//        }
            deviceExtensionDao.deleteBySrcAndCode(device.getSrc(), device.getCode());
            deviceDao.delete(device);
            ThreadPoolUtil.getInstance().submit(() -> flushAllDeviceInfo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException("删除失败", e);
        }
    }

    private Long parseToLong(double x) {
        double pow = Math.pow(10, 12);
        return (long) (x * pow);
    }

    private void toEntity(Device device, DeviceRequest deviceRequest) {
        if (StringUtils.isNotBlank(deviceRequest.getCode())) {
            device.setCode(MacUtil.toTrimMac(deviceRequest.getCode()));
            device.setMac(MacUtil.generateMac(device.getCode()));
            deviceRequest.setCode(null);
        }
        if (deviceRequest.getInstallAt() != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MyDateUtil.yyyy_MM_dd_HH_mm_ss);
                device.setInstallAt(simpleDateFormat.parse(deviceRequest.getInstallAt()));
                deviceRequest.setInstallAt(null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (null != deviceRequest.getLongitude() && null != deviceRequest.getLatitude() && 0 != deviceRequest.getLongitude() && 0 != deviceRequest.getLatitude()) {
            try {
                Long gridCode = Long.parseLong("" + (int) (deviceRequest.getLongitude() * 1000)
                        + (int) (deviceRequest.getLatitude() * 1000));
                device.setGridCode(gridCode);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("transform device.gridCode error!");
            }
            device.setLongitude(this.parseToLong(deviceRequest.getLongitude()));
            device.setLatitude(this.parseToLong(deviceRequest.getLatitude()));
            deviceRequest.setLatitude(null);
            deviceRequest.setLongitude(null);
        }
        device.setUpdateAt(new Date());
    }

    public void downloadDevices(DeviceSearchRequest request, HttpServletResponse response) {
        try {
            List<String> contents = new ArrayList<>();
            int start = 0;
            long startTime = System.currentTimeMillis();
            List<DeviceDto> deviceDtos = this.findAll(request);
            deviceDtos = this.dataProcess(deviceDtos);
            boolean flag = true;
            int LOAD_SIZE = 50;
            do {
                int count = deviceDtos.size();
                int end = count - start > LOAD_SIZE ? start + LOAD_SIZE : count;
                List<DeviceDto> list = deviceDtos.subList(start, end);
                if (end == count)
                    flag = false;
                start = end;
                List<String[]> rowData = new ArrayList<>();
                list.forEach(a -> {
                    String[] rowValue = new String[10];
                    rowValue[0] = a.getName();
                    rowValue[1] = a.getAddress();
                    rowValue[2] = a.getCode();
                    rowValue[3] = a.getSrc() == null ? "" : a.getSrc().getName();
                    rowValue[4] = PlaceDeviceUtil.getCityAreaName(a.getDistrict());
                    switch (a.getIsOnline()) {
                        case 1:
                            rowValue[5] = "在线";
                            break;
                        case 0:
                            rowValue[5] = "离线";
                            break;
                        default:
                            rowValue[5] = "";
                    }
                    switch (a.getQualify()) {
                        case 0:
                            rowValue[6] = "不合格";
                            break;
                        case 1:
                            rowValue[6] = "合格";
                            break;
                        case 2:
                            rowValue[6] = "待定";
                            break;
                        default:
                            rowValue[6] = "";
                    }
                    rowValue[7] = a.getAverage() == null ? "0" : String.valueOf(a.getAverage());
                    rowValue[8] = a.getDataEndTime() == null ? "-" : MyDateUtil.getDateStr(a.getDataEndTime().getTime());
                    rowValue[9] = a.getIpPort();
                    rowData.add(rowValue);
                });
                contents.add(ExportUtil.genContentByStringList(rowData));
            } while (flag);
            log.debug("load export data used " + (System.currentTimeMillis() - startTime) + "ms");
            String fileName = "device" + System.currentTimeMillis();
            if (!ExportUtil.doExport(contents, "设备名称,设备地址,设备编码,数据来源,区县名称,设备状态,合格状态,采集数据平均值（周）,最晚上报时间,IP", fileName, response))
                log.error("writing csv file error!");
        } catch (Exception e) {
            throw new ServiceException("download error!", e);
        }
    }

    private void whetherCodeIsUnique(String srcCode, String placeCode, String deviceCode) {
        List<Device> srcCount = deviceDao.isCodeUsedInSrc(srcCode, Lists.newArrayList(MacUtil.toTrimMac(deviceCode)));
        if (srcCount.size() > 0) {
            throw new ValidException("同一数据源（编码" + srcCount.get(0).getSrc() + "）下设备编码重复");
        }
        List<Device> placeCount = deviceDao.isCodeUsedInPlace(placeCode, Lists.newArrayList(MacUtil.toTrimMac(deviceCode)));
        if (placeCount.size() > 0) {
            throw new ValidException("同一场所（编码" + placeCount.get(0).getSrc() + "）下设备编码重复");
        }
    }

    public void exportDevices(DeviceSearchRequest request, HttpServletResponse response) {
        try {
            Long start = System.currentTimeMillis();
            List<Department> depts = departmentDao.findAll();
            Map<Integer, String> deptMap = new HashMap<>();
            depts.forEach(dept -> {
                deptMap.put(dept.getId(), dept.getName());
            });
            List<DeviceModel> models = deviceModelDao.findAll();
            Map<Integer, DeviceModel> modelMap = models.stream().collect(Collectors.toMap(DeviceModel::getId, Function.identity(), (k1, k2) -> k2));
            List<DeviceSubType> subTypes = deviceSubTypeDao.findAll();
            Map<Integer, DeviceSubType> subTypeMap = subTypes.stream().collect(Collectors.toMap(DeviceSubType::getId, Function.identity(), (k1, k2) -> k2));
            request.setPage(0);
            request.setSize(50000);
            List<DeviceDto> data = this.findAll(request);
            List<String> contents = Lists.newArrayList();
            List<String[]> rowData = Lists.newArrayList();
            Map<Long, String> errorSrcMap = new HashMap<>();
            data.forEach(dto -> {
                String[] rowValue = new String[37];
                int i = 0;
                rowValue[i++] = dto.getName();
                rowValue[i++] = dto.getCode();
                rowValue[i++] = "'" + dto.getPlaceCode();
                rowValue[i++] = dto.getSrc().getName();
                if (null == subTypeMap.get(dto.getType())) {
                    log.error("find DeviceSubType in Device error!deviceid=" + dto.getId());
                    rowValue[i++] = "";
                } else {
                    rowValue[i++] = subTypeMap.get(dto.getType()).getName();// 设备子类型
                }
                if (null == modelMap.get(dto.getModel())) {
                    rowValue[i++] = "";
                } else {
                    rowValue[i++] = modelMap.get(dto.getModel()).getName();// 设备型号
                }
//                rowValue[i++] = dto.getAddress();// 设备地址
                rowValue[i++] = 1 == dto.getIsOnline() ? "在线" : 0 == dto.getIsOnline() ? "离线" : "";
                if (null == dto.getFixed())
                    rowValue[i++] = "";
                else
                    rowValue[i++] = 1 == dto.getFixed() ? "移动" : 0 == dto.getFixed() ? "固定" : "";
                rowValue[i++] = deptMap.get(dto.getDepartmentId());
                if (null != dto.getSrc()) {
                    if (null != dto.getSrc() && null != dto.getSrc().getVendor()) {
                        rowValue[i++] = dto.getSrc().getVendor().getName();
                    } else {
                        rowValue[i++] = "";
                        errorSrcMap.put(dto.getSrc().getId(), dto.getSrc().getName());
                    }
                } else {
                    rowValue[i++] = "";
                    log.error("device: id=" + dto.getId() + " have no src");
                }
                rowValue[i++] = StringUtils.isBlank(dto.getIpPort()) ? "" : dto.getIpPort();
                if (null == dto.getDataEndTime() || 0 == dto.getDataEndTime().getTime()) {
                    rowValue[i++] = "";
                } else {
                    rowValue[i++] = MyDateUtil.getDateStr(dto.getDataEndTime());
                }
                rowValue[i++] = null == dto.getInstallAt() ? "" : dto.getInstallAt().toString();
                rowValue[i++] = dto.getPlaceAddress();// 安装地址
                rowValue[i++] = dto.getLongitude() + "," + dto.getLatitude();
                rowValue[i++] = dto.getMergerName().replace(",", "/");
                rowValue[i++] = StringUtils.isBlank(dto.getSoftwareVersion()) ? "" : dto.getSoftwareVersion();
                rowValue[i++] = StringUtils.isBlank(dto.getFirmwareVersion()) ? "" : dto.getFirmwareVersion();
                rowValue[i++] = StringUtils.isBlank(dto.getRepo()) ? "" : dto.getRepo();
                rowValue[i++] = null == dto.getCollectionRadius() ? "" : dto.getCollectionRadius().toString();
                rowValue[i++] = null == dto.getCollectionInterval() ? "" : dto.getCollectionInterval().toString();
                rowValue[i++] = StringUtils.isBlank(dto.getdId()) ? "" : dto.getdId();
                rowValue[i++] = StringUtils.isBlank(dto.getSsId()) ? "" : dto.getSsId();
                rowValue[i++] = StringUtils.isBlank(dto.getInstallerName()) ? "" : dto.getInstallerName();
                rowValue[i++] = StringUtils.isBlank(dto.getInstallerPhone()) ? "" : dto.getInstallerPhone();
                rowValue[i++] = StringUtils.isBlank(dto.getAuthType()) ? "" : "0".equals(dto.getAuthType()) ? "认证码认证" : "1".equals(dto.getAuthType()) ? "非认证码认证" : "其他";
                rowValue[i++] = StringUtils.isBlank(dto.getAuthSrc()) ? "" : AuthenticationSrcType.getDescById(Integer.valueOf(dto.getAuthSrc()));
                rowValue[i++] = StringUtils.isBlank(dto.getInternetEnvironment()) ? "" : InternetEnvironment.getDescById(Integer.valueOf(dto.getInternetEnvironment()));
                rowValue[i++] = StringUtils.isBlank(dto.getInstallType()) ? "" : "0".equals(dto.getInstallType()) ? "室内" : "室外";
                rowValue[i++] = StringUtils.isBlank(dto.getItvAccount()) ? "" : dto.getItvAccount();
                rowValue[i++] = StringUtils.isBlank(dto.getInstallRoom()) ? "" : dto.getInstallRoom();
                rowValue[i++] = StringUtils.isBlank(dto.getInstallFloor()) ? "" : dto.getInstallFloor();
                rowValue[i++] = StringUtils.isBlank(dto.getSubwayLineInfo()) ? "" : dto.getSubwayLineInfo();
                rowValue[i++] = StringUtils.isBlank(dto.getSubwayVehicleInfo()) ? "" : dto.getSubwayVehicleInfo();
                rowValue[i++] = StringUtils.isBlank(dto.getSubwayCompartmentNum()) ? "" : dto.getSubwayCompartmentNum();
                rowValue[i++] = StringUtils.isBlank(dto.getSubwayCarCode()) ? "" : dto.getSubwayCarCode();
                rowValue[i] = StringUtils.isBlank(dto.getSubwayStationInfo()) ? "" : dto.getSubwayStationInfo();
                rowData.add(rowValue);
            });
            for (Map.Entry<Long, String> entry : errorSrcMap.entrySet()) {
                log.error("srcId = " + entry.getKey() + ", name = " + entry.getValue() + " have no vendor");
            }
            contents.add(ExportUtil.genContentByStringList(rowData));
            log.debug("load export data used " + (System.currentTimeMillis() - start) + "ms");
            String fileName = "设备管理" + System.currentTimeMillis();
            if (!ExportUtil.doExport(contents, "设备名称,设备编码,场所编码,数据源,设备子类型,设备型号,设备状态,设备采集方式,所属部门,设备供应商,设备IP,最晚上报时间,安装时间,安装地址,安装经纬度,所属区县,软件版本号,固件版本号,特征库,采集半径(米),采集间隔(秒),设备ID,SSID,安装人姓名,安装人电话,认证方式,实名认证数据来源,终端上网环境,设备安装类型,itv账号,房间号,楼层,地铁路线信息,地铁车辆信息,地铁车厢编号,车牌号码,站点信息", fileName, response))
                log.error("writing csv file error!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> countPlaceAndDevice() {
        List<Map<String, Object>> tempList = new ArrayList<>();
        List<Object[]> resultList = deviceDao.findCollectTypeDevices(onlineTime * 1000, cityId);
        Map<String, Long> map = resultList.stream().collect(Collectors.groupingBy(a -> String.valueOf(a[2]), Collectors.counting()));//设备类型统计
        List<DeviceType> types = deviceTypeDao.findAll();
        types.forEach(type -> {
            Map<String, Object> result = new HashMap<>();
            String deviceType = type.getName();
            Long typeCount = map.get(deviceType);
            result.put("deviceType", deviceType);
            result.put("typeCount", typeCount == null ? 0 : typeCount);
            tempList.add(result);
        });
        Map<String, Long> onLine = resultList.stream().collect(Collectors.groupingBy(a -> String.valueOf(a[3]), Collectors.counting()));//在线状态统计
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("placeCount", placeDao.countByCity(cityId + ""));
        resultMap.put("deviceCount", resultList.size());
        Long onlineCount = onLine.get("1");
        Long offlineCount = onLine.get("0");
        resultMap.put("onlineCount", onlineCount == null ? 0L : onlineCount);
        resultMap.put("offlineCount", offlineCount == null ? 0L : offlineCount);
        resultMap.put("deviceTypeGroup", tempList);
        return resultMap;
    }

    public Map<String, Object> uploadAndParse(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new ArgumentException("请上传CSV格式的文件");
        }
        Map<String, Object> resultMap = new HashMap<>();
        String fileName = file.getOriginalFilename();
        try {
            File saveFile = FileUtil.upload(file, uploadPath);
            log.info("upload file success");
            List<String[]> contents = FileUtil.parseCsvFile(saveFile);
            log.info("content size:" + contents.size());
            String[] headers = contents.remove(0);
            resultMap.put("header", headers);
            resultMap.put("fileName", fileName);
            int size = contents.size();
            size = size > 100 ? 100 : size;
            resultMap.put("content", contents.subList(0, size));
            return resultMap;
        } catch (Exception e) {
            log.error("file upload or parse error", e);
            throw new ServiceException("file upload or parse error", e);
        }
    }

    public void importCsvFile(String fileName) {
        String filePath = uploadPath + File.separator + fileName;
        File file = new File(filePath);
        List<String[]> contents = FileUtil.parseCsvFile(file);//获取文件内容

        List<DeviceSubType> deviceTypes = deviceSubTypeDao.findAll();//设备类型
        Map<String, Integer> typeMap = deviceTypes.stream().collect(Collectors.toMap(DeviceSubType::getName, DeviceSubType::getId, (k1, k2) -> k1));

        List<Place> places = placeDao.findAllPlaceIdCode();//设备对应场所
        Map<String, Place> placeMap = places.stream().collect(Collectors.toMap(Place::getCode, Function.identity(), (k1, k2) -> k1));

        List<DeviceModel> models = deviceModelDao.findAll();//设备型号
        Map<String, Integer> modelMap = new HashMap<>();
        models.stream().forEach(model -> {
            modelMap.put(model.getDeviceSubType() + "|" + model.getVendorId() + "|" + model.getName(), model.getId());
        });
        List<Vendor> vendors = vendorDao.findAll();
        Map<String, Integer> vendorMap = vendors.stream().collect(Collectors.toMap(Vendor::getName, Vendor::getId, (k1, k2) -> k1));
        contents.remove(0);
        List<Device> deviceList = new ArrayList<>();
        List<DeviceExtension> deList = new ArrayList<>();
        List<DeviceTimeStreamStatistic> dtssList = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            List<String> errorMsg = new ArrayList<>();
            String[] content = contents.get(i);
            //按行校验
            Src src = checkContentData(content, deviceList, typeMap, vendorMap, modelMap, placeMap, errorMsg);
            if (errorMsg.size() > 0) {
                String validation = "第" + (i + 1) + "行数据存在以下错误：" + StringUtils.join(errorMsg, ",");
                throw new ValidException(validation);
            }
            Device device = this.setDeviceEntity(content, placeMap, typeMap, vendorMap, src, errorMsg);
            DeviceExtension de = this.setDeviceExtendionEntity(content, modelMap, typeMap, src, errorMsg);//设备扩展信息
            de.setSrc(device.getSrc());
            DeviceTimeStreamStatistic dtss = new DeviceTimeStreamStatistic();//固件软件版本
            dtss.setSrc(device.getSrc());
            dtss.setCode(device.getCode());
            dtss.setSoftwareVersion(content[9]);
            dtss.setFirmwareVersion(content[10]);
            deviceList.add(device);
            deList.add(de);
            dtssList.add(dtss);
        }
        //------文件内数据校验重复
        Map<String, List<Device>> deviceMap = deviceList.stream().collect(Collectors.groupingBy(a -> a.getSrc() + "|" + a.getCode()));
        deviceMap.forEach((k, v) -> {
            if (v.size() > 1) {
                throw new ValidException("导入的文件中数据源编码和设备编码重复:" + v.get(0).getSrc() + "," + k.split("\\|")[1]);
            }
        });
        List<Device> existDevice = deviceDao.findBySrcAndCode(deviceMap.keySet());
        if (CollectionUtils.isNotEmpty(existDevice)) {
            throw new ValidException("数据源编码和设备编码已存在:" + existDevice.get(0).getSrc() + "," + existDevice.get(0).getCode());
        }
        try {
            deviceDao.saveAll(deviceList);
            deviceExtensionDao.saveAll(deList);
            deviceTimeStreamStatisticDao.saveAll(dtssList);
            ThreadPoolUtil.getInstance().submit(() -> flushAllDeviceInfo());
        } catch (Exception e) {
            log.error("import error", e);
            throw new RuntimeException("import error", e);
        } finally {//执行完毕后删除文件
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private Src checkContentData(String[] content, List<Device> deviceList, Map<String, Integer> typeMap, Map<String, Integer> vendorMap, Map<String, Integer> modelMap, Map<String, Place> placeMap, List<String> errorMsg) {
        //------必填校验
        Map<Integer, String> must = new HashMap<>();
        must.put(0, "设备名称必填");
        must.put(1, "设备编码必填");
        must.put(2, "数据源必填");
        must.put(3, "设备子类型必填");
        must.put(4, "设备供应商必填");
        must.put(6, "设备采集方式必填");
        must.put(7, "对应场所编码必填");
        must.put(8, "安装时间必填");
        must.put(11, "详细地址必填");
        must.put(12, "经度坐标必填");
        must.put(13, "纬度坐标必填");
        for (Integer i : must.keySet()) {
            if (StringUtils.isBlank(content[i]))
                errorMsg.add(must.get(i));
        }
        //------数据错误校验
        Src src = srcDao.findByName(content[2]);
        if (src == null) {
            errorMsg.add("找不到对应数据源");
        }
        Integer type = typeMap.get(content[3].replaceAll("[-:：\\s]", ""));
        if (type == null)
            errorMsg.add("找不到对应设备子类型");
        if (null != src && null == src.getVendor()) {
            log.error("数据源" + src.getName() + "没有供应商信息");
        }
        Integer vendorId = vendorMap.get(content[4].replaceAll("[-:：\\s]", ""));
        if (vendorId == null)
            errorMsg.add("找不到对应供应商");
        if (null != src && null == src.getVendor() && StringUtils.isNotBlank(content[5])) {
            String key = type + "|" + src.getVendor().getId() + "|" + content[5];
            if (modelMap.get(key) == null) {
                errorMsg.add("找不到对应型号");
            }
        }
        Place place = placeMap.get(content[7].replaceAll("[-:：\\s]", ""));
        if (place == null)
            errorMsg.add("找不到编码为" + content[7] + "的场所");
        if (src != null && src.getVendor() != null) {
            if (!src.getVendor().getName().equals(content[4].replaceAll("[-:：\\s]", ""))) {
                errorMsg.add("数据源与供应商无关联");
            }
        }
        try {
            if (StringUtils.isNotBlank(content[8]))
                DateUtils.parseDateStrictly(content[8], MyDateUtil.IMPORT_DATE_PATTERNS);
        } catch (ParseException e) {
            errorMsg.add("安装时间格式错误");
        }
        try {
            if (StringUtils.isNotBlank(content[12])) {
                long longitude = (long) (Math.pow(10, 12) * Double.valueOf(content[12]));
            }
        } catch (Exception e) {
            errorMsg.add("经度数据错误");
        }
        try {
            if (StringUtils.isNotBlank(content[13])) {
                long longitude = (long) (Math.pow(10, 12) * Double.valueOf(content[13]));
            }
        } catch (Exception e) {
            errorMsg.add("纬度数据错误");
        }
        String deviceCollection = content[6].replaceAll("[-:：\\s]", "");
        if (!"固定".equals(deviceCollection) && !("移动".equals(deviceCollection))) {
            errorMsg.add("设备采集方式不存在");
        }
        if (StringUtils.isNotBlank(content[20])) {
            String authType = "认证码认证".equals(content[20]) ? "0" : "非认证码认证".equals(content[20]) ? "1" : "其他".equals(content[20]) ? "2" : null;
            if (null == authType)
                errorMsg.add("认证方式不存在");
            else
                content[20] = authType;
        }
        if (StringUtils.isNotBlank(content[21])) {
            if (null == AuthenticationSrcType.getIdByDesc(content[21]))
                errorMsg.add("实名认证数据来源不存在");
            else
                content[21] = AuthenticationSrcType.getIdByDesc(content[21]).toString();
        }
        if (StringUtils.isNotBlank(content[22])) {
            String installType = "室内".equals(content[22]) ? "0" : "室外".equals(content[22]) ? "1" : null;
            if (null == installType)
                errorMsg.add("设备安装类型错误");
            else
                content[22] = installType;
        }
        if (StringUtils.isNotBlank(content[23])) {
            Integer ie = InternetEnvironment.getIdByDesc(content[23]);
            if (null == ie)
                errorMsg.add("终端上网环境不存在");
            else
                content[23] = ie.toString();
        }
        return src;
    }

    private Device setDeviceEntity(String[] content, Map<String, Place> placeMap, Map<String, Integer> typeMap, Map<String, Integer> vendorMap, Src src, List<String> errorMsg) {
        Device device = new Device();
        device.setName(content[0]);
        device.setCode(content[1].replaceAll("[-:：\\s]", ""));
        device.setType(typeMap.get(content[3].replaceAll("[-:：\\s]", "")));//设备类型
        device.setVendorId(vendorMap.get(content[4].replaceAll("[-:：\\s]", "")));
        Place place = placeMap.get(content[7].replaceAll("[-:：\\s]", ""));
        device.setPlaceId(place.getId());
        device.setPlaceCode(place.getCode());
        device.setSrc(src.getCode());//数据源
        device.setStandardCode(src.getVendor().getCode() + device.getCode());
        Long id = IdGenerator.generateDeviceId(src.getCode(), device.getCode());// src和code生成
        device.setId(id);
        try {
            device.setInstallAt(DateUtils.parseDateStrictly(content[8], MyDateUtil.IMPORT_DATE_PATTERNS));
        } catch (Exception e) {
            e.printStackTrace();
        }
        device.setAddress(content[11]);
        device.setLongitude((long) (Math.pow(10, 12) * Double.valueOf(content[12])));
        device.setLatitude((long) (Math.pow(10, 12) * Double.valueOf(content[13])));
        return device;
    }

    private DeviceExtension setDeviceExtendionEntity(String[] content, Map<String, Integer> modelMap, Map<String, Integer> typeMap, Src src, List<String> errorMsg) {
        DeviceExtension de = new DeviceExtension();
        de.setCode(content[1].replaceAll("[-:：\\s]", ""));
        if (StringUtils.isNotBlank(content[5])) {
            Integer subType = typeMap.get(content[3].replaceAll("[-:：\\s]", ""));
            String key = subType + "|" + src.getVendor().getId() + "|" + content[5];
            Integer modelId = modelMap.get(key);
            if (modelId != null) {
                de.setModel(modelId);
            }
        }
        String deviceCollectMethod = content[6].replaceAll("[-:：\\s]", "");
        Integer fixed = "固定".equals(deviceCollectMethod) ? 0 : 1;
        de.setFixed(fixed);
        if (StringUtils.isNotBlank(content[14])) {
            de.setCollectionRadius(Long.valueOf(content[14]));
        }
        if (StringUtils.isNotBlank(content[15])) {
            de.setCollectionInterval(Long.valueOf(content[15]));
        }
        de.setDId(content[16]);
        de.setSsid(content[17]);
        de.setInstallerName(content[18]);
        de.setInstallerPhone(content[19]);
        de.setAuthType(content[20]);
        de.setAuthSrc(content[21]);
        de.setInstallType(content[22]);
        de.setInternetEnvironment(content[23]);
        de.setInstallFloor(content[24]);
        de.setInstallRoom(content[25]);
        de.setSubwayLineInfo(content[26]);
        de.setSubwayVehicleInfo(content[27]);
        de.setSubwayCompartmentNum(content[28]);
        de.setSubwayCarCode(content[29]);
        de.setSubwayStationInfo(content[30]);
        de.setItvAccount(content[31]);
        return de;
    }

    public DeviceDto findOneBySrcAndCode(String src, String code) {
        try {
            StringBuilder hql = buildSql();
            hql.append(" and device.src='").append(src).append("'");
            hql.append(" and device.code='").append(code).append("'");
            Query<DeviceDto> query = (Query<DeviceDto>) entityManager.createQuery(hql.toString());
            List<DeviceDto> resultList = query.list();
            if (CollectionUtils.isNotEmpty(resultList)) {
                return resultList.get(0);
            }
        } catch (Exception e) {
            log.error("search error", e);
            throw new DBException("search error", e);
        }
        return null;
    }

    /**
     * 修改设备状态
     * @param deviceId
     * @param status ( 0: 删除 | 1：正常 | 2：暂停)
     * @return
     */
    public void changeDeviceStatus(Long deviceId, Integer status) {

        //检查设备是否存在
        Device device = this.deviceDao.findOneByPkId(deviceId);
        if( device==null ){
            throw new ValidException("设备(id:"+deviceId+")不存在。");
        }

        //获取设备状态（ 0:离线 | 1:在线 | -1:未知状态 ）
        Integer isOnline=this.getDeviceStatus(device);
        if( isOnline==-1 ){
            throw new ValidException("设备状态未知（离线、在线）！");
        }

        //非离线状态的设备，不可以变更为暂停和删除
        if(isOnline!=0 && ( status==0 || status==2 ) ){
            throw new ValidException("非离线状态的设备，不能暂停和删除！");
        }
        this.deviceDao.updateDeviceStatusByPkId(deviceId,status);
    }

    /**
     * 根据设备ID获取设备状态 isOnline
     * @param device
     * @return
     */
    public Integer getDeviceStatus(Device device) {

        Integer isOnline = -1;

        String src = device.getSrc();
        String code = device.getCode();

        DeviceTimeStreamStatistic deviceTimeStreamStatistic = this.deviceTimeStreamStatisticDao.selectDeviceStreamStatistic(src, code);

        if(deviceTimeStreamStatistic==null){
            return -1;
        }

        if (null == deviceTimeStreamStatistic.getHeartbeatTime() || 0 == deviceTimeStreamStatistic.getHeartbeatTime())
            isOnline = 0;
        else{
            long now = new Date().getTime();
            isOnline = now - onlineTime * 1000 > deviceTimeStreamStatistic.getHeartbeatTime() ? 0 : 1;
        }

        return isOnline;
    }


}
