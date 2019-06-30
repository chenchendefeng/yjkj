package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:05
 */
@Getter
@Setter
@ToString(callSuper=true)
public class LineCollisionRequest extends JudgeRequest {
    @ApiModelProperty(value = "开始时间", required = true, example = "1502858803000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1502858803000")
    private Long endDate;
    @ApiModelProperty(value = "路线碰撞区域")
    private List<Area> areaList;
    @ApiModelProperty(value = "匹配系数", required = true, example = "2")
    private Integer matchCount;
    @ApiModelProperty(value = "筛选对象值列表")
    private List<String> objectValueList;
}
