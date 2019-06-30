package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RequestRecordRequest {
    private Integer caseId;
    private String op;
    private List<Integer> userIds;
    private List<String> searchTypes;
    private List<String> secondSearchTypes; // 二次碰撞的类型选择
    @ApiModelProperty(value = "开始时间", example = "1502858803000")
    private Long beginDate = 0L;
    @ApiModelProperty(value = "结束时间", example = "1502858803000")
    private Long endDate = 0L;
    private Integer pageNo;
    private Integer pageSize;
    private String remark; // 备注的模糊搜索
}
