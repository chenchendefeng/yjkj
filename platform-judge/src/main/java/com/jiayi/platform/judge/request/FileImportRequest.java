package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class FileImportRequest extends JudgeRequest {
    @ApiModelProperty(value = "请求上传文件时返回的文件名(带后缀.csv)", required = true)
    @NotNull(message = "文件名不能为空")
    private String fileName;
    @ApiModelProperty(value = "结果集名称")
    @NotNull(message = "结果集名称不能为空")
    private String resultName;
    @ApiModelProperty(value = "导入数据title", required = true)
    private List<FileObjectTypeInfo> objectTypes;
}
