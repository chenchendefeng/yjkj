package com.jiayi.platform.judge.response;

import com.jiayi.platform.common.web.dto.PageResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FileParsingResponse extends PageResult<String[]> {
    @ApiModelProperty(value = "表头")
    private String[] header;
    private boolean[] isNotEmpty;

    public FileParsingResponse(String[] header, boolean[]isNotEmpty, List<String[]> contents, Long total, Integer page, Integer size) {
        super(contents, total, page, size);
        this.header = header;
        this.isNotEmpty = isNotEmpty;
    }
}
