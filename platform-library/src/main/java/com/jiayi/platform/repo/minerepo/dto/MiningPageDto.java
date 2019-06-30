package com.jiayi.platform.repo.minerepo.dto;


import com.jiayi.platform.common.web.dto.PageResult;

import java.util.List;

public class MiningPageDto<T> extends PageResult<T> {
    public MiningPageDto(List<T> data, Long total, Integer page, Integer size) {
        super(data, total, page, size);
    }
}
