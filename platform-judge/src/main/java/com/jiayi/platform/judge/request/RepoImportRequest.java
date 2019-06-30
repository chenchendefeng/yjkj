package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RepoImportRequest extends JudgeRequest {
    @ApiModelProperty(value = "库类型:1常用库,2挖掘库", example = "1")
    @NotNull(message = "库类型不能为空")
    private Integer repoType;
    @ApiModelProperty(value = "库ID")
    @NotNull(message = "库ID不能为空")
    private Long repoId;
    @ApiModelProperty(value = "结果集名称", example = "test结果集")
    @NotNull(message = "结果集名称不能为空")
    private String resultName;
}
