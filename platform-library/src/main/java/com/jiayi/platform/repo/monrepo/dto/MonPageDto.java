package com.jiayi.platform.repo.monrepo.dto;



import com.jiayi.platform.security.core.dto.PageResult;

import java.util.List;

public class MonPageDto<T> extends PageResult<T> {
    public MonPageDto(List<T> data, Long total, Integer page, Integer size) {
        super(data, total, page, size);
    }
}
