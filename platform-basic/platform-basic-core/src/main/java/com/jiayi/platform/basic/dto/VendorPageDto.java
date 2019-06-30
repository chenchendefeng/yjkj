package com.jiayi.platform.basic.dto;

import com.jiayi.platform.common.web.dto.PageResult;

import java.util.List;

public class VendorPageDto<T> extends PageResult<T> {
    public VendorPageDto(List<T> data, Long total, Integer page, Integer size) {
        super(data, total, page, size);
    }
}
