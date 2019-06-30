package com.jiayi.platform.repo.monrepo.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.repo.monrepo.dao.MonObjectDao;
import com.jiayi.platform.repo.monrepo.dao.MonPersonDao;
import com.jiayi.platform.repo.monrepo.dao.MonRepoDao;
import com.jiayi.platform.repo.monrepo.dto.MonObjDesc;
import com.jiayi.platform.repo.monrepo.dto.MonPageDto;
import com.jiayi.platform.repo.monrepo.dto.MonPersonDto;
import com.jiayi.platform.repo.monrepo.dto.MonUtilDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorObject;
import com.jiayi.platform.repo.monrepo.entity.MonitorPerson;
import com.jiayi.platform.repo.monrepo.entity.MonitorRepo;
import com.jiayi.platform.repo.monrepo.enums.MonitorTypeEnum;
import com.jiayi.platform.repo.monrepo.vo.MonPersonRequest;
import com.jiayi.platform.repo.monrepo.vo.MonPersonSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonRemarkVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.Future;

@Service
@Transactional
public class MonPersonService {
    private static Logger log = LoggerFactory.getLogger(MonPersonService.class);
    @Autowired
    private MonPersonDao monPersonDao;
    @Autowired
    private MonObjectDao monObjectDao;
    @Autowired
    private MonRepoDao monRepoDao;
    @Autowired
    private MonRepoObjectsCountManager monRepoObjectsCountManager;

    public MonPageDto<MonPersonDto> findMonPersonList(MonPersonSearchVo monPersonSearchVo) {
        /*
        List<MonPersonDto> monPersonDtoAll = monPersonDao.selectMonPersonList(monPersonSearchVo);

        if (monPersonSearchVo.getPage() == null) {
            monPersonSearchVo.setPage(0);
        }
        if (monPersonSearchVo.getSize() == null) {
            monPersonSearchVo.setSize(10);
        }

        int start = monPersonSearchVo.getPage() * monPersonSearchVo.getSize();
        int count = monPersonDtoAll.size();
        int end = count - start > monPersonSearchVo.getSize() ? start + monPersonSearchVo.getSize() : count;
        if(CollectionUtils.isEmpty(monPersonDtoAll) || start > end) {
            return new MonPageDto<MonPersonDto>(Lists.newArrayList(), (long) count, monPersonSearchVo.getPage(), 0);
        }
        List<MonPersonDto> list = monPersonDtoAll.subList(start, end);
        return new MonPageDto<MonPersonDto>(list, (long)count, monPersonSearchVo.getPage(), list.size());
        */

        if (monPersonSearchVo.getPage() == null) {
            monPersonSearchVo.setPage(0);
        }
        if (monPersonSearchVo.getSize() == null) {
            monPersonSearchVo.setSize(10);
        }

        log.trace("perf-monPerson: begin find data and count");
        Future<Long> countFuture = ThreadPoolUtil.getInstance()
                .submit(() -> monPersonDao.countMonPersonList(monPersonSearchVo));
        List<MonPersonDto> list = monPersonDao.selectMonPersonList(monPersonSearchVo);

        try {
            Long count = countFuture.get();

            log.trace("perf-monPerson: end find data and count");

            if(CollectionUtils.isEmpty(list)) {
                return new MonPageDto<MonPersonDto>(Lists.newArrayList(), (long) count, monPersonSearchVo.getPage(), 0);
            }
            return new MonPageDto<MonPersonDto>(list, (long)count, monPersonSearchVo.getPage(), list.size());
        } catch (Exception e) {
            throw new ArgumentException("monPersonList impala search error", e);
        }
    }

    public MonPersonDto addMonPerson(MonPersonRequest monPersonRequest) {
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(monPersonRequest.getRepoId());
        if (monitorRepo == null) { throw new ArgumentException("常用库不存在"); }

        MonitorPerson monitorPerson = new MonitorPerson();
        monitorPerson.setUid(MonUtilDto.generateUid());
        monitorPerson.setName(monPersonRequest.getName());
        monitorPerson.setSex(monPersonRequest.getSex());
        monitorPerson.setAge(monPersonRequest.getAge());
        monitorPerson.setBirthplace(monPersonRequest.getBirthplace());
        monitorPerson.setAddress(monPersonRequest.getAddress());
        monitorPerson.setCertCode(monPersonRequest.getCertCode());
        monitorPerson.setPhone(monPersonRequest.getPhone());
        monitorPerson.setDescription(monPersonRequest.getDescription());
        monitorPerson.setMonitorType(monPersonRequest.getMonitorType());
        monitorPerson.setRepoId(monPersonRequest.getRepoId());
        monitorPerson.setUserId(monPersonRequest.getUserId());
        monitorPerson.setMonObjList(monPersonRequest.getMonObjList());
        monitorPerson.setCreateAt(System.currentTimeMillis());
        monitorPerson.setUpdateAt(monitorPerson.getCreateAt());
        monitorPerson.setMd5(DigestUtils.md5Hex(monitorPerson.toString()));
        int count = monPersonDao.countMonPersonByMd5(monitorPerson.getMd5());
        if (count > 0) { throw new ArgumentException("该人员已存在库中"); }
        monPersonDao.insertMonPerson(monitorPerson);

        if (monitorPerson.getMonObjList() != null) {
            for (MonObjDesc monObjDesc : monitorPerson.getMonObjList()) {
                MonitorObject monitorObject = new MonitorObject();
                monitorObject.setUid(MonUtilDto.generateUid());
                monitorObject.setObjectName("");
                monitorObject.setObjectType(monObjDesc.getObjType());
                monitorObject.setObjectValue(monObjDesc.getObjValue());
                monitorObject.setVendorDesc("");
                monitorObject.setDescription("");
                monitorObject.setPersonId(monitorPerson.getUid());
                monitorObject.setRepoId(monitorPerson.getRepoId());
                monitorObject.setUserId(monitorPerson.getUserId());
                monitorObject.setCreateAt(monitorPerson.getCreateAt());
                monitorObject.setUpdateAt(monitorPerson.getUpdateAt());
                monPersonDao.insertMonObject(monitorObject);
            }
        }

        MonPersonDto monPersonDto = new MonPersonDto();
        toMonPersonDto(monitorPerson, monPersonDto);

        updatePersonAndObjectCount();

        return monPersonDto;
    }

    private void updatePersonAndObjectCount() {
        ThreadPoolUtil.getInstance().submit(() -> {
            monRepoObjectsCountManager.updatePersonCount();
            monRepoObjectsCountManager.updateObjectCount();
        });
    }

    public void batchAddPersons(List<MonitorPerson> persons) {
        if (persons.isEmpty()) { return; }
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(persons.get(0).getRepoId());
        if (monitorRepo == null) { throw new ArgumentException("常用库不存在"); }
        monPersonDao.batchInsertPersons(persons);
    }

    public void batchAddObjects(List<MonitorObject> objects) {
        if (objects.isEmpty()) { return; }
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(objects.get(0).getRepoId());
        if (monitorRepo == null) { throw new ArgumentException("常用库不存在"); }
        monObjectDao.batchInsertObjects(objects);
    }

    public MonPersonDto modifyMonPerson(Long id, MonPersonRequest monPersonRequest) {
        MonitorPerson monitorPerson = monPersonDao.selectMonPersonById(id);
        if (monPersonRequest.getName() != null) monitorPerson.setName(monPersonRequest.getName());
        if (monPersonRequest.getSex() != null)  monitorPerson.setSex(monPersonRequest.getSex());
        if (monPersonRequest.getAge() != null)  monitorPerson.setAge(monPersonRequest.getAge());
        if (monPersonRequest.getBirthplace() != null) monitorPerson.setBirthplace(monPersonRequest.getBirthplace());
        if (monPersonRequest.getAddress() != null) monitorPerson.setAddress(monPersonRequest.getAddress());
        if (monPersonRequest.getCertCode() != null) monitorPerson.setCertCode(monPersonRequest.getCertCode());
        if (monPersonRequest.getPhone() != null) monitorPerson.setPhone(monPersonRequest.getPhone());
        if (monPersonRequest.getDescription() != null) monitorPerson.setDescription(monPersonRequest.getDescription());
        if (monPersonRequest.getMonitorType() != null) monitorPerson.setMonitorType(monPersonRequest.getMonitorType());
        if (monPersonRequest.getUserId() != null) monitorPerson.setUserId(monPersonRequest.getUserId());
        if (monPersonRequest.getMonObjList() != null) monitorPerson.setMonObjList(monPersonRequest.getMonObjList());
        monitorPerson.setUpdateAt(System.currentTimeMillis());
        monitorPerson.setMd5(DigestUtils.md5Hex(monitorPerson.toString()));
        monPersonDao.updateMonPerson(monitorPerson);

        if (monPersonRequest.getMonObjList() != null) {
            monPersonDao.deleteMonObjectByPersonId(id);
            for (MonObjDesc monObjDesc : monitorPerson.getMonObjList()) {
                MonitorObject monitorObject = new MonitorObject();
                monitorObject.setUid(MonUtilDto.generateUid());
                monitorObject.setObjectName("");
                monitorObject.setObjectType(monObjDesc.getObjType());
                monitorObject.setObjectValue(monObjDesc.getObjValue());
                monitorObject.setVendorDesc("");
                monitorObject.setDescription("");
                monitorObject.setPersonId(monitorPerson.getUid());
                monitorObject.setRepoId(monitorPerson.getRepoId());
                monitorObject.setUserId(monitorPerson.getUserId());
                monitorObject.setCreateAt(monitorPerson.getCreateAt());
                monitorObject.setUpdateAt(monitorPerson.getUpdateAt());
                monPersonDao.insertMonObject(monitorObject);
            }
        }

        MonPersonDto monPersonDto = new MonPersonDto();
        toMonPersonDto(monitorPerson, monPersonDto);
        return monPersonDto;
    }

    void toMonPersonDto(MonitorPerson monitorPerson, MonPersonDto monPersonDto) {
        monPersonDto.setUid(monitorPerson.getUid());
        monPersonDto.setName(monitorPerson.getName());
        monPersonDto.setSex(monitorPerson.getSex());
        monPersonDto.setAge(monitorPerson.getAge());
        monPersonDto.setBirthplace(monitorPerson.getBirthplace());
        monPersonDto.setAddress(monitorPerson.getAddress());
        monPersonDto.setMonitorType(monitorPerson.getMonitorType());
        monPersonDto.setCertCode(monitorPerson.getCertCode());
        monPersonDto.setPhone(monitorPerson.getPhone());
        monPersonDto.setDescription(monitorPerson.getDescription());
        monPersonDto.setCreateAt(monitorPerson.getCreateAt());
        monPersonDto.setUpdateAt(monitorPerson.getUpdateAt());
        String monObjValues = "";
        if (monitorPerson.getMonObjList() != null) {
            for (int i = 0, size = monitorPerson.getMonObjList().size(); i < size; i++) {
                MonObjDesc monObjDesc = monitorPerson.getMonObjList().get(i);
                monObjValues += CollectType.getByCode(monObjDesc.getObjType()).desc() + " " + monObjDesc.getObjValue();
                if (i < size - 1) monObjValues += ", ";
            }
        }
        monPersonDto.setObjectValues(monObjValues);
        monPersonDto.setRepoId(monitorPerson.getRepoId());
    }

    public void impExcel(MultipartFile file, Long repoId, Integer userId) {
        try {
            List<MonitorPerson> persons = new ArrayList<>();
            List<MonitorObject> objects = new ArrayList<>();

            List<Map<Integer, String>> list = MonUtilDto.readExcelContentByList(file);
            for (int i = 0, size = list.size(); i < size; i++) {
                Map<Integer, String> map = list.get(i);
                String name = map.get(0);
                if (name == null || name.equals("")) throw new ArgumentException("第" + (i + 2) + "行，第1列，姓名不能为空");
                String objectValues = map.get(4);
                List<MonObjDesc> monObjDescs = new ArrayList();
                for (String obj : Arrays.asList(objectValues.split(","))) {
                    obj = obj.trim();
                    int pos = obj.indexOf(" ");
                    if (pos != -1) {
                        MonObjDesc objDesc = new MonObjDesc();
                        try {
                            objDesc.setObjType(CollectType.getByLabel(obj.substring(0, pos)).code());
                        } catch (ArgumentException ae) {
                            throw new ArgumentException("第" + (i + 2) + "行，第5列，布控要素的类型非法");
                        }
                        objDesc.setObjValue(obj.substring(pos).trim());
                        monObjDescs.add(objDesc);
                    }
                }

                MonitorPerson monitorPerson = new MonitorPerson();
                monitorPerson.setName(map.get(0));
                monitorPerson.setSex(map.get(1));
                monitorPerson.setAge(Double.valueOf(map.get(2)).intValue());
                monitorPerson.setBirthplace(map.get(3));
                monitorPerson.setMonObjList(monObjDescs);  //4
                try {
                    monitorPerson.setMonitorType(MonitorTypeEnum.getTypeByDesc(map.get(5)));
                } catch (ArgumentException ae) {
                    throw new ArgumentException("第" + (i + 2) + "行，第6列，布控类型非法");
                }
                monitorPerson.setCertCode(map.get(6));
                monitorPerson.setPhone(map.get(7));
                monitorPerson.setAddress(map.get(8));
                monitorPerson.setDescription(map.get(9));
                monitorPerson.setUid(MonUtilDto.generateUid());
                monitorPerson.setRepoId(repoId);
                monitorPerson.setUserId(userId);
                monitorPerson.setCreateAt(System.currentTimeMillis());
                monitorPerson.setUpdateAt(monitorPerson.getCreateAt());
                monitorPerson.setMd5(DigestUtils.md5Hex(monitorPerson.toString()));
                int count = monPersonDao.countMonPersonByMd5(monitorPerson.getMd5());
                if (count > 0) {
                    throw new ArgumentException("第" + (i + 2) + "行，该人员已存在库中");
                }
                persons.add(monitorPerson);

                if (monitorPerson.getMonObjList() != null) {
                    for (MonObjDesc monObjDesc : monitorPerson.getMonObjList()) {
                        MonitorObject monitorObject = new MonitorObject();
                        monitorObject.setUid(MonUtilDto.generateUid());
                        monitorObject.setObjectName("");
                        monitorObject.setObjectType(monObjDesc.getObjType());
                        monitorObject.setObjectValue(monObjDesc.getObjValue());
                        monitorObject.setVendorDesc("");
                        monitorObject.setDescription("");
                        monitorObject.setPersonId(monitorPerson.getUid());
                        monitorObject.setRepoId(monitorPerson.getRepoId());
                        monitorObject.setUserId(monitorPerson.getUserId());
                        monitorObject.setCreateAt(monitorPerson.getCreateAt());
                        monitorObject.setUpdateAt(monitorPerson.getUpdateAt());
                        objects.add(monitorObject);
                    }
                }
            }

            batchAddPersons(persons);
            batchAddObjects(objects);

            updatePersonAndObjectCount();
        }
        catch (ArgumentException ae) {
            throw ae;
        }
        catch (Exception e) {
            throw new ArgumentException("数据格式错误！");
        }
    }

    public void impCsv(MultipartFile file, Long repoId, Integer userId) {
        try {
            List<MonitorPerson> persons = new ArrayList<>();
            List<MonitorObject> objects = new ArrayList<>();

            List<Map<Integer, String>> list = MonUtilDto.readCsvContentByList(file);
            for (int i = 0, size = list.size(); i < size; i++) {
                Map<Integer, String> map = list.get(i);
                String name = map.get(0);
                if (name == null || name.equals("")) throw new ArgumentException("第" + (i + 2) + "行，第1列，姓名不能为空");
                String objectValues = map.get(4);
                List<MonObjDesc> monObjDescs = new ArrayList();
                for (String obj : Arrays.asList(objectValues.split(","))) {
                    obj = obj.trim();
                    int pos = obj.indexOf(" ");
                    if (pos != -1) {
                        MonObjDesc objDesc = new MonObjDesc();
                        try {
                            objDesc.setObjType(CollectType.getByLabel(obj.substring(0, pos)).code());
                        } catch (ArgumentException ae) {
                            throw new ArgumentException("第" + (i + 2) + "行，第5列，布控要素的类型非法");
                        }
                        objDesc.setObjValue(obj.substring(pos).trim());
                        monObjDescs.add(objDesc);
                    }
                }

                MonitorPerson monitorPerson = new MonitorPerson();
                monitorPerson.setName(map.get(0));
                monitorPerson.setSex(map.get(1));
                monitorPerson.setAge(Double.valueOf(map.get(2)).intValue());
                monitorPerson.setBirthplace(map.get(3));
                monitorPerson.setMonObjList(monObjDescs);  //4
                try {
                    monitorPerson.setMonitorType(MonitorTypeEnum.getTypeByDesc(map.get(5)));
                } catch (ArgumentException ae) {
                    throw new ArgumentException("第" + (i + 2) + "行，第6列，布控类型非法");
                }
                monitorPerson.setCertCode(map.get(6));
                monitorPerson.setPhone(map.get(7));
                monitorPerson.setAddress(map.get(8));
                monitorPerson.setDescription(map.get(9));
                monitorPerson.setUid(MonUtilDto.generateUid());
                monitorPerson.setRepoId(repoId);
                monitorPerson.setUserId(userId);
                monitorPerson.setCreateAt(System.currentTimeMillis());
                monitorPerson.setUpdateAt(monitorPerson.getCreateAt());
                monitorPerson.setMd5(DigestUtils.md5Hex(monitorPerson.toString()));
                int count = monPersonDao.countMonPersonByMd5(monitorPerson.getMd5());
                if (count > 0) {
                    throw new ArgumentException("第" + (i + 2) + "行，该人员已存在库中");
                }
                persons.add(monitorPerson);

                if (monitorPerson.getMonObjList() != null) {
                    for (MonObjDesc monObjDesc : monitorPerson.getMonObjList()) {
                        MonitorObject monitorObject = new MonitorObject();
                        monitorObject.setUid(MonUtilDto.generateUid());
                        monitorObject.setObjectName("");
                        monitorObject.setObjectType(monObjDesc.getObjType());
                        monitorObject.setObjectValue(monObjDesc.getObjValue());
                        monitorObject.setVendorDesc("");
                        monitorObject.setDescription("");
                        monitorObject.setPersonId(monitorPerson.getUid());
                        monitorObject.setRepoId(monitorPerson.getRepoId());
                        monitorObject.setUserId(monitorPerson.getUserId());
                        monitorObject.setCreateAt(monitorPerson.getCreateAt());
                        monitorObject.setUpdateAt(monitorPerson.getUpdateAt());
                        objects.add(monitorObject);
                    }
                }
            }

            batchAddPersons(persons);
            batchAddObjects(objects);

            updatePersonAndObjectCount();
        }
        catch (ArgumentException ae) {
            throw ae;
        }
        catch (Exception e) {
            throw new ArgumentException("数据格式错误，请另存为CSV格式重新导入！");
        }
    }

    public void deleteMonPerson(Long id) {
        monPersonDao.deleteMonObjectByPersonId(id);
        monPersonDao.deleteMonPersonById(id);

        updatePersonAndObjectCount();
    }

    public void modifyMonRepoRemark(MonRemarkVo monRemarkVo){
        try {
            monPersonDao.updateMonPersonResc(monRemarkVo,new Date().getTime());
        } catch (Exception e) {
            throw new ArgumentException("update remark error", e);
        }
    }
}
