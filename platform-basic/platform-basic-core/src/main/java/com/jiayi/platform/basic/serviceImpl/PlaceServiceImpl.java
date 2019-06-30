package com.jiayi.platform.basic.serviceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiayi.platform.basic.dao.*;
import com.jiayi.platform.basic.dto.CityDto;
import com.jiayi.platform.basic.dto.PlaceDetailDto;
import com.jiayi.platform.basic.dto.ProvinceCityDistrictDto;
import com.jiayi.platform.basic.entity.*;
import com.jiayi.platform.basic.enums.*;
import com.jiayi.platform.basic.request.PlaceRequest;
import com.jiayi.platform.basic.request.PlaceSearchRequest;
import com.jiayi.platform.basic.service.PlaceService;
import com.jiayi.platform.basic.util.FileUtil;
import com.jiayi.platform.basic.util.PlaceDeviceUtil;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.BeanUtils;
import com.jiayi.platform.common.util.MyDateUtil;
import com.jiayi.platform.common.web.dto.PageResult;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.entity.Department;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private static Logger log = LoggerFactory.getLogger(PlaceService.class);

    @Autowired
    private PlaceDao placeDao;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private PlaceTagRelationDao placeTagRelationDao;
    @Autowired
    private PlaceLabelRelationDao placeLabelRelationDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private PlaceTagDao placeTagDao;
    @Autowired
    private PlaceLabelDao placeLabelDao;
    @Autowired
    private SrcDao srcDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private VendorDao vendorDao;
    @Value("${prodcut.city.id:441200}")
    private Long cityId;//配置的城市id

    private static List<CityDto> cityList = null;

    @Value("${fileimport.upload.path:D://fileimport/upload/}")
    private String uploadPath;

    @PostConstruct
    public void cityInit() {
        Map<Long, ProvinceCityDistrictDto> districtMap = Maps.newHashMap();
        Iterable<City> list = cityDao.findAll();
        Map<Long, List<City>> groupByPidMap = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.groupingBy(City::getPid));
        // 第一级省
        List<City> oneGradeCity = groupByPidMap.get(0l);
        List<CityDto> oneGradeCityDto = Lists.newArrayList();
        oneGradeCity.forEach(a -> {
            CityDto city1Dto = new CityDto(a.getId(), a.getName(), a.getMergerName());
            // 第二级市
            List<City> twoGradeCity = groupByPidMap.get(a.getId());
            if (CollectionUtils.isNotEmpty(twoGradeCity)) {
                List<CityDto> twoGradeCityDto = Lists.newArrayList();
                twoGradeCity.forEach(b -> {
                    CityDto city2Dto = new CityDto(b.getId(), b.getName(), b.getMergerName());
                    twoGradeCityDto.add(city2Dto);
                    if (cityId.equals(b.getId()))
                        districtMap.put(b.getId(), new ProvinceCityDistrictDto(b.getId(), b.getName(), b.getMergerName(), a.getId(), b.getId()));
                    // 第三级区
                    List<City> threeGradeCity = groupByPidMap.get(b.getId());
                    if (CollectionUtils.isNotEmpty(threeGradeCity)) {

                        List<CityDto> threeGradeCityDto = Lists.newArrayList();
                        threeGradeCity.forEach(c -> {
                            ProvinceCityDistrictDto city3Dto = new ProvinceCityDistrictDto(c.getId(), c.getName(), c.getMergerName(), a.getId(), b.getId());
                            threeGradeCityDto.add(city3Dto);
                            districtMap.put(c.getId(), city3Dto);
                        });
                        city2Dto.setNextLevel(threeGradeCityDto);
                    }
                });
                city1Dto.setNextLevel(twoGradeCityDto);
                oneGradeCityDto.add(city1Dto);
            }
        });
        PlaceDeviceUtil.districtMap = districtMap;
        cityList = oneGradeCityDto;
    }

    public List<CityDto> getCity() {
        if (cityList == null) {
            cityInit();
        }
        return cityList;
    }

    public Map<Long, ProvinceCityDistrictDto> getDistrict() {
        if (PlaceDeviceUtil.districtMap == null) {
            cityInit();
        }
        return PlaceDeviceUtil.districtMap;
    }

    private Specification<Place> specification(PlaceSearchRequest placeSearchRequest) {
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(cb.equal(root.get("city"), cityId));
            if (StringUtils.isNotBlank(placeSearchRequest.getDistrictId())) {
                list.add(cb.equal(root.get("district"), placeSearchRequest.getDistrictId()));
            }
            if (placeSearchRequest.getDepartmentId() != null) {
                if (placeSearchRequest.isIgnoreNull())// 不包含部门为空的
                    list.add(cb.equal(root.get("department").get("id"), placeSearchRequest.getDepartmentId()));
                else
                    list.add(cb.or(cb.equal(root.get("department").get("id"), placeSearchRequest.getDepartmentId()), cb.isNull(root.get("department").get("id"))));
            }
//            if (null != placeSearchRequest.getSrcId()) {
//                list.add(cb.equal(root.get("src").get("id"), placeSearchRequest.getSrcId()));
//            }
            if (StringUtils.isNotBlank(placeSearchRequest.getCode())) {
                list.add(cb.like(root.get("code"), "%" + placeSearchRequest.getCode().trim() + "%"));
            }
            if (StringUtils.isNotBlank(placeSearchRequest.getName())) {
                String name = "%" + placeSearchRequest.getName().trim() + "%";
                list.add(cb.or(cb.like(root.get("name"), name), cb.like(root.get("address"), name)));
            }
//            if (placeSearchRequest.getVendorId() != null) {
//                list.add(cb.equal(root.get("src").get("vendor").get("id"), placeSearchRequest.getVendorId()));
//            }
            if (null != placeSearchRequest.getBeginDate() && null != placeSearchRequest.getEndDate()) {
                list.add(cb.between(root.get("installAt"), new Date(Long.valueOf(placeSearchRequest.getBeginDate())), new Date(Long.valueOf(placeSearchRequest.getEndDate()))));
            }
            return cb.and(list.toArray(new Predicate[0]));
        };
    }

    public PageResult<?> findAllPlace(PlaceSearchRequest placeSearchRequest) {
        try {
            if (placeSearchRequest.getPage() == null)
                placeSearchRequest.setPage(0);
            if (placeSearchRequest.getSize() == null) {
                placeSearchRequest.setSize(10);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "installAt");
            Pageable pageable = PageRequest.of(placeSearchRequest.getPage(), placeSearchRequest.getSize(), sort);
            Page<Place> pageResult = placeDao.findAll(this.specification(placeSearchRequest), pageable);
            List<Place> list = pageResult.getContent();
            List<PlaceDetailDto> placeDtos = this.convertDto(list);
            return new PageResult<>(placeDtos, pageResult.getTotalElements(), placeSearchRequest.getPage(),
                    pageResult.getContent().size());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    private List<PlaceDetailDto> convertDto(List<Place> places) {
        List<Object[]> deviceList = deviceDao.countDeviceByPlace();
        Map<Long, Long> deviceMap = new HashMap<>();
        for (Object[] objects : deviceList) {
            deviceMap.put(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()));
        }
        List<PlaceDetailDto> placeDtos = places.stream().map(place -> {
            PlaceDetailDto placeDetailDto = new PlaceDetailDto();
            placeDetailDto.setLatitude(place.getLatitude());
            placeDetailDto.setLongitude(place.getLongitude());
            placeDetailDto.setPlaceId(place.getId().toString());
            BeanCopier copier = BeanCopier.create(Place.class, PlaceDetailDto.class, false);
            copier.copy(place, placeDetailDto, null);
            placeDetailDto.setDeviceCount(deviceMap.get(place.getId()) != null ? deviceMap.get(place.getId()) : 0L);
            placeDetailDto.getDepartmentIds();
            return placeDetailDto;
        }).collect(Collectors.toList());
        return placeDtos;
    }

    public Place addPlace(PlaceRequest placeRequest) {
        if (StringUtils.isNotEmpty(placeRequest.getCode()) && 0 < placeDao.findCode(placeRequest.getCode()))
            throw new ValidException("场所编码重复");
        if (CollectionUtils.isNotEmpty(placeRequest.getPlaceLabels())
                && placeLabelDao.getTopLevelCount(placeRequest.getPlaceLabels()).size() > 0) {
            throw new ValidException("同一标签分类只能选择一种二级标签");
        }
        Place place = new Place();
        try {
            place.setCreateAt(new Date());
            this.setPlaceProperties(place, placeRequest);
//            BeanUtils.getInstance().copyPropertiesIgnoreNull(place, placeRequest);
            BeanCopier copier = BeanCopier.create(PlaceRequest.class, Place.class, false);
            copier.copy(placeRequest, place, null);
            place = placeDao.save(place);
            placeTagDao.findById(placeRequest.getPlaceTags().get(0)).orElseThrow(() -> new DBException("find placeTag by id error"));
            savePlaceTag(place, placeRequest.getPlaceTags());
            savePlaceLabel(place, placeRequest.getPlaceLabels());
            return place;
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void deletePlace(Long id) {
        int count = placeDao.isUsedInDevice(id);
        if (count > 0)
            throw new ValidException("删除失败，场所已关联设备");
        try {
            placeLabelRelationDao.deleteByPlaceId(id);
            placeTagRelationDao.deleteByPlaceId(id);
            placeDao.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public PlaceDetailDto findOne(Long id) {
        try {
            Place place = placeDao.findById(id).orElseThrow(() -> new DBException("find by id error"));
            PlaceDetailDto placeDetailDto = new PlaceDetailDto();
            BeanCopier copier = BeanCopier.create(Place.class, PlaceDetailDto.class, false);
            copier.copy(place, placeDetailDto, null);
            placeDetailDto.setDeviceCount(deviceDao.countDeviceByPlace(place.getId()));
            placeDetailDto.setPlaceId(place.getId().toString());
            placeDetailDto.setLatitude(place.getLatitude());
            placeDetailDto.setLongitude(place.getLongitude());
            return placeDetailDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Place updatePlace(Long id, PlaceRequest placeRequest) {
        Place place = placeDao.findById(id).orElseThrow(() -> new DBException("find by id error"));
        if (StringUtils.isNotBlank(placeRequest.getCode()) && !placeRequest.getCode().equals(place.getCode())
                && 0 < placeDao.findCode(placeRequest.getCode())) {
            throw new ValidException("场所编码重复");
        }
        if (CollectionUtils.isNotEmpty(placeRequest.getPlaceLabels())
                && placeLabelDao.getTopLevelCount(placeRequest.getPlaceLabels()).size() > 0) {
            throw new ValidException("同一标签分类只能选择一种二级标签");
        }
        // 查询该场所下所有设备的设备编码
        List<String> codeList = deviceDao.findCodeByPlaceId(place.getId());

        // 场所下已经有设备，则不允许修改code
        if (CollectionUtils.isNotEmpty(codeList) && null != placeRequest.getCode() && !placeRequest.getCode().equals(place.getCode())) {
            throw new ValidException("场所下已关联设备，不能修改场所编码");
        }
        if (null != placeRequest.getDepartmentId()) {
            Department department = departmentDao.findById(placeRequest.getDepartmentId()).orElseThrow(() -> new DBException("find src by id error"));
            place.setDepartment(department);
            placeRequest.setDepartmentId(null);
        }
        if (placeRequest.isDeleteDept()) {
            place.setDepartment(null);
            placeRequest.setDepartmentId(null);
        }
        try {
            this.setPlaceProperties(place, placeRequest);
            BeanUtils.getInstance().copyPropertiesIgnoreNull(place, placeRequest);
            place = placeDao.save(place);
            placeTagRelationDao.deleteByPlaceId(id);
            savePlaceTag(place, placeRequest.getPlaceTags());
            placeLabelRelationDao.deleteByPlaceId(id);
            savePlaceLabel(place, placeRequest.getPlaceLabels());
            return place;
        } catch (Exception e) {
            log.error("", e);
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    private void savePlaceTag(Place place, List<Long> placeTags) {
        Set<PlaceTagRelation> ptList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(placeTags)) {
            for (Long placeTagId : placeTags) {
                PlaceTagRelation placeTagRelation = new PlaceTagRelation();
                PlaceTag placeTag = new PlaceTag();
                placeTag.setId(placeTagId);
                placeTagRelation.setTag(placeTag);
                placeTagRelation.setPlace(place);
                placeTagRelation.setCreateDate(new Date());
                placeTagRelation.setUpdateDate(new Date());
                ptList.add(placeTagRelation);
            }
            place.setPlaceTagRelation(ptList);
            placeTagRelationDao.saveAll(ptList);
        }
    }

    private void savePlaceLabel(Place place, List<String> labels) {
        Set<PlaceLabelRelation> plSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(labels)) {
            List<PlaceLabel> placeLabels = placeLabelDao.findByCodeIn(labels);
            for (PlaceLabel placeLabel : placeLabels) {
                PlaceLabelRelation placeLabelRelation = new PlaceLabelRelation();
                placeLabelRelation.setPlaceLabel(placeLabel);
                placeLabelRelation.setPlace(place);
                plSet.add(placeLabelRelation);
            }
            place.setPlaceLabelRelation(plSet);
            placeLabelRelationDao.saveAll(plSet);
        }
    }

    private void setPlaceProperties(Place place, PlaceRequest placeRequest) {
        if (null != placeRequest.getDepartmentId()) {
            Department department = new Department();
            department.setId(placeRequest.getDepartmentId());
            place.setDepartment(department);
            placeRequest.setDepartmentId(null);
        }
        double pow = Math.pow(10, 12);
        if (null != placeRequest.getLongitude())
            place.setLongitude((long) (placeRequest.getLongitude() * pow));
        if (null != placeRequest.getLatitude())
            place.setLatitude((long) (placeRequest.getLatitude() * pow));
        if (placeRequest.getLongitude() != null && placeRequest.getLatitude() != null) {
            try {
                Long gridCode = Long.parseLong((int) (placeRequest.getLongitude() * 1000) + ""
                        + (int) (placeRequest.getLongitude() * 1000));
                place.setGridCode(gridCode);
            } catch (Exception e) {
                System.out.println("transform place.gridCode error!");
                e.printStackTrace();
            }
        }
        placeRequest.setLongitude(null);
        placeRequest.setLatitude(null);
        place.setUpdateAt(new Date());
    }

    public void download(PlaceSearchRequest placeSearchRequest, HttpServletResponse response) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            List<String> contents = new ArrayList<>();
            List<Place> data = new ArrayList<>();
            int index = 0;
            long start = System.currentTimeMillis();
            int LOAD_SIZE = 5000;
            do {
                index++;
                Pageable pageable = PageRequest.of(index - 1, LOAD_SIZE, sort);
                Page<Place> pageResult = placeDao.findAll(this.specification(placeSearchRequest), pageable);
                data.clear();
                data = pageResult.getContent();

                List<String[]> rowData = new ArrayList<>();
                data.forEach(place -> {
                    String[] rowValue = new String[8];
                    rowValue[0] = place.getName();
                    rowValue[1] = place.getAddress();
                    rowValue[2] = place.getCode();
                    rowValue[3] = PlaceDeviceUtil.getCityAreaName(place.getDistrict());
                    rowValue[4] = place.getContactName();
                    rowValue[5] = place.getContactPhone();
                    Set<PlaceTagRelation> tags = place.getPlaceTagRelation();
                    String placeTag = "";
                    if (CollectionUtils.isNotEmpty(tags)) {
                        Optional<String> op = tags.stream().filter(a -> a.getTag() != null)
                                .map(a -> a.getTag().getName()).reduce((a, b) -> a + "," + b);
                        placeTag = op.orElse("");
                    }
                    rowValue[6] = placeTag;
                    Set<PlaceLabelRelation> labels = place.getPlaceLabelRelation();
                    String placeLabel = "";
                    if (CollectionUtils.isNotEmpty(labels)) {
                        Optional<String> op = labels.stream().filter(a -> a.getPlaceLabel() != null)
                                .map(a -> a.getPlaceLabel().getName()).reduce((a, b) -> a + "," + b);
                        placeLabel = op.orElse("");
                    }
                    rowValue[7] = placeLabel;
                    rowData.add(rowValue);
                });
                contents.add(ExportUtil.genContentByStringList(rowData));
            } while (data.size() == LOAD_SIZE);
            log.debug("load export data used " + (System.currentTimeMillis() - start) + "ms");
            String fileName = "place" + System.currentTimeMillis();
            if (!ExportUtil.doExport(contents, "场所名称,场所地址,场所编码,行政地区,联系人姓名,联系人电话,场所分类,场所标签", fileName, response))//,数据来源
                log.error("writing csv file error!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportPlaceList(PlaceSearchRequest placeSearchRequest, HttpServletResponse response) {
        try {
            placeSearchRequest.setPage(0);
            placeSearchRequest.setSize(50000);
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            Pageable pageable = PageRequest.of(placeSearchRequest.getPage(), placeSearchRequest.getSize(), sort);
            long start = System.currentTimeMillis();
            Page<Place> pageResult = placeDao.findAll(this.specification(placeSearchRequest), pageable);
            List<Place> list = pageResult.getContent();
            List<PlaceDetailDto> placeDtos = this.convertDto(list);
            List<String> contents = new ArrayList<>();
            List<String[]> rowData = new ArrayList<>();
            List<Vendor> vendors = vendorDao.findAll();
//            List<Department> deptList = departmentDao.findAll();
//            Map<Integer,Department> departmentMap = StreamSupport.stream(deptList.spliterator(),false).collect(toMap(Department::getId,Function.identity(), (k1, k2) -> k1));
            Map<String, Vendor> vendorMap = StreamSupport.stream(vendors.spliterator(), false).collect(toMap(Vendor::getCode, Function.identity(), (k1, k2) -> k1));
            placeDtos.forEach(item -> {
                String[] rowValue = new String[26];
                int i = 0;
                rowValue[i++] = item.getName();
                List<String> placeTags = new ArrayList<>();
                item.getPlaceTagRelation().forEach(tag -> {
                    placeTags.add(tag.getTag().getName());
                });
                String tags = StringUtils.join(placeTags, ",");
                rowValue[i++] = null == tags ? "" : tags;// 场所类型

                rowValue[i++] = item.getCode();
                if (null != item.getDepartment()) {
                    rowValue[i++] = item.getDepartmentNames();
                } else {
                    rowValue[i++] = "";
                }
                rowValue[i++] = PlaceTypeEnum.getNameById(item.getPlaceType());
                rowValue[i++] = MyDateUtil.getDateStr(item.getInstallAt().getTime());
                rowValue[i++] = item.getDeviceCount().toString();
                rowValue[i++] = item.getAddress();
                String longitude = null == item.getLongitude() ? "" : item.getLongitude().toString();
                String latitude = null == item.getLatitude() ? "" : item.getLatitude().toString();
                if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
                    rowValue[i++] = longitude + "," + latitude;
                } else {
                    rowValue[i++] = "";
                }
                rowValue[i++] = item.getCityArea();
                rowValue[i++] = null == item.getInforMan() ? "" : item.getInforMan();
                rowValue[i++] = null == item.getInforManTel() ? "" : "'" + item.getInforManTel();
                rowValue[i++] = null == item.getPrincipal() ? "" : item.getPrincipal();
                rowValue[i++] = null == item.getPrincipalCertType() ? "" : ContactCertType.getNameById(item.getPrincipalCertType());
                rowValue[i++] = null == item.getPrincipalCertCode() ? "" : "'" + item.getPrincipalCertCode();
                rowValue[i++] = null == item.getInforManTel() ? "" : "'" + item.getInforManTel();
                if (StringUtils.isNotBlank(item.getOpenAt()) && StringUtils.isNotBlank(item.getCloseAt()) && null != item.getOpenAt() && null != item.getCloseAt()) {
                    rowValue[i++] = item.getOpenAt();
                    rowValue[i++] = item.getCloseAt();
                } else {
                    rowValue[i++] = "";
                    rowValue[i++] = "";
                }
                rowValue[i++] = null == item.getStatus() ? "" : OperatingStatus.getNameByStatus(item.getStatus());
                rowValue[i++] = null == item.getNetType() ? "" : NetType.getNameById(item.getNetType());
                rowValue[i++] = null == item.getProducerCode() ? "" : ProducerType.getNameById(item.getProducerCode());
                rowValue[i++] = null == item.getAuthAccount() ? "" : item.getAuthAccount();
                rowValue[i++] = null == item.getExitIp() ? "" : item.getExitIp();

                List<String> placeLabels = new ArrayList<>();
                item.getPlaceLabelRelation().forEach(label -> {
                    placeLabels.add(label.getPlaceLabel().getName());
                });
                String placeLabel = StringUtils.join(placeLabels, ",");
                rowValue[i++] = null == placeLabel ? "" : placeLabel;// 场所标签
                rowValue[i] = null == item.getTerminalFactoryOrgCode() ? "" : null == vendorMap.get(item.getTerminalFactoryOrgCode()) ? "" : vendorMap.get(item.getTerminalFactoryOrgCode()).getName();
                rowData.add(rowValue);
            });
            contents.add(ExportUtil.genContentByStringList(rowData));
            log.debug("load export data used " + (System.currentTimeMillis() - start) + "ms");
            String fileName = "place" + System.currentTimeMillis();
            if (!ExportUtil.doExport(contents, "场所名称,场所类型,场所编码,所属部门,经营性质,安装时间,设备数量,安装地址," +//,数据源
                    "安装经纬度,所属区县,安装人姓名,安装人电话,场所经营法人,经营法人有效证件类型,经营法人有效证件号码,法人联系方式,营业开始时间," +
                    "营业结束时间,营业状态,接入方式,接入服务商,网络认证账号或固定IP,外网IP,场所标签,供应商名称", fileName, response))
                log.error("writing csv file error!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generatePlaceCode(String district, List<Long> placeTags, Integer placeType) {
        PlaceTag placeTag = placeTagDao.findById(placeTags.get(0)).orElseThrow(() -> new DBException("find placeTag by id error"));
        String placeCode = "";
        do {
            placeCode = PlaceDeviceUtil.generatePlaceCode(district, placeTag.getCode(),
                    placeType, RandomUtils.nextLong(000000, 999999));
        } while (0 < placeDao.findCode(placeCode));
        return placeCode;
    }

    public Map<String, Object> uploadAndParse(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new ArgumentException("请上传CSV格式的文件");
        }
        Map<String, Object> resultMap = new HashMap<>();
        String fileName = file.getOriginalFilename();
        try {
            File saveFile = FileUtil.upload(file, uploadPath);
            List<String[]> contents = FileUtil.parseCsvFile(saveFile);

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

    public void importPlaces(String fileName) {
        String filePath = uploadPath + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ArgumentException("文件不存在");
        }
        List<String[]> contents = FileUtil.parseCsvFile(file);//获取导入文件所有内容

        Iterable<PlaceTag> tagData = placeTagDao.findAll();// 分类
        Map<String, PlaceTag> tagMap = StreamSupport.stream(tagData.spliterator(), false)
                .collect(toMap(PlaceTag::getName, Function.identity(), (k1, k2) -> k1));

        Map<Long, ProvinceCityDistrictDto> cityDtoMap = this.getDistrict();//省市区
        Map<String, ProvinceCityDistrictDto> districtMap = cityDtoMap.values().stream().filter(a -> a.getCity().equals(cityId))
                .collect(Collectors.toMap(ProvinceCityDistrictDto::getName, Function.identity(), (k1, k2) -> k1));

        Iterable<Department> departs = departmentDao.findAll();//部门
        Map<String, Department> departMap = StreamSupport.stream(departs.spliterator(), false)
                .collect(toMap(Department::getName, Function.identity(), (k1, k2) -> k1));

        Iterable<Src> src = srcDao.findAll();//数据源
        Map<String, Src> srcMap = StreamSupport.stream(src.spliterator(), false)
                .collect(toMap(Src::getName, Function.identity(), (k1, k2) -> k1));

        Iterable<PlaceLabel> placeLable = placeLabelDao.findAll();//标签
        Map<String, PlaceLabel> placeLableMap = StreamSupport.stream(placeLable.spliterator(), false)
                .collect(toMap(PlaceLabel::getName, Function.identity(), (k1, k2) -> k1));

        List<Vendor> vendors = vendorDao.findAll();
        Map<String, Vendor> vendorMap = StreamSupport.stream(vendors.spliterator(), false).collect(toMap(Vendor::getName, Function.identity(), (k1, k2) -> k1));

        List<Place> places = new ArrayList<>();
        contents.remove(0);
        for (int i = 0; i < contents.size(); i++) {
            String[] content = contents.get(i);
            List<String> errorMsg = new ArrayList<>();
            checkData(content, tagMap, districtMap, departMap, vendorMap, errorMsg);//数据校验
            if (errorMsg.size() > 0)
                throw new ValidException("第" + (i + 1) + "行存在以下错误：" + StringUtils.join(errorMsg, ","));
            Place place = setPlaceEntity(content, tagMap, districtMap, departMap, srcMap, placeLableMap, vendorMap);
            places.add(place);
        }
        Map<String, List<Place>> placeMap = places.stream().collect(Collectors.groupingBy(Place::getCode));
        placeMap.forEach((k, v) -> {
            if (v.size() > 1) {
                throw new ValidException("场所编码重复:" + k);
            }
        });
        List<Place> existPlace = placeDao.findByPlaceCodes(placeMap.keySet());
        if (CollectionUtils.isNotEmpty(existPlace)) {
            throw new ValidException("场所编码已存在:" + existPlace.get(0).getCode());
        }
        try {
            placeDao.saveAll(places);//级联添加
        } catch (ArgumentException ae) {
            log.error("", ae);
            throw ae;
        } catch (Exception e) {
            log.error("place import error", e);
            throw new ServiceException("place import error", e);
        } finally {//执行完毕后删除文件
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void checkData(String[] content, Map<String, PlaceTag> tagMap, Map<String, ProvinceCityDistrictDto> districtMap, Map<String, Department> departMap, Map<String, Vendor> vendorMap, List<String> errorMsg) {
        //------必填校验
        Map<Integer, String> must = new HashMap<>();
        must.put(0, "场所名称必填");
        must.put(1, "场所类型必填");
        must.put(2, "经营性质必填");
        must.put(3, "安装时间必填");
        must.put(4, "所属区县必填");
        must.put(5, "所属部门必填");
        must.put(6, "场所编码必填");
        must.put(7, "详细地址必填");
        must.put(8, "经度坐标必填");
        must.put(9, "纬度坐标必填");
        for (Integer i : must.keySet()) {
            if (StringUtils.isBlank(content[i]))
                errorMsg.add(must.get(i));
        }
        //数据校验
        int i = 0;
        String[] tag = content[++i].split(",");
        Set<PlaceTagRelation> tags = new HashSet<>();
        for (String value : tag) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            PlaceTag placeTag = tagMap.get(value);
            if (null == placeTag)
                errorMsg.add("找不到场所类型：" + value);
        }
        Integer placeType = PlaceTypeEnum.getIdByName(content[++i]);
        if (null == placeType)
            errorMsg.add("场所性质错误");
        try {
            DateUtils.parseDateStrictly(content[++i], MyDateUtil.IMPORT_DATE_PATTERNS);// 安装时间
        } catch (ParseException e) {
            errorMsg.add("安装时间格式错误");
        }
        String[] mergeName = content[++i].split("/");
        if (mergeName.length < 2) {
            errorMsg.add("请按模板格式填写所属区县");
        } else {
            String cityName = mergeName[mergeName.length - 2];
            String districtName = mergeName[mergeName.length - 1];
            ProvinceCityDistrictDto city = districtMap.get(cityName);
            ProvinceCityDistrictDto district = districtMap.get(districtName);
            if (city == null) {
                List<ProvinceCityDistrictDto> correctCity = districtMap.values().stream().filter(a -> a.getCity() == a.getId()).collect(Collectors.toList());
                errorMsg.add("所属区县应属:" + correctCity.get(0).getName());
            } else {
                if (district == null)
                    errorMsg.add("找不到所属区县:" + districtName);
            }
        }
        Department department = departMap.get(content[++i]);
        if (department == null) {
            errorMsg.add("找不到所属部门:" + content[i]);
        }
        ++i;
        ++i;
        try {
            long longitude = (long) (Math.pow(10, 12) * Double.valueOf(content[++i]));
        } catch (Exception e) {
            errorMsg.add("经度数据错误");
        }
        try {
            long latitude = (long) (Math.pow(10, 12) * Double.valueOf(content[++i]));
        } catch (Exception e) {
            errorMsg.add("纬度数据错误");
        }
        // 以下为非必填项
        ++i;
        if (StringUtils.isNotBlank(content[++i])) {
            if (null == OperatingStatus.getStatusByDesc(content[i]))
                errorMsg.add("营业状态不存在");
        }
        ++i;
        ++i;
        if (StringUtils.isNotBlank(content[++i])) {
            if (null == NetType.getIdByName(content[i]))
                errorMsg.add("接入方式不存在");
        }
        if (StringUtils.isNotBlank(content[++i])) {
            if (null == ProducerType.getIdByShortName(content[i]))
                errorMsg.add("接入服务商不存在");
        }
        ++i;
        ++i;
        if (StringUtils.isNotBlank(content[++i])) {
            if (null == ContactCertType.getIdByDesc(content[i]))
                errorMsg.add("法人有效证件类型不存在");
        }
        i += 5;
        if (StringUtils.isNotBlank(content[++i])) {
            if (null == vendorMap.get(content[i]))
                errorMsg.add("供应商不存在");
        }
    }

    private Place setPlaceEntity(String[] content, Map<String, PlaceTag> tagMap, Map<String, ProvinceCityDistrictDto> districtMap, Map<String, Department> departMap, Map<String, Src> srcMap, Map<String, PlaceLabel> placeLableMap, Map<String, Vendor> vendorMap) {
        int i = 0;
        Place place = new Place();
        place.setName(content[i]);
        String[] tag = content[++i].split(",");
        Set<PlaceTagRelation> tags = new HashSet<>();
        for (String value : tag) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            PlaceTag placeTag = tagMap.get(value);
            PlaceTagRelation tagRelation = new PlaceTagRelation();
            tagRelation.setCreateDate(new Date());
            tagRelation.setUpdateDate(new Date());
            tagRelation.setTag(placeTag);
            tagRelation.setPlace(place);
            tags.add(tagRelation);
        }
        place.setPlaceTagRelation(tags);// 场所类型
        Integer placeType = PlaceTypeEnum.getIdByName(content[++i]);
        place.setPlaceType(placeType);//场所性质
        try {
            place.setInstallAt(DateUtils.parseDateStrictly(content[++i], MyDateUtil.IMPORT_DATE_PATTERNS));// 安装时间
        } catch (ParseException e) {
            throw new ArgumentException("安装时间格式错误", e);
        }
        String[] district = content[++i].split("/");
        ProvinceCityDistrictDto districtId = districtMap.get(district[1]);
        place.setDistrict(districtId.getId().toString());// 所属区县
        place.setCity(districtId.getCity().toString());
        place.setProvince(districtId.getProvince().toString());

        Department department = departMap.get(content[++i]);
        place.setDepartment(department);// 所属部门
        place.setCode(content[++i]);// 场所编码
        place.setAddress(content[++i]);// 详细地址
        long longitude = (long) (Math.pow(10, 12) * Double.valueOf(content[++i]));
        long latitude = (long) (Math.pow(10, 12) * Double.valueOf(content[++i]));
        place.setLongitude(longitude);
        place.setLatitude(latitude);
        // 以下为非必填项
        if (StringUtils.isNotBlank(content[++i])) {//外网IP
            place.setExitIp(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {//营业状态
            place.setStatus(OperatingStatus.getStatusByDesc(content[i]));
        }
        if (StringUtils.isNotBlank(content[++i])) {//安装人姓名
            place.setInforMan(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {//安装人电话
            place.setInforManTel(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {//接入方式
            place.setNetType(NetType.getIdByName(content[i]));
        }
        if (StringUtils.isNotBlank(content[++i])) {//接入服务商
            place.setProducerCode(ProducerType.getIdByShortName(content[i]));
        }
        if (StringUtils.isNotBlank(content[++i])) {//场所经营法人
            place.setPrincipal(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {//法人联系方式
            place.setPrincipalTel(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {
            place.setPrincipalCertType(ContactCertType.getIdByDesc(content[i]));//法人有效证件类型
        }
        if (StringUtils.isNotBlank(content[++i])) {
            place.setPrincipalCertCode(content[i]);//法人有效证件号码
        }
        if (StringUtils.isNotBlank(content[++i])) {
            place.setOpenAt(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {
            place.setCloseAt(content[i]);
        }
        if (StringUtils.isNotBlank(content[++i])) {//网络认证账号
            place.setAuthAccount(content[i]);
        }
        String[] label = content[++i].split(",");//场所标签
        Set<PlaceLabelRelation> labels = new HashSet<>();
        for (String value : label) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            PlaceLabelRelation plr = new PlaceLabelRelation();
            PlaceLabel pl = placeLableMap.get(value);
            plr.setPlaceLabel(pl);
            plr.setPlace(place);
            labels.add(plr);
        }
        place.setPlaceLabelRelation(labels);// 场所标签
        if (StringUtils.isNotBlank(content[++i])) {// 供应商
            Vendor vendor = vendorMap.get(content[i]);
            place.setTerminalFactoryOrgCode(vendor.getCode());
        }
        return place;
    }

    @Override
    public List<Place> findAll() {
        return placeDao.findAll();
    }

    @Override
    public List<Place> findIdCodeNameAddressAll() {
        return placeDao.findIdCodeNameAddressAll();
    }

//    @Override
//    public List<Long> findPlaceIdByAddress(String address) {
//        return placeDao.findPlaceIdByAddress(address);
//    }

    @Override
    public String getCityShortName() {
        City city = cityDao.findById(cityId).orElseThrow(() -> new DBException("city not found"));
        return city.getShortName();
    }

    @Override
    public boolean isHavePlace(Integer deptId) {
        int count = placeDao.isHavePlace(deptId);
        return count > 0;
    }

    public Map<String, String> findAllCertType() {
        Map<String, String> map = new HashMap<>();
        for (ContactCertType value : ContactCertType.values()) {
            map.put(value.getId(), value.getDesc());
        }
        return map;
    }

    public List<Map<String, String>> findByTypeAndValue(String type, String value) {
        if ("1".equals(type)) {
            return placeDao.findByFuzzyName(value);
        } else if ("2".equals(type)) {
            return placeDao.findByFuzzyAddress(value);
        }
        throw new ArgumentException("请求类型错误");
    }
}
