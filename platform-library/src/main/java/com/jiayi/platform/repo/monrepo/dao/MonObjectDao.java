package com.jiayi.platform.repo.monrepo.dao;

import com.jiayi.platform.repo.monrepo.dto.MonObjectDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorObject;
import com.jiayi.platform.repo.monrepo.vo.MonObjectSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonRemarkVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MonObjectDao {
    List<MonObjectDto> selectMonObjectList (MonObjectSearchVo monObjectSearchVo);
    Long countMonObjectList (MonObjectSearchVo monObjectSearchVo);
    MonitorObject selectMonObjectById (Long id);
    int countMonObjectByMd5 (String md5);
    void insertMonObject (MonitorObject monitorObject);
    void batchInsertObjects (List<MonitorObject> monitorObjects);
    void updateMonObject (MonitorObject monitorObject);
    void deleteMonObjectById (Long id);
    void updateMonObjectResc (@Param ("request") MonRemarkVo monRemarkVo, @Param ("now") Long now);
}
