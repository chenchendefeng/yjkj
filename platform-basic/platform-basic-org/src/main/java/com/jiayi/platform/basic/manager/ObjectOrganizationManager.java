package com.jiayi.platform.basic.manager;

import com.jiayi.platform.basic.dao.IMEICompanyDao;
import com.jiayi.platform.basic.dao.MacCompanyDao;
import com.jiayi.platform.basic.dao.PlateNumberDao;
import com.jiayi.platform.basic.entity.*;
import com.jiayi.platform.basic.util.DevTypeUtil;
import com.jiayi.platform.basic.util.IMEIUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

@Component
public class ObjectOrganizationManager {

    public static Log log = LogFactory.getLog(ObjectOrganizationManager.class);
    @Autowired
    private IMSIHomeManager imsiManager;

    @Autowired
    private MacCompanyDao macCompanyDao;

    @Autowired
    private PlateNumberDao plateNumberDao;

    @Autowired
    private IMEICompanyDao imeiCompanyDao;

    private int maxLimit = 10000;

    /**
     * 保存MA-L 注册类型的MAC
     */
    private Map<String, MacCompany> malMacMap;
    /**
     * 保存 MA-M 注册类型的MAC
     */
    private Map<String, MacCompany> mamMacMap;
    /**
     * 保存 MA-S注册类型的MAC
     */
    private Map<String, MacCompany> masMacMap;


    /**
     * 保存车牌对应的地址信息 key为车牌  PlateNumber 为车牌所在的城市信息
     */
    private Map<String, PlateNumber> plateNumberMap;
    private int plateNumberLen = 2;

    /**
     * 保存IMEI对应的手机品牌 厂家信息,key为前8位IMEI编码。
     */
    private Map<String, IMEICompany> imeiMap;
    private int imeiCodeLen = 8;

    public ObjectOrganizationManager() {
        malMacMap = new HashMap<String, MacCompany>();
        mamMacMap = new HashMap<String, MacCompany>();
        masMacMap = new HashMap<String, MacCompany>();

        plateNumberMap = new HashMap<String, PlateNumber>();

        imeiMap = new HashMap<String, IMEICompany>();
    }

    /**
     * 取得某种类型对像值 对应的 所属 组织信息
     *
     * @param type  mac,imsi,carno,imei
     * @param value 对像值
     * @return ObjectOrganizationInfo 组织信息
     */
    public ObjectOrganizationInfo getObjectOrganization(String type, String value) {
        String typelc = type.toLowerCase();
        if (DevTypeUtil.IMSI_TYPE.startsWith(typelc)) {
            return imsiManager.getOrganization(value);
        } else if (DevTypeUtil.MAC_TYPE.startsWith(typelc)) {
            return getMacOrganization(value);
        } else if (DevTypeUtil.CARNO_TYPE.startsWith(typelc)) {
            return getCarnoOrganization(value);
        } else if (DevTypeUtil.IMEI_TYPE.startsWith(typelc)) {
            return getIMEIOrganization(value);
        }
        return null;
    }

    public ObjectCityInfo getObjectCity(String type, String value) {
        ObjectCityInfo info = new ObjectCityInfo();
        String typelc = type.toLowerCase();
        if (DevTypeUtil.IMSI_TYPE.startsWith(typelc)) {
            PhoneHome dto = imsiManager.getIMSIHome(value);
            if (dto != null && dto.getCityId() != null) {
                info.setCityId(dto.getCityId());
            }
        } else if (DevTypeUtil.CARNO_TYPE.startsWith(typelc)) {
            PlateNumber dto = getPlateNumber(value);
            if (dto != null && StringUtils.isNotBlank(dto.getCityId())) {
                info.setCityId(Long.valueOf(dto.getCityId()));
            }
        } else {
            throw new UnsupportedOperationException("查询的类型不正确");
        }
        return info;
    }

    @Scheduled(fixedRate = 36000000)
    public void loadConfig() {
        if (imsiManager == null) {
            imsiManager = new IMSIHomeManager();
        }

        try {
            imsiManager.loadPhoneHome();
            loadMacCompany();
            loadPlateNumberAddr();
            loadIMEICompany();
        } catch (Exception e) {
            log.error("loadConfig Exception : ", e);
        }
    }

    /**
     * 从数据库加载mac地址信息到内存中
     */
    private void loadMacCompany() {
        try {
            Future<Long> countFuture = ThreadPoolUtil.getInstance().submit(() -> macCompanyDao.count());
            Long count = countFuture.get();

            int pages = (count.intValue() - 1) / maxLimit + 1;
            for (int index = 0; index < pages; index++) {
                PageRequest pageRequest = new PageRequest(index, maxLimit);
                Future<List<MacCompany>> LocationsFuture = ThreadPoolUtil.getInstance()
                        .submit(() -> macCompanyDao.findAll(pageRequest).getContent());
                addMacCompanies(LocationsFuture.get());
            }
        } catch (Exception e) {
            log.error("loadMacCompany Exception : ", e);
        }
    }

    /**
     * 增加 或更新 MAC 厂家信息MAP
     *
     * @param list
     */
    private void addMacCompanies(List<MacCompany> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        list.forEach(dto -> {
            String key = dto.getAssignment();
            if (MacCompany.REGISTRY_MA_L_VALUE.equals(dto.getRegistry())) {
                malMacMap.put(key, dto);
            } else if (MacCompany.REGISTRY_MA_M_VALUE.equals(dto.getRegistry())) {
                mamMacMap.put(key, dto);
            } else if (MacCompany.REGISTRY_MA_S_VALUE.equals(dto.getRegistry())) {
                masMacMap.put(key, dto);
            }
        });
    }

    /**
     * 取得MAC地址对应的 所属的组织机构信息
     *
     * @param value MAC值
     * @return
     */
    public ObjectOrganizationInfo getMacOrganization(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        String macKey = value.substring(0, MacCompany.MA_S_LEN);
        MacCompany dto = masMacMap.get(macKey);
        if (dto == null) {
            macKey = value.substring(0, MacCompany.MA_M_LEN);
            dto = mamMacMap.get(macKey);
            if (dto == null) {
                macKey = value.substring(0, MacCompany.MA_L_LEN);
                dto = malMacMap.get(macKey);
            }
        }

        ObjectOrganizationInfo info = new ObjectOrganizationInfo();
        if (dto != null) {
            if (StringUtils.isEmpty(dto.getOrganizationNameCn())) {
                info.setOrganizationName(dto.getOrganizationName());
                //info.setOrganizationAddress(dto.getOrganizationAddress());
            } else {
                info.setOrganizationName(dto.getOrganizationNameCn());
                //info.setOrganizationAddress(dto.getOrganizationAddressCn());
            }
        }
        return info;
    }


    /**
     * 加载车牌对应的地址信息 到内存中
     */
    private void loadPlateNumberAddr() {
        try {
            Future<Long> countFuture = ThreadPoolUtil.getInstance().submit(() -> plateNumberDao.count());
            Long count = countFuture.get();

            int pages = (count.intValue() - 1) / maxLimit + 1;
            for (int index = 0; index < pages; index++) {
                PageRequest pageRequest = new PageRequest(index, maxLimit);
                Future<List<PlateNumber>> LocationsFuture = ThreadPoolUtil.getInstance()
                        .submit(() -> plateNumberDao.findAll(pageRequest).getContent());
                addPlateNumbers(LocationsFuture.get());
            }
        } catch (Exception e) {
            log.error("loadPlateNumberAddr Exception : ", e);
        }
    }

    /**
     * 加载或 更新车牌信息 到 车牌map
     *
     * @param list
     */
    private void addPlateNumbers(List<PlateNumber> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        list.forEach(dto -> {
            String key = dto.getCode();
            plateNumberMap.put(key, dto);
        });
    }

    /**
     * 取得车版号 对应的城市信息
     *
     * @param value 车牌号
     * @return
     */
    public ObjectOrganizationInfo getCarnoOrganization(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        PlateNumber dto = getPlateNumber(value);
        ObjectOrganizationInfo info = new ObjectOrganizationInfo();
        if (dto != null) {
            info.setOrganizationName(dto.getAddress());
            //info.setOrganizationAddress(dto.getAddress());
        }
        return info;
    }

    @Nullable
    private PlateNumber getPlateNumber(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String key = value.substring(0, plateNumberLen);
        return plateNumberMap.get(key);
    }


    /**
     * 加载IMEI 手机厂家信息
     */
    private void loadIMEICompany() {
        try {
            Future<Long> countFuture = ThreadPoolUtil.getInstance().submit(() -> imeiCompanyDao.count());
            Long count = countFuture.get();

            int pages = (count.intValue() - 1) / maxLimit + 1;
            for (int index = 0; index < pages; index++) {
                PageRequest pageRequest = new PageRequest(index, maxLimit);
                Future<List<IMEICompany>> LocationsFuture = ThreadPoolUtil.getInstance()
                        .submit(() -> imeiCompanyDao.findAll(pageRequest).getContent());
                addIMEI(LocationsFuture.get());
            }
        } catch (Exception e) {
            log.error("loadIMEICompany Exception : ", e);
        }
    }

    /**
     * 增加IMEI到MAP
     *
     * @param list
     */
    private void addIMEI(List<IMEICompany> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        list.forEach(dto -> {
            String key = dto.getCode();
            imeiCodeLen = key.length();
            imeiMap.put(key, dto);
        });
    }

    /**
     * 通过IMEI 值 取得对应的手机厂商信息
     *
     * @param value
     * @return
     */
    public ObjectOrganizationInfo getIMEIOrganization(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        String key = value.substring(0, imeiCodeLen);
        ObjectOrganizationInfo info = new ObjectOrganizationInfo();
        IMEICompany dto = imeiMap.get(key);
        if (dto != null && !StringUtils.isEmpty(dto.getOrganizationName())) {
            info.setOrganizationName(dto.getOrganizationName());
            if (!StringUtils.isEmpty(dto.getModel())) ;
            {
                info.setOrganizationAddress(dto.getModel());
            }
        }
        return info;
    }

    private void initIMEI() {
        imeiMap.forEach((key, value) -> {
            try {
                String imei = value.getCheckImei();
                if (!StringUtils.isEmpty(imei)) {
                    String imeiNew = IMEIUtil.checkDigit(imei);
                    Optional<IMEICompany> imeiCompany = imeiCompanyDao.findById(key);
                    if (imeiCompany.isPresent()) {
                        IMEICompany imeiCompanyData = imeiCompany.get();
                        imeiCompanyData.setUpdateTime(new Date());
                        imeiCompanyData.setCheckImei(imeiNew);
                        imeiCompanyDao.save(imeiCompanyData);
                    }
                }
            } catch (Exception e) {
                log.error("initIMEI:" + value.toString(), e);
            }
        });


    }


}
