package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.query.FileImportQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FileImportDao {
    void insertFileImport(FileImportQuery query);

    List<Map<String, Object>> selectFileImport(@Param("uid") Long uid, @Param("fields") List<String> fields, @Param("limit") Integer limit,
                                                   @Param("offset") Long offset);

    Long countFileImport(@Param("uid") Long uid);
}
