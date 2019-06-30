package com.jiayi.platform.basic.manager;

import com.jiayi.platform.basic.dao.PhoneHomeDao;
import com.jiayi.platform.basic.entity.ObjectOrganizationInfo;
import com.jiayi.platform.basic.entity.PhoneHome;
import com.jiayi.platform.basic.util.IMSIUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class IMSIHomeManager {

    private static Logger log = LoggerFactory.getLogger(IMSIHomeManager.class);

    @Autowired
    private PhoneHomeDao phoneHomeDao;

    private Map<Long, PhoneHome> phoneHomeDtoMap;
   // @Autowired
   // private PhoneHomeCacheDao phoneHomeCacheDao;
    private ReentrantLock mapLock = new ReentrantLock();

    private int maxLimit = 20000;


    public IMSIHomeManager(){
        phoneHomeDtoMap = new HashMap<Long,PhoneHome>();
    }


    public void loadPhoneHome(){
        try {
            Future<Long> countFuture = ThreadPoolUtil.getInstance().submit(() ->phoneHomeDao.count());
            Long count = countFuture.get();

            int pages = (count.intValue() - 1) / maxLimit + 1;
            List<Future<List<PhoneHome>>> phoneFutureList = new ArrayList<>();
            for (int index = 0; index < pages; index++) {
                PageRequest pageRequest = new PageRequest(index, maxLimit);
                Future<List<PhoneHome>> LocationsFuture = ThreadPoolUtil.getInstance()
                        .submit(() -> phoneHomeDao.findAll(pageRequest).getContent());
                phoneFutureList.add(LocationsFuture);
            }

            for (Future<List<PhoneHome>> LocationsFuture: phoneFutureList) {
                addPhoneHomes(LocationsFuture.get());
            }
        }catch (Exception e) {
            log.error("",e);

        }
    }

    /**
     * 将查询出来的信息保存到MAP
     * @param list
     */
    private void addPhoneHomes(List<PhoneHome> list){
        if (list == null || list.size() == 0){
            return;
        }

        //phoneHomeCacheDao.putAll(list);
        list.forEach(dto->{
            Long key = Long.valueOf(dto.getNumber());
            phoneHomeDtoMap.put(key,dto);
        });
    }

    /**
     * 取得IMSI的通用归属信息
     * @param value
     * @return
     */
    public ObjectOrganizationInfo getOrganization(String value){
        PhoneHome dto = getIMSIHome(value);
        ObjectOrganizationInfo info = new ObjectOrganizationInfo();
        if (dto == null){
            return info;
        }
        if(!StringUtils.isEmpty(dto.getCity())){
            info.setOrganizationName(dto.getCity()+dto.getOperator());
        }else {
            info.setOrganizationName(dto.getOperator());
        }

        if(!StringUtils.isEmpty(dto.getProvince())){
           // info.setOrganizationAddress(dto.getProvince());
        }
        return info;
    }

    /**
     * 取得IMSI 对应的所在运营商所在地
     * @param imsi
     * @return PhoneLocationDto
     */
    public PhoneHome getIMSIHome(String imsi){
        long start = System.nanoTime();
        if (StringUtils.isEmpty(imsi)){
            return null;
        }

        String number = IMSIUtil.getMobileNumber(imsi);
        if (!StringUtils.isEmpty(number)){
            Long phone = Long.parseLong(number);
            //PhoneHome dto = phoneHomeCacheDao.getPhoneHomeByNumber(phone);//phoneHomeDtoMap.get(phone);
            PhoneHome dto = phoneHomeDtoMap.get(phone);
            if (dto != null){
                //log.info("get {} PhoneHome  costs {}ns", imsi, System.nanoTime() - start);
                return dto;
            }
        }
        //没有获取到所在地运营商信息
        String operator = IMSIUtil.getMobileOperator(imsi);
        if (StringUtils.isEmpty(operator)){
            return null;
        }else {
            PhoneHome dto = new PhoneHome();
            dto.setOperator(operator);
            return dto;
        }
    }
}
