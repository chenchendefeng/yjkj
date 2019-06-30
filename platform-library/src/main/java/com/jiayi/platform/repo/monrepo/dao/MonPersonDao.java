package com.jiayi.platform.repo.monrepo.dao;

import com.jiayi.platform.repo.monrepo.dto.MonPersonDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorObject;
import com.jiayi.platform.repo.monrepo.entity.MonitorPerson;
import com.jiayi.platform.repo.monrepo.vo.MonPersonSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonRemarkVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonPersonDao {
    List<MonPersonDto> selectMonPersonList (MonPersonSearchVo monPersonSearchVo);
    Long countMonPersonList (MonPersonSearchVo monPersonSearchVo);
    MonitorPerson selectMonPersonById (Long id);
    int countMonPersonByMd5 (String md5);
    void insertMonPerson (MonitorPerson monitorPerson);
    void batchInsertPersons (List<MonitorPerson> list);
    void updateMonPerson (MonitorPerson monitorPerson);
    void insertMonObject (MonitorObject monitorObject);
    void deleteMonPersonById (Long personId);
    void deleteMonObjectByPersonId (Long personId);
    void updateMonPersonResc (@Param ("request") MonRemarkVo monRemarkVo, @Param ("now") Long now);
}
