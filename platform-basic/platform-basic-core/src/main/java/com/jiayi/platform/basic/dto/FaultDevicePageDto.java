package com.jiayi.platform.basic.dto;

import com.jiayi.platform.common.web.dto.PageResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FaultDevicePageDto<T> extends PageResult<T> {
    private Integer noCreateCount;
    private Integer errTimeCount;

    public FaultDevicePageDto(List<T> data, Long total, Integer page, Integer size, Integer noCreateCount, Integer errTimeCount) {
        super(data, total, page, size);
        this.noCreateCount = noCreateCount;
        this.errTimeCount = errTimeCount;
    }
}