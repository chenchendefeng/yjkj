package com.jiayi.platform.judge.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author : weichengke
 * @date : 2019-04-18 17:45
 */
@Getter
@Setter
@ToString
public class JudgeRequest {
    @ApiModelProperty(value = "案件ID", required = true, example = "123456")
    @NotNull(message = "案件ID不能为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String caseId;
    @ApiModelProperty(value = "用户ID", required = true, example = "111")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected Long userId;
    @ApiModelProperty(value = "对象类型", example = "mac")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String objectTypeName;
}
