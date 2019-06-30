package com.jiayi.platform.judge.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileImportResponse {
    @ApiModelProperty(value = "下载错误文件使用的id")
    private long queryId;
    @ApiModelProperty(value = "导入成功数量")
    private int successNum;
    @ApiModelProperty(value = "导入失败数量")
    private int errorNum;

    public FileImportResponse(long queryId, int successNum, int errorNum) {
        this.queryId = queryId;
        this.successNum = successNum;
        this.errorNum = errorNum;
    }
}
