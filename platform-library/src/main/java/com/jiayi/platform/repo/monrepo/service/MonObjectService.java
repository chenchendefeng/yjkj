package com.jiayi.platform.repo.monrepo.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.repo.monrepo.dao.MonObjectDao;
import com.jiayi.platform.repo.monrepo.dao.MonPersonDao;
import com.jiayi.platform.repo.monrepo.dao.MonRepoDao;
import com.jiayi.platform.repo.monrepo.dto.MonObjectDto;
import com.jiayi.platform.repo.monrepo.dto.MonPageDto;
import com.jiayi.platform.repo.monrepo.dto.MonUtilDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorObject;
import com.jiayi.platform.repo.monrepo.entity.MonitorPerson;
import com.jiayi.platform.repo.monrepo.entity.MonitorRepo;
import com.jiayi.platform.repo.monrepo.vo.MonObjectRequest;
import com.jiayi.platform.repo.monrepo.vo.MonObjectSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonRemarkVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Transactional
public class MonObjectService {
    @Autowired
    private MonObjectDao monObjectDao;
    @Autowired
    private MonPersonDao monPersonDao;
    @Autowired
    private MonRepoDao monRepoDao;
    @Autowired
    private MonRepoObjectsCountManager monRepoObjectsCountManager;

    public MonPageDto<MonObjectDto> findMonObjectList(MonObjectSearchVo monObjectSearchVo) {
        /*
        List<MonObjectDto> monObjectDtoAll = monObjectDao.selectMonObjectList(monObjectSearchVo);

        if (monObjectSearchVo.getPage() == null) {
            monObjectSearchVo.setPage(0);
        }
        if (monObjectSearchVo.getSize() == null) {
            monObjectSearchVo.setSize(10);
        }

        int start = monObjectSearchVo.getPage() * monObjectSearchVo.getSize();
        int count = monObjectDtoAll.size();
        int end = count - start > monObjectSearchVo.getSize() ? start + monObjectSearchVo.getSize() : count;
        if(CollectionUtils.isEmpty(monObjectDtoAll) || start > end) {
            return new MonPageDto<MonObjectDto>(Lists.newArrayList(), (long) count, monObjectSearchVo.getPage(), 0);
        }
        List<MonObjectDto> list = monObjectDtoAll.subList(start, end);
        return new MonPageDto<MonObjectDto>(list, (long)count, monObjectSearchVo.getPage(), list.size());
        */

        if (monObjectSearchVo.getPage() == null) {
            monObjectSearchVo.setPage(0);
        }
        if (monObjectSearchVo.getSize() == null) {
            monObjectSearchVo.setSize(10);
        }

        Future<List<MonObjectDto>> dataFuture = ThreadPoolUtil.getInstance()
                .submit(() -> monObjectDao.selectMonObjectList(monObjectSearchVo));
        Future<Long> countFuture = ThreadPoolUtil.getInstance()
                .submit(() -> monObjectDao.countMonObjectList(monObjectSearchVo));

        try {
            List<MonObjectDto> list = dataFuture.get();
            Long count = countFuture.get();

            if(CollectionUtils.isEmpty(list)) {
                return new MonPageDto<MonObjectDto>(Lists.newArrayList(), (long) count, monObjectSearchVo.getPage(), 0);
            }
            return new MonPageDto<MonObjectDto>(list, (long)count, monObjectSearchVo.getPage(), list.size());
        } catch (Exception e) {
            throw new ArgumentException("monObjectList impala search error", e);
        }
    }

    public MonObjectDto addMonObject(MonObjectRequest monObjectRequest) {
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(monObjectRequest.getRepoId());
        if (monitorRepo == null) { throw new ArgumentException("常用库不存在"); }

        MonitorObject monitorObject = new MonitorObject();
        monitorObject.setUid(MonUtilDto.generateUid());
        monitorObject.setObjectName(monObjectRequest.getObjectName());
        monitorObject.setObjectType(monObjectRequest.getObjectType());
        monitorObject.setObjectValue(monObjectRequest.getObjectValue());
        monitorObject.setVendorDesc(monObjectRequest.getVendorDesc());
        monitorObject.setDescription(monObjectRequest.getDescription());
        monitorObject.setRepoId(monObjectRequest.getRepoId());
        monitorObject.setUserId(monObjectRequest.getUserId());
        monitorObject.setPersonId(monObjectRequest.getPersonId());
        monitorObject.setName(monObjectRequest.getName());
        monitorObject.setCertCode(monObjectRequest.getCertCode());
        monitorObject.setPhone(monObjectRequest.getPhone());
        monitorObject.setAddress(monObjectRequest.getAddress());
        monitorObject.setCreateAt(System.currentTimeMillis());
        monitorObject.setUpdateAt(monitorObject.getCreateAt());
        if (monitorObject.getPersonId() == null) {
            monitorObject.setMd5(DigestUtils.md5Hex(monitorObject.toString()));
        }
        monObjectDao.insertMonObject(monitorObject);

        MonObjectDto monObjectDto = new MonObjectDto();
        toMonObjectDto(monitorObject, monObjectDto);

        updateObjectsCount();

        return monObjectDto;
    }

    public MonObjectDto modifyMonObject(Long id, MonObjectRequest monObjectRequest) {
        MonitorObject monitorObject = monObjectDao.selectMonObjectById(id);
        if (monObjectRequest.getObjectName() != null) monitorObject.setObjectName(monObjectRequest.getObjectName());
        if (monObjectRequest.getObjectType() != null) monitorObject.setObjectType(monObjectRequest.getObjectType());
        if (monObjectRequest.getObjectValue() != null) monitorObject.setObjectValue(monObjectRequest.getObjectValue());
        if (monObjectRequest.getVendorDesc() != null) monitorObject.setVendorDesc(monObjectRequest.getVendorDesc());
        if (monObjectRequest.getDescription() != null) monitorObject.setDescription(monObjectRequest.getDescription());
        if (monObjectRequest.getUserId() != null) monitorObject.setUserId(monObjectRequest.getUserId());
        if (monObjectRequest.getPersonId() == null) {
            if (monObjectRequest.getName() != null) monitorObject.setName(monObjectRequest.getName());
            if (monObjectRequest.getCertCode() != null) monitorObject.setCertCode(monObjectRequest.getCertCode());
            if (monObjectRequest.getPhone() != null) monitorObject.setPhone(monObjectRequest.getPhone());
            if (monObjectRequest.getAddress() != null) monitorObject.setAddress(monObjectRequest.getAddress());
        }
        monitorObject.setUpdateAt(System.currentTimeMillis());
        if (monitorObject.getPersonId() == null) {
            monitorObject.setMd5(DigestUtils.md5Hex(monitorObject.toString()));
        }
        monObjectDao.updateMonObject(monitorObject);

        if (monObjectRequest.getPersonId() != null) {
            MonitorPerson monitorPerson = monPersonDao.selectMonPersonById(monObjectRequest.getPersonId());
            if (monObjectRequest.getName() != null) monitorPerson.setName(monObjectRequest.getName());
            if (monObjectRequest.getAddress() != null) monitorPerson.setAddress(monObjectRequest.getAddress());
            if (monObjectRequest.getCertCode() != null) monitorPerson.setCertCode(monObjectRequest.getCertCode());
            if (monObjectRequest.getPhone() != null) monitorPerson.setPhone(monObjectRequest.getPhone());
            if (monObjectRequest.getUserId() != null) monitorPerson.setUserId(monObjectRequest.getUserId());
            monitorPerson.setUpdateAt(System.currentTimeMillis());
            monPersonDao.updateMonPerson(monitorPerson);
        }

        MonObjectDto monObjectDto = new MonObjectDto();
        toMonObjectDto(monitorObject, monObjectDto);
        return monObjectDto;
    }

    void toMonObjectDto(MonitorObject monitorObject, MonObjectDto monObjectDto) {
        monObjectDto.setUid(monitorObject.getUid());
        monObjectDto.setObjectName(monitorObject.getObjectName());
        monObjectDto.setObjectType(monitorObject.getObjectType());
        monObjectDto.setObjectValue(monitorObject.getObjectValue());
        monObjectDto.setVendorDesc(monitorObject.getVendorDesc());
        monObjectDto.setDescription(monitorObject.getDescription());
        monObjectDto.setPersonId(monitorObject.getPersonId());
        monObjectDto.setRepoId(monitorObject.getRepoId());
        monObjectDto.setName(monitorObject.getName());
        monObjectDto.setCertCode(monitorObject.getCertCode());
        monObjectDto.setPhone(monitorObject.getPhone());
        monObjectDto.setAddress(monitorObject.getAddress());
        monObjectDto.setCreateAt(monitorObject.getCreateAt());
        monObjectDto.setUpdateAt(monitorObject.getUpdateAt());
    }

    public void deleteMonObject(Long id) {
        monObjectDao.deleteMonObjectById(id);
        updateObjectsCount();
    }

    public void impExcel(MultipartFile file, Long repoId, Integer userId) {
        List<MonitorObject> objects = new ArrayList<>();

        List<Map<Integer, String>> list = MonUtilDto.readExcelContentByList(file);
        for (int i = 0, size = list.size(); i < size; i++) {
            Map<Integer, String> map = list.get(i);
            String objectName = map.get(0);
            String objectType = map.get(1);
            String objectValue = map.get(2);
            if (objectName == null || objectName.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第1列，物品名称不能为空");
            if (objectType == null || objectType.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第2列，实体类型不能为空");
            if (objectValue == null || objectValue.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第3列，实体值不能为空");

            MonitorObject monitorObject = new MonitorObject();
            monitorObject.setObjectName(objectName);
            try {
                monitorObject.setObjectType(CollectType.getByLabel(objectType).code());
            } catch (ArgumentException ae) {
                throw new ArgumentException("第" + (i + 2) + "行，第2列，实体类型非法");
            }
            monitorObject.setObjectValue(objectValue);
            monitorObject.setName(map.get(3));
            monitorObject.setCertCode(map.get(4));
            monitorObject.setPhone(map.get(5));
            monitorObject.setAddress(map.get(6));
            monitorObject.setDescription(map.get(7));
            monitorObject.setUid(MonUtilDto.generateUid());
            monitorObject.setPersonId(null);
            monitorObject.setRepoId(repoId);
            monitorObject.setUserId(userId);
            monitorObject.setCreateAt(System.currentTimeMillis());
            monitorObject.setUpdateAt(monitorObject.getCreateAt());
            monitorObject.setMd5(DigestUtils.md5Hex(monitorObject.toString()));
            int count = monObjectDao.countMonObjectByMd5(monitorObject.getMd5());
            if (count > 0) { throw new ArgumentException("第"+ (i+2) + "行，该物品已存在库中"); }
            objects.add(monitorObject);
        }

        batchAddObjects(objects);

        updateObjectsCount();
    }

    public void impCsv(MultipartFile file, Long repoId, Integer userId) {
        List<MonitorObject> objects = new ArrayList<>();

        List<Map<Integer, String>> list = MonUtilDto.readCsvContentByList(file);
        for (int i = 0, size = list.size(); i < size; i++) {
            Map<Integer, String> map = list.get(i);
            String objectName = map.get(0);
            String objectType = map.get(1);
            String objectValue = map.get(2);
            if (objectName == null || objectName.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第1列，物品名称不能为空");
            if (objectType == null || objectType.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第2列，实体类型不能为空");
            if (objectValue == null || objectValue.equals(""))
                throw new ArgumentException("第" + (i + 2) + "行，第3列，实体值不能为空");

            MonitorObject monitorObject = new MonitorObject();
            monitorObject.setObjectName(objectName);
            try {
                monitorObject.setObjectType(CollectType.getByLabel(objectType).code());
            } catch (ArgumentException ae) {
                throw new ArgumentException("第" + (i + 2) + "行，第2列，实体类型非法");
            }
            monitorObject.setObjectValue(objectValue);
            monitorObject.setName(map.get(3));
            monitorObject.setCertCode(map.get(4));
            monitorObject.setPhone(map.get(5));
            monitorObject.setAddress(map.get(6));
            monitorObject.setDescription(map.get(7));
            monitorObject.setUid(MonUtilDto.generateUid());
            monitorObject.setPersonId(null);
            monitorObject.setRepoId(repoId);
            monitorObject.setUserId(userId);
            monitorObject.setCreateAt(System.currentTimeMillis());
            monitorObject.setUpdateAt(monitorObject.getCreateAt());
            monitorObject.setMd5(DigestUtils.md5Hex(monitorObject.toString()));
            int count = monObjectDao.countMonObjectByMd5(monitorObject.getMd5());
            if (count > 0) { throw new ArgumentException("第"+ (i+2) + "行，该物品已存在库中"); }
            objects.add(monitorObject);
        }

        batchAddObjects(objects);

        updateObjectsCount();
    }

    public void batchAddObjects(List<MonitorObject> objects) {
        if (objects.isEmpty()) { return; }
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(objects.get(0).getRepoId());
        if (monitorRepo == null) { throw new ArgumentException("常用库不存在"); }
        monObjectDao.batchInsertObjects(objects);
    }

    private void updateObjectsCount() {
        ThreadPoolUtil.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                monRepoObjectsCountManager.updateObjectCount();
            }
        });
    }

    public void modifyMonRepoRemark(MonRemarkVo monRemarkVo){
        try {
            monObjectDao.updateMonObjectResc(monRemarkVo,new Date().getTime());
        } catch (DBException e) {
            throw new DBException("monitor object modify description error");
        }
    }
}
